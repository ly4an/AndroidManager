package ru.facilicom24.manager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.facilicom24.manager.R;
import ru.facilicom24.manager.activities.MobileActivity;
import ru.facilicom24.manager.activities.ScheduleActivity;
import ru.facilicom24.manager.activities.SexActivity;
import ru.facilicom24.manager.dialogs.DatePickerDialogFragment;
import ru.facilicom24.manager.dialogs.TimePickerDialogFragment;
import ru.facilicom24.manager.model.ScheduleItem;

import static ru.facilicom24.manager.fragments.MobileFragment2.InteractionItemType.Choose;
import static ru.facilicom24.manager.fragments.MobileFragment2.InteractionItemType.Number;

public class MobileFragment2
		extends BaseFragment
		implements
		View.OnClickListener,
		TimePickerDialogFragment.ITimePickerDialogListener {

	static final int CHOOSE_SEX = 2010;
	static final int CHOOSE_SCHEDULE = 2020;

	int selectedIndex;

	ListView mobileMenu;
	List<InteractionItem> menu;
	InteractionAdapter mobileMenuAdapter;
	Calendar requestDate;
	Calendar fromTime;
	Calendar toTime;
	JSONObject parameters;

	public MobileFragment2() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Date currentDate = new Date();

		requestDate = Calendar.getInstance();
		requestDate.setTime(currentDate);

		requestDate.set(Calendar.HOUR_OF_DAY, 0);
		requestDate.set(Calendar.MINUTE, 0);
		requestDate.set(Calendar.SECOND, 0);
		requestDate.set(Calendar.MILLISECOND, 0);

		fromTime = Calendar.getInstance();
		fromTime.setTime(currentDate);

		fromTime.set(Calendar.HOUR_OF_DAY, 10);
		fromTime.set(Calendar.MINUTE, 0);
		fromTime.set(Calendar.SECOND, 0);
		fromTime.set(Calendar.MILLISECOND, 0);

		toTime = Calendar.getInstance();
		toTime.setTime(currentDate);

		toTime.set(Calendar.HOUR_OF_DAY, 10);
		toTime.set(Calendar.MINUTE, 0);
		toTime.set(Calendar.SECOND, 0);
		toTime.set(Calendar.MILLISECOND, 0);

		menu = new ArrayList<>();

		menu.add(new InteractionItem(Choose, "Дата", MobileActivity.dateToString(requestDate.getTime())));
		menu.add(new InteractionItem(Choose, "Должность", "Выбрать"));
		menu.add(new InteractionItem(Choose, "Пол", "Выбрать"));
		menu.add(new InteractionItem(Choose, "Время начала", MobileActivity.timeToString(fromTime.getTime())));
		menu.add(new InteractionItem(Choose, "Время окончания", MobileActivity.timeToString(toTime.getTime())));
		menu.add(new InteractionItem(Number, "Количество", "1"));

		View view = inflater.inflate(R.layout.fragment_mobile_2, container, false);

		mobileMenu = (ListView) view.findViewById(R.id.mobile_menu);
		mobileMenuAdapter = new InteractionAdapter();
		mobileMenu.setAdapter(mobileMenuAdapter);

		parameters = new JSONObject();

		try {
			parameters.put("RequestDate", "");

			parameters.put("ScheduleId", "");
			parameters.put("ScheduleCode", "");
			parameters.put("ScheduleName", "");
			parameters.put("MaxQuantity", 0);

			parameters.put("SexId", 0);
			parameters.put("FromTime", "");
			parameters.put("ToTime", "");
			parameters.put("Quantity", 0);
		} catch (JSONException exception) {
			exception.printStackTrace();
		}

		view.findViewById(R.id.next).setOnClickListener(this);

		mobileMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				switch (i) {
					case 0:
						showDialog(DatePickerDialogFragment.newInstance(requestDate), DATE_PICKER_DIALOG);
						break;

					case 1: {
						Intent intent = new Intent(getActivity(), ScheduleActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

						String scheduleId = parameters.optString("ScheduleId");

						if (scheduleId.length() > 0) {
							intent.putExtra("ScheduleId", scheduleId);
						}

						startActivityForResult(intent, CHOOSE_SCHEDULE);
					}
					break;

					case 2: {
						Intent intent = new Intent(getActivity(), SexActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

						int sexId = parameters.optInt("SexId", 0);

						if (sexId != 0) {
							intent.putExtra("SexId", sexId);
						}

						startActivityForResult(intent, CHOOSE_SEX);
					}
					break;

					case 3: {
						selectedIndex = i;
						TimePickerDialogFragment dialog = TimePickerDialogFragment.newInstance(fromTime, (MobileFragment2) getActivity().getSupportFragmentManager().findFragmentByTag(MobileActivity.MOBILE_FRAGMENT_2_TAG));
						dialog.show(getActivity().getSupportFragmentManager(), MobileActivity.MOBILE_FRAGMENT_2_FROM_TIME_TAG);
					}
					break;

					case 4: {
						selectedIndex = i;
						TimePickerDialogFragment dialog = TimePickerDialogFragment.newInstance(toTime, (MobileFragment2) getActivity().getSupportFragmentManager().findFragmentByTag(MobileActivity.MOBILE_FRAGMENT_2_TAG));
						dialog.show(getActivity().getSupportFragmentManager(), MobileActivity.MOBILE_FRAGMENT_2_TO_TIME_TAG);
					}
					break;
				}
			}
		});

		return view;
	}

	@Override
	public void onClick(View v) {
		hideKeyboard();

		EditText edit_quantity = (EditText) mobileMenu.findViewById(R.id.edit_quantity);

		int quantity = 0;

		try {
			quantity = Integer.parseInt(edit_quantity.getText().toString());
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		menu.get(5).setOption(Integer.toString(quantity));

		String fromDate = "";
		String toDate = "";

		try {
			fromDate = MobileActivity.parameters.getString("FromDate");
			toDate = MobileActivity.parameters.getString("ToDate");
		} catch (JSONException exception) {
			exception.printStackTrace();
		}

		if (fromDate.length() > 0 && toDate.length() > 0) {
			Calendar _fromDate = MobileActivity.stringToDate(fromDate);
			Calendar _toDate = MobileActivity.stringToDate(toDate);

			if (requestDate.before(_fromDate) || requestDate.after(_toDate)) {
				showAlertDialog(R.id.alert_dialog, R.string.mobile_alert_title, R.string.mobile_alert_date);
				return;
			}
		}

		String scheduleId = "";
		String scheduleCode = "";
		String scheduleName = "";

		int maxQuantity = 0;

		try {
			scheduleId = parameters.getString("ScheduleId");
			scheduleCode = parameters.getString("ScheduleCode");
			scheduleName = parameters.getString("ScheduleName");

			maxQuantity = parameters.getInt("MaxQuantity");
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		if (scheduleId.length() == 0) {
			showAlertDialog(R.id.alert_dialog, R.string.mobile_alert_title, R.string.mobile_alert_schedule);
			return;
		}

		int sexId = 0;

		try {
			sexId = parameters.getInt("SexId");
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		if (sexId == 0) {
			showAlertDialog(R.id.alert_dialog, R.string.mobile_alert_title, R.string.mobile_alert_sex);
			return;
		}

		if (fromTime.after(toTime) || fromTime.equals(toTime)) {
			showAlertDialog(R.id.alert_dialog, R.string.mobile_alert_title, R.string.mobile_alert_time);
			return;
		}

		if (quantity == 0 || quantity > maxQuantity) {
			showAlertDialog(R.id.alert_dialog, getString(R.string.mobile_alert_title), String.format(getString(R.string.mobile_alert_quantity), String.valueOf(maxQuantity)));
			return;
		}

		String _requestDate = MobileActivity.dateToString(requestDate.getTime());

		String _fromTime = MobileActivity.timeToString(fromTime.getTime());
		String _toTime = MobileActivity.timeToString(toTime.getTime());

		try {
			parameters.put("RequestDate", _requestDate);

			parameters.put("FromTime", _fromTime);
			parameters.put("ToTime", _toTime);
		} catch (JSONException exception) {
			exception.printStackTrace();
		}

		ArrayList<ScheduleItem> schedules = new ArrayList<>();

		try {
			schedules = (ArrayList<ScheduleItem>) MobileActivity.parameters.get("Schedules");
		} catch (JSONException exception) {
			exception.printStackTrace();
		}

		schedules.add(new ScheduleItem(_requestDate, scheduleId, scheduleCode, scheduleName, sexId, _fromTime, _toTime, quantity, maxQuantity));

		getActivity().getSupportFragmentManager().beginTransaction().remove(this).add(R.id.content, new MobileFragment3(), MobileActivity.MOBILE_FRAGMENT_3_TAG).commit();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
				case CHOOSE_SEX: {
					menu.get(2).setOption(data.getStringExtra("Title"));
					mobileMenuAdapter.notifyDataSetChanged();

					try {
						parameters.put("SexId", data.getIntExtra("SexId", 0));
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
				break;

				case CHOOSE_SCHEDULE: {
					menu.get(1).setOption(data.getStringExtra("Title"));
					mobileMenuAdapter.notifyDataSetChanged();

					try {
						parameters.put("ScheduleId", data.getStringExtra("ScheduleId"));

						parameters.put("ScheduleCode", data.getStringExtra("Code"));
						parameters.put("ScheduleName", data.getStringExtra("Title"));

						parameters.put("MaxQuantity", data.getIntExtra("Quantity", 0));
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
				break;
			}
		}
	}

	@Override
	public void onDateSet(Calendar calendar) {
		requestDate = calendar;

		menu.get(0).setOption(MobileActivity.dateToString(calendar.getTime()));
		mobileMenuAdapter.notifyDataSetChanged();
	}

	@Override
	public void onTimeSet(Calendar calendar) {
		switch (selectedIndex) {
			case 3:
				fromTime = calendar;
				break;

			case 4:
				toTime = calendar;
				break;
		}

		menu.get(selectedIndex).setOption(MobileActivity.timeToString(calendar.getTime()));
		mobileMenuAdapter.notifyDataSetChanged();
	}

	enum InteractionItemType {
		Choose,
		Number
	}

	class InteractionItem {

		InteractionItemType type;

		String title;
		String option;

		InteractionItem(InteractionItemType type, String title, String option) {
			this.type = type;

			this.title = title;
			this.option = option;
		}

		void setOption(String option) {
			this.option = option;
		}
	}

	class InteractionAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return menu.size();
		}

		@Override
		public InteractionItem getItem(int i) {
			return menu.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			InteractionItem menuItem = getItem(i);

			switch (menuItem.type) {
				case Choose: {
					view = getActivity().getLayoutInflater().inflate(R.layout.interaction_item, viewGroup, false);

					TextView title = (TextView) view.findViewById(R.id.interaction_title);
					TextView option = (TextView) view.findViewById(R.id.interaction_option);

					title.setText(menuItem.title);
					option.setText(menuItem.option);
				}
				break;

				case Number: {
					view = getActivity().getLayoutInflater().inflate(R.layout.interaction_number_item, viewGroup, false);

					TextView title = (TextView) view.findViewById(R.id.interaction_title);
					final EditText editText = (EditText) view.findViewById(R.id.edit_quantity);

					title.setText(menuItem.title);
					editText.setText(menuItem.option);

					editText.setOnKeyListener(new EditText.OnKeyListener() {
						public boolean onKey(View v, int keyCode, KeyEvent event) {
							if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
								menu.get(5).setOption(editText.getText().toString());
								return true;
							}

							return false;
						}
					});
				}
				break;
			}

			return view;
		}
	}
}
