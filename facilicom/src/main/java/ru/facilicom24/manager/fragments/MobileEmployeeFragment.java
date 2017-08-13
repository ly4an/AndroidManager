package ru.facilicom24.manager.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.model.PartTimeWorkerEmployee;
import ru.facilicom24.manager.retrofit.RFService;

public class MobileEmployeeFragment
		extends BaseFragment
		implements
		AdapterView.OnItemClickListener {

	ListView listView;
	EditText searchField;
	EmpolyeeListAdapter adapter;
	List<PartTimeWorkerEmployee> emptyList;

	public MobileEmployeeFragment() {
		emptyList = new ArrayList<>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mobile_employee, container, false);

		searchField = (EditText) view.findViewById(R.id.searchField);
		searchField.addTextChangedListener(new TextWatcher() {
			String tailPattern = "";

			@Override
			public void onTextChanged(CharSequence sequence, int arg1, int arg2, int arg3) {
				String pattern = sequence.toString().toLowerCase();

				if (tailPattern.length() < pattern.length() && pattern.length() == 3) {
					hideKeyboard();

					final ProgressDialog dialog = new ProgressDialog(getActivity());

					dialog.setMessage(getString(R.string.mobile_job_load));
					dialog.setCancelable(false);
					dialog.setCanceledOnTouchOutside(false);

					dialog.show();

					RFService.partTimeWorkerEmployee(pattern, new Callback<List<PartTimeWorkerEmployee>>() {
						@Override
						public void onResponse(Call<List<PartTimeWorkerEmployee>> call, Response<List<PartTimeWorkerEmployee>> response) {
							dialog.dismiss();

							if (response != null) {
								List<PartTimeWorkerEmployee> partTimeWorkerEmployees = response.body();

								if (partTimeWorkerEmployees != null && !partTimeWorkerEmployees.isEmpty()) {
									adapter.setEmployees(partTimeWorkerEmployees);
								} else {
									adapter.setEmployees(emptyList);
									Toast.makeText(getActivity(), R.string.employee_no_data, Toast.LENGTH_LONG).show();
								}
							} else {
								adapter.setEmployees(emptyList);
								Toast.makeText(getActivity(), R.string.employee_no_data, Toast.LENGTH_LONG).show();
							}

							adapter.notifyDataSetChanged();
						}

						@Override
						public void onFailure(Call<List<PartTimeWorkerEmployee>> call, Throwable t) {
							dialog.dismiss();

							adapter.setEmployees(emptyList);
							Toast.makeText(getActivity(), R.string.employee_no_data, Toast.LENGTH_LONG).show();

							adapter.notifyDataSetChanged();
						}
					});
				} else if ((tailPattern.length() < pattern.length() && pattern.length() > 3)
						|| (tailPattern.length() > pattern.length() && pattern.length() >= 3)) {
					adapter.getFilter().filter(sequence);
				} else if (tailPattern.length() > pattern.length() && pattern.length() < 3) {
					adapter.setEmployees(emptyList);
					adapter.notifyDataSetChanged();
				}

				tailPattern = pattern;
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});

		listView = (ListView) view.findViewById(R.id.listView);
		listView.setOnItemClickListener(this);

		adapter = new EmpolyeeListAdapter(getActivity());
		adapter.setEmployees(emptyList);

		listView.setAdapter(adapter);

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		PartTimeWorkerEmployee employee = adapter.getItem(i);

		Intent intent = new Intent();

		intent.putExtra("EmployeeId", employee.getEmpId());
		intent.putExtra("EmployeeName", employee.getEmpFIO());

		getActivity().setResult(Activity.RESULT_OK, intent);

		getActivity().finish();
	}

	private class EmpolyeeListAdapter
			extends BaseAdapter
			implements Filterable {

		LayoutInflater inflater;

		List<PartTimeWorkerEmployee> employees;
		List<PartTimeWorkerEmployee> filteredEmployees;

		EmpolyeeListAdapter(Context context) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		void setEmployees(List<PartTimeWorkerEmployee> employees) {
			this.employees = employees;
			this.filteredEmployees = employees;
		}

		@Override
		public int getCount() {
			return filteredEmployees.size();
		}

		@Override
		public PartTimeWorkerEmployee getItem(int i) {
			return filteredEmployees.get(i);
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

			((TextView) view.findViewById(R.id.name)).setText(filteredEmployees.get(position).getEmpFIO());

			return view;
		}

		@Override
		public Filter getFilter() {
			return new Filter() {

				@Override
				protected FilterResults performFiltering(CharSequence sequence) {
					FilterResults results = new FilterResults();
					String pattern = sequence.toString().toLowerCase();
					ArrayList<PartTimeWorkerEmployee> list = new ArrayList<>();

					for (PartTimeWorkerEmployee employee : employees) {
						if (employee.getEmpFIO().toLowerCase().contains(pattern)) {
							list.add(employee);
						}
					}

					results.values = list;
					results.count = list.size();

					return results;
				}

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					filteredEmployees = (List<PartTimeWorkerEmployee>) results.values;
					notifyDataSetChanged();
				}
			};
		}
	}
}
