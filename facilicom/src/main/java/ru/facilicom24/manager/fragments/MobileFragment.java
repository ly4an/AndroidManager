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
import org.json.JSONException;
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
import ru.facilicom24.manager.activities.NomenclatureGroupActivity;
import ru.facilicom24.manager.cache.DaoActAccountRepository;
import ru.facilicom24.manager.cache.DaoClientRepository;
import ru.facilicom24.manager.cache.DaoMobileRepository;
import ru.facilicom24.manager.cache.DaoNomenclatureGroupRepository;
import ru.facilicom24.manager.cache.DaoScheduleRepository;
import ru.facilicom24.manager.dialogs.DatePickerDialogFragment;
import ru.facilicom24.manager.model.ScheduleItem;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.utils.NetworkHelper;

public class MobileFragment
		extends BaseFragment
		implements View.OnClickListener {

	static final int CHOOSE_CLIENT = 1002;
	static final int CHOOSE_OBJECT = 1003;
	static final int CHOOSE_NOMENCLATURE_GROUP = 2000;

	int selectedIndex;

	ListView mobileMenu;
	List<InteractionItem> menu;
	InteractionAdapter mobileMenuAdapter;
	Calendar fromDate;
	Calendar toDate;

	public MobileFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		String _fromDate = fromDate();
		String _toDate = toDate();

		fromDate = MobileActivity.stringToDate(_fromDate);
		toDate = MobileActivity.stringToDate(_toDate);

		menu = new ArrayList<>();

		menu.add(new InteractionItem("Клиент", clientName()));
		menu.add(new InteractionItem("Объект", accountName()));
		menu.add(new InteractionItem("Номенклатурная\nгруппа", nomenclatureGroupName()));
		menu.add(new InteractionItem("Период с", _fromDate));
		menu.add(new InteractionItem("Период по", _toDate));

		View view = inflater.inflate(R.layout.fragment_mobile, container, false);

		mobileMenu = (ListView) view.findViewById(R.id.mobile_menu);
		mobileMenuAdapter = new InteractionAdapter();
		mobileMenu.setAdapter(mobileMenuAdapter);

		view.findViewById(R.id.next).setOnClickListener(this);

		mobileMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				switch (i) {
					case 0: {
						Intent intent = new Intent(getActivity(), CheckClientActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

						int accountId = MobileActivity.parameters.optInt("AccountId", 0);

						if (accountId != 0) {
							intent.putExtra("AccountId", accountId);
						}

						startActivityForResult(intent, CHOOSE_CLIENT);
					}
					break;

					case 1: {
						Intent intent = new Intent(getActivity(), CheckActAccountActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

						int clientId = MobileActivity.parameters.optInt("ClientId", 0);

						if (clientId != 0) {
							intent.putExtra("ClientId", clientId);
						}

						startActivityForResult(intent, CHOOSE_OBJECT);
					}
					break;

					case 2: {
						Intent intent = new Intent(getActivity(), NomenclatureGroupActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

						String nomenclatureGroupCode = MobileActivity.parameters.optString("NomenclatureGroupCode", "");

						if (nomenclatureGroupCode.length() > 0) {
							intent.putExtra("NomenclatureGroupCode", nomenclatureGroupCode);
						}

						startActivityForResult(intent, CHOOSE_NOMENCLATURE_GROUP);
					}
					break;

					case 3:
						selectedIndex = i;
						showDialog(DatePickerDialogFragment.newInstance(fromDate), DATE_PICKER_DIALOG);
						break;

					case 4:
						selectedIndex = i;
						showDialog(DatePickerDialogFragment.newInstance(toDate), DATE_PICKER_DIALOG);
						break;
				}
			}
		});

		return view;
	}

	@Override
	public void onClick(View view) {
		int accountId = 0;

		try {
			accountId = MobileActivity.parameters.getInt("AccountId");
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		if (accountId == 0) {
			showAlertDialog(R.id.alert_dialog, R.string.mobile_alert_title, R.string.mobile_alert_no_account);
			return;
		}

		String nomenclatureGroupCode = "";

		try {
			nomenclatureGroupCode = MobileActivity.parameters.getString("NomenclatureGroupCode");
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		if (nomenclatureGroupCode.length() == 0) {
			showAlertDialog(R.id.alert_dialog, R.string.mobile_alert_title, R.string.mobile_alert_nogroup);
			return;
		}

		if (fromDate.after(toDate)) {
			showAlertDialog(R.id.alert_dialog, R.string.mobile_alert_title, R.string.mobile_alert_period);
			return;
		}

		String _fromDate = MobileActivity.dateToString(fromDate.getTime());
		String _toDate = MobileActivity.dateToString(toDate.getTime());

		try {
			MobileActivity.parameters.put("FromDate", _fromDate);
			MobileActivity.parameters.put("ToDate", _toDate);
		} catch (JSONException exception) {
			exception.printStackTrace();
		}

		if (NetworkHelper.isConnected(getActivity())) {

			showProgressDialog(R.string.mobile_schedule_get);
			FacilicomNetworkClient.getSchedule(accountId, nomenclatureGroupCode, _fromDate, _toDate, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					hideProgressDialog();

					JSONArray schedules = checkSchedule(responseBody);

					if (schedules.length() > 0) {
						parseSchedule(schedules);

						ArrayList<ScheduleItem> _schedules = new ArrayList<>();

						try {
							_schedules = (ArrayList<ScheduleItem>) MobileActivity.parameters.get("Schedules");
						} catch (Exception exception) {
							exception.printStackTrace();
						}

						_schedules.clear();

						if (MobileActivity.mobile != null) {
							DaoMobileRepository.delete(getActivity(), MobileActivity.mobile);
						}

						getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content, new MobileFragment2(), MobileActivity.MOBILE_FRAGMENT_2_TAG).commit();
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
				case CHOOSE_CLIENT: {
					menu.get(0).setOption(data.getStringExtra("Title"));
					menu.get(1).setOption("Выбрать");

					mobileMenuAdapter.notifyDataSetChanged();

					try {
						MobileActivity.parameters.put("ClientId", data.getIntExtra("ClientId", 0));
						MobileActivity.parameters.put("AccountId", 0);
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
				break;

				case CHOOSE_OBJECT: {
					menu.get(1).setOption(String.format("%s, %s", data.getStringExtra("Title"), data.getStringExtra("Address")));
					mobileMenuAdapter.notifyDataSetChanged();

					try {
						MobileActivity.parameters.put("AccountId", data.getIntExtra("AccountId", 0));
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
				break;

				case CHOOSE_NOMENCLATURE_GROUP: {
					menu.get(2).setOption(data.getStringExtra("Title"));
					mobileMenuAdapter.notifyDataSetChanged();

					try {
						MobileActivity.parameters.put("NomenclatureGroupCode", data.getStringExtra("NomenclatureGroupCode"));
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
		switch (selectedIndex) {
			case 3:
				fromDate = calendar;
				break;

			case 4:
				toDate = calendar;
				break;
		}

		menu.get(selectedIndex).setOption(MobileActivity.dateToString(calendar.getTime()));
		mobileMenuAdapter.notifyDataSetChanged();
	}

	String clientName() {
		String result = "Выбрать";

		int clientId = MobileActivity.parameters.optInt("ClientId", 0);

		if (clientId != 0) {
			List<Client> clients = DaoClientRepository.getClientForClientId(getActivity(), clientId);

			if (clients.size() > 0) {
				result = clients.get(0).getName();
			}
		}

		return result;
	}

	String accountName() {
		String result = "Выбрать";

		int accountId = MobileActivity.parameters.optInt("AccountId", 0);

		if (accountId != 0) {
			List<ActAccount> accounts = DaoActAccountRepository.getActAccountForDirectumId(getActivity(), accountId);

			if (accounts.size() > 0) {
				result = String.format("%s, %s", accounts.get(0).getName(), accounts.get(0).getAddress());
			}
		}

		return result;
	}

	String nomenclatureGroupName() {
		String result = "Выбрать";

		String code = MobileActivity.parameters.optString("NomenclatureGroupCode", "");

		if (code.length() > 0) {
			List<NomenclatureGroup> nomenclatureGroups = DaoNomenclatureGroupRepository.getNomenclatureGroupsByCode(getActivity(), code);

			if (nomenclatureGroups.size() > 0) {
				result = nomenclatureGroups.get(0).getName();
			}
		}

		return result;
	}

	String fromDate() {
		String result = MobileActivity.parameters.optString("FromDate", "");

		if (result.length() == 0) {
			result = MobileActivity.dateToString(new Date());
		}

		return result;
	}

	String toDate() {
		String result = MobileActivity.parameters.optString("ToDate", "");

		if (result.length() == 0) {
			result = MobileActivity.dateToString(new Date());
		}

		return result;
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

	class InteractionItem {
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
			if (view == null) {
				view = getActivity().getLayoutInflater().inflate(R.layout.interaction_item, viewGroup, false);
			}

			view.setTag(i);

			InteractionItem menuItem = getItem(i);

			TextView title = (TextView) view.findViewById(R.id.interaction_title);
			TextView option = (TextView) view.findViewById(R.id.interaction_option);

			title.setText(menuItem.title);
			option.setText(menuItem.option);

			return view;
		}
	}
}
