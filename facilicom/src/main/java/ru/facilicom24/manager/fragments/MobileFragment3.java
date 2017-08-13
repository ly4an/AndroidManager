package ru.facilicom24.manager.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import database.Mobile;
import database.Schedule;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.activities.LoginActivity;
import ru.facilicom24.manager.activities.MobileActivity;
import ru.facilicom24.manager.cache.DaoMobileRepository;
import ru.facilicom24.manager.cache.DaoScheduleRepository;
import ru.facilicom24.manager.dialogs.AlertDialogFragment;
import ru.facilicom24.manager.model.ScheduleItem;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.utils.NetworkHelper;

public class MobileFragment3
		extends BaseFragment
		implements View.OnClickListener {

	int selectedIndex;

	ListView list;
	InteractionAdapter adapter;
	ArrayList<ScheduleItem> schedules;

	public MobileFragment3() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		schedules = new ArrayList<>();

		try {
			schedules = (ArrayList<ScheduleItem>) MobileActivity.parameters.get("Schedules");
		} catch (JSONException exception) {
			exception.printStackTrace();
		}

		View view = inflater.inflate(R.layout.fragment_mobile_3, container, false);

		list = (ListView) view.findViewById(R.id.list);
		adapter = new InteractionAdapter();
		list.setAdapter(adapter);

		view.findViewById(R.id.add).setOnClickListener(this);

		return view;
	}

	public void send() {
		if (schedules.size() > 0) {
			showAlertDialog(R.id.mobile_send, R.string.mobile_alert_title, R.string.mobile_alert_send, R.string.mobile_alert_yes, R.string.mobile_alert_no);
		} else {
			showAlertDialog(R.id.alert_dialog, R.string.mobile_alert_title, R.string.mobile_alert_empty);
		}
	}

	public void apply() {
		if (schedules.size() > 0) {
			showAlertDialog(R.id.mobile_apply, R.string.mobile_alert_title, R.string.mobile_alert_apply, R.string.mobile_alert_yes, R.string.mobile_alert_no);
		} else {
			showAlertDialog(R.id.alert_dialog, R.string.mobile_alert_title, R.string.mobile_alert_empty);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.add:
				getActivity().getSupportFragmentManager().beginTransaction().remove(this).add(R.id.content, new MobileFragment2(), MobileActivity.MOBILE_FRAGMENT_2_TAG).commit();
				break;

			case R.id.delete:
				selectedIndex = (int) v.getTag();
				showAlertDialog(R.id.mobile_delete, R.string.mobile_alert_title, R.string.mobile_alert_delete, R.string.mobile_alert_yes, R.string.mobile_alert_no);
				break;
		}
	}

	@Override
	public void onAlertPositiveBtnClicked(AlertDialogFragment dialog) {
		dialog.dismiss();

		switch (dialog.getDialogId()) {
			case R.id.mobile_send:
			case R.id.mobile_apply: {
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
				String userName = preferences.getString(LoginActivity.USERNAME, null);

				int clientId = MobileActivity.parameters.optInt("ClientId", 0);
				int accountId = MobileActivity.parameters.optInt("AccountId", 0);

				String nomenclatureGroupCode = MobileActivity.parameters.optString("NomenclatureGroupCode", "");

				String fromDate = MobileActivity.parameters.optString("FromDate", "");
				String toDate = MobileActivity.parameters.optString("ToDate", "");

				String scheduleCode = "";
				List<Schedule> _schedules = DaoScheduleRepository.getAll(getActivity());

				if (_schedules.size() > 0) {
					scheduleCode = _schedules.get(0).getScheduleCode();
				}

				final JSONObject object = MobileActivity.mobileToJSON(
						clientId,
						accountId,
						nomenclatureGroupCode,
						scheduleCode,
						fromDate,
						toDate,
						userName,
						schedules
				);

				switch (dialog.getDialogId()) {
					case R.id.mobile_send:
						if (NetworkHelper.isConnected(getActivity())) {
							showProgressDialog(R.string.mobile_apply_message);

							FacilicomNetworkClient.postMobile(getActivity(), object.toString(), new AsyncHttpResponseHandler() {
								@Override
								public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
									hideProgressDialog();

									if (MobileActivity.mobile != null) {
										DaoMobileRepository.delete(getActivity(), MobileActivity.mobile);
									}

									Toast.makeText(getActivity(), R.string.mobile_toast_sent_done, Toast.LENGTH_LONG).show();
									getActivity().finish();
								}

								@Override
								public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
									hideProgressDialog();
									Toast.makeText(getActivity(), R.string.mobile_toast_sent_fail, Toast.LENGTH_LONG).show();
								}
							});
						} else {
							Toast.makeText(getActivity(), R.string.errorConnection, Toast.LENGTH_LONG).show();
						}
						break;

					case R.id.mobile_apply:
						send(object, R.string.mobile_toast_apply, false);
						break;
				}
			}

			break;

			case R.id.mobile_delete:
				schedules.remove(selectedIndex);
				adapter.notifyDataSetChanged();
				break;
		}
	}

	@Override
	public void onAlertNegativeBtnClicked(AlertDialogFragment dialog) {
		dialog.dismiss();
	}

	void send(JSONObject object, int messageId, boolean send) {
		Mobile mobile = MobileActivity.mobile != null ? MobileActivity.mobile : new Mobile();

		mobile.setJson(object.toString());
		mobile.setSend(send);

		DaoMobileRepository.insertOrUpdate(getActivity(), mobile);

		Toast.makeText(getActivity(), messageId, Toast.LENGTH_LONG).show();
		getActivity().finish();
	}

	private class InteractionAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return schedules.size();
		}

		@Override
		public ScheduleItem getItem(int i) {
			return schedules.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			if (view == null) {
				view = getActivity().getLayoutInflater().inflate(R.layout.interaction_mobile_item, viewGroup, false);
			}

			ImageView delete = (ImageView) view.findViewById(R.id.delete);

			TextView date = (TextView) view.findViewById(R.id.date);
			TextView time = (TextView) view.findViewById(R.id.time);
			TextView schedule = (TextView) view.findViewById(R.id.schedule);
			TextView quantity = (TextView) view.findViewById(R.id.quantity);

			ScheduleItem item = getItem(i);

			date.setText(item.getRequestDate().substring(0, 5));
			time.setText(String.format("%s - %s", item.getFromTime(), item.getToTime()));
			schedule.setText(item.getScheduleName());
			quantity.setText(String.valueOf(item.getQuantity()));

			delete.setTag(i);
			delete.setOnClickListener((MobileFragment3) getActivity().getSupportFragmentManager().findFragmentByTag(MobileActivity.MOBILE_FRAGMENT_3_TAG));

			return view;
		}
	}
}
