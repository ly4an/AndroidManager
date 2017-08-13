package ru.facilicom24.manager.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;

public class TaskTypeActivity
		extends FragmentActivity
		implements
		AdapterView.OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_tasktype);

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);

		ArrayList<TaskTypeItem> objects = new ArrayList<>();

		objects.add(new TaskTypeItem(2, getString(R.string.task_type_operational)));
		objects.add(new TaskTypeItem(1, getString(R.string.task_type_strategy)));

		TaskTypeListAdapter adapter = new TaskTypeListAdapter(this, objects);
		listView.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TaskTypeItem type = (TaskTypeItem) parent.getAdapter().getItem(position);

		Intent intent = new Intent();

		intent.putExtra("Type", type.id);
		intent.putExtra("Name", type.name);

		setResult(RESULT_OK, intent);

		finish();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class TaskTypeItem {
		int id;
		String name;

		TaskTypeItem(int id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	private class TaskTypeListAdapter extends BaseAdapter {
		LayoutInflater inflater;

		List<TaskTypeItem> objects;

		TaskTypeListAdapter(Context context, List<TaskTypeItem> objects) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.objects = objects;
		}

		@Override
		public int getCount() {
			return objects.size();
		}

		@Override
		public TaskTypeItem getItem(int i) {
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

			((TextView) view.findViewById(R.id.name)).setText(objects.get(position).name);

			return view;
		}
	}
}
