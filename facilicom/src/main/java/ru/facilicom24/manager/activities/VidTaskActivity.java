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

import database.VidTask;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoVidTaskRepository;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;

public class VidTaskActivity
		extends FragmentActivity
		implements
		AdapterView.OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_vidtask);

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);

		ArrayList<VidTaskItem> objects = new ArrayList<>();
		for (VidTask task : DaoVidTaskRepository.getAllVidTask(this)) {
			objects.add(new VidTaskItem(task.getVidTaskID(), task.getVidTaskName()));
		}

		TaskKindListAdapter adapter = new TaskKindListAdapter(this, objects);
		listView.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		VidTaskItem kind = (VidTaskItem) parent.getAdapter().getItem(position);

		Intent intent = new Intent();

		intent.putExtra("Kind", kind.id);
		intent.putExtra("Name", kind.name);

		setResult(RESULT_OK, intent);

		finish();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class VidTaskItem {
		int id;
		String name;

		VidTaskItem(int id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	private class TaskKindListAdapter extends BaseAdapter {
		LayoutInflater inflater;

		List<VidTaskItem> objects;

		TaskKindListAdapter(Context context, List<VidTaskItem> objects) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.objects = objects;
		}

		@Override
		public int getCount() {
			return objects.size();
		}

		@Override
		public VidTaskItem getItem(int i) {
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
