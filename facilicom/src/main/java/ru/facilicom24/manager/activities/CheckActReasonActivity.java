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

import database.ActReason;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoActReasonRepository;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;

public class CheckActReasonActivity
		extends FragmentActivity
		implements
		OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	ListView lv;
	List<ActReason> mCheckObjects;
	CheckClientListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.check_act_reason_activity);

		lv = (ListView) findViewById(R.id.listView);
		lv.setOnItemClickListener(this);

		new LoadingTask(this).execute();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ActReason reason = (ActReason) parent.getAdapter().getItem(position);
		int reasonID = reason.getActReasonID();

		Intent intent = new Intent();

		intent.putExtra("ReasonId", reasonID);
		intent.putExtra("Title", reason.getName());

		setResult(RESULT_OK, intent);

		finish();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class LoadingTask extends AsyncTask<Void, Void, String> {
		public CheckActReasonActivity activity;

		ProgressDialog progressDialog;

		LoadingTask(CheckActReasonActivity activity) {
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
			mCheckObjects = DaoActReasonRepository.getAllActReasons(CheckActReasonActivity.this);
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

		List<ActReason> objects;
		List<ActReason> arrayListObjects;

		CheckClientListAdapter(Context context, List<ActReason> list) {
			mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arrayListObjects = objects = list;
		}

		@Override
		public int getCount() {
			return arrayListObjects.size();
		}

		@Override
		public ActReason getItem(int i) {
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
					arrayListObjects = (List<ActReason>) results.values;
					notifyDataSetChanged();
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					ArrayList<ActReason> FilteredArrayNames = new ArrayList<ActReason>();

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
