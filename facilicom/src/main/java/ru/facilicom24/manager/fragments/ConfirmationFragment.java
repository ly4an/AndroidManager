package ru.facilicom24.manager.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.activities.MapActivity;
import ru.facilicom24.manager.activities.PotSellActivity;
import ru.facilicom24.manager.activities.TaskActivity;
import ru.facilicom24.manager.dialogs.AlertDialogFragment;
import ru.facilicom24.manager.dialogs.CheckDialog;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.utils.NetworkHelper;
import ru.facilicom24.manager.utils.SessionManager;
import ru.facilicom24.manager.views.FontTextView;

public class ConfirmationFragment
		extends BaseFragment
		implements
		CheckDialog.ICheckDialogListener,
		AlertDialogFragment.IAlertDialogListener {

	static final int TASK_ACTIVITY = 1;
	static final String CHECK_BLANK_FRAGMENT = "check_blank";

	static final String LOYALITY = "Loyality";
	static final String NEWVOLUME = "NewVolume";

	static final String SATISFACTION = "Satisfaction";
	static final String THIRD = "Third";
	static final String PLAN = "Plan";

	static final String _SATISFACTION = "_Satisfaction";
	static final String _THIRD = "_Third";
	static final String _PLAN = "_Plan";

	static final String COMMUNICATION = "Communication";
	static final String TASKS = "Tasks";

	CheckQuestionsFragment mCheckQuestionsFragment;

	FontTextView mValueDescription;

	FontTextView mQualityDescription;
	FontTextView mSpeedDescription;

	FontTextView _mQualityDescription;
	FontTextView _mSpeedDescription;

	RatingBar actRatingBar1;
	RatingBar actRatingBar2;
	RatingBar actRatingBar3;
	RatingBar actRatingBar4;
	RatingBar actRatingBar5;

	RadioListener radioListener1;
	RadioListener radioListener2;
	RadioListener radioListener3;

	JSONArray tasks;
	JSONObject parameters;

	public ConfirmationFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_confirmation, container, false);

		parameters = SessionManager.getInstance().getAct();

		if (parameters != null) {
			mCheckQuestionsFragment = CheckQuestionsFragment.newInstance(parameters.optString(COMMUNICATION, ""));

			mCheckQuestionsFragment.setTargetFragment(this, 0);
			getFragmentManager().beginTransaction().add(R.id.steps, mCheckQuestionsFragment, CHECK_BLANK_FRAGMENT).commit();

			try {
				tasks = parameters.getJSONArray(TASKS);
			} catch (Exception exception) {
				tasks = new JSONArray();
			}

			mValueDescription = (FontTextView) view.findViewById(R.id.value_description);

			mQualityDescription = (FontTextView) view.findViewById(R.id.quality_description);
			mSpeedDescription = (FontTextView) view.findViewById(R.id.speed_description);

			_mQualityDescription = (FontTextView) view.findViewById(R.id._quality_description);
			_mSpeedDescription = (FontTextView) view.findViewById(R.id._speed_description);

			radioListener1 = new RadioListener(view, R.id.actRadioButton1, R.id.actRadioButton2, R.id.actRadioButton3);
			radioListener2 = new RadioListener(view, R.id.actRadioButton4, R.id.actRadioButton5, R.id.actRadioButton6);
			radioListener3 = new RadioListener(view, R.id.actRadioButton7, R.id.actRadioButton8, R.id.actRadioButton9);

			actRatingBar1 = (RatingBar) view.findViewById(R.id.actRatingBar1);
			actRatingBar2 = (RatingBar) view.findViewById(R.id.actRatingBar2);
			actRatingBar3 = (RatingBar) view.findViewById(R.id.actRatingBar3);
			actRatingBar4 = (RatingBar) view.findViewById(R.id.actRatingBar4);
			actRatingBar5 = (RatingBar) view.findViewById(R.id.actRatingBar5);

			//

			String[] messages1 = {
					"",
					"Нелояльный",
					"Безразличный",
					"Лояльный",
					"Адвокат"
			};

			String[] messages2 = {
					"",
					"Не соответствует требованиям",
					"Имеются замечания",
					"Соответствует требованиям",
					"Превосходит требования"
			};

			actRatingBar1.setOnRatingBarChangeListener(new RatingListener(mValueDescription, messages1));
			actRatingBar2.setOnRatingBarChangeListener(new RatingListener(mQualityDescription, messages2));
			actRatingBar3.setOnRatingBarChangeListener(new RatingListener(mSpeedDescription, messages2));
			actRatingBar4.setOnRatingBarChangeListener(new RatingListener(_mQualityDescription, messages2));
			actRatingBar5.setOnRatingBarChangeListener(new RatingListener(_mSpeedDescription, messages2));

			((LayerDrawable) actRatingBar1.getProgressDrawable()).getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
			((LayerDrawable) actRatingBar2.getProgressDrawable()).getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
			((LayerDrawable) actRatingBar3.getProgressDrawable()).getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
			((LayerDrawable) actRatingBar4.getProgressDrawable()).getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
			((LayerDrawable) actRatingBar5.getProgressDrawable()).getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

			actRatingBar1.setRating(parameters.optInt(LOYALITY, 0));
			radioListener1.setValue(parameters.optInt(NEWVOLUME, 3));

			actRatingBar2.setRating(parameters.optInt(SATISFACTION, 0));
			actRatingBar3.setRating(parameters.optInt(THIRD, 0));
			radioListener2.setValue(parameters.optInt(PLAN, 3));

			actRatingBar4.setRating(parameters.optInt(_SATISFACTION, 0));
			actRatingBar5.setRating(parameters.optInt(_THIRD, 0));
			radioListener3.setValue(parameters.optInt(_PLAN, 3));

			//

			view.findViewById(R.id.newTask).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					saveAct();
					startActivityForResult(new Intent(getActivity(), TaskActivity.class), TASK_ACTIVITY);
				}
			});
		}

		return view;
	}

	public void onBackPressed() {
		showDialog(CheckDialog.newInstance(), "CHECK_DIALOG");
	}

	@Override
	public void onSaveBtnClicked() {
		saveAct();
		getActivity().finish();
	}

	@Override
	public void onSendBtnClicked() {
		if (checkValues()) {
			String serviceTypeId = parameters.optString(InteractionsFragment.ACT_TYPE_ID);

			if (serviceTypeId != null && !serviceTypeId.isEmpty()) {
				if (tasks.length() > 0) {
					sendAct();
				} else {
					showAlertDialog(R.id.alert_dialog, getString(R.string.mobile_alert_title), getString(R.string.task_message));
				}
			} else {
				sendAct();
			}
		}
	}

	@Override
	public void onDeleteBtnClicked() {
		SessionManager.getInstance().saveAct(null);
		getActivity().finish();
	}

	@Override
	public void onAlertPositiveBtnClicked(AlertDialogFragment dialog) {
		String title = dialog.getArguments().getString("title");

		if (title != null && !title.equals("Ошибка") && !title.equals("Сообщение")) {
			getActivity().finish();
		}
	}

	@Override
	public void onAlertDialogCancel(AlertDialogFragment dialog) {
		String title = dialog.getArguments().getString("title");

		if (title != null && !title.equals("Ошибка") && !title.equals("Сообщение")) {
			getActivity().finish();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case TASK_ACTIVITY:
				try {
					tasks.put(new JSONObject(data.getStringExtra("Task")));
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				break;
		}
	}

	public boolean checkValues() {
		if (mCheckQuestionsFragment.getCommunication().length() == 0) {
			String message = String.format("%s: %s", getString(R.string.confirmation_no_value), getString(R.string.confirmation_result_label));
			showAlertDialog(R.id.alert_dialog, "Ошибка", message);
			return false;
		} else {
			if (mCheckQuestionsFragment.getCommunication().replaceAll("[ .,!\\-()]", "").length() < 80) {
				String message = String.format("%s: %s", getString(R.string.confirmation_result_label), getString(R.string.confirmation_result_not_full));
				showAlertDialog(R.id.alert_dialog, "Ошибка", message);
				return false;
			}
		}

		if (actRatingBar1.getRating() == 0) {
			String message = String.format("%s: %s", getString(R.string.confirmation_no_value), getString(R.string.confirmation_mark));
			showAlertDialog(R.id.alert_dialog, "Ошибка", message);
			return false;
		}

		if (actRatingBar2.getRating() == 0) {
			String message = String.format("%s: %s. %s", getString(R.string.confirmation_no_value), getString(R.string.confirmation_mark_client), getString(R.string.confirmation_quality));
			showAlertDialog(R.id.alert_dialog, "Ошибка", message);
			return false;
		}

		if (actRatingBar3.getRating() == 0) {
			String message = String.format("%s: %s. %s", getString(R.string.confirmation_no_value), getString(R.string.confirmation_mark_client), getString(R.string.confirmation_operative));
			showAlertDialog(R.id.alert_dialog, "Ошибка", message);
			return false;
		}

		if (actRatingBar4.getRating() == 0) {
			String message = String.format("%s: %s. %s", getString(R.string.confirmation_no_value), getString(R.string.confirmation_mark_manager), getString(R.string.confirmation_quality));
			showAlertDialog(R.id.alert_dialog, "Ошибка", message);
			return false;
		}

		if (actRatingBar5.getRating() == 0) {
			String message = String.format("%s: %s. %s", getString(R.string.confirmation_no_value), getString(R.string.confirmation_mark_manager), getString(R.string.confirmation_operative));
			showAlertDialog(R.id.alert_dialog, "Ошибка", message);
			return false;
		}

		return true;
	}

	void saveAct() {
		try {
			parameters.put(LOYALITY, Math.round(actRatingBar1.getRating()));
			parameters.put(NEWVOLUME, Math.round(radioListener1.getValue()));

			parameters.put(SATISFACTION, Math.round(actRatingBar2.getRating()));
			parameters.put(THIRD, Math.round(actRatingBar3.getRating()));
			parameters.put(PLAN, Math.round(radioListener2.getValue()));

			parameters.put(_SATISFACTION, Math.round(actRatingBar4.getRating()));
			parameters.put(_THIRD, Math.round(actRatingBar5.getRating()));
			parameters.put(_PLAN, Math.round(radioListener3.getValue()));

			parameters.put(COMMUNICATION, mCheckQuestionsFragment.getCommunication());
			parameters.put(TASKS, tasks);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		SessionManager.getInstance().saveAct(parameters.toString());
	}

	public void sendAct() {
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getActivity());

		float longitude = preference.getFloat(MapActivity.OBJECT_FIXED_Y, 0);
		float latitude = preference.getFloat(MapActivity.OBJECT_FIXED_X, 0);

		String id = UUID.randomUUID().toString();

		int clientId = parameters.optInt(InteractionsFragment.CLIENT_ID);
		int accountId = parameters.optInt(InteractionsFragment.ACCOUNT_ID);
		String serviceTypeId = parameters.optString(InteractionsFragment.ACT_TYPE_ID);
		int potSellNumber = parameters.optInt(PotSellActivity.POT_SELL_NUMBER);
		int contactId = parameters.optInt(InteractionsFragment.CONTACT_ID);

		JSONObject params = new JSONObject();

		try {
			for (int i = 0; i < tasks.length(); i++) {
				JSONObject task = tasks.optJSONObject(i);

				task.put("ActUID", id);

				task.put("ClientID", clientId);
				task.put("AccountID", accountId);
				task.put("ServiceTypeUID", serviceTypeId);
				task.put("PotSellNumber", potSellNumber);
				task.put("ContactID", contactId);
			}

			params.put("ID", id);

			params.put("CreatedOn", parameters.optString("CreatedOn"));

			params.put("ActTypeId", parameters.optInt(InteractionsFragment.SERVICE_TYPE_ID));
			params.put("ReasonId", parameters.optInt(InteractionsFragment.REASON_ID));
			params.put("ClientId", clientId);
			params.put("AccountId", accountId);
			params.put("ServiceTypeId", serviceTypeId);
			params.put("PotSellNumber", potSellNumber);
			params.put("ContactId", contactId);

			params.put("Questions", mCheckQuestionsFragment.getCommunication());

			params.put("ClientMark", (int) actRatingBar1.getRating());
			params.put("ExpandService", radioListener1.getValue());

			params.put("QualityServiceClientMark", (int) actRatingBar2.getRating());
			params.put("ManagementClientMark", (int) actRatingBar3.getRating());
			params.put("NextWorkClientMark", radioListener2.getValue());

			params.put("QualityServiceOurMark", (int) actRatingBar4.getRating());
			params.put("ManagementOurMark", (int) actRatingBar5.getRating());
			params.put("NextWorkOurMark", radioListener3.getValue());

			params.put("TaskList", tasks);

			params.put("Longitude", longitude);
			params.put("Latitude", latitude);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		if (NetworkHelper.isConnected(getActivity())) {
			showProgressDialog(R.string.actSend);

			FacilicomNetworkClient.postAct(getActivity(), params.toString(), new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					hideProgressDialog();
					SessionManager.getInstance().saveAct(null);

					Toast.makeText(getActivity(), R.string.actSendOk, Toast.LENGTH_LONG).show();

					getActivity().finish();
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					hideProgressDialog();
					Toast.makeText(getActivity(), R.string.actSendError, Toast.LENGTH_LONG).show();
				}
			});
		} else {
			JSONArray acts = SessionManager.getInstance().getActs();

			if (acts == null) {
				acts = new JSONArray();
			}

			acts.put(params);

			SessionManager.getInstance().saveActs(acts);
			SessionManager.getInstance().saveAct(null);

			getActivity().finish();
		}
	}

	private class RatingListener implements RatingBar.OnRatingBarChangeListener {
		String[] messages;
		FontTextView textView;

		RatingListener(FontTextView textView, String[] messages) {
			this.textView = textView;
			this.messages = messages;
		}

		@Override
		public void onRatingChanged(RatingBar bar, float v, boolean b) {
			textView.setText(messages[(int) v]);
		}
	}

	private class RadioListener {
		RadioButton actRadioButton1;
		RadioButton actRadioButton2;
		RadioButton actRadioButton3;

		RadioListener(View view, int actRadioButton1, int actRadioButton2, int actRadioButton3) {
			this.actRadioButton1 = (RadioButton) view.findViewById(actRadioButton1);
			this.actRadioButton2 = (RadioButton) view.findViewById(actRadioButton2);
			this.actRadioButton3 = (RadioButton) view.findViewById(actRadioButton3);

			// this.actRadioButton3.setChecked(true);

			final RadioListener _this = this;

			this.actRadioButton1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					_this.actRadioButton1.setChecked(true);
					_this.actRadioButton2.setChecked(false);
					_this.actRadioButton3.setChecked(false);
				}
			});

			this.actRadioButton2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					_this.actRadioButton1.setChecked(false);
					_this.actRadioButton2.setChecked(true);
					_this.actRadioButton3.setChecked(false);
				}
			});

			this.actRadioButton3.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					_this.actRadioButton1.setChecked(false);
					_this.actRadioButton2.setChecked(false);
					_this.actRadioButton3.setChecked(true);
				}
			});
		}

		public int getValue() {
			return actRadioButton1.isChecked() ? 1 : actRadioButton2.isChecked() ? 2 : 3;
		}

		public void setValue(int value) {
			actRadioButton1.setChecked(false);
			actRadioButton2.setChecked(false);
			actRadioButton3.setChecked(false);

			switch (value) {
				case 1:
					actRadioButton1.setChecked(true);
					break;

				case 2:
					actRadioButton2.setChecked(true);
					break;

				default:
					actRadioButton3.setChecked(true);
					break;
			}
		}
	}
}
