package ru.facilicom24.manager.activities;

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

import java.util.ArrayList;
import java.util.List;

import database.TaskEmployee;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoTaskEmployeeRepository;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;

public class EmployeeActivity
		extends FragmentActivity
		implements AdapterView.OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	ListView listView;
	EditText searchField;

	EmployeeListAdapter adapter;
	List<TaskEmployee> Employees;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_employee);

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);

		searchField = (EditText) findViewById(R.id.searchField);

		Employees = DaoTaskEmployeeRepository.getAllTaskEmployee(this);
		adapter = new EmployeeListAdapter(this, Employees);
		listView.setAdapter(adapter);

		searchField.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				EmployeeActivity.this.adapter.getFilter().filter(cs);
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
		TaskEmployee employee = (TaskEmployee) parent.getAdapter().getItem(position);

		Intent intent = new Intent();

		intent.putExtra("Employee", employee.getTaskEmployeeLogin());
		intent.putExtra("Name", employee.getTaskEmployeeName());

		setResult(RESULT_OK, intent);

		finish();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class EmployeeListAdapter extends BaseAdapter implements Filterable {
		LayoutInflater inflater;

		List<TaskEmployee> objects;
		List<TaskEmployee> _objects;

		EmployeeListAdapter(Context context, List<TaskEmployee> objects) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.objects = objects;
			_objects = objects;
		}

		@Override
		public int getCount() {
			return objects.size();
		}

		@Override
		public TaskEmployee getItem(int i) {
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

			((TextView) view.findViewById(R.id.name)).setText(objects.get(position).getTaskEmployeeName());

			return view;
		}

		@Override
		public Filter getFilter() {
			return new Filter() {

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					ArrayList<TaskEmployee> FilteredArrayNames = new ArrayList<>();

					String str = constraint.toString().toLowerCase();

					for (int i = 0; i < _objects.size(); i++) {
						String name = String.valueOf(_objects.get(i).getTaskEmployeeName());

						if (name.toLowerCase().contains(str)) {
							FilteredArrayNames.add(_objects.get(i));
						}
					}

					results.count = FilteredArrayNames.size();
					results.values = FilteredArrayNames;

					return results;
				}

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					objects = (List<TaskEmployee>) results.values;
					notifyDataSetChanged();
				}
			};
		}
	}
}
