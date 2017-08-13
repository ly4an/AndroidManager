package ru.facilicom24.manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.views.FontButton;

public class TaskMenuActivity
		extends FragmentActivity
		implements
		View.OnClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	final static int RUNNING = 0;
	final static int CONTROL = 1;
	final static int MAIN = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_task_menu);

		((ListView) findViewById(R.id.menu)).setAdapter(new MenuAdapter());
	}

	@Override
	public void onClick(View v) {
		switch ((int) v.getTag()) {
			case RUNNING: {
				startActivity(new Intent(this, TaskListActivity.class).putExtra(TaskListActivity.TS_TASKS_MODE, TaskListActivity.Mode.Running));
			}
			break;

			case CONTROL: {
				startActivity(new Intent(this, TaskListActivity.class).putExtra(TaskListActivity.TS_TASKS_MODE, TaskListActivity.Mode.Control));
			}
			break;

			case MAIN: {
				startActivity(new Intent(this, TaskMainActivity.class));
			}
			break;
		}
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	class MenuItem {
		int id;
		boolean enabled;

		public MenuItem(int id, boolean enabled) {
			this.id = id;
			this.enabled = enabled;
		}
	}

	class MenuAdapter extends BaseAdapter {
		ArrayList<MenuItem> menu;

		MenuAdapter() {
			menu = new ArrayList<>();

			menu.add(new MenuItem(R.string.task_menu_1, true));
			menu.add(new MenuItem(R.string.task_menu_2, true));
			menu.add(new MenuItem(R.string.task_menu_3, true));
		}

		@Override
		public int getCount() {
			return menu.size();
		}

		@Override
		public MenuItem getItem(int i) {
			return menu.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.main_menu_item, viewGroup, false);
			}

			view.setTag(i);

			MenuItem item = getItem(i);

			((FontButton) view).setText(item.id);
			view.setEnabled(item.enabled);

			view.setOnClickListener(TaskMenuActivity.this);

			return view;
		}
	}
}
