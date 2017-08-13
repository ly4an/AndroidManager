package ru.facilicom24.manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import database.ServiceRequest;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.views.FontButton;

public class RequestControlMenuActivity
		extends FragmentActivity
		implements
		View.OnClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	final static int MINE = 0;
	final static int ASSIGNED = 1;
	final static int INPROGRESS = 2;
	final static int POSTPONED = 3;
	final static int SERVICED = 4;
	final static int CANCELED = 5;
	final static int REWORK = 6;
	final static int DEADLINEOUT = 7;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_request_control_menu);

		((ListView) findViewById(R.id.menuListView)).setAdapter(new MenuAdapter());
	}

	@Override
	public void onClick(View view) {
		switch ((int) view.getTag()) {
			case MINE: {
				startActivity(new Intent(this, RequestListActivity.class).putExtra(RequestListActivity.MODE_EXTRA, RequestListActivity.Mode.Mine));
			}
			break;

			case ASSIGNED: {
				startActivity(new Intent(this, RequestListActivity.class).putExtra(RequestListActivity.MODE_EXTRA, RequestListActivity.Mode.Assigned));
			}
			break;

			case INPROGRESS: {
				startActivity(new Intent(this, RequestListActivity.class).putExtra(RequestListActivity.MODE_EXTRA, RequestListActivity.Mode.InProgress));
			}
			break;

			case POSTPONED: {
				startActivity(new Intent(this, RequestListActivity.class).putExtra(RequestListActivity.MODE_EXTRA, RequestListActivity.Mode.Postponed));
			}
			break;

			case SERVICED: {
				startActivity(new Intent(this, RequestListActivity.class).putExtra(RequestListActivity.MODE_EXTRA, RequestListActivity.Mode.Serviced));
			}
			break;

			case CANCELED: {
				startActivity(new Intent(this, RequestListActivity.class).putExtra(RequestListActivity.MODE_EXTRA, RequestListActivity.Mode.Canceled));
			}
			break;

			case REWORK: {
				startActivity(new Intent(this, RequestListActivity.class).putExtra(RequestListActivity.MODE_EXTRA, RequestListActivity.Mode.Rework));
			}
			break;

			case DEADLINEOUT: {
				startActivity(new Intent(this, RequestListActivity.class).putExtra(RequestListActivity.MODE_EXTRA, RequestListActivity.Mode.DeadLineOut));
			}
			break;
		}
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	class MenuItem {
		String name;
		boolean enabled;

		public MenuItem(String name, boolean enabled) {
			this.name = name;
			this.enabled = enabled;
		}

		public MenuItem(String name) {
			this(name, true);
		}
	}

	private class MenuAdapter extends BaseAdapter {
		List<MenuItem> menu;

		MenuAdapter() {
			menu = new ArrayList<>();

			ArrayList<List<ServiceRequest>> serviceRequests = new ArrayList<>();

			serviceRequests.add(RequestListActivity.load(RequestListActivity.Mode.Mine));
			serviceRequests.add(RequestListActivity.load(RequestListActivity.Mode.Assigned));
			serviceRequests.add(RequestListActivity.load(RequestListActivity.Mode.InProgress));
			serviceRequests.add(RequestListActivity.load(RequestListActivity.Mode.Postponed));
			serviceRequests.add(RequestListActivity.load(RequestListActivity.Mode.Serviced));
			serviceRequests.add(RequestListActivity.load(RequestListActivity.Mode.Canceled));
			serviceRequests.add(RequestListActivity.load(RequestListActivity.Mode.Rework));
			serviceRequests.add(RequestListActivity.load(RequestListActivity.Mode.DeadLineOut));

			menu.add(new MenuItem(TextUtils.concat(
					getString(R.string.request_control_menu_1),
					toString(serviceRequests.get(0))
			).toString()));

			menu.add(new MenuItem(TextUtils.concat(
					getString(R.string.request_control_menu_2),
					toString(serviceRequests.get(1))
			).toString()));

			menu.add(new MenuItem(TextUtils.concat(
					getString(R.string.request_control_menu_3),
					toString(serviceRequests.get(2))
			).toString()));

			menu.add(new MenuItem(TextUtils.concat(
					getString(R.string.request_control_menu_4),
					toString(serviceRequests.get(3))
			).toString()));

			menu.add(new MenuItem(TextUtils.concat(
					getString(R.string.request_control_menu_5),
					toString(serviceRequests.get(4))
			).toString()));

			menu.add(new MenuItem(TextUtils.concat(
					getString(R.string.request_control_menu_6),
					toString(serviceRequests.get(5))
			).toString()));

			menu.add(new MenuItem(TextUtils.concat(
					getString(R.string.request_control_menu_7),
					toString(serviceRequests.get(6))
			).toString()));

			menu.add(new MenuItem(TextUtils.concat(
					getString(R.string.request_control_menu_8),
					toString(serviceRequests.get(7))
			).toString()));
		}

		String toString(List<ServiceRequest> serviceRequests) {
			return serviceRequests != null && serviceRequests.size() > 0 ? TextUtils.concat(" (", String.valueOf(serviceRequests.size()), ")").toString() : "";
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

			((FontButton) view).setText(item.name);
			view.setEnabled(item.enabled);

			view.setOnClickListener(RequestControlMenuActivity.this);

			return view;
		}
	}
}
