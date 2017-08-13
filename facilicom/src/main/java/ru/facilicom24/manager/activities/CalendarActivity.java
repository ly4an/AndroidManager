package ru.facilicom24.manager.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import database.Appointment;
import database.AppointmentDao;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.network.FacilicomNetworkParser;
import ru.facilicom24.manager.utils.NetworkHelper;

public class CalendarActivity
		extends FragmentActivity
		implements
		View.OnClickListener,
		CalendarPickerView.OnDateSelectedListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	final static public int NEW_APPOINTMENT_REQUEST_CODE = 1;
	final static public int EDIT_APPOINTMENT_REQUEST_CODE = 2;
	final static public int APPOINTMENT_LIST_REQUEST_CODE = 3;

	final static public String ID_EXTRA = "ID_EXTRA";
	final static public String DATE_EXTRA = "DATE_EXTRA";

	Calendar todayCalendar;
	AppointmentDao appointmentDao;
	List<Appointment> appointments;
	GestureDetector gestureDetector;
	CalendarPickerView calendarPickerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_calendar);

		todayCalendar = Calendar.getInstance();

		todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
		todayCalendar.clear(Calendar.MINUTE);
		todayCalendar.clear(Calendar.SECOND);
		todayCalendar.clear(Calendar.MILLISECOND);

		calendarPickerView = (CalendarPickerView) findViewById(R.id.calendarPickerView);

		appointmentDao = FacilicomApplication.getInstance().getDaoSession().getAppointmentDao();
		appointments = appointmentDao.loadAll();

		ArrayList<CalendarCellDecorator> decorators = new ArrayList<>();
		decorators.add(new MXCalendarCellDecorator());
		calendarPickerView.setDecorators(decorators);

		calendarPickerView.setOnDateSelectedListener(this);

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
						todayCalendar.add(Calendar.MONTH, 1);
						calendarInit();
						return false;
					} else if (motionEvent1.getX() - motionEvent.getX() > SWIPE_MIN_DISTANCE && Math.abs(v) > SWIPE_THRESHOLD_VELOCITY) {
						todayCalendar.add(Calendar.MONTH, -1);
						calendarInit();
						return false;
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}

				return false;
			}
		});

		calendarPickerView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				return gestureDetector.onTouchEvent(motionEvent);
			}
		});

		findViewById(R.id.backMonthImageButton).setOnClickListener(this);
		findViewById(R.id.nextMonthImageButton).setOnClickListener(this);
		findViewById(R.id.newAppointmentFontButton).setOnClickListener(this);

		calendarInit();

		if (NetworkHelper.isConnected(this)) {
			FacilicomNetworkClient.getAppointments(new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					FacilicomApplication.getInstance().getDaoSession().getAppointmentDao().deleteAll();
					FacilicomNetworkParser.parseAppointments(responseBody);

					appointments = appointmentDao.loadAll();
					calendarInit();

					Toast.makeText(CalendarActivity.this, R.string.data_refresh_done, Toast.LENGTH_LONG).show();
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				}
			});
		}
	}

	void calendarInit() {
		Calendar minCalendar = (Calendar) todayCalendar.clone();
		minCalendar.set(Calendar.DAY_OF_MONTH, 1);

		Calendar maxCalendar = (Calendar) minCalendar.clone();
		maxCalendar.add(Calendar.MONTH, 1);

		calendarPickerView.init(minCalendar.getTime(), maxCalendar.getTime());
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.backMonthImageButton: {
				todayCalendar.add(Calendar.MONTH, -1);
				calendarInit();
			}
			break;

			case R.id.nextMonthImageButton: {
				todayCalendar.add(Calendar.MONTH, 1);
				calendarInit();
			}
			break;

			case R.id.newAppointmentFontButton: {
				Date date = calendarPickerView.getSelectedDate();

				if (date != null) {
					Calendar dateTimeNow = Calendar.getInstance();

					dateTimeNow.set(Calendar.HOUR_OF_DAY, 0);
					dateTimeNow.clear(Calendar.MINUTE);
					dateTimeNow.clear(Calendar.SECOND);
					dateTimeNow.clear(Calendar.MILLISECOND);

					if (date.getTime() >= dateTimeNow.getTime().getTime()) {
						startActivityForResult(new Intent(this, CalendarEditActivity.class).putExtra(DATE_EXTRA, date), NEW_APPOINTMENT_REQUEST_CODE);
					} else {
						Toast.makeText(this, R.string.calendar_select_date_min, Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(this, R.string.calendar_select_date, Toast.LENGTH_LONG).show();
				}
			}
			break;
		}
	}

	@Override
	public void onDateSelected(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.MILLISECOND, -1);

		if (appointmentDao.queryBuilder()
				.where(AppointmentDao.Properties.Date.between(date, calendar.getTime()))
				.count() > 0) {

			startActivityForResult(new Intent(this, CalendarListActivity.class).putExtra(DATE_EXTRA, date), APPOINTMENT_LIST_REQUEST_CODE);
		}
	}

	@Override
	public void onDateUnselected(Date date) {
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (data != null) {
				switch (requestCode) {
					case NEW_APPOINTMENT_REQUEST_CODE: {
						if (data.getBooleanExtra(CalendarEditActivity.INSERT_REPLACE_DONE, false)) {
							appointments = appointmentDao.loadAll();
							calendarInit();
						}
					}
					break;
				}
			}
		} else {
			if (resultCode == Activity.RESULT_CANCELED) {
				switch (requestCode) {
					case APPOINTMENT_LIST_REQUEST_CODE: {
						appointments = appointmentDao.loadAll();
						calendarInit();
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

	private class MXCalendarCellDecorator implements CalendarCellDecorator {
		final int dayMilliseconds = 24 * 60 * 60 * 1000;

		int _4dp;
		int _10dp;

		RelativeLayout.LayoutParams imageViewLayoutParams;

		MXCalendarCellDecorator() {
			_4dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
			_10dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

			imageViewLayoutParams = new RelativeLayout.LayoutParams(_10dp, _10dp);

			imageViewLayoutParams.setMargins(_4dp, _4dp, _4dp, _4dp);
			imageViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			imageViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		}

		@Override
		public void decorate(CalendarCellView cellView, Date date) {
			if (cellView != null && cellView.getChildCount() > 0) {
				View view = cellView.getChildAt(cellView.getChildCount() - 1);

				MXRelativeLayout mxRelativeLayout;
				if (!view.getClass().equals(MXRelativeLayout.class)) {
					ImageView imageView = new ImageView(CalendarActivity.this);

					imageView.setLayoutParams(imageViewLayoutParams);
					imageView.setImageResource(R.drawable.dark_orange_dot);

					mxRelativeLayout = new MXRelativeLayout(CalendarActivity.this);
					mxRelativeLayout.addView(imageView);

					cellView.addView(mxRelativeLayout);
				} else {
					mxRelativeLayout = (MXRelativeLayout) view;
				}

				int visibility = View.INVISIBLE;
				for (Appointment appointment : appointments) {
					if (appointment.getDate() != null
							&& appointment.getDate().getTime() >= date.getTime()
							&& appointment.getDate().getTime() < date.getTime() + dayMilliseconds) {

						visibility = View.VISIBLE;
						break;
					}
				}

				mxRelativeLayout.setVisibility(visibility);
			}
		}

		class MXRelativeLayout extends RelativeLayout {
			MXRelativeLayout(Context context) {
				super(context);
			}
		}
	}
}
