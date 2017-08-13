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

import database.ActAccount;
import database.Client;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoActAccountRepository;
import ru.facilicom24.manager.cache.DaoClientRepository;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;

public class CheckActAccountActivity
		extends FragmentActivity
		implements
		OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	final static public String SHOW_ALL = "SHOW_ALL";

	ListView lv;
	EditText searchField;
	CheckClientListAdapter adapter;
	List<ActAccount> mCheckObjects;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.check_act_account_activity);

		lv = (ListView) findViewById(R.id.listView);
		lv.setOnItemClickListener(this);

		searchField = (EditText) findViewById(R.id.searchField);
		searchField.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				CheckActAccountActivity.this.adapter.getFilter().filter(cs);
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
		ActAccount account = (ActAccount) parent.getAdapter().getItem(position);

		setResult(RESULT_OK, new Intent()
				.putExtra("AccountId", account.getDirectumID())
				.putExtra("Title", account.getName())
				.putExtra("Address", account.getAddress())
		);

		finish();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class LoadingTask extends AsyncTask<Void, Void, String> {
		public CheckActAccountActivity activity;

		ProgressDialog progressDialog;

		LoadingTask(CheckActAccountActivity activity) {
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
			int clientId = activity.getIntent().getIntExtra("ClientId", 0);
			boolean showAll = getIntent().getBooleanExtra(SHOW_ALL, false);

			if (clientId != 0) {
				List<Client> clients = DaoClientRepository.getClientForClientId(CheckActAccountActivity.this, clientId);

				if (clients != null && clients.size() > 0) {
					if (showAll) {
						mCheckObjects = DaoActAccountRepository.getActAccountForClientId(CheckActAccountActivity.this, clients.get(0).getId());
					} else {
						mCheckObjects = DaoActAccountRepository.getActAccountForClientId(CheckActAccountActivity.this, clients.get(0).getId(), DaoActAccountRepository.ACTIVE_ACCOUNT_STATUS);
					}
				} else {
					mCheckObjects = new ArrayList<>();
				}
			} else {
				if (showAll) {
					mCheckObjects = DaoActAccountRepository.getAllActAccounts(CheckActAccountActivity.this);
				} else {
					mCheckObjects = DaoActAccountRepository.getAllActAccounts(CheckActAccountActivity.this, DaoActAccountRepository.ACTIVE_ACCOUNT_STATUS);
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

		List<ActAccount> objects;
		List<ActAccount> arrayListObjects;

		CheckClientListAdapter(Context context, List<ActAccount> list) {
			mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arrayListObjects = objects = list;
		}

		@Override
		public int getCount() {
			return arrayListObjects.size();
		}

		@Override
		public ActAccount getItem(int i) {
			return arrayListObjects.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {

			if (view == null) {
				view = mLayoutInflater.inflate(R.layout.account_list_item, parent, false);
			}

			ActAccount actAccount = arrayListObjects.get(position);

			((TextView) view.findViewById(R.id.name)).setText(actAccount.getName());
			((TextView) view.findViewById(R.id.address)).setText(actAccount.getAddress());
			view.findViewById(R.id.closeSignTextView).setVisibility(actAccount.getStatus() == 1 ? View.GONE : View.VISIBLE);

			return view;
		}

		@Override
		public Filter getFilter() {
			return new Filter() {
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					arrayListObjects = (List<ActAccount>) results.values;
					notifyDataSetChanged();
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					ArrayList<ActAccount> filteredArrayNames = new ArrayList<>();

					String pattern = constraint.toString().toLowerCase();

					for (ActAccount actAccount : objects) {
						String name = actAccount.getName();
						String address = actAccount.getAddress();

						name = name != null ? name.toLowerCase() : "";
						address = address != null ? address.toLowerCase() : "";

						if (name.contains(pattern) || address.contains(pattern)) {
							filteredArrayNames.add(actAccount);
						}
					}

					results.values = filteredArrayNames;
					results.count = filteredArrayNames.size();

					return results;
				}
			};
		}
	}
}
