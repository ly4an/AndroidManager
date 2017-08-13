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

import database.Schedule;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoScheduleRepository;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;

public class ScheduleActivity
		extends FragmentActivity
		implements AdapterView.OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	ListView listView;
	EditText searchField;

	ScheduleListAdapter adapter;
	List<Schedule> schedules;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_schedule);

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);

		searchField = (EditText) findViewById(R.id.searchField);

		schedules = DaoScheduleRepository.getAll(this);
		adapter = new ScheduleListAdapter(this, schedules);
		listView.setAdapter(adapter);

		searchField.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				ScheduleActivity.this.adapter.getFilter().filter(cs);
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
		Schedule schedule = (Schedule) parent.getAdapter().getItem(position);

		Intent intent = new Intent();

		intent.putExtra("ScheduleId", schedule.getScheduleID());

		intent.putExtra("Code", schedule.getScheduleCode());
		intent.putExtra("Title", schedule.getPosition());
		intent.putExtra("Quantity", schedule.getQuantity());

		setResult(RESULT_OK, intent);

		finish();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class ScheduleListAdapter extends BaseAdapter implements Filterable {
		LayoutInflater inflater;

		List<Schedule> objects;
		List<Schedule> _objects;

		ScheduleListAdapter(Context context, List<Schedule> objects) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.objects = objects;
			_objects = objects;
		}

		@Override
		public int getCount() {
			return objects.size();
		}

		@Override
		public Schedule getItem(int i) {
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

			((TextView) view.findViewById(R.id.name)).setText(objects.get(position).getPosition());

			return view;
		}

		@Override
		public Filter getFilter() {
			return new Filter() {

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					ArrayList<Schedule> FilteredArrayNames = new ArrayList<Schedule>();

					String str = constraint.toString().toLowerCase();

					for (int i = 0; i < _objects.size(); i++) {
						String dataNames = String.valueOf(_objects.get(i).getPosition());

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
					objects = (List<Schedule>) results.values;
					notifyDataSetChanged();
				}
			};
		}
	}
}
