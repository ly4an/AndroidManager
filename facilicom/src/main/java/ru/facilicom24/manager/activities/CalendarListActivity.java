package ru.facilicom24.manager.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import database.Appointment;
import database.AppointmentDao;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;

public class CalendarListActivity
		extends FragmentActivity
		implements
		View.OnClickListener,
		ListView.OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	Date date;
	TextView titleFontTextView;
	ListView appointmentsListView;
	GestureDetector gestureDetector;
	AppointmentsAdapter appointmentsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_calendar_list);

		findViewById(R.id.newAppointmentFontButton).setOnClickListener(this);
		findViewById(R.id.backDayImageButton).setOnClickListener(this);
		findViewById(R.id.nextDayImageButton).setOnClickListener(this);

		date = (Date) getIntent().getSerializableExtra(CalendarActivity.DATE_EXTRA);

		titleFontTextView = (TextView) findViewById(R.id.titleFontTextView);
		titleFontTextView.setText(FacilicomApplication.dateTimeFormat4.format(date));

		appointmentsAdapter = new AppointmentsAdapter(date);

		gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {

			static final int SWIPE_MIN_DISTANCE = 120;
			static final int SWIPE_MAX_OFF_PATH = 250;
			static final int SWIPE_THRESHOLD_VELOCITY = 200;

			@Override
			public boolean onDown(MotionEvent motionEvent) {
				return false;
			}

			@Override
			public void onShowPress(MotionEvent motionEvent) {
			}

			@Override
			public boolean onSingleTapUp(MotionEvent motionEvent) {
				return false;
			}

			@Override
			public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
				return false;
			}

			@Override
			public void onLongPress(MotionEvent motionEvent) {
			}

			@Override
			public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
				try {
					if (Math.abs(motionEvent.getY() - motionEvent1.getY()) > SWIPE_MAX_OFF_PATH) {
						return false;
					} else if (motionEvent.getX() - motionEvent1.getX() > SWIPE_MIN_DISTANCE && Math.abs(v) > SWIPE_THRESHOLD_VELOCITY) {
						titleFontTextView.setText(FacilicomApplication.dateTimeFormat4.format(date = appointmentsAdapter.add(1)));
						listViewInit();
						return false;
					} else if (motionEvent1.getX() - motionEvent.getX() > SWIPE_MIN_DISTANCE && Math.abs(v) > SWIPE_THRESHOLD_VELOCITY) {
						titleFontTextView.setText(FacilicomApplication.dateTimeFormat4.format(date = appointmentsAdapter.add(-1)));
						listViewInit();
						return false;
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}

				return false;
			}
		});

		appointmentsListView = (ListView) findViewById(R.id.appointmentsListView);

		appointmentsListView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				return gestureDetector.onTouchEvent(motionEvent);
			}
		});

		appointmentsListView.setOnItemClickListener(this);
		appointmentsListView.setAdapter(appointmentsAdapter);

		listViewInit();
	}

	void listViewInit() {
		appointmentsListView.setVisibility(appointmentsListView.getCount() > 0 ? View.VISIBLE : View.GONE);
		findViewById(R.id.noAppointmentsTextView).setVisibility(appointmentsListView.getCount() > 0 ? View.GONE : View.VISIBLE);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.newAppointmentFontButton: {
				Calendar dateTimeNow = Calendar.getInstance();

				dateTimeNow.set(Calendar.HOUR_OF_DAY, 0);
				dateTimeNow.clear(Calendar.MINUTE);
				dateTimeNow.clear(Calendar.SECOND);
				dateTimeNow.clear(Calendar.MILLISECOND);

				if (date.getTime() >= dateTimeNow.getTime().getTime()) {
					startActivityForResult(new Intent(this, CalendarEditActivity.class).putExtra(CalendarActivity.DATE_EXTRA, date), CalendarActivity.NEW_APPOINTMENT_REQUEST_CODE);
				} else {
					Toast.makeText(this, R.string.calendar_select_date_min, Toast.LENGTH_LONG).show();
				}
			}
			break;

			case R.id.backDayImageButton: {
				titleFontTextView.setText(FacilicomApplication.dateTimeFormat4.format(date = appointmentsAdapter.add(-1)));
				listViewInit();
			}
			break;

			case R.id.nextDayImageButton: {
				titleFontTextView.setText(FacilicomApplication.dateTimeFormat4.format(date = appointmentsAdapter.add(1)));
				listViewInit();
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		startActivityForResult(new Intent(this, CalendarEditActivity.class)
						.putExtra(CalendarActivity.DATE_EXTRA, date)
						.putExtra(CalendarActivity.ID_EXTRA, ((Appointment) appointmentsListView.getItemAtPosition(i)).getId()),
				CalendarActivity.EDIT_APPOINTMENT_REQUEST_CODE
		);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (data != null) {
				switch (requestCode) {
					case CalendarActivity.NEW_APPOINTMENT_REQUEST_CODE:
					case CalendarActivity.EDIT_APPOINTMENT_REQUEST_CODE: {
						if (data.getBooleanExtra(CalendarEditActivity.INSERT_REPLACE_DONE, false)) {
							appointmentsAdapter.add(0);
							listViewInit();
						}
					}
					break;
				}
			}
		}
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	private class AppointmentsAdapter
			extends BaseAdapter {

		Date date;
		LayoutInflater layoutInflater;
		List<Appointment> appointments;

		AppointmentsAdapter(Date date) {
			this.date = date;
			layoutInflater = (LayoutInflater) CalendarListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			appointments = queryAppointments();
		}

		@Override
		public int getCount() {
			return appointments.size();
		}

		@Override
		public Object getItem(int i) {
			return appointments.get(i);
		}

		@Override
		public long getItemId(int i) {
			return appointments.get(i).getId();
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			if (view == null) {
				view = layoutInflater.inflate(R.layout.appointment_list_item, viewGroup, false);
			}

			Appointment appointment = (Appointment) getItem(i);

			((TextView) view.findViewById(R.id.subjectTextView)).setText(appointment.getSubject());

			TextView placeTextView = (TextView) view.findViewById(R.id.placeTextView);
			if (appointment.getPlace() != null && !appointment.getPlace().trim().isEmpty()) {
				placeTextView.setVisibility(View.VISIBLE);
				placeTextView.setText(appointment.getPlace());
			} else {
				placeTextView.setVisibility(View.GONE);
			}

			((TextView) view.findViewById(R.id.startTextView)).setText(getTime(appointment.getStart()));
			((TextView) view.findViewById(R.id.endTextView)).setText(getTime(appointment.getEnd()));

			return view;
		}

		String getTime(String value) {
			if (value != null && !value.isEmpty()) {
				String[] parts = TextUtils.split(value, " ");
				return parts.length > 1 ? parts[1] : "*";
			} else {
				return "*";
			}
		}

		Date add(int day) {
			Calendar calendar = Calendar.getInstance();

			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_MONTH, day);

			date = calendar.getTime();
			appointments = queryAppointments();

			notifyDataSetChanged();

			return date;
		}

		List<Appointment> queryAppointments() {
			Calendar calendar = Calendar.getInstance();

			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.MILLISECOND, -1);

			return FacilicomApplication.getInstance().getDaoSession().getAppointmentDao().queryBuilder()
					.where(AppointmentDao.Properties.Date.between(date, calendar.getTime()))
					.list();
		}
	}
}
