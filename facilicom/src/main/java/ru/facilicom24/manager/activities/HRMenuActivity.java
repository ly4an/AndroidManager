package ru.facilicom24.manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.views.FontButton;

public class HRMenuActivity
		extends BaseActivity
		implements View.OnClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	ArrayList<MenuItem> menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_hrmenu);

		menu = new ArrayList<>();

		menu.add(new MenuItem(R.string.hrmenuactivity_menu_1));
		menu.add(new MenuItem(R.string.hrmenuactivity_menu_2));
		menu.add(new MenuItem(R.string.hrmenuactivity_menu_3));
		menu.add(new MenuItem(R.string.hrmenuactivity_menu_4));
		menu.add(new MenuItem(R.string.hrmenuactivity_menu_5));
		menu.add(new MenuItem(R.string.hrmenuactivity_menu_6));
		menu.add(new MenuItem(R.string.request_menu_activity_6));

		((ListView) findViewById(R.id.menu)).setAdapter(new MenuAdapter());
	}

	@Override
	public void onClick(View view) {
		Intent intent = null;
		switch ((int) view.getTag()) {
			case 0: {
				intent = new Intent(this, MobileActivity.class);
			}
			break;

			case 1: {
				intent = new Intent(this, MobilePartTimeActivity.class);
			}
			break;

			case 2: {
				intent = new Intent(this, PersonActivity.class);
			}
			break;

			case 3: {
				intent = new Intent(this, AccessActivity.class);
			}
			break;

			case 4: {
				intent = new Intent(this, DismissActivity.class);
			}
			break;

			case 5: {
				intent = new Intent(this, PhoneActivity.class);
			}
			break;

			case 6: {
				intent = new Intent(this, ServiceRequestActivity.class);
			}
		}

		if (intent != null) {
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivityForResult(intent, (int) view.getTag());
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

		public MenuItem(int id) {
			this(id, true);
		}
	}

	private class MenuAdapter extends BaseAdapter {

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

			view.setOnClickListener(HRMenuActivity.this);

			return view;
		}
	}
}
