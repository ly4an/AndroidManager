package ru.facilicom24.manager.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import database.CleaningReason;
import database.FacilityUrgencyType;
import database.FacilityUrgencyTypeDao;
import database.OrioArticle;
import database.OrioType;
import database.WorkType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.CheckBlanksRepository;
import ru.facilicom24.manager.cache.CheckObjectsRepository;
import ru.facilicom24.manager.cache.CheckTypesRepository;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.fragments.CreateCheckFragment;
import ru.facilicom24.manager.model.CheckBlank;
import ru.facilicom24.manager.model.CheckObject;
import ru.facilicom24.manager.model.CheckType;
import ru.facilicom24.manager.retrofit.RFService;

public class DictionaryActivity
		extends FragmentActivity
		implements
		AdapterView.OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	final static public String REQUEST_CODE = "REQUEST_CODE";

	final static public String PARAMETER_GROUP_UID = "PARAMETER_GROUP_UID";
	final static public String PARAMETER_DIRECTUM_ID = "PARAMETER_DIRECTUM_ID";
	final static public String PARAMETER_CATEGORY_UID = "PARAMETER_CATEGORY_UID";

	final static public String KEY = "KEY";
	final static public String VALUE = "VALUE";

	DictionaryAdapter dictionaryAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_dictionary);

		if ((dictionaryAdapter = newInstance()) != null) {
			((TextView) findViewById(R.id.titleTextView)).setText(dictionaryAdapter.getCaption());

			// List

			ListView dictionaryListView = (ListView) findViewById(R.id.dictionaryListView);

			dictionaryListView.setAdapter(dictionaryAdapter);
			dictionaryListView.setOnItemClickListener(this);

			dictionaryAdapter.notifyDataSetChanged();

			// Search

			((EditText) findViewById(R.id.searchEditText)).addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
					dictionaryAdapter.getFilter().filter(cs);
				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				}

				@Override
				public void afterTextChanged(Editable arg0) {
				}
			});
		}
	}

	DictionaryAdapter newInstance() {
		switch (getIntent().getIntExtra(REQUEST_CODE, 0)) {
			case RequestNewActivity.URGENCY:
				return new FacilityUrgencyTypeAdapter();

			case RequestNewActivity.WORK_TYPE:
				return new WorkTypeAdapter();

			case RequestNewActivity.CLEANING_REASON:
				return new CleaningReasonAdapter();

			case ServiceRequestActivity.DICTIONARY_ORIO_TYPE:
				return new OrioTypeAdapter();

			case ServiceRequestActivity.DICTIONARY_ORIO_ARTICLE:
				return new OrioArticleAdapter();

			case ServiceRequestActivity.DICTIONARY_ORIO_INVENTORY:
				return new OrioInventoryAdapter();

			case CreateCheckFragment.CREATECHECKFRAGMENT_CATEGORY:
				return new CategoryAdapter();

			case CreateCheckFragment.CREATECHECKFRAGMENT_ACCOUNT:
				return new AccountAdapter();

			case CreateCheckFragment.CREATECHECKFRAGMENT_BLANK:
				return new CheckBlankAdapter();
		}

		return null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		setResult(Activity.RESULT_OK, dictionaryAdapter.getResult(position));
		finish();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	// DictionaryAdapter

	interface IDictionaryFilterPerformFiltering<T> {
		boolean rule(T item, String pattern);
	}

	abstract class DictionaryAdapter<T>
			extends BaseAdapter
			implements Filterable {

		protected List<T> items;
		List<T> viewItems;

		@Override
		public int getCount() {
			return viewItems.size();
		}

		@Override
		public T getItem(int position) {
			return viewItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public View getView(View convertView, ViewGroup parent, String text) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.activity_dictionary_item, parent, false);
			}

			((TextView) convertView.findViewById(R.id.nameTextView)).setText(text);

			return convertView;
		}

		public View getView(View convertView, ViewGroup parent, String text, String note) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.activity_dictionary_item, parent, false);
			}

			((TextView) convertView.findViewById(R.id.nameTextView)).setText(text);

			TextView noteTextView = (TextView) convertView.findViewById(R.id.noteTextView);

			noteTextView.setText(note);
			noteTextView.setVisibility(View.VISIBLE);

			return convertView;
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();

			findViewById(R.id.dictionaryListView).setVisibility(getCount() > 0 ? View.VISIBLE : View.GONE);
			findViewById(R.id.emptyTextView).setVisibility(getCount() > 0 ? View.GONE : View.VISIBLE);
		}

		abstract String getCaption();

		abstract Intent getResult(int position);
	}

	// DictionaryFilter

	private class DictionaryFilter<T> extends Filter {
		DictionaryAdapter dictionaryAdapter;
		IDictionaryFilterPerformFiltering dictionaryFilterPerformFiltering;

		DictionaryFilter(DictionaryAdapter dictionaryAdapter, IDictionaryFilterPerformFiltering dictionaryFilterPerformFiltering) {
			this.dictionaryAdapter = dictionaryAdapter;
			this.dictionaryFilterPerformFiltering = dictionaryFilterPerformFiltering;
		}

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			List<Object> values = new ArrayList<>();
			String pattern = constraint.toString().toLowerCase();

			for (Object item : dictionaryAdapter.items) {
				if (dictionaryFilterPerformFiltering.rule(item, pattern)) {
					values.add(item);
				}
			}

			FilterResults results = new FilterResults();

			results.values = values;
			results.count = values.size();

			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			dictionaryAdapter.viewItems = (List<T>) results.values;
			dictionaryAdapter.notifyDataSetChanged();
		}
	}

	// FacilityUrgencyTypeAdapter

	private class FacilityUrgencyTypeAdapter extends DictionaryAdapter<FacilityUrgencyType> {
		FacilityUrgencyTypeAdapter() {
			items = viewItems = FacilicomApplication.getInstance().getDaoSession().getFacilityUrgencyTypeDao().queryBuilder()
					.where(FacilityUrgencyTypeDao.Properties.ID.eq(getIntent().getIntExtra(PARAMETER_DIRECTUM_ID, 0)))
					.list();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getView(convertView, parent, getItem(position).getUrgencyType());
		}

		@Override
		public Filter getFilter() {
			return new DictionaryFilter<>(this, new IDictionaryFilterPerformFiltering<FacilityUrgencyType>() {
				@Override
				public boolean rule(FacilityUrgencyType item, String pattern) {
					return item.getUrgencyType().toLowerCase().contains(pattern);
				}
			});
		}

		@Override
		String getCaption() {
			return getString(R.string.activity_dictionary_urgency);
		}

		@Override
		Intent getResult(int position) {
			FacilityUrgencyType item = getItem(position);

			return new Intent()
					.putExtra(KEY, item.getUrgencyType())
					.putExtra(VALUE, item.getUrgencyType());
		}
	}

	// WorkTypeAdapter

	private class WorkTypeAdapter extends DictionaryAdapter<WorkType> {
		WorkTypeAdapter() {
			items = viewItems = FacilicomApplication.getInstance().getDaoSession().getWorkTypeDao().loadAll();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getView(convertView, parent, getItem(position).getSystemName());
		}

		@Override
		public Filter getFilter() {
			return new DictionaryFilter<>(this, new IDictionaryFilterPerformFiltering<WorkType>() {
				@Override
				public boolean rule(WorkType item, String pattern) {
					return item.getSystemName().toLowerCase().contains(pattern);
				}
			});
		}

		@Override
		String getCaption() {
			return getString(R.string.activity_dictionary_work_type);
		}

		@Override
		Intent getResult(int position) {
			WorkType item = getItem(position);

			return new Intent()
					.putExtra(KEY, item.getSystemGUID())
					.putExtra(VALUE, item.getSystemName());
		}
	}

	// CleaningReasonAdapter

	private class CleaningReasonAdapter extends DictionaryAdapter<CleaningReason> {
		CleaningReasonAdapter() {
			items = viewItems = FacilicomApplication.getInstance().getDaoSession().getCleaningReasonDao().loadAll();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getView(convertView, parent, getItem(position).getReasonName());
		}

		@Override
		public Filter getFilter() {
			return new DictionaryFilter<>(this, new IDictionaryFilterPerformFiltering<CleaningReason>() {
				@Override
				public boolean rule(CleaningReason item, String pattern) {
					return item.getReasonName().toLowerCase().contains(pattern);
				}
			});
		}

		@Override
		String getCaption() {
			return getString(R.string.activity_dictionary_cleaning_reason);
		}

		@Override
		Intent getResult(int position) {
			CleaningReason item = getItem(position);

			return new Intent()
					.putExtra(KEY, item.getReasonID())
					.putExtra(VALUE, item.getReasonName());
		}
	}

	// OrioTypeAdapter

	private class OrioTypeAdapter extends DictionaryAdapter<OrioType> {
		OrioTypeAdapter() {
			items = viewItems = FacilicomApplication.getInstance().getDaoSession().getOrioTypeDao().loadAll();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getView(convertView, parent, getItem(position).getName());
		}

		@Override
		public Filter getFilter() {
			return new DictionaryFilter<>(this, new IDictionaryFilterPerformFiltering<OrioType>() {
				@Override
				public boolean rule(OrioType item, String pattern) {
					return item.getName().toLowerCase().contains(pattern);
				}
			});
		}

		@Override
		String getCaption() {
			return getString(R.string.orioType);
		}

		@Override
		Intent getResult(int position) {
			OrioType item = getItem(position);

			return new Intent()
					.putExtra(KEY, item.getUID())
					.putExtra(VALUE, item.getName());
		}
	}

	// OrioArticleAdapter

	private class OrioArticleAdapter extends DictionaryAdapter<OrioArticle> {
		OrioArticleAdapter() {
			items = viewItems = FacilicomApplication.getInstance().getDaoSession().getOrioArticleDao().loadAll();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getView(convertView, parent, getItem(position).getName());
		}

		@Override
		public Filter getFilter() {
			return new DictionaryFilter<>(this, new IDictionaryFilterPerformFiltering<OrioArticle>() {
				@Override
				public boolean rule(OrioArticle item, String pattern) {
					return item.getName().toLowerCase().contains(pattern);
				}
			});
		}

		@Override
		String getCaption() {
			return getString(R.string.orioArticle);
		}

		@Override
		Intent getResult(int position) {
			OrioArticle item = getItem(position);

			return new Intent()
					.putExtra(KEY, item.getUID())
					.putExtra(VALUE, item.getName());
		}
	}

	// OrioInventoryAdapter

	private class OrioInventoryAdapter extends DictionaryAdapter<OrioInventory> {
		OrioInventoryAdapter() {
			items = viewItems = new ArrayList<>();

			final ProgressDialog progressDialog = new ProgressDialog(DictionaryActivity.this);

			progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);

			progressDialog.setIndeterminate(true);
			progressDialog.setMessage(getString(R.string.progress));

			progressDialog.show();

			RFService.orioInventory(
					getIntent().getStringExtra(PARAMETER_GROUP_UID),
					getIntent().getIntExtra(PARAMETER_DIRECTUM_ID, 0),
					new Callback<JsonArray>() {
						@Override
						public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
							progressDialog.dismiss();

							if (response != null && response.body() != null) {
								for (JsonElement jsonElement : response.body()) {
									JsonObject jsonObject = jsonElement.getAsJsonObject();

									viewItems.add(new OrioInventory(
											jsonObject.get("UID").getAsString(),
											jsonObject.get("Name").getAsString()
									));
								}
							}

							notifyDataSetChanged();
						}

						@Override
						public void onFailure(Call<JsonArray> call, Throwable t) {
							progressDialog.dismiss();
						}
					}
			);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getView(convertView, parent, getItem(position).Name);
		}

		@Override
		public Filter getFilter() {
			return new DictionaryFilter<>(this, new IDictionaryFilterPerformFiltering<OrioInventory>() {
				@Override
				public boolean rule(OrioInventory item, String pattern) {
					return item.Name.toLowerCase().contains(pattern);
				}
			});
		}

		@Override
		String getCaption() {
			return getString(R.string.orioInventory);
		}

		@Override
		Intent getResult(int position) {
			OrioInventory item = getItem(position);

			return new Intent()
					.putExtra(KEY, item.UID)
					.putExtra(VALUE, item.Name);
		}
	}

	// CategoryAdapter

	private class CategoryAdapter extends DictionaryAdapter<CheckType> {
		CategoryAdapter() {
			items = viewItems = new CheckTypesRepository(DictionaryActivity.this).getAll();
		}

		@Override
		String getCaption() {
			return getString(R.string.check_category);
		}

		@Override
		Intent getResult(int position) {
			CheckType checkType = getItem(position);

			return new Intent()
					.putExtra(KEY, checkType.getCheckId())
					.putExtra(VALUE, checkType.getCheckName());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getView(convertView, parent, getItem(position).getCheckName());
		}

		@Override
		public Filter getFilter() {
			return new DictionaryFilter<>(this, new IDictionaryFilterPerformFiltering<CheckType>() {
				@Override
				public boolean rule(CheckType checkType, String pattern) {
					return checkType.getCheckName().toLowerCase().contains(pattern);
				}
			});
		}
	}

	// AccountAdapter

	private class AccountAdapter extends DictionaryAdapter<CheckObject> {
		AccountAdapter() {
			CheckType checkType = new CheckTypesRepository(DictionaryActivity.this).getTypeById(getIntent().getStringExtra(PARAMETER_CATEGORY_UID));

			if (checkType != null) {
				items = viewItems = new CheckObjectsRepository(DictionaryActivity.this).getAll(checkType);
			} else {
				items = viewItems = new ArrayList<>();
			}
		}

		@Override
		String getCaption() {
			return getString(R.string.check_account);
		}

		@Override
		Intent getResult(int position) {
			CheckObject checkObject = getItem(position);

			return new Intent()
					.putExtra(KEY, checkObject.getCheckObjectId())
					.putExtra(VALUE, getName(checkObject));
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CheckObject checkObject = getItem(position);
			return getView(convertView, parent, checkObject.getCheckObjectName(), checkObject.getCheckObjectAddress());
		}

		@Override
		public Filter getFilter() {
			return new DictionaryFilter<>(this, new IDictionaryFilterPerformFiltering<CheckObject>() {
				@Override
				public boolean rule(CheckObject checkObject, String pattern) {
					return getName(checkObject).toLowerCase().contains(pattern);
				}
			});
		}

		String getName(CheckObject checkObject) {
			return TextUtils.concat(checkObject.getCheckObjectName(), ". ", checkObject.getCheckObjectAddress()).toString();
		}
	}

	// CheckBlankAdapter

	private class CheckBlankAdapter extends DictionaryAdapter<CheckBlank> {
		CheckBlankAdapter() {
			CheckType checkType = new CheckTypesRepository(DictionaryActivity.this).getTypeById(getIntent().getStringExtra(PARAMETER_CATEGORY_UID));

			if (checkType != null) {
				items = viewItems = new CheckBlanksRepository(DictionaryActivity.this).getAll(checkType);
			} else {
				items = viewItems = new ArrayList<>();
			}
		}

		@Override
		String getCaption() {
			return getString(R.string.check_blank);
		}

		@Override
		Intent getResult(int position) {
			CheckBlank checkBlank = getItem(position);

			return new Intent()
					.putExtra(KEY, checkBlank.getCheckBlankId())
					.putExtra(VALUE, checkBlank.getName());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getView(convertView, parent, getItem(position).getName());
		}

		@Override
		public Filter getFilter() {
			return new DictionaryFilter<>(this, new IDictionaryFilterPerformFiltering<CheckBlank>() {
				@Override
				public boolean rule(CheckBlank checkBlank, String pattern) {
					return checkBlank.getName().toLowerCase().contains(pattern);
				}
			});
		}
	}

	class OrioInventory {
		String UID;
		String Name;

		OrioInventory(String UID, String Name) {
			this.UID = UID;
			this.Name = Name;
		}
	}
}
