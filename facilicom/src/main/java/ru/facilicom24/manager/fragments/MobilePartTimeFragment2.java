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

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ru.facilicom24.manager.R;
import ru.facilicom24.manager.activities.MobileActivity;
import ru.facilicom24.manager.activities.MobileEmployeeActivity;
import ru.facilicom24.manager.activities.MobilePartTimeActivity;
import ru.facilicom24.manager.activities.ScheduleActivity;
import ru.facilicom24.manager.dialogs.DatePickerDialogFragment;
import ru.facilicom24.manager.dialogs.TimePickerDialogFragment;
import ru.facilicom24.manager.utils.Common;

public class MobilePartTimeFragment2
		extends BaseFragment
		implements
		View.OnClickListener,
		AdapterView.OnItemClickListener,
		TimePickerDialogFragment.ITimePickerDialogListener {

	final static public String TAG = "MobileJobActivityFragment2";

	final static int CHOOSE_SCHEDULE = 1;
	final static int CHOOSE_EMPLOYEE = 2;

	final static String CHOOSE = "Выбрать";

	ItemContext itemContext;
	InteractionAdapter adapter;
	ArrayList<InteractionItem> items;

	int selectedIndex;
	ListView listView;

	public MobilePartTimeFragment2() {
		itemContext = new ItemContext();
		adapter = new InteractionAdapter();
	}

	public MobilePartTimeActivity.DataContext getDataContext() {
		return ((MobilePartTimeActivity) getActivity()).getDataContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mobile_parttime_2, container, false);

		items = new ArrayList<>();

		items.add(new InteractionItem(InteractionType.ITEM, "Дата", getDate()));
		items.add(new InteractionItem(InteractionType.ITEM, "Должность", getScheduleName()));
		items.add(new InteractionItem(InteractionType.ITEM, "Время начала", getStartTime()));
		items.add(new InteractionItem(InteractionType.ITEM, "Время окончания", getEndTime()));
		items.add(new InteractionItem(InteractionType.ITEM, "ФИО", getEmployeeName()));
		items.add(new InteractionItem(InteractionType.MONEY, "Стоимость", getPrice()));

		view.findViewById(R.id.apply).setOnClickListener(this);

		listView = (ListView) view.findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		listView.setAdapter(adapter);

		return view;
	}

	@Override
	public void onClick(View view) {
		hideKeyboard();

		switch (view.getId()) {
			case R.id.apply: {
				EditText value = (EditText) listView.findViewById(R.id.interaction_value);

				try {
					itemContext.setPrice(Float.parseFloat(value.getText().toString()));
				} catch (Exception exception) {
					exception.printStackTrace();
				}

				items.get(5).setOption(Common.floatToString(itemContext.getPrice()));

				if (itemContext.getDate().before(getDataContext().getFromDate()) || itemContext.getDate().after(getDataContext().getToDate())) {
					showAlertDialog(R.id.alert_dialog, R.string.mobile_alert_title, R.string.mobile_alert_date);
					return;
				}

				if (itemContext.getScheduleId() == null) {
					showAlertDialog(R.id.alert_dialog, R.string.mobile_alert_title, R.string.mobile_alert_schedule);
					return;
				}

				if (itemContext.getStartTime().after(itemContext.getEndTime()) || itemContext.getStartTime().equals(itemContext.getEndTime())) {
					showAlertDialog(R.id.alert_dialog, R.string.mobile_alert_title, R.string.mobile_alert_time);
					return;
				}

				if (itemContext.getEmployeeId() == null) {
					showAlertDialog(R.id.alert_dialog, R.string.mobile_alert_title, R.string.mobile_alert_employee);
					return;
				}

				if (itemContext.getPrice() == 0) {
					showAlertDialog(R.id.alert_dialog, R.string.mobile_alert_title, R.string.mobile_alert_price);
					return;
				}

				getDataContext().getEmployees().add(itemContext);
				getActivity().getSupportFragmentManager().beginTransaction().remove(this).add(R.id.content, new MobilePartTimeFragment3(), MobilePartTimeFragment3.TAG).commit();
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		switch (i) {
			case 0: {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(itemContext.getDate());
				showDialog(DatePickerDialogFragment.newInstance(calendar), DATE_PICKER_DIALOG);
			}
			break;

			case 1: {
				Intent intent = new Intent(getActivity(), ScheduleActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivityForResult(intent, CHOOSE_SCHEDULE);
			}
			break;

			case 2: {
				selectedIndex = i;

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(itemContext.getStartTime());

				TimePickerDialogFragment dialog = TimePickerDialogFragment.newInstance(calendar, MobilePartTimeFragment2.this);
				dialog.show(getActivity().getSupportFragmentManager(), MobilePartTimeFragment2.TAG);
			}
			break;

			case 3: {
				selectedIndex = i;

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(itemContext.getEndTime());

				TimePickerDialogFragment dialog = TimePickerDialogFragment.newInstance(calendar, MobilePartTimeFragment2.this);
				dialog.show(getActivity().getSupportFragmentManager(), MobilePartTimeFragment2.TAG);
			}
			break;

			case 4: {
				Intent intent = new Intent(getActivity(), MobileEmployeeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivityForResult(intent, CHOOSE_EMPLOYEE);
			}
			break;
		}
	}

	@Override
	public void onDateSet(Calendar calendar) {
		itemContext.setDate(calendar.getTime());

		items.get(0).setOption(getDate());
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
				case CHOOSE_SCHEDULE: {
					itemContext.setScheduleId(data.getStringExtra("ScheduleId"));
					itemContext.setScheduleCode(data.getStringExtra("Code"));
					itemContext.setScheduleName(data.getStringExtra("Title"));
					itemContext.setPrice(0);

					items.get(1).setOption(getScheduleName());
					adapter.notifyDataSetChanged();
				}
				break;

				case CHOOSE_EMPLOYEE: {
					itemContext.setEmployeeId(data.getStringExtra("EmployeeId"));
					itemContext.setEmployeeName(data.getStringExtra("EmployeeName"));

					items.get(4).setOption(getEmployeeName());
					adapter.notifyDataSetChanged();
				}
				break;
			}
		}
	}

	@Override
	public void onTimeSet(Calendar calendar) {
		switch (selectedIndex) {
			case 2: {
				itemContext.setStartTime(calendar.getTime());

				items.get(selectedIndex).setOption(getStartTime());
				adapter.notifyDataSetChanged();
			}
			break;

			case 3: {
				itemContext.setEndTime(calendar.getTime());

				items.get(selectedIndex).setOption(getEndTime());
				adapter.notifyDataSetChanged();
			}
			break;
		}
	}

	String getDate() {
		Date result = itemContext.getDate();

		if (result == null) {
			result = MobileActivity.today();
			itemContext.setDate(result);
		}

		return MobileActivity.dateToString(result);
	}

	String getScheduleName() {
		String result = itemContext.getScheduleName();

		if (result == null) {
			result = CHOOSE;
		}

		return result;
	}

	String getStartTime() {
		Date result = itemContext.getStartTime();

		if (result == null) {
			result = MobileActivity.todayHourMinute(10, 0);
			itemContext.setStartTime(result);
		}

		return MobileActivity.timeToString(result);
	}

	String getEndTime() {
		Date result = itemContext.getEndTime();

		if (result == null) {
			result = MobileActivity.todayHourMinute(10, 0);
			itemContext.setEndTime(result);
		}

		return MobileActivity.timeToString(result);
	}

	String getEmployeeName() {
		String result = itemContext.getEmployeeName();

		if (result == null) {
			result = CHOOSE;
		}

		return result;
	}

	String getPrice() {
		return Common.floatToString(itemContext.getPrice());
	}

	private enum InteractionType {
		ITEM,
		MONEY
	}

	static public class ItemContext {

		@JsonProperty("date")
		Date date;

		@JsonProperty("scheduleId")
		String scheduleId;
		@JsonProperty("scheduleCode")
		String scheduleCode;
		@JsonProperty("scheduleName")
		String scheduleName;

		@JsonProperty("startTime")
		Date startTime;
		@JsonProperty("endTime")
		Date endTime;

		@JsonProperty("employeeId")
		String employeeId;
		@JsonProperty("employeeName")
		String employeeName;

		@JsonProperty("price")
		float price;

		// get

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public String getScheduleId() {
			return scheduleId;
		}

		void setScheduleId(String scheduleId) {
			this.scheduleId = scheduleId;
		}

		public String getScheduleCode() {
			return scheduleCode;
		}

		void setScheduleCode(String scheduleCode) {
			this.scheduleCode = scheduleCode;
		}

		String getScheduleName() {
			return scheduleName;
		}

		void setScheduleName(String scheduleName) {
			this.scheduleName = scheduleName;
		}

		public Date getStartTime() {
			return startTime;
		}

		// set

		void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public Date getEndTime() {
			return endTime;
		}

		void setEndTime(Date endTime) {
			this.endTime = endTime;
		}

		public String getEmployeeId() {
			return employeeId;
		}

		void setEmployeeId(String employeeId) {
			this.employeeId = employeeId;
		}

		public String getEmployeeName() {
			return employeeName;
		}

		void setEmployeeName(String employeeName) {
			this.employeeName = employeeName;
		}

		public float getPrice() {
			return price;
		}

		void setPrice(float price) {
			this.price = price;
		}
	}

	private class InteractionItem {
		InteractionType type;

		String title;
		String option;

		InteractionItem(InteractionType type, String title, String option) {
			this.type = type;

			this.title = title;
			this.option = option;
		}

		InteractionType getType() {
			return type;
		}

		void setOption(String option) {
			this.option = option;
		}
	}

	private class InteractionAdapter extends BaseAdapter implements EditText.OnKeyListener {
		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public InteractionItem getItem(int i) {
			return items.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			InteractionItem item = getItem(i);

			switch (item.getType()) {
				case ITEM: {
					view = getActivity().getLayoutInflater().inflate(R.layout.interaction_item, viewGroup, false);

					TextView title = (TextView) view.findViewById(R.id.interaction_title);
					TextView option = (TextView) view.findViewById(R.id.interaction_option);

					title.setText(item.title);
					option.setText(item.option);
				}
				break;

				case MONEY: {
					view = getActivity().getLayoutInflater().inflate(R.layout.interaction_money_item, viewGroup, false);

					TextView title = (TextView) view.findViewById(R.id.interaction_title);
					EditText value = (EditText) view.findViewById(R.id.interaction_value);

					title.setText(item.title);

					value.setText(item.option);
					value.setOnKeyListener(this);
				}
				break;
			}

			return view;
		}

		@Override
		public boolean onKey(View view, int i, KeyEvent keyEvent) {
			if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
				EditText value = (EditText) view.findViewById(R.id.interaction_value);
				items.get(5).setOption(value.getText().toString());
				return true;
			}

			return false;
		}
	}
}
