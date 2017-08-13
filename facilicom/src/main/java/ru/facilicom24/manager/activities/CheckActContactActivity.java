package ru.facilicom24.manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
import database.ActContact;
import database.Client;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoActAccountRepository;
import ru.facilicom24.manager.cache.DaoActContactRepository;
import ru.facilicom24.manager.cache.DaoClientRepository;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;

public class CheckActContactActivity
		extends FragmentActivity
		implements
		OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.check_act_contact_activity);

		//

		List<ActContact> actContacts = new ArrayList<>();

		int clientId = getIntent().getIntExtra("ClientId", 0);
		int accountId = getIntent().getIntExtra("AccountId", 0);

		if (clientId != 0) {
			List<Client> clients = DaoClientRepository.getClientForClientId(this, clientId);

			if (clients != null && !clients.isEmpty()) {
				Client client = clients.get(0);
				actContacts = DaoActContactRepository.getActContactForClientId(this, client.getId());
			}
		}

		if (accountId != 0) {
			List<ActAccount> accounts = DaoActAccountRepository.getActAccountForDirectumId(this, accountId);

			if (accounts != null && !accounts.isEmpty()) {
				long actId = accounts.get(0).getId();
				actContacts = DaoActContactRepository.getActContactForAccountId(this, actId);
			}
		}

		final CheckClientListAdapter adapter = new CheckClientListAdapter(actContacts);

		//

		((EditText) findViewById(R.id.searchField)).addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				adapter.getFilter().filter(cs);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});

		//

		ListView listView = (ListView) findViewById(R.id.listView);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ActContact contact = (ActContact) parent.getAdapter().getItem(position);

		setResult(RESULT_OK, new Intent()
				.putExtra("ContactId", contact.getActContactID())
				.putExtra("Title", contact.getName())
		);

		finish();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class CheckClientListAdapter
			extends BaseAdapter
			implements Filterable {

		List<ActContact> objects;
		List<ActContact> arrayListObjects;

		CheckClientListAdapter(List<ActContact> list) {
			arrayListObjects = objects = list;
		}

		@Override
		public int getCount() {
			return arrayListObjects.size();
		}

		@Override
		public ActContact getItem(int i) {
			return arrayListObjects.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.object_list_item, parent, false);
			}

			((TextView) view.findViewById(R.id.name)).setText(arrayListObjects.get(position).getName());
			view.findViewById(R.id.address).setVisibility(View.GONE);

			return view;
		}

		@Override
		public Filter getFilter() {
			return new Filter() {

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					ArrayList<ActContact> names = new ArrayList<>();
					String pattern = constraint.toString().trim().toLowerCase();

					for (int i = 0; i < objects.size(); i++) {
						if (String.valueOf(objects.get(i).getName()).toLowerCase().contains(pattern)) {
							names.add(objects.get(i));
						}
					}

					FilterResults results = new FilterResults();

					results.count = names.size();
					results.values = names;

					return results;
				}

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					arrayListObjects = (List<ActContact>) results.values;
					notifyDataSetChanged();
				}
			};
		}
	}
}
