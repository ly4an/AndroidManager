package ru.facilicom24.manager.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import database.ActServiceType;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoActServiceTypeRepository;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;

public class CheckActServiceTypeActivity
		extends FragmentActivity
		implements
		OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	ListView lv;
	CheckClientListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.check_act_service_type_activity);

		lv = (ListView) findViewById(R.id.listView);
		lv.setOnItemClickListener(this);

		new LoadingTask(this).execute();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ActServiceType serviceType = (ActServiceType) parent.getAdapter().getItem(position);
		String serviceTypeId = serviceType.getServiceTypeId();

		Intent intent = new Intent();

		intent.putExtra("ActTypeId", serviceTypeId);
		intent.putExtra("Title", serviceType.getName());

		setResult(RESULT_OK, intent);

		finish();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class LoadingTask extends AsyncTask<Void, Void, String> {
		public CheckActServiceTypeActivity activity;
		ProgressDialog progressDialog;

		LoadingTask(CheckActServiceTypeActivity activity) {
			this.activity = activity;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(activity);
			progressDialog.setMessage("Загрузка");
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			List<ActServiceType> actAccounts = DaoActServiceTypeRepository.getAllActServiceTypes(CheckActServiceTypeActivity.this);

			int clientId = activity.getIntent().getIntExtra("ClientId", 0);
			int accountId = activity.getIntent().getIntExtra("AccountId", 0);

			boolean Status = activity.getIntent().getBooleanExtra("Status", true);

			if (clientId != 0) {
				actAccounts = DaoActServiceTypeRepository.getActServiceTypesForCliendId(CheckActServiceTypeActivity.this, clientId);

				if (!Status) {
					for (ActServiceType act : actAccounts) {
						if (!act.getStatus()) {
							actAccounts.remove(act);
						}
					}
				}
			}

			if (accountId != 0) {
				actAccounts = DaoActServiceTypeRepository.getActServiceTypesForAccountId(CheckActServiceTypeActivity.this, accountId);
			}

			List<ActServiceType> mCheckObjects = new ArrayList<ActServiceType>();

			for (int i = 0; i < actAccounts.size(); i++) {
				ActServiceType service = actAccounts.get(i);

				boolean newType = true;
				for (int j = 0; j < mCheckObjects.size(); j++) {
					if (mCheckObjects.get(j).getName().equalsIgnoreCase(service.getName())) {
						newType = false;
					}
				}

				if (newType) {
					mCheckObjects.add(service);
				}
			}

			adapter = new CheckClientListAdapter(activity, mCheckObjects);

			return "";
		}

		@Override
		protected void onPostExecute(final String result) {
			lv.setAdapter(adapter);
			progressDialog.dismiss();
		}
	}

	private class CheckClientListAdapter extends BaseAdapter implements Filterable {
		LayoutInflater mLayoutInflater;
		List<ActServiceType> arrayListObjects;
		List<ActServiceType> objects;

		CheckClientListAdapter(Context context, List<ActServiceType> list) {
			mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arrayListObjects = objects = list;
		}

		@Override
		public int getCount() {
			return arrayListObjects.size();
		}

		@Override
		public ActServiceType getItem(int i) {
			return arrayListObjects.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				view = mLayoutInflater.inflate(R.layout.object_list_item, parent, false);
			}

			((TextView) view.findViewById(R.id.name)).setText(arrayListObjects.get(position).getName());
			TextView addressView = (TextView) view.findViewById(R.id.address);
			addressView.setVisibility(View.GONE);
			return view;
		}

		@Override
		public Filter getFilter() {
			return new Filter() {
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					arrayListObjects = (List<ActServiceType>) results.values;
					notifyDataSetChanged();
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {

					FilterResults results = new FilterResults();
					ArrayList<ActServiceType> FilteredArrayNames = new ArrayList<ActServiceType>();

					String str = constraint.toString().toLowerCase();
					for (int i = 0; i < objects.size(); i++) {
						String dataNames = String.valueOf(objects.get(i).getName());
						if (dataNames.toLowerCase().contains(str)) {
							FilteredArrayNames.add(objects.get(i));
						}
					}

					results.count = FilteredArrayNames.size();
					results.values = FilteredArrayNames;
					return results;
				}
			};
		}
	}
}
