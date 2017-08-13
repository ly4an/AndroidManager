package ru.facilicom24.manager.activities;

import android.os.Bundle;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import database.PartTime;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoPartTimeRepository;
import ru.facilicom24.manager.fragments.CaptionFragment;
import ru.facilicom24.manager.fragments.MobilePartTimeFragment;
import ru.facilicom24.manager.fragments.MobilePartTimeFragment2;
import ru.facilicom24.manager.fragments.MobilePartTimeFragment3;

public class MobilePartTimeActivity
		extends BaseActivity
		implements CaptionFragment.OnFragmentInteractionListener {

	PartTime partTime;
	DataContext dataContext;

	public static JSONObject mobileToJSON(
			int accountId,
			String nomenclatureGroupCode,
			String scheduleCode,
			String userName,
			ArrayList<MobilePartTimeFragment2.ItemContext> employees
	) {
		JSONObject object;

		try {
			JSONArray records = new JSONArray();

			for (int index = 0; index < employees.size(); index++) {
				MobilePartTimeFragment2.ItemContext item = employees.get(index);

				JSONObject record = new JSONObject();

				record.put("Date", FacilicomApplication.dateTimeFormat5.format(item.getDate()));

				record.put("WorkOn", FacilicomApplication.dateTimeFormat7.format(item.getStartTime()));
				record.put("WorkOff", FacilicomApplication.dateTimeFormat7.format(item.getEndTime()));

				record.put("ScheduleID", item.getScheduleId());

				record.put("EmpID", item.getEmployeeId());
				record.put("EmpFIO", item.getEmployeeName());
				record.put("Price", item.getPrice());

				// Quantity
				// Sex

				records.put(record);
			}

			object = new JSONObject();

			object.put("DirectumID", accountId);
			object.put("NomGroupCode", nomenclatureGroupCode);
			object.put("ScheduleCode", scheduleCode);
			object.put("Email", userName);
			object.put("Records", records);
		} catch (Exception exception) {
			exception.printStackTrace();
			object = null;
		}

		return object;
	}

	public PartTime getPartTime() {
		return partTime;
	}

	public DataContext getDataContext() {
		return dataContext;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_mobile_parttime);

		List<PartTime> partTimes = DaoPartTimeRepository.getAllNotSend(this);

		if (partTimes.size() > 0) {
			partTime = partTimes.get(0);

			ObjectMapper mapper = new ObjectMapper();
			StringReader reader = new StringReader(partTime.getJson());

			try {
				dataContext = mapper.readValue(reader, DataContext.class);

				getSupportFragmentManager().beginTransaction()
						.add(R.id.content, new MobilePartTimeFragment(), MobilePartTimeFragment.TAG)
						.add(R.id.content, new MobilePartTimeFragment3(), MobilePartTimeFragment3.TAG)
						.commit();
			} catch (Exception exception) {
				exception.printStackTrace();

				dataContext = new DataContext();
				getSupportFragmentManager().beginTransaction().add(R.id.content, new MobilePartTimeFragment(), MobilePartTimeFragment.TAG).commit();
			}
		} else {
			dataContext = new DataContext();
			getSupportFragmentManager().beginTransaction().add(R.id.content, new MobilePartTimeFragment(), MobilePartTimeFragment.TAG).commit();
		}
	}

	@Override
	public void onBackPressed() {
		MobilePartTimeFragment2 fragment2 = (MobilePartTimeFragment2) getSupportFragmentManager().findFragmentByTag(MobilePartTimeFragment2.TAG);
		MobilePartTimeFragment3 fragment3 = (MobilePartTimeFragment3) getSupportFragmentManager().findFragmentByTag(MobilePartTimeFragment3.TAG);

		if (fragment2 != null && dataContext.getEmployees().size() == 0) {
			getSupportFragmentManager().beginTransaction().remove(fragment2).commit();
		} else if (fragment2 != null && dataContext.getEmployees().size() > 0) {
			getSupportFragmentManager().beginTransaction().remove(fragment2).add(R.id.content, new MobilePartTimeFragment3(), MobilePartTimeFragment3.TAG).commit();
		} else if (fragment3 != null) {
			getSupportFragmentManager().beginTransaction().remove(fragment3).commit();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	@Override
	public void captionFragmentOnSendPressed() {
		((MobilePartTimeFragment3) getSupportFragmentManager().findFragmentByTag(MobilePartTimeFragment3.TAG)).send();
	}

	@Override
	public void captionFragmentOnSavePressed() {
		((MobilePartTimeFragment3) getSupportFragmentManager().findFragmentByTag(MobilePartTimeFragment3.TAG)).save();
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
		return getSupportFragmentManager().findFragmentByTag(MobilePartTimeFragment3.TAG) != null;
	}

	@Override
	public boolean saveIcon() {
		return getSupportFragmentManager().findFragmentByTag(MobilePartTimeFragment3.TAG) != null;
	}

	@Override
	public boolean historyIcon() {
		return false;
	}

	@Override
	public boolean albumIcon() {
		return false;
	}

	static public class DataContext {

		@JsonProperty("clientId")
		int clientId;
		@JsonProperty("clientName")
		String clientName;

		@JsonProperty("accountId")
		int accountId;
		@JsonProperty("accountName")
		String accountName;

		@JsonProperty("nomenclatureGroupCode")
		String nomenclatureGroupCode;

		@JsonProperty("fromDate")
		Date fromDate;
		@JsonProperty("toDate")
		Date toDate;

		@JsonProperty("employees")
		ArrayList<MobilePartTimeFragment2.ItemContext> employees;

		// get

		public int getClientId() {
			return clientId;
		}

		public void setClientId(int clientId) {
			this.clientId = clientId;
		}

		public String getClientName() {
			return clientName;
		}

		public void setClientName(String clientName) {
			this.clientName = clientName;
		}

		public int getAccountId() {
			return accountId;
		}

		public void setAccountId(int accountId) {
			this.accountId = accountId;
		}

		public String getAccountName() {
			return accountName;
		}

		public void setAccountName(String accountName) {
			this.accountName = accountName;
		}

		// set

		public String getNomenclatureGroupCode() {
			return nomenclatureGroupCode;
		}

		public void setNomenclatureGroupCode(String nomenclatureGroupCode) {
			this.nomenclatureGroupCode = nomenclatureGroupCode;
		}

		public Date getFromDate() {
			return fromDate;
		}

		public void setFromDate(Date fromDate) {
			this.fromDate = fromDate;
		}

		public Date getToDate() {
			return toDate;
		}

		public void setToDate(Date toDate) {
			this.toDate = toDate;
		}

		public ArrayList<MobilePartTimeFragment2.ItemContext> getEmployees() {
			return employees;
		}

		public void setEmployees(ArrayList<MobilePartTimeFragment2.ItemContext> employees) {
			this.employees = employees;
		}
	}
}
