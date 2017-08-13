package ru.facilicom24.manager.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import database.Mobile;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoMobileRepository;
import ru.facilicom24.manager.fragments.CaptionFragment;
import ru.facilicom24.manager.fragments.MobileFragment;
import ru.facilicom24.manager.fragments.MobileFragment3;
import ru.facilicom24.manager.model.ScheduleItem;

public class MobileActivity
		extends BaseActivity
		implements CaptionFragment.OnFragmentInteractionListener {

	static public final String MOBILE_FRAGMENT_TAG = "mobile_fragment";
	static public final String MOBILE_FRAGMENT_2_TAG = "mobile_fragment_2";
	static public final String MOBILE_FRAGMENT_3_TAG = "mobile_fragment_3";

	static public final String MOBILE_FRAGMENT_2_FROM_TIME_TAG = "mobile_fragment_2_from_time";
	static public final String MOBILE_FRAGMENT_2_TO_TIME_TAG = "mobile_fragment_2_to_time";

	static public Mobile mobile;
	static public JSONObject parameters;

	static public String dateToString(Date date) {
		return FacilicomApplication.dateTimeFormat4.format(date);
	}

	static public String timeToString(Date date) {
		return FacilicomApplication.dateTimeFormat8.format(date);
	}

	static public Calendar stringToDate(String date) {
		Calendar calendar = Calendar.getInstance();

		String[] parts = date.split("\\.");

		if (parts.length == 3) {
			int day = 0;
			int month = 0;
			int year = 0;

			try {
				day = Integer.parseInt(parts[0], 10);
				month = Integer.parseInt(parts[1], 10);
				year = Integer.parseInt(parts[2], 10);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			if (day != 0 && month != 0 && year != 0) {
				calendar.set(Calendar.DAY_OF_MONTH, day);
				calendar.set(Calendar.MONTH, month - 1);
				calendar.set(Calendar.YEAR, year);

				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
			}
		}

		return calendar;
	}

	static public Calendar stringToTime(Calendar baseDate, String time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(baseDate.getTime());

		String[] parts = time.split("\\:");

		if (parts.length == 2) {
			int hour = -1;
			int minute = -1;

			try {
				hour = Integer.parseInt(parts[0], 10);
				minute = Integer.parseInt(parts[1], 10);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			if (hour != -1 && minute != -1) {
				calendar.set(Calendar.HOUR_OF_DAY, hour);
				calendar.set(Calendar.MINUTE, minute);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
			}
		}

		return calendar;
	}

	public static JSONObject mobileToJSON(
			int clientId,
			int directumId,
			String nomGroupCode,
			String scheduleCode,
			String fromDate,
			String toDate,
			String email,
			ArrayList<ScheduleItem> schedules
	) {
		JSONObject object;

		try {
			JSONArray records = new JSONArray();

			for (int index = 0; index < schedules.size(); index++) {
				ScheduleItem item = schedules.get(index);

				Calendar requestDate = MobileActivity.stringToDate(item.getRequestDate());
				String _requestDate = FacilicomApplication.dateTimeFormat5.format(requestDate.getTime());

				Calendar workOn = MobileActivity.stringToTime(requestDate, item.getFromTime());
				String _workOn = FacilicomApplication.dateTimeFormat7.format(workOn.getTime());

				Calendar workOff = MobileActivity.stringToTime(requestDate, item.getToTime());
				String _workOff = FacilicomApplication.dateTimeFormat7.format(workOff.getTime());

				int sex = item.getSexId() == 2 ? 0 : 1;

				JSONObject record = new JSONObject();

				record.put("Quantity", item.getQuantity());

				record.put("Date", _requestDate);
				record.put("WorkOn", _workOn);
				record.put("WorkOff", _workOff);

				record.put("Sex", sex);
				record.put("ScheduleID", item.getScheduleId());

				// Optional

				record.put("ScheduleCode", item.getScheduleCode());
				record.put("ScheduleName", item.getScheduleName());
				record.put("MaxQuantity", item.getMaxQuantity());

				records.put(record);
			}

			object = new JSONObject();

			object.put("DirectumID", directumId);
			object.put("NomGroupCode", nomGroupCode);
			object.put("ScheduleCode", scheduleCode);
			object.put("Email", email);
			object.put("Records", records);

			// Optional

			Calendar _fromDate = MobileActivity.stringToDate(fromDate);
			String __fromDate = DateFormat.format("yyyy-MM-dd", _fromDate.getTime()).toString();

			Calendar _toDate = MobileActivity.stringToDate(toDate);
			String __toDate = DateFormat.format("yyyy-MM-dd", _toDate.getTime()).toString();

			object.put("ClientID", clientId);
			object.put("FromDate", __fromDate);
			object.put("ToDate", __toDate);
		} catch (Exception exception) {
			exception.printStackTrace();
			object = null;
		}

		return object;
	}

	static public Date today() {
		Calendar calendar = Calendar.getInstance();

		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return new Date(calendar.getTimeInMillis());
	}

	static public Date todayHourMinute(int hour, int minute) {
		Calendar calendar = Calendar.getInstance();

		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), hour, minute, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return new Date(calendar.getTimeInMillis());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_mobile);

		parameters = new JSONObject();

		List<Mobile> Mobiles = DaoMobileRepository.getAllNotSend(this);

		if (Mobiles.size() == 0) {
			mobile = null;

			try {
				parameters.put("ClientId", 0);
				parameters.put("AccountId", 0);
				parameters.put("NomenclatureGroupCode", "");
				parameters.put("FromDate", "");
				parameters.put("ToDate", "");

				parameters.put("Schedules", new ArrayList<ScheduleItem>());
			} catch (JSONException exception) {
				exception.printStackTrace();
			}
		} else {
			mobile = Mobiles.get(0);

			try {
				JSONObject object = new JSONObject(mobile.getJson());

				parameters.put("ClientId", object.optInt("ClientID", 0));
				parameters.put("AccountId", object.optInt("DirectumID", 0));
				parameters.put("NomenclatureGroupCode", object.optString("NomGroupCode", ""));

				String fromDate = object.optString("FromDate", "");
				String toDate = object.optString("ToDate", "");

				try {
					fromDate = dateToString(FacilicomApplication.dateTimeFormat5.parse(fromDate));
					toDate = dateToString(FacilicomApplication.dateTimeFormat5.parse(toDate));
				} catch (Exception exception) {
					exception.printStackTrace();
				}

				parameters.put("FromDate", fromDate);
				parameters.put("ToDate", toDate);

				JSONArray records = object.optJSONArray("Records");
				ArrayList<ScheduleItem> schedules = new ArrayList<>();

				for (int index = 0; index < records.length(); index++) {
					JSONObject record = (JSONObject) records.get(index);

					String requestDate = record.optString("Date", "");

					String fromTime = record.optString("WorkOn", "").replace('T', ' ').replace('Z', ' ').trim();
					String toTime = record.optString("WorkOff", "").replace('T', ' ').replace('Z', ' ').trim();

					try {
						requestDate = dateToString(FacilicomApplication.dateTimeFormat5.parse(requestDate));

						fromTime = timeToString(FacilicomApplication.dateTimeFormat6.parse(fromTime));
						toTime = timeToString(FacilicomApplication.dateTimeFormat6.parse(toTime));
					} catch (Exception exception) {
						exception.printStackTrace();
					}

					int sexId = record.optInt("Sex", 0) == 0 ? 2 : 1;

					ScheduleItem scheduleItem = new ScheduleItem(
							requestDate,

							record.optString("ScheduleID", ""),
							record.optString("ScheduleCode", ""),
							record.optString("ScheduleName", ""),

							sexId,

							fromTime,
							toTime,

							record.optInt("Quantity", 0),
							record.optInt("MaxQuantity", 0)
					);

					schedules.add(scheduleItem);
				}

				parameters.put("Schedules", schedules);
			} catch (JSONException exception) {
				exception.printStackTrace();
			}
		}

		android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		transaction.add(R.id.content, new MobileFragment(), MOBILE_FRAGMENT_TAG);

		if (mobile != null) {
			transaction.add(R.id.content, new MobileFragment3(), MobileActivity.MOBILE_FRAGMENT_3_TAG);
		}

		transaction.commit();
	}

	@Override
	public void onBackPressed() {
		Fragment fragment = getSupportFragmentManager().findFragmentByTag(MOBILE_FRAGMENT_3_TAG);

		if (fragment != null) {
			getSupportFragmentManager().beginTransaction().remove(fragment).commit();
		} else {
			fragment = getSupportFragmentManager().findFragmentByTag(MOBILE_FRAGMENT_2_TAG);

			if (fragment != null) {
				ArrayList<ScheduleItem> schedules = new ArrayList<>();

				try {
					schedules = (ArrayList<ScheduleItem>) parameters.get("Schedules");
				} catch (Exception exception) {
					exception.printStackTrace();
				}

				if (schedules.size() > 0) {
					getSupportFragmentManager().beginTransaction().remove(fragment).add(R.id.content, new MobileFragment3(), MobileActivity.MOBILE_FRAGMENT_3_TAG).commit();
				} else {
					getSupportFragmentManager().beginTransaction().remove(fragment).commit();
				}
			} else {
				finish();
			}
		}
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	@Override
	public void captionFragmentOnSendPressed() {
		((MobileFragment3) getSupportFragmentManager().findFragmentByTag(MOBILE_FRAGMENT_3_TAG)).send();
	}

	@Override
	public void captionFragmentOnSavePressed() {
		((MobileFragment3) getSupportFragmentManager().findFragmentByTag(MOBILE_FRAGMENT_3_TAG)).apply();
	}

	@Override
	public void captionFragmentOnHistoryPressed() {
	}

	@Override
	public void captionFragmentOnAlbumPressed() {
	}

	@Override
	public boolean backIcon() {
		return true;
	}

	@Override
	public boolean sendIcon() {
		return getSupportFragmentManager().findFragmentByTag(MOBILE_FRAGMENT_3_TAG) != null;
	}

	@Override
	public boolean saveIcon() {
		return getSupportFragmentManager().findFragmentByTag(MOBILE_FRAGMENT_3_TAG) != null;
	}

	@Override
	public boolean historyIcon() {
		return false;
	}

	@Override
	public boolean albumIcon() {
		return false;
	}
}
