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

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import java.io.StringWriter;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import database.PartTime;
import database.Schedule;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.activities.LoginActivity;
import ru.facilicom24.manager.activities.MobileActivity;
import ru.facilicom24.manager.activities.MobilePartTimeActivity;
import ru.facilicom24.manager.cache.DaoPartTimeRepository;
import ru.facilicom24.manager.cache.DaoScheduleRepository;
import ru.facilicom24.manager.dialogs.AlertDialogFragment;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.utils.Common;
import ru.facilicom24.manager.utils.NetworkHelper;

public class MobilePartTimeFragment3
		extends BaseFragment
		implements View.OnClickListener {

	final static public String TAG = "MobilePartTimeFragment3";
	InteractionAdapter adapter = new InteractionAdapter();
	int selectedIndex;

	public MobilePartTimeFragment3() {
	}

	public MobilePartTimeActivity.DataContext getDataContext() {
		return ((MobilePartTimeActivity) getActivity()).getDataContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mobile_parttime_3, container, false);

		view.findViewById(R.id.add).setOnClickListener(this);

		((ListView) view.findViewById(R.id.list)).setAdapter(adapter);

		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.add: {
				getActivity().getSupportFragmentManager().beginTransaction().remove(this).add(R.id.content, new MobilePartTimeFragment2(), MobilePartTimeFragment2.TAG).commit();
			}
			break;

			case R.id.delete: {
				selectedIndex = (int) v.getTag();
				showAlertDialog(R.id.mobile_delete, R.string.mobile_alert_title, R.string.mobile_alert_delete, R.string.mobile_alert_yes, R.string.mobile_alert_no);
			}
			break;
		}
	}

	public void save() {
		if (getDataContext().getEmployees().size() > 0) {
			showAlertDialog(R.id.mobile_apply, R.string.mobile_alert_title, R.string.mobile_alert_apply, R.string.mobile_alert_yes, R.string.mobile_alert_no);
		} else {
			showAlertDialog(R.id.alert_dialog, R.string.mobile_alert_title, R.string.mobile_alert_empty);
		}
	}

	public void send() {
		if (getDataContext().getEmployees().size() > 0) {
			showAlertDialog(R.id.mobile_send, R.string.mobile_alert_title, R.string.mobile_alert_send, R.string.mobile_alert_yes, R.string.mobile_alert_no);
		} else {
			showAlertDialog(R.id.alert_dialog, R.string.mobile_alert_title, R.string.mobile_alert_empty);
		}
	}

	@Override
	public void onAlertPositiveBtnClicked(AlertDialogFragment dialog) {
		dialog.dismiss();

		switch (dialog.getDialogId()) {
			case R.id.mobile_apply: {
				ObjectMapper mapper = new ObjectMapper();
				StringWriter writer = new StringWriter();

				try {
					mapper.writeValue(writer, getDataContext());

					PartTime partTime = ((MobilePartTimeActivity) getActivity()).getPartTime();
					PartTime partTimeEntity = partTime != null ? partTime : new PartTime();

					partTimeEntity.setJson(writer.toString());
					partTimeEntity.setSend(false);

					DaoPartTimeRepository.insertOrUpdate(getActivity(), partTimeEntity);

					getActivity().finish();
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
			break;

			case R.id.mobile_send: {
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
				String userName = preferences.getString(LoginActivity.USERNAME, null);

				MobilePartTimeActivity.DataContext dataContext = getDataContext();

				String scheduleCode = "";
				List<Schedule> schedules = DaoScheduleRepository.getAll(getActivity());

				if (schedules.size() > 0) {
					scheduleCode = schedules.get(0).getScheduleCode();
				}

				final JSONObject object = MobilePartTimeActivity.mobileToJSON(
						dataContext.getAccountId(),
						dataContext.getNomenclatureGroupCode(),
						scheduleCode,
						userName,
						dataContext.getEmployees()
				);

				if (NetworkHelper.isConnected(getActivity())) {
					showProgressDialog(R.string.mobile_apply_message);

					FacilicomNetworkClient.postMobile(getActivity(), object.toString(), new AsyncHttpResponseHandler() {

						@Override
						public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
							hideProgressDialog();

							PartTime partTime = ((MobilePartTimeActivity) getActivity()).getPartTime();

							if (partTime != null) {
								DaoPartTimeRepository.delete(getActivity(), partTime);
							}

							Toast.makeText(getActivity(), R.string.mobile_toast_parttime_sent_done, Toast.LENGTH_LONG).show();
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
			}
			break;

			case R.id.mobile_delete: {
				getDataContext().getEmployees().remove(selectedIndex);
				adapter.notifyDataSetChanged();
			}
			break;
		}
	}

	@Override
	public void onAlertNegativeBtnClicked(AlertDialogFragment dialog) {
		dialog.dismiss();
	}

	private class InteractionAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return getDataContext().getEmployees().size();
		}

		@Override
		public MobilePartTimeFragment2.ItemContext getItem(int i) {
			return getDataContext().getEmployees().get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			if (view == null) {
				view = getActivity().getLayoutInflater().inflate(R.layout.interaction_mobile_parttime_item, viewGroup, false);
			}

			ImageView delete = (ImageView) view.findViewById(R.id.delete);

			TextView date = (TextView) view.findViewById(R.id.date);
			TextView time = (TextView) view.findViewById(R.id.time);
			TextView employee = (TextView) view.findViewById(R.id.employee);
			TextView price = (TextView) view.findViewById(R.id.price);

			MobilePartTimeFragment2.ItemContext item = getItem(i);

			date.setText(MobileActivity.dateToString(item.getDate()).substring(0, 5));
			time.setText(String.format("%1$s - %2$s", MobileActivity.timeToString(item.getStartTime()), MobileActivity.timeToString(item.getEndTime())));

			employee.setText(item.getEmployeeName());
			price.setText(Common.floatToString(item.getPrice()));

			delete.setTag(i);
			delete.setOnClickListener(MobilePartTimeFragment3.this);

			return view;
		}
	}
}
