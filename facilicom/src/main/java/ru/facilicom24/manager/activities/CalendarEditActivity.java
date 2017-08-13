package ru.facilicom24.manager.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import database.Appointment;
import database.AppointmentAttender;
import database.AppointmentAttenderDao;
import database.AppointmentDao;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.utils.NFAdapter;
import ru.facilicom24.manager.utils.NFItem;
import ru.facilicom24.manager.utils.NetworkHelper;
import ru.facilicom24.manager.views.FontButton;

public class CalendarEditActivity
		extends FragmentActivity
		implements
		View.OnClickListener,
		ListView.OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	final static public String INSERT_REPLACE_DONE = "INSERT_REPLACE_DONE";

	final static int MENU_SUBJECT = 0;
	final static int MENU_PLACE = 1;
	final static int MENU_START = 2;
	final static int MENU_END = 3;
	final static int MENU_ATTENDERS = 4;
	final static int MENU_BODY = 5;

	final static String STATUS_CREATE = "Create";
	final static String STATUS_CANCEL = "Cancel";
	final static String STATUS_DECLINE = "Decline";
	final static String STATUS_UPDATE = "Update";

	final static int USER_IS_OWNER = 1;

	final static String VALIDATE_EXPRESSION_EMAILS = "(\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w+)+)+([ ,;]+\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w+)+)*";

	Date date;
	NFAdapter formAdapter;
	Appointment appointment;
	ArrayList<NFItem> formItems;
	AppointmentDao appointmentDao;
	AppointmentAttenderDao appointmentAttenderDao;
	List<AppointmentAttender> appointmentAttenders;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_calendar_edit);

		date = (Date) getIntent().getSerializableExtra(CalendarActivity.DATE_EXTRA);
		appointmentDao = FacilicomApplication.getInstance().getDaoSession().getAppointmentDao();
		appointmentAttenderDao = FacilicomApplication.getInstance().getDaoSession().getAppointmentAttenderDao();

		long appointmentId = getIntent().getLongExtra(CalendarActivity.ID_EXTRA, 0);

		if (appointmentId > 0) {
			List<Appointment> appointments = appointmentDao.queryBuilder()
					.where(AppointmentDao.Properties.Id.eq(appointmentId))
					.list();

			if (appointments != null && appointments.size() > 0) {
				appointment = appointments.get(0);
				appointmentAttenders = appointment.getAttenders();
			}
		}

		if (appointment == null) {
			Calendar dateCalendar = Calendar.getInstance();

			dateCalendar.setTime(date);
			dateCalendar.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

			Calendar fromDateTime = Calendar.getInstance();
			Calendar toDateTime = Calendar.getInstance();

			fromDateTime.setTime(dateCalendar.getTime());
			toDateTime.setTime(dateCalendar.getTime());

			dateCalendar.add(Calendar.HOUR_OF_DAY, 1);
			fromDateTime.set(Calendar.HOUR_OF_DAY, dateCalendar.get(Calendar.HOUR_OF_DAY));

			dateCalendar.add(Calendar.HOUR_OF_DAY, 1);
			toDateTime.set(Calendar.HOUR_OF_DAY, dateCalendar.get(Calendar.HOUR_OF_DAY));

			appointment = new Appointment(
					null,
					null,
					null,
					FacilicomApplication.dateTimeFormat9.format(fromDateTime.getTime()),
					FacilicomApplication.dateTimeFormat9.format(toDateTime.getTime()),
					null,
					null,
					null,
					USER_IS_OWNER,
					date
			);

			appointmentAttenders = new ArrayList<>();
		}

		((TextView) findViewById(R.id.titleFontTextView)).setText(appointment.getId() == null
				? R.string.calendar_new_title
				: R.string.calendar_edit_title
		);

		formItems = new ArrayList<>();

		formItems.add(new NFItem(NFItem.Type.Value, "Тема", emptyCheck(appointment.getSubject())));
		formItems.add(new NFItem(NFItem.Type.Value, "Место", emptyCheck(appointment.getPlace())));
		formItems.add(new NFItem(NFItem.Type.Date, "Начало", appointment.getStart()));
		formItems.add(new NFItem(NFItem.Type.Date, "Окончание", appointment.getEnd()));
		formItems.add(new NFItem(NFItem.Type.Value, "Участники", emptyCheck(appointmentAttenders, "Нет")));
		formItems.add(new NFItem(NFItem.Type.Value, "Текст", emptyCheck(appointment.getBody())));

		formAdapter = new NFAdapter(this, formItems);

		ListView appointmentFormListView = (ListView) findViewById(R.id.appointmentFormListView);

		appointmentFormListView.setAdapter(formAdapter);
		appointmentFormListView.setOnItemClickListener(this);

		FontButton applyAppointmentFontButton = (FontButton) findViewById(R.id.applyAppointmentFontButton);
		FontButton cancelAppointmentFontButton = (FontButton) findViewById(R.id.cancelAppointmentFontButton);
		FontButton declainAppointmentFontButton = (FontButton) findViewById(R.id.declainAppointmentFontButton);

		if (appointment.getUserIsOwner() != null && appointment.getUserIsOwner() == 1) {
			if (appointment.getId() == null) {
				applyAppointmentFontButton.setVisibility(View.VISIBLE);
				cancelAppointmentFontButton.setVisibility(View.GONE);
				declainAppointmentFontButton.setVisibility(View.GONE);

				applyAppointmentFontButton.setOnClickListener(this);
			} else {
				applyAppointmentFontButton.setVisibility(View.VISIBLE);
				cancelAppointmentFontButton.setVisibility(View.VISIBLE);
				declainAppointmentFontButton.setVisibility(View.GONE);

				applyAppointmentFontButton.setOnClickListener(this);
				cancelAppointmentFontButton.setOnClickListener(this);
			}
		} else {
			applyAppointmentFontButton.setVisibility(View.GONE);
			cancelAppointmentFontButton.setVisibility(View.GONE);
			declainAppointmentFontButton.setVisibility(View.VISIBLE);

			declainAppointmentFontButton.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.applyAppointmentFontButton: {
				apply();
			}
			break;

			case R.id.cancelAppointmentFontButton: {
				cancelDeclain(R.string.calendar_cancel_appointment, STATUS_CANCEL, R.string.calendar_cancel_appointment_toast);
			}
			break;

			case R.id.declainAppointmentFontButton: {
				cancelDeclain(R.string.calendar_declain_appointment, STATUS_DECLINE, R.string.calendar_declain_appointment_toast);
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		if (appointment.getUserIsOwner() == 1) {
			switch (i) {
				case MENU_SUBJECT: {
					startActivityForResult(new Intent(this, TextActivity.class)
									.putExtra(TextActivity.CAPTION, formItems.get(MENU_SUBJECT).getName())
									.putExtra(TextActivity.TEXT, appointment.getSubject()),
							MENU_SUBJECT
					);
				}
				break;

				case MENU_PLACE: {
					startActivityForResult(new Intent(this, TextActivity.class)
									.putExtra(TextActivity.CAPTION, formItems.get(MENU_PLACE).getName())
									.putExtra(TextActivity.TEXT, appointment.getPlace()),
							MENU_PLACE
					);
				}
				break;

				case MENU_START: {
					try {
						final Calendar calendar = Calendar.getInstance();
						calendar.setTime(FacilicomApplication.dateTimeFormat9.parse(appointment.getStart()));

						new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
							@Override
							public void onTimeSet(TimePicker timePicker, int i, int i1) {
								calendar.set(Calendar.HOUR_OF_DAY, i);
								calendar.set(Calendar.MINUTE, i1);

								appointment.setStart(FacilicomApplication.dateTimeFormat9.format(calendar.getTime()));

								formItems.get(MENU_START).setText(appointment.getStart());
								formAdapter.notifyDataSetChanged();
							}
						}, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
				break;

				case MENU_END: {
					try {
						final Calendar calendar = Calendar.getInstance();
						calendar.setTime(FacilicomApplication.dateTimeFormat9.parse(appointment.getEnd()));

						new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
							@Override
							public void onTimeSet(TimePicker timePicker, int i, int i1) {
								calendar.set(Calendar.HOUR_OF_DAY, i);
								calendar.set(Calendar.MINUTE, i1);

								appointment.setEnd(FacilicomApplication.dateTimeFormat9.format(calendar.getTime()));

								formItems.get(MENU_END).setText(appointment.getEnd());
								formAdapter.notifyDataSetChanged();
							}
						}, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
				break;

				case MENU_ATTENDERS: {
					startActivityForResult(new Intent(this, TextActivity.class)
									.putExtra(TextActivity.CAPTION, formItems.get(MENU_ATTENDERS).getName())
									.putExtra(TextActivity.TEXT, emptyCheck(appointmentAttenders, ""))
									.putExtra(TextActivity.VALIDATE_EXPRESSION, VALIDATE_EXPRESSION_EMAILS)
									.putExtra(TextActivity.VALIDATE_MESSAGE, R.string.calendar_attenders_validate),
							MENU_ATTENDERS
					);
				}
				break;

				case MENU_BODY: {
					startActivityForResult(new Intent(this, TextActivity.class)
									.putExtra(TextActivity.CAPTION, formItems.get(MENU_BODY).getName())
									.putExtra(TextActivity.TEXT, appointment.getBody()),
							MENU_BODY
					);
				}
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (data != null) {
				switch (requestCode) {
					case MENU_SUBJECT: {
						String text = data.getStringExtra(TextActivity.TEXT);
						appointment.setSubject(text);

						formItems.get(MENU_SUBJECT).setText(emptyCheck(text));
						formAdapter.notifyDataSetChanged();
					}
					break;

					case MENU_PLACE: {
						String text = data.getStringExtra(TextActivity.TEXT);
						appointment.setPlace(text);

						formItems.get(MENU_PLACE).setText(emptyCheck(text));
						formAdapter.notifyDataSetChanged();
					}
					break;

					case MENU_ATTENDERS: {
						String text = data.getStringExtra(TextActivity.TEXT);

						String[] values = TextUtils.split(text, "[ ,;]+");

						appointmentAttenders.clear();

						for (String value : values) {
							if (value != null && !value.isEmpty()) {
								appointmentAttenders.add(new AppointmentAttender(null, appointment.getId(), value.trim()));
							}
						}

						formItems.get(MENU_ATTENDERS).setText(emptyCheck(appointmentAttenders, "Нет"));
						formAdapter.notifyDataSetChanged();
					}
					break;

					case MENU_BODY: {
						String text = data.getStringExtra(TextActivity.TEXT);
						appointment.setBody(text);

						formItems.get(MENU_BODY).setText(emptyCheck(text));
						formAdapter.notifyDataSetChanged();
					}
					break;
				}
			}
		}
	}

	void apply() {
		ArrayList<String> messages = new ArrayList<>();

		if (appointment.getSubject() == null || appointment.getSubject().isEmpty()) {
			messages.add(formItems.get(MENU_SUBJECT).getName());
		}

		if (appointment.getPlace() == null || appointment.getPlace().isEmpty()) {
			messages.add(formItems.get(MENU_PLACE).getName());
		}

		if (appointment.getStart() != null && appointment.getEnd() != null) {
			try {
				Date startDate = FacilicomApplication.dateTimeFormat9.parse(appointment.getStart());
				Date endDate = FacilicomApplication.dateTimeFormat9.parse(appointment.getEnd());

				if (startDate.getTime() >= endDate.getTime()) {
					messages.add(getString(R.string.calendar_date_validate));
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				messages.add(getString(R.string.calendar_date_validate));
			}
		} else {
			messages.add(getString(R.string.calendar_date_validate));
		}

		if (appointmentAttenders == null || appointmentAttenders.size() == 0) {
			messages.add(formItems.get(MENU_ATTENDERS).getName());
		}

		if (appointment.getBody() == null || appointment.getBody().isEmpty()) {
			messages.add(formItems.get(MENU_BODY).getName());
		}

		if (messages.size() == 0) {
			if (NetworkHelper.isConnected(this)) {
				String status = appointment.getID() != null ? STATUS_UPDATE : STATUS_CREATE;
				final int messageId = appointment.getID() != null ? R.string.calendar_apply_edit_done : R.string.calendar_apply_new_done;

				String date = FacilicomApplication.dateTimeFormat2.format(appointment.getDate());

				JSONObject appointmentObject = new JSONObject();

				try {
					String start = FacilicomApplication.dateTimeFormat2.format(FacilicomApplication.dateTimeFormat9.parse(appointment.getStart()));
					String end = FacilicomApplication.dateTimeFormat2.format(FacilicomApplication.dateTimeFormat9.parse(appointment.getEnd()));

					JSONArray attenders = new JSONArray();
					for (AppointmentAttender appointmentAttender : appointmentAttenders) {
						attenders.put(new JSONObject()
								.put("Email", appointmentAttender.getEmail())
						);
					}

					appointmentObject
							.put("Subject", appointment.getSubject())
							.put("Body", appointment.getBody())
							.put("Start", start)
							.put("End", end)
							.put("Place", appointment.getPlace())
							.put("ID", appointment.getID())
							.put("Status", status)
							.put("UserIsOwner", appointment.getUserIsOwner())
							.put("Attenders", attenders)
							.put("Date", date);

					final ProgressDialog progressDialog = ProgressDialog.show(this, null, getString(R.string.calendar_apply_appointment_progress), true, false);
					FacilicomNetworkClient.postAppointment(this, appointmentObject, new AsyncHttpResponseHandler() {

						@Override
						public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
							if (progressDialog != null) {
								progressDialog.dismiss();
							}

							if (appointment.getId() == null) {
								try {
									appointment.setID(new JSONObject(new String(responseBody, "UTF-8")).optString("ID"));
								} catch (Exception exception) {
									exception.printStackTrace();
								}

								appointmentDao.insert(appointment);

								for (AppointmentAttender appointmentAttender : appointmentAttenders) {
									appointmentAttender.setAppointmentId(appointment.getId());
								}

								appointmentAttenderDao.insertInTx(appointmentAttenders);
							} else {
								appointmentDao.update(appointment);

								if (appointmentAttenders != null && appointmentAttenders.size() > 0 && appointmentAttenders.get(0).getId() == null) {
									appointmentAttenderDao.queryBuilder()
											.where(AppointmentAttenderDao.Properties.Id.eq(appointment.getId()))
											.buildDelete()
											.executeDeleteWithoutDetachingEntities();

									appointmentAttenderDao.insertInTx(appointmentAttenders);
								}
							}

							setResult(Activity.RESULT_OK, new Intent().putExtra(INSERT_REPLACE_DONE, true));
							finish();

							Toast.makeText(CalendarEditActivity.this, messageId, Toast.LENGTH_LONG).show();
						}

						@Override
						public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
							if (progressDialog != null) {
								progressDialog.dismiss();
							}

							messageDialog(R.string.calendar_apply_error);
						}
					});
				} catch (Exception exception) {
					exception.printStackTrace();
					messageDialog(R.string.calendar_apply_error);
				}
			} else {
				messageDialog(R.string.errorConnection);
			}
		} else {
			messageDialog(TextUtils.join(", ", messages));
		}
	}

	void cancelDeclain(int messageId, final String status, final int toastMessageId) {
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.ic_info_white_48dp)
				.setTitle(R.string.mobile_alert_title)
				.setMessage(getString(messageId))
				.setPositiveButton(R.string.btn_yes, new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						if (NetworkHelper.isConnected(CalendarEditActivity.this)) {
							try {
								final ProgressDialog progressDialog = ProgressDialog.show(CalendarEditActivity.this, null, getString(R.string.calendar_apply_appointment_progress), true, false);
								FacilicomNetworkClient.postAppointment(
										CalendarEditActivity.this,

										new JSONObject()
												.put("ID", appointment.getID())
												.put("Status", status),

										new AsyncHttpResponseHandler() {

											@Override
											public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
												if (progressDialog != null) {
													progressDialog.dismiss();
												}

												appointmentAttenderDao.queryBuilder()
														.where(AppointmentAttenderDao.Properties.Id.eq(appointment.getId()))
														.buildDelete()
														.executeDeleteWithoutDetachingEntities();

												appointmentDao.delete(appointment);

												setResult(Activity.RESULT_OK, new Intent().putExtra(INSERT_REPLACE_DONE, true));
												finish();

												Toast.makeText(CalendarEditActivity.this, toastMessageId, Toast.LENGTH_LONG).show();
											}

											@Override
											public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
												if (progressDialog != null) {
													progressDialog.dismiss();
												}

												messageDialog(R.string.calendar_apply_error);
											}
										}
								);
							} catch (Exception exception) {
								exception.printStackTrace();
								messageDialog(R.string.calendar_apply_error);
							}
						} else {
							messageDialog(R.string.errorConnection);
						}
					}
				})
				.setNegativeButton(R.string.btn_no, null)
				.show();
	}

	String emptyCheck(String value) {
		return value != null && !value.isEmpty() ? value : "Нет";
	}

	String emptyCheck(List<AppointmentAttender> appointmentAttenders, String defaultValue) {
		String result;

		if (appointmentAttenders != null && appointmentAttenders.size() > 0) {
			ArrayList<String> list = new ArrayList<>();
			for (AppointmentAttender appointmentAttender : appointmentAttenders) {
				list.add(appointmentAttender.getEmail());
			}

			result = TextUtils.join(", ", list);
		} else {
			result = defaultValue;
		}

		return result;
	}

	void messageDialog(int messageId) {
		messageDialog(getString(messageId));
	}

	void messageDialog(String message) {
		new AlertDialog.Builder(CalendarEditActivity.this)
				.setIcon(R.drawable.ic_info_white_48dp)
				.setTitle(R.string.mobile_alert_title)
				.setMessage(message)
				.setPositiveButton(R.string.mobile_next, null)
				.show();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}
}
