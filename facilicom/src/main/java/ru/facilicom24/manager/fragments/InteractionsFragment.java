package ru.facilicom24.manager.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import database.ActAccount;
import database.Client;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.activities.CheckActAccountActivity;
import ru.facilicom24.manager.activities.CheckActContactActivity;
import ru.facilicom24.manager.activities.CheckActReasonActivity;
import ru.facilicom24.manager.activities.CheckActServiceTypeActivity;
import ru.facilicom24.manager.activities.CheckActTypeActivity;
import ru.facilicom24.manager.activities.CheckClientActivity;
import ru.facilicom24.manager.activities.ConfirmationActivity;
import ru.facilicom24.manager.activities.PotSellActivity;
import ru.facilicom24.manager.cache.DaoActAccountRepository;
import ru.facilicom24.manager.dialogs.AlertDialogFragment;
import ru.facilicom24.manager.dialogs.CategoryPotSellDialog;
import ru.facilicom24.manager.utils.SessionManager;

public class InteractionsFragment
		extends BaseFragment
		implements
		View.OnClickListener,
		AdapterView.OnItemClickListener,
		AlertDialogFragment.IAlertDialogListener {

	static final public String ACT_TYPE_ID = "ActTypeId";
	static final public String REASON_ID = "ReasonId";
	static final public String CLIENT_ID = "ClientId";
	static final public String ACCOUNT_ID = "AccountId";
	static final public String SERVICE_TYPE_ID = "ServiceTypeId";
	static final public String CONTACT_ID = "ContactId";
	static final public String CATEGORY_POTSELL_DIALOG = "CATEGORY_POTSELL_DIALOG";

	static final int CHOOSE_TYPE = 1000;
	static final int CHOOSE_REASON = 1001;
	static final int CHOOSE_CLIENT = 1002;
	static final int CHOOSE_OBJECT = 1003;
	static final int CHOOSE_SERVICE = 1004;
	static final int CHOOSE_CONTACT = 1005;
	static final int CHOOSE_POTSELL = 1006;
	static final int CHOOSE_BACK = 10000;
	static final int SERVICE_TYPE_VISIT = 22;
	static final int SERVICE_TYPE_CALL = 23;

	final static int RESET_CLIENT = 0b1000;
	final static int RESET_ACCOUNT = 0b0100;
	final static int RESET_ACT_TYPE_POT_SELL_NUMBER = 0b0010;
	final static int RESET_CONTACT = 0b0001;

	final static int MENU_TYPE = 0;
	final static int MENU_REASON = 1;
	final static int MENU_CLIENT = 2;
	final static int MENU_ACCOUNT = 3;
	final static int MENU_CATEGORY = 4;
	final static int MENU_CONTACT = 5;

	JSONObject parameters;
	List<InteractionItem> menu;
	InteractionAdapter mAdapter;

	public InteractionsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_interactions, container, false);

		if (SessionManager.getInstance().getAct() == null) {
			menu = new ArrayList<>();

			menu.add(new InteractionItem("Тип", "Выбрать"));
			menu.add(new InteractionItem("Причина", "Выбрать"));
			menu.add(new InteractionItem("Клиент", "Выбрать"));
			menu.add(new InteractionItem("Объект", "Выбрать"));
			menu.add(new InteractionItem("Вид деятельности /\nПродажа", "Выбрать"));
			menu.add(new InteractionItem("Контактное лицо", "Выбрать"));

			view.findViewById(R.id.apply).setOnClickListener(this);

			Date dateTimeNow = new Date();
			((TextView) view.findViewById(R.id.date)).setText(FacilicomApplication.dateTimeFormat4.format(dateTimeNow));

			parameters = new JSONObject();

			try {
				parameters.put("CreatedOn", FacilicomApplication.dateTimeFormat10.format(dateTimeNow));
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			mAdapter = new InteractionAdapter();

			//

			ListView listView = (ListView) view.findViewById(R.id.main_menu);

			listView.setAdapter(mAdapter);
			listView.setOnItemClickListener(this);
		} else {
			startActivityForResult(new Intent(getActivity(), ConfirmationActivity.class)
							.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
					CHOOSE_BACK);
		}

		return view;
	}

	@Override
	public void onClick(View view) {
		if (parameters.optInt(SERVICE_TYPE_ID) == 0) {
			showAlertDialog(R.id.alert_dialog, "Ошибка", "Не заполнено поле - Тип");
			hideProgressDialog();
			return;
		}

		if (parameters.optInt(REASON_ID) == 0) {
			showAlertDialog(R.id.alert_dialog, "Ошибка", "Не заполнено поле - Причина");
			hideProgressDialog();
			return;
		}

		int clientId = parameters.optInt(CLIENT_ID);
		int accountId = parameters.optInt(ACCOUNT_ID);

		if (clientId == 0 && accountId == 0) {
			showAlertDialog(R.id.alert_dialog, "Ошибка", "Не заполнены поля Клиент или Объект");
			hideProgressDialog();
			return;
		}

		String actTypeId = parameters.optString(ACT_TYPE_ID);

		if ((actTypeId == null || actTypeId.isEmpty())
				&& parameters.optInt(PotSellActivity.POT_SELL_NUMBER) == 0) {

			showAlertDialog(R.id.alert_dialog, "Ошибка", "Не заполнено поле - Вид деятельности / Продажа");
			hideProgressDialog();
			return;
		}

		if (parameters.optInt(CONTACT_ID) == 0) {
			showAlertDialog(R.id.alert_dialog, "Ошибка", "Не заполнено поле - Контактное лицо");
			hideProgressDialog();
			return;
		}

		SessionManager.getInstance().saveAct(parameters.toString());

		startActivityForResult(new Intent(getActivity(), ConfirmationActivity.class)
						.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
				CHOOSE_BACK);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void onBackPressed() {
		getActivity().finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (data != null) {
				switch (requestCode) {
					case CHOOSE_TYPE: {
						try {
							parameters.put(SERVICE_TYPE_ID, data.getIntExtra(ACT_TYPE_ID, 0));
						} catch (Exception exception) {
							exception.printStackTrace();
						}

						menu.get(0).setTitle(data.getStringExtra("Title"));

						reset(RESET_CLIENT | RESET_ACCOUNT | RESET_ACT_TYPE_POT_SELL_NUMBER | RESET_CONTACT);
						mAdapter.notifyDataSetChanged();
					}
					break;

					case CHOOSE_REASON: {
						try {
							parameters.put(REASON_ID, data.getIntExtra(REASON_ID, 0));
						} catch (Exception exception) {
							exception.printStackTrace();
						}

						menu.get(1).setTitle(data.getStringExtra("Title"));

						reset(RESET_CLIENT | RESET_ACCOUNT | RESET_ACT_TYPE_POT_SELL_NUMBER | RESET_CONTACT);
						mAdapter.notifyDataSetChanged();
					}
					break;

					case CHOOSE_CLIENT: {
						try {
							parameters.put(CLIENT_ID, data.getIntExtra(CLIENT_ID, 0));
						} catch (Exception exception) {
							exception.printStackTrace();
						}

						menu.get(2).setTitle(data.getStringExtra("Title"));

						reset(RESET_ACCOUNT | RESET_ACT_TYPE_POT_SELL_NUMBER | RESET_CONTACT);
						mAdapter.notifyDataSetChanged();
					}
					break;

					case CHOOSE_OBJECT: {
						int accountId = data.getIntExtra(ACCOUNT_ID, 0);
						int serviceTypeId = parameters.optInt(SERVICE_TYPE_ID);

						if (serviceTypeId == 0 || serviceTypeId == SERVICE_TYPE_CALL
								|| (serviceTypeId == SERVICE_TYPE_VISIT && FacilicomApplication.checkInRule(accountId, FacilicomApplication.isMinsk(getActivity()) ? 0 : 2))) {

							List<ActAccount> accounts = DaoActAccountRepository.getActAccountForDirectumId(getActivity(), accountId);

							if (accounts != null && !accounts.isEmpty()) {
								Client client = accounts.get(0).getClient();

								if (client != null) {
									try {
										parameters.put(CLIENT_ID, client.getClientID());
									} catch (Exception exception) {
										exception.printStackTrace();
									}

									menu.get(2).setTitle(client.getName());
								}
							}

							try {
								parameters.put(ACCOUNT_ID, accountId);
							} catch (Exception exception) {
								exception.printStackTrace();
							}

							menu.get(3).setTitle(TextUtils.concat(data.getStringExtra("Title"), ", ", data.getStringExtra("Address")).toString());

							reset(RESET_ACT_TYPE_POT_SELL_NUMBER | RESET_CONTACT);
							mAdapter.notifyDataSetChanged();
						} else {
							showAlertDialog(R.id.alert_dialog, getString(R.string.mobile_alert_title), getString(R.string.confirmation_location_act_message));
						}
					}
					break;

					case CHOOSE_SERVICE: {
						parameters.remove(PotSellActivity.POT_SELL_NUMBER);

						try {
							parameters.put(ACT_TYPE_ID, data.getStringExtra(ACT_TYPE_ID));
						} catch (Exception exception) {
							exception.printStackTrace();
						}

						menu.get(4).setTitle(data.getStringExtra("Title"));
						mAdapter.notifyDataSetChanged();
					}
					break;

					case CHOOSE_POTSELL: {
						parameters.remove(ACT_TYPE_ID);

						try {
							parameters.put(PotSellActivity.POT_SELL_NUMBER, data.getIntExtra(PotSellActivity.POT_SELL_NUMBER, 0));
						} catch (Exception exception) {
							exception.printStackTrace();
						}

						menu.get(4).setTitle(data.getStringExtra(PotSellActivity.POT_SELL_NAME));
						mAdapter.notifyDataSetChanged();
					}
					break;

					case CHOOSE_CONTACT: {
						try {
							parameters.put(CONTACT_ID, data.getIntExtra(CONTACT_ID, 0));
						} catch (Exception exception) {
							exception.printStackTrace();
						}

						menu.get(5).setTitle(data.getStringExtra("Title"));
						mAdapter.notifyDataSetChanged();
					}
					break;
				}
			}
		} else {
			if (requestCode == CHOOSE_BACK) {
				onBackPressed();
			}
		}
	}

	void reset(int pattern) {
		if ((pattern & RESET_CLIENT) == RESET_CLIENT) {
			try {
				parameters.put(CLIENT_ID, 0);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			menu.get(2).setTitle("Выбрать");
		}

		if ((pattern & RESET_ACCOUNT) == RESET_ACCOUNT) {
			try {
				parameters.put(ACCOUNT_ID, 0);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			menu.get(3).setTitle("Выбрать");
		}

		if ((pattern & RESET_ACT_TYPE_POT_SELL_NUMBER) == RESET_ACT_TYPE_POT_SELL_NUMBER) {
			parameters.remove(ACT_TYPE_ID);
			parameters.remove(PotSellActivity.POT_SELL_NUMBER);

			menu.get(4).setTitle("Выбрать");
		}

		if ((pattern & RESET_CONTACT) == RESET_CONTACT) {
			try {
				parameters.put(CONTACT_ID, 0);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			menu.get(5).setTitle("Выбрать");
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
			case MENU_TYPE: {
				startActivityForResult(new Intent(getActivity(), CheckActTypeActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), CHOOSE_TYPE);
			}
			break;

			case MENU_REASON: {
				startActivityForResult(new Intent(getActivity(), CheckActReasonActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), CHOOSE_REASON);
			}
			break;

			case MENU_CLIENT: {
				menuClient();
			}
			break;

			case MENU_ACCOUNT: {
				menuAccount();
			}
			break;

			case MENU_CATEGORY: {
				menuCategory();
			}
			break;

			case MENU_CONTACT: {
				menuContact();
			}
			break;
		}
	}

	void menuClient() {
		Intent clientIntent = new Intent(getActivity(), CheckClientActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		int accountId = parameters.optInt(ACCOUNT_ID, 0);

		if (accountId > 0) {
			clientIntent.putExtra(ACCOUNT_ID, accountId);
		}

		int reasonId = parameters.optInt(REASON_ID);
		for (int _reasonId : FacilicomApplication.ClientsAndAccountsAllowShowAllReasons) {
			if (_reasonId == reasonId) {
				clientIntent.putExtra(CheckClientActivity.SHOW_ALL, true);
				break;
			}
		}

		startActivityForResult(clientIntent, CHOOSE_CLIENT);
	}

	void menuAccount() {
		Intent actAccountIntent = new Intent(getActivity(), CheckActAccountActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		int clientId = parameters.optInt(CLIENT_ID, 0);

		if (clientId > 0) {
			actAccountIntent.putExtra(CLIENT_ID, clientId);
		}

		int reasonId = parameters.optInt(REASON_ID);
		for (int _reasonId : FacilicomApplication.ClientsAndAccountsAllowShowAllReasons) {
			if (_reasonId == reasonId) {
				actAccountIntent.putExtra(CheckClientActivity.SHOW_ALL, true);
				break;
			}
		}

		startActivityForResult(actAccountIntent, CHOOSE_OBJECT);
	}

	void menuCategory() {
		final int clientId = parameters.optInt(CLIENT_ID, 0);
		final int accountId = parameters.optInt(ACCOUNT_ID, 0);

		if (clientId == 0 && accountId == 0) {
			showAlertDialog(R.id.alert_dialog, "Ошибка", "Не заполнены поля Клиент или Объект");
			hideProgressDialog();
			return;
		}

		CategoryPotSellDialog.newInstance(new CategoryPotSellDialog.IListener() {

			@Override
			public void onCategory() {
				Intent actServiceIntent = new Intent(getActivity(), CheckActServiceTypeActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

				if (clientId > 0) {
					if ((parameters.optInt(REASON_ID, 0) != 7)
							&& (parameters.optInt(REASON_ID, 0) != 20)
							&& (parameters.optInt(REASON_ID, 0) != 21)) {

						actServiceIntent.putExtra("Status", false);
					} else {
						actServiceIntent.putExtra("Status", true);
					}

					actServiceIntent.putExtra(CLIENT_ID, clientId);
				}

				if (accountId > 0) {
					actServiceIntent.putExtra(ACCOUNT_ID, accountId);
				}

				startActivityForResult(actServiceIntent, CHOOSE_SERVICE);
			}

			@Override
			public void onPotSell() {
				startActivityForResult(new Intent(getActivity(), PotSellActivity.class)
								.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
								.putExtra(CLIENT_ID, clientId),
						CHOOSE_POTSELL);
			}
		}).show(getFragmentManager(), CATEGORY_POTSELL_DIALOG);
	}

	void menuContact() {
		int clientId = parameters.optInt(CLIENT_ID, 0);
		int accountId = parameters.optInt(ACCOUNT_ID, 0);

		if (clientId == 0 && accountId == 0) {
			showAlertDialog(R.id.alert_dialog, "Ошибка", "Не заполнены поля Клиент или Объект");
			hideProgressDialog();
			return;
		}

		Intent actContactIntent = new Intent(getActivity(), CheckActContactActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		if (clientId > 0) {
			actContactIntent.putExtra(CLIENT_ID, clientId);
		}

		if (accountId > 0) {
			actContactIntent.putExtra(ACCOUNT_ID, accountId);
		}

		startActivityForResult(actContactIntent, CHOOSE_CONTACT);
	}

	private class InteractionItem {

		String mTitleResId;
		String mOptionResId;

		InteractionItem(String title, String option) {
			mTitleResId = title;
			mOptionResId = option;
		}

		public void setTitle(String title) {
			mOptionResId = title;
		}
	}

	private class InteractionAdapter extends BaseAdapter {
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

			TextView title = (TextView) view.findViewById(R.id.interaction_title);
			TextView option = (TextView) view.findViewById(R.id.interaction_option);

			view.setTag(i);

			InteractionItem menuItem = getItem(i);

			title.setText(menuItem.mTitleResId);
			option.setText(menuItem.mOptionResId);

			return view;
		}
	}
}
