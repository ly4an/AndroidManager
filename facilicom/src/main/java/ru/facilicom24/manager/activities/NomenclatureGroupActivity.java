package ru.facilicom24.manager.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import database.NomenclatureGroup;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoNomenclatureGroupRepository;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;

public class NomenclatureGroupActivity
		extends FragmentActivity
		implements
		AdapterView.OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	ListView listView;
	EditText searchField;

	List<NomenclatureGroup> objects;
	NomenclatureGroupListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_nomenclature_group);

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);

		searchField = (EditText) findViewById(R.id.searchField);

		LoadingTask task = new LoadingTask(this);
		task.execute();

		searchField.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				NomenclatureGroupActivity.this.adapter.getFilter().filter(cs);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		NomenclatureGroup nomenclatureGroup = (NomenclatureGroup) parent.getAdapter().getItem(position);

		Intent intent = new Intent();

		intent.putExtra("NomenclatureGroupCode", nomenclatureGroup.getCode());
		intent.putExtra("Title", nomenclatureGroup.getName());

		setResult(RESULT_OK, intent);

		finish();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class NomenclatureGroupListAdapter extends BaseAdapter implements Filterable {

		LayoutInflater inflater;

		List<NomenclatureGroup> objects;
		List<NomenclatureGroup> _objects;

		NomenclatureGroupListAdapter(Context context, List<NomenclatureGroup> objects) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.objects = objects;
			_objects = objects;
		}

		@Override
		public int getCount() {
			return objects.size();
		}

		@Override
		public NomenclatureGroup getItem(int i) {
			return objects.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				view = inflater.inflate(R.layout.list_item, parent, false);
			}

			((TextView) view.findViewById(R.id.name)).setText(objects.get(position).getName());

			return view;
		}

		@Override
		public Filter getFilter() {
			return new Filter() {

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					ArrayList<NomenclatureGroup> FilteredArrayNames = new ArrayList<NomenclatureGroup>();

					String str = constraint.toString().toLowerCase();

					for (int i = 0; i < _objects.size(); i++) {
						String dataNames = String.valueOf(_objects.get(i).getName());

						if (dataNames.toLowerCase().contains(str)) {
							FilteredArrayNames.add(_objects.get(i));
						}
					}

					results.count = FilteredArrayNames.size();
					results.values = FilteredArrayNames;

					return results;
				}

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					objects = (List<NomenclatureGroup>) results.values;
					notifyDataSetChanged();
				}
			};
		}
	}

	private class LoadingTask extends AsyncTask<Void, Void, String> {

		ProgressDialog dialog;
		NomenclatureGroupActivity activity;

		LoadingTask(NomenclatureGroupActivity activity) {
			this.activity = activity;
		}

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(activity);

			dialog.setMessage(getString(R.string.nomenclature_group_load));
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

			dialog.show();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			objects = DaoNomenclatureGroupRepository.getAllNomenclatureGroups(NomenclatureGroupActivity.this);
			adapter = new NomenclatureGroupListAdapter(activity, objects);

			return "";
		}

		@Override
		protected void onPostExecute(final String result) {
			listView.setAdapter(adapter);
			dialog.dismiss();
		}
	}
}
