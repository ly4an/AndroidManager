package ru.facilicom24.manager.activities;

import android.app.Activity;
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

public class RequestMenuActivity
		extends FragmentActivity
		implements
		View.OnClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	final static int ASSIGN = 0;
	final static int MARK = 1;
	final static int CONTROL = 2;
	final static int SEARCH = 3;
	final static int REQUEST_NEW = 4;

	MenuAdapter menuAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_request_menu);

		menuAdapter = new MenuAdapter();
		((ListView) findViewById(R.id.menu)).setAdapter(menuAdapter);
	}

	@Override
	public void onClick(View view) {
		switch ((int) view.getTag()) {
			case ASSIGN: {
				startActivityForResult(new Intent(this, RequestListActivity.class).putExtra(RequestListActivity.MODE_EXTRA, RequestListActivity.Mode.Execute), ASSIGN);
			}
			break;

			case MARK: {
				startActivityForResult(new Intent(this, RequestListActivity.class).putExtra(RequestListActivity.MODE_EXTRA, RequestListActivity.Mode.Mark), MARK);
			}
			break;

			case CONTROL: {
				startActivity(new Intent(this, RequestControlMenuActivity.class));
			}
			break;

			case SEARCH: {
				startActivity(new Intent(this, RequestListActivity.class).putExtra(RequestListActivity.MODE_EXTRA, RequestListActivity.Mode.All));
			}
			break;

			case REQUEST_NEW: {
				startActivity(new Intent(this, RequestNewActivity.class));
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && data != null) {
			switch ((RequestListActivity.Mode) data.getSerializableExtra(RequestListActivity.MODE_EXTRA)) {
				case Execute: {
					menuAdapter.menu.get(ASSIGN).name = TextUtils.concat(
							getString(R.string.request_menu_activity_1),
							menuAdapter.toString(RequestListActivity.load(RequestListActivity.Mode.Execute))
					).toString();

					menuAdapter.notifyDataSetChanged();
				}
				break;

				case Mark: {
					menuAdapter.menu.get(MARK).name = TextUtils.concat(
							getString(R.string.request_menu_activity_2),
							menuAdapter.toString(RequestListActivity.load(RequestListActivity.Mode.Mark))
					).toString();

					menuAdapter.notifyDataSetChanged();
				}
				break;
			}
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
		ArrayList<MenuItem> menu;

		MenuAdapter() {
			menu = new ArrayList<>();

			ArrayList<List<ServiceRequest>> serviceRequests = new ArrayList<>();

			serviceRequests.add(RequestListActivity.load(RequestListActivity.Mode.Execute));
			serviceRequests.add(RequestListActivity.load(RequestListActivity.Mode.Mark));

			menu.add(new MenuItem(TextUtils.concat(
					getString(R.string.request_menu_activity_1),
					toString(serviceRequests.get(0))
			).toString()));

			menu.add(new MenuItem(TextUtils.concat(
					getString(R.string.request_menu_activity_2),
					toString(serviceRequests.get(1))
			).toString()));

			menu.add(new MenuItem(getString(R.string.request_menu_activity_3)));
			menu.add(new MenuItem(getString(R.string.request_menu_activity_4)));
			menu.add(new MenuItem(getString(R.string.request_menu_activity_5)));
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

			view.setOnClickListener(RequestMenuActivity.this);

			return view;
		}
	}
}
