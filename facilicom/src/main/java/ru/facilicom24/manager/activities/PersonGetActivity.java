package ru.facilicom24.manager.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.model.PartTimeWorkerEmployee;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.network.FacilicomNetworkParser;

public class PersonGetActivity
		extends FragmentActivity
		implements
		AdapterView.OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	static public final String SUBDIVISION_UID = "SubdivisionUID";

	ListView listView;
	EditText searchField;

	PartTimeWorkerEmployeeListAdapter adapter;
	PartTimeWorkerEmployeeListAdapter emptyAdapter;

	String getSubdivisionUID() {
		return getIntent().getStringExtra(SUBDIVISION_UID);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_person_get);

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);

		searchField = (EditText) findViewById(R.id.searchField);

		if (getSubdivisionUID() == null) {
			emptyAdapter = new PartTimeWorkerEmployeeListAdapter(this, new ArrayList<PartTimeWorkerEmployee>());

			searchField.setHint(R.string.search_3);
			searchField.addTextChangedListener(new TextWatcher() {

				int charSequenceLength = -1;

				@Override
				public void onTextChanged(CharSequence charSequence, int arg1, int arg2, int arg3) {
					if (charSequenceLength < 3 && charSequence.length() >= 3 && listView.getAdapter() == emptyAdapter) {
						bind(charSequence);
					} else if (charSequenceLength >= 3 && charSequence.length() < 3 && listView.getAdapter() != emptyAdapter) {
						listView.setAdapter(emptyAdapter);
					} else if (listView.getAdapter() != emptyAdapter) {
						// ((PartTimeWorkerEmployeeListAdapter) listView.getAdapter()).getFilter().filter(charSequence);
						bind(charSequence);
					}

					charSequenceLength = charSequence.length();
				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				}

				@Override
				public void afterTextChanged(Editable arg0) {
				}

				void bind(CharSequence charSequence) {
					final ProgressDialog dialog = new ProgressDialog(PersonGetActivity.this);

					dialog.setMessage(getString(R.string.person_get_load));
					dialog.setCancelable(false);
					dialog.setCanceledOnTouchOutside(false);

					dialog.show();

					FacilicomNetworkClient.getEmployeeForBinding(charSequence.toString(), new AsyncHttpResponseHandler() {

						@Override
						public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
							dialog.dismiss();

							adapter = new PartTimeWorkerEmployeeListAdapter(PersonGetActivity.this, FacilicomNetworkParser.parseEmployee(responseBody));
							listView.setAdapter(adapter);
						}

						@Override
						public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
							dialog.dismiss();
							serverError();
						}
					});
				}
			});

			listView.setAdapter(emptyAdapter);
		} else {
			searchField.setHint(R.string.search);
			searchField.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					PersonGetActivity.this.adapter.getFilter().filter(s);
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});

			final ProgressDialog dialog = new ProgressDialog(this);

			dialog.setMessage(getString(R.string.person_get_load));
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

			dialog.show();

			FacilicomNetworkClient.getEmployeeForUnbinding(getSubdivisionUID(), new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					dialog.dismiss();

					adapter = new PartTimeWorkerEmployeeListAdapter(PersonGetActivity.this, FacilicomNetworkParser.parseEmployee(responseBody));
					listView.setAdapter(adapter);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					dialog.dismiss();
					serverError();
				}
			});
		}
	}

	void serverError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(PersonGetActivity.this);

		builder.setTitle(R.string.message_label);
		builder.setMessage(R.string.server_error);
		builder.setNegativeButton(R.string.next_button, null);

		builder.create().show();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		PartTimeWorkerEmployee partTimeWorkerEmployee = (PartTimeWorkerEmployee) parent.getAdapter().getItem(position);

		Intent intent = new Intent();

		intent.putExtra("UID", partTimeWorkerEmployee.getEmpId());
		intent.putExtra("Name", partTimeWorkerEmployee.getEmpFIO());

		setResult(RESULT_OK, intent);

		finish();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class PartTimeWorkerEmployeeListAdapter
			extends BaseAdapter
			implements Filterable {

		LayoutInflater inflater;

		List<PartTimeWorkerEmployee> itemsSource;
		List<PartTimeWorkerEmployee> filterableItemsSource;

		PartTimeWorkerEmployeeListAdapter(Context context, List<PartTimeWorkerEmployee> partTimeWorkerEmployees) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			itemsSource = partTimeWorkerEmployees;
			filterableItemsSource = partTimeWorkerEmployees;
		}

		@Override
		public int getCount() {
			return filterableItemsSource.size();
		}

		@Override
		public PartTimeWorkerEmployee getItem(int i) {
			return filterableItemsSource.get(i);
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

			((TextView) view.findViewById(R.id.name)).setText(filterableItemsSource.get(position).getEmpFIO());

			return view;
		}

		@Override
		public Filter getFilter() {
			return new Filter() {

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					ArrayList<PartTimeWorkerEmployee> FilteredArrayNames = new ArrayList<>();

					String pattern = constraint.toString().toLowerCase();

					for (int i = 0; i < itemsSource.size(); i++) {
						String value = String.valueOf(itemsSource.get(i).getEmpFIO()).toLowerCase();

						if (value.contains(pattern)) {
							FilteredArrayNames.add(itemsSource.get(i));
						}
					}

					FilterResults results = new FilterResults();

					results.values = FilteredArrayNames;
					results.count = FilteredArrayNames.size();

					return results;
				}

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					filterableItemsSource = (List<PartTimeWorkerEmployee>) results.values;
					notifyDataSetChanged();
				}
			};
		}
	}
}
