package ru.facilicom24.manager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import database.ActAccount;
import database.Client;
import database.NomenclatureGroup;
import database.Schedule;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.activities.CheckActAccountActivity;
import ru.facilicom24.manager.activities.CheckClientActivity;
import ru.facilicom24.manager.activities.MobileActivity;
import ru.facilicom24.manager.activities.MobilePartTimeActivity;
import ru.facilicom24.manager.activities.NomenclatureGroupActivity;
import ru.facilicom24.manager.cache.DaoActAccountRepository;
import ru.facilicom24.manager.cache.DaoClientRepository;
import ru.facilicom24.manager.cache.DaoNomenclatureGroupRepository;
import ru.facilicom24.manager.cache.DaoScheduleRepository;
import ru.facilicom24.manager.dialogs.DatePickerDialogFragment;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.utils.NetworkHelper;

public class MobilePartTimeFragment
		extends BaseFragment
		implements
		View.OnClickListener,
		AdapterView.OnItemClickListener {

	static final public String TAG = "MobilePartTimeFragment";

	static final int CHOOSE_CLIENT = 1002;
	static final int CHOOSE_OBJECT = 1003;
	static final int CHOOSE_NOMENCLATURE_GROUP = 2000;

	final static String CHOOSE = "Выбрать";

	List<InteractionItem> items;
	InteractionAdapter adapter = new InteractionAdapter();

	int selectedIndex;

	public MobilePartTimeFragment() {
	}

	public MobilePartTimeActivity.DataContext getDataContext() {
		return ((MobilePartTimeActivity) getActivity()).getDataContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mobile_parttime, container, false);

		view.findViewById(R.id.next).setOnClickListener(this);

		items = new ArrayList<>();

		items.add(new InteractionItem("Клиент", clientName()));
		items.add(new InteractionItem("Объект", accountName()));
		items.add(new InteractionItem("Номенклатурная\nгруппа", nomenclatureGroupName()));
		items.add(new InteractionItem("Период с", fromDate()));
		items.add(new InteractionItem("Период по", toDate()));

		ListView mobileMenu = (ListView) view.findViewById(R.id.mobile_menu);
		mobileMenu.setOnItemClickListener(this);
		mobileMenu.setAdapter(adapter);

		return view;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.next: {
				int accountId = getDataContext().getAccountId();

				if (accountId == 0) {
					showAlertDialog(R.id.alert_dialog, R.string.mobile_alert_title, R.string.mobile_alert_no_account);
					return;
				}

				String nomenclatureGroupCode = getDataContext().getNomenclatureGroupCode();

				if (nomenclatureGroupCode == null) {
					showAlertDialog(R.id.alert_dialog, R.string.mobile_alert_title, R.string.mobile_alert_nogroup);
					return;
				}

				Date fromDate = getDataContext().getFromDate();
				Date toDate = getDataContext().getToDate();

				if (fromDate.after(toDate)) {
					showAlertDialog(R.id.alert_dialog, R.string.mobile_alert_title, R.string.mobile_alert_period);
					return;
				}

				if (NetworkHelper.isConnected(getActivity())) {
					showProgressDialog(R.string.mobile_schedule_get);

					FacilicomNetworkClient.getSchedule(
							accountId,
							nomenclatureGroupCode,
							MobileActivity.dateToString(getDataContext().getFromDate()),
							MobileActivity.dateToString(getDataContext().getToDate()),
							new AsyncHttpResponseHandler() {

								@Override
								public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
									hideProgressDialog();

									JSONArray schedules = checkSchedule(responseBody);

									if (schedules.length() > 0) {
										parseSchedule(schedules);

										getDataContext().setEmployees(new ArrayList<MobilePartTimeFragment2.ItemContext>());
										getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content, new MobilePartTimeFragment2(), MobilePartTimeFragment2.TAG).commit();
									} else {
										Toast.makeText(getActivity(), R.string.mobile_alert_no_schedule_2, Toast.LENGTH_LONG).show();
									}
								}

								@Override
								public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
									hideProgressDialog();
									Toast.makeText(getActivity(), R.string.mobile_alert_no_schedule, Toast.LENGTH_LONG).show();
								}
							});
				} else {
					Toast.makeText(getActivity(), R.string.errorConnection, Toast.LENGTH_LONG).show();
				}
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		switch (i) {
			case 0: {
				Intent intent = new Intent(getActivity(), CheckClientActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivityForResult(intent, CHOOSE_CLIENT);
			}
			break;

			case 1: {
				Intent intent = new Intent(getActivity(), CheckActAccountActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

				int clientId = getDataContext().getClientId();

				if (clientId != 0) {
					intent.putExtra("ClientId", clientId);
				}

				startActivityForResult(intent, CHOOSE_OBJECT);
			}
			break;

			case 2: {
				Intent intent = new Intent(getActivity(), NomenclatureGroupActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivityForResult(intent, CHOOSE_NOMENCLATURE_GROUP);
			}
			break;

			case 3: {
				selectedIndex = i;

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(getDataContext().getFromDate());
				showDialog(DatePickerDialogFragment.newInstance(calendar), DATE_PICKER_DIALOG);
			}
			break;

			case 4: {
				selectedIndex = i;

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(getDataContext().getToDate());
				showDialog(DatePickerDialogFragment.newInstance(calendar), DATE_PICKER_DIALOG);
			}
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
				case CHOOSE_CLIENT: {
					String option = data.getStringExtra("Title");

					items.get(0).setOption(option);
					items.get(1).setOption(CHOOSE);

					adapter.notifyDataSetChanged();

					getDataContext().setClientId(data.getIntExtra("ClientId", 0));
					getDataContext().setClientName(option);

					getDataContext().setAccountId(0);
				}
				break;

				case CHOOSE_OBJECT: {
					String option = String.format("%s, %s", data.getStringExtra("Title"), data.getStringExtra("Address"));
					items.get(1).setOption(option);
					adapter.notifyDataSetChanged();

					getDataContext().setAccountId(data.getIntExtra("AccountId", 0));
					getDataContext().setAccountName(option);
				}
				break;

				case CHOOSE_NOMENCLATURE_GROUP: {
					items.get(2).setOption(data.getStringExtra("Title"));
					adapter.notifyDataSetChanged();

					getDataContext().setNomenclatureGroupCode(data.getStringExtra("NomenclatureGroupCode"));
				}
				break;
			}
		}
	}

	@Override
	public void onDateSet(Calendar calendar) {
		switch (selectedIndex) {
			case 3:
				getDataContext().setFromDate(calendar.getTime());
				break;

			case 4:
				getDataContext().setToDate(calendar.getTime());
				break;
		}

		items.get(selectedIndex).setOption(MobileActivity.dateToString(calendar.getTime()));
		adapter.notifyDataSetChanged();
	}

	String clientName() {
		String result = CHOOSE;

		int clientId = getDataContext().getClientId();

		if (clientId != 0) {
			List<Client> clients = DaoClientRepository.getClientForClientId(getActivity(), clientId);

			if (clients.size() > 0) {
				result = clients.get(0).getName();
			}
		}

		return result;
	}

	String accountName() {
		String result = CHOOSE;

		int accountId = getDataContext().getAccountId();

		if (accountId != 0) {
			List<ActAccount> accounts = DaoActAccountRepository.getActAccountForDirectumId(getActivity(), accountId);

			if (accounts.size() > 0) {
				result = String.format("%s, %s", accounts.get(0).getName(), accounts.get(0).getAddress());
			}
		}

		return result;
	}

	// Behavior

	String nomenclatureGroupName() {
		String result = CHOOSE;

		String code = getDataContext().getNomenclatureGroupCode();

		if (code != null) {
			List<NomenclatureGroup> nomenclatureGroups = DaoNomenclatureGroupRepository.getNomenclatureGroupsByCode(getActivity(), code);

			if (nomenclatureGroups.size() > 0) {
				result = nomenclatureGroups.get(0).getName();
			}
		}

		return result;
	}

	String fromDate() {
		Date result = getDataContext().getFromDate();

		if (result == null) {
			result = MobileActivity.today();
			getDataContext().setFromDate(result);
		}

		return MobileActivity.dateToString(result);
	}

	String toDate() {
		Date result = getDataContext().getToDate();

		if (result == null) {
			result = MobileActivity.today();
			getDataContext().setToDate(result);
		}

		return MobileActivity.dateToString(result);
	}

	JSONArray checkSchedule(byte[] responseBody) {
		JSONArray array = new JSONArray();

		try {
			array = new JSONArray(new String(responseBody, "UTF-8"));
		} catch (Exception exception) {
			Log.d("checkSchedule", exception.getMessage(), exception);
		}

		return array;
	}

	void parseSchedule(JSONArray array) {
		try {
			DaoScheduleRepository.deleteAll(getActivity());

			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.optJSONObject(i);

				Schedule schedule = new Schedule();

				schedule.setScheduleID(object.optString("ScheduleID", ""));
				schedule.setScheduleCode(object.optString("ScheduleCode", ""));
				schedule.setPosition(object.optString("Position", ""));
				schedule.setQuantity(object.optInt("Quantity", 0));

				DaoScheduleRepository.insertOrUpdate(getActivity(), schedule);
			}
		} catch (Exception exception) {
			Log.d("parseSchedule", exception.getMessage(), exception);
		}
	}

	private class InteractionItem {
		String title;
		String option;

		InteractionItem(String title, String option) {
			this.title = title;
			this.option = option;
		}

		void setOption(String option) {
			this.option = option;
		}
	}

	private class InteractionAdapter extends BaseAdapter {

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
			if (view == null) {
				view = getActivity().getLayoutInflater().inflate(R.layout.interaction_item, viewGroup, false);
			}

			TextView title = (TextView) view.findViewById(R.id.interaction_title);
			TextView option = (TextView) view.findViewById(R.id.interaction_option);

			InteractionItem item = getItem(i);

			title.setText(item.title);
			option.setText(item.option);

			return view;
		}
	}
}
