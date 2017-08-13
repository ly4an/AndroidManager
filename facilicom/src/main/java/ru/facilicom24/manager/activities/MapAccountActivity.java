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

import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.model.MapAccount;

public class MapAccountActivity
		extends FragmentActivity
		implements
		OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	ListView listView;
	EditText searchField;
	AccountListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.map_act_account_activity);

		searchField = (EditText) findViewById(R.id.searchField);
		searchField.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence sequence, int arg1, int arg2, int arg3) {
				if (adapter != null) {
					adapter.getFilter().filter(sequence);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);

		(new LoadingTask(this)).execute();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MapAccount account = (MapAccount) parent.getAdapter().getItem(position);

		Intent intent = new Intent();

		intent.putExtra("Id", account.getId());
		intent.putExtra("Name", account.getName());
		intent.putExtra("Distance", account.getDistance());

		setResult(RESULT_OK, intent);

		finish();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class LoadingTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog dialog;
		MapAccountActivity activity;

		LoadingTask(MapAccountActivity activity) {
			this.activity = activity;
		}

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(activity);

			dialog.setMessage(getString(R.string.map_load));
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... value) {
			adapter = new AccountListAdapter(activity, FacilicomApplication.getInstance().getMapAccounts());
			return null;
		}

		@Override
		protected void onPostExecute(final Void result) {
			listView.setAdapter(adapter);
			dialog.dismiss();
		}
	}

	private class AccountListAdapter
			extends BaseAdapter
			implements Filterable {

		LayoutInflater inflater;

		List<MapAccount> accounts;
		List<MapAccount> filteredAccounts;

		AccountListAdapter(Context context, List<MapAccount> accounts) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			this.accounts = accounts;
			filteredAccounts = accounts;
		}

		@Override
		public int getCount() {
			return filteredAccounts.size();
		}

		@Override
		public MapAccount getItem(int i) {
			return filteredAccounts.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				view = inflater.inflate(R.layout.object_list_item, parent, false);
			}

			MapAccount account = filteredAccounts.get(position);

			int distance = (int) (account.getDistance() * 20) * 50;

			if (distance > 0) {
				((TextView) view.findViewById(R.id.name)).setText(String.format(getString(R.string.activity_map_account_format), account.getName(), distance));
			} else {
				((TextView) view.findViewById(R.id.name)).setText(account.getName());
			}

			TextView addressView = (TextView) view.findViewById(R.id.address);

			addressView.setVisibility(View.VISIBLE);
			addressView.setText(account.getAddress());

			return view;
		}

		@Override
		public Filter getFilter() {
			return new Filter() {

				@Override
				protected FilterResults performFiltering(CharSequence sequence) {
					FilterResults results = new FilterResults();
					ArrayList<MapAccount> list = new ArrayList<>();
					String pattern = sequence.toString().toLowerCase();

					for (int i = 0; i < accounts.size(); i++) {
						MapAccount account = accounts.get(i);

						if (account.getName().toLowerCase().contains(pattern) || account.getAddress().toLowerCase().contains(pattern)) {
							list.add(accounts.get(i));
						}
					}

					results.values = list;
					results.count = list.size();

					return results;
				}

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					filteredAccounts = (List<MapAccount>) results.values;
					notifyDataSetChanged();
				}
			};
		}
	}
}
