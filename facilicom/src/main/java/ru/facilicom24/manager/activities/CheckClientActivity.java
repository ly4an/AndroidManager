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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import database.Client;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoClientRepository;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;

public class CheckClientActivity
		extends FragmentActivity
		implements
		OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	final static public String SHOW_ALL = "SHOW_ALL";

	ListView listView;
	EditText searchField;
	List<Client> mCheckObjects;
	CheckClientListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.check_client_activity);

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);

		searchField = (EditText) findViewById(R.id.searchField);
		searchField.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				if (CheckClientActivity.this.adapter != null) {
					CheckClientActivity.this.adapter.getFilter().filter(cs);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});

		new LoadingTask(this).execute();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Client client = (Client) parent.getAdapter().getItem(position);

		setResult(RESULT_OK, new Intent()
				.putExtra("ClientId", client.getClientID())
				.putExtra("Title", client.getName())
		);

		finish();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class LoadingTask extends AsyncTask<Void, Void, String> {
		public CheckClientActivity activity;

		ProgressDialog progressDialog;

		LoadingTask(CheckClientActivity activity) {
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
			if (getIntent().getBooleanExtra(SHOW_ALL, false)) {
				mCheckObjects = DaoClientRepository.getAllClients(CheckClientActivity.this);
			} else {
				mCheckObjects = DaoClientRepository.getAllClients(CheckClientActivity.this, DaoClientRepository.ACTIVE_CLIENT_STATUS);
			}

			adapter = new CheckClientListAdapter(activity, mCheckObjects);

			return "";
		}

		@Override
		protected void onPostExecute(final String result) {
			progressDialog.dismiss();
			listView.setAdapter(adapter);
		}
	}

	private class CheckClientListAdapter extends BaseAdapter implements Filterable {

		List<Client> objects;
		List<Client> arrayListObjects;
		LayoutInflater mLayoutInflater;

		CheckClientListAdapter(Context context, List<Client> list) {
			mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arrayListObjects = objects = list;
		}

		@Override
		public int getCount() {
			return arrayListObjects.size();
		}

		@Override
		public Client getItem(int i) {
			return arrayListObjects.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {

			if (view == null) {
				view = mLayoutInflater.inflate(R.layout.client_list_item, parent, false);
			}

			Client client = arrayListObjects.get(position);

			((TextView) view.findViewById(R.id.name)).setText(client.getName());
			view.findViewById(R.id.closeSignTextView).setVisibility(client.getStatus() == 1 ? View.GONE : View.VISIBLE);

			return view;
		}

		@Override
		public Filter getFilter() {
			return new Filter() {

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					arrayListObjects = (List<Client>) results.values;
					notifyDataSetChanged();
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					ArrayList<Client> FilteredArrayNames = new ArrayList<Client>();

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
