package ru.facilicom24.manager.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import database.ActAccount;
import database.ActAccountDao;
import database.ActContact;
import database.ActContactDao;
import database.ActReason;
import database.ActReasonDao;
import database.ActServiceType;
import database.ActServiceTypeDao;
import database.ActType;
import database.ActTypeDao;
import database.Appointment;
import database.AppointmentAttender;
import database.AppointmentAttenderDao;
import database.AppointmentDao;
import database.CleaningReason;
import database.CleaningReasonDao;
import database.Client;
import database.ClientDao;
import database.ContactAccount;
import database.ContactAccountDao;
import database.ContactClient;
import database.ContactClientDao;
import database.Country;
import database.CountryDao;
import database.DaoSession;
import database.FacilityUrgencyType;
import database.FacilityUrgencyTypeDao;
import database.Fake;
import database.FakeDao;
import database.Mobile;
import database.NomenclatureGroup;
import database.NomenclatureGroupDao;
import database.OrioArticle;
import database.OrioArticleDao;
import database.OrioType;
import database.OrioTypeDao;
import database.PotSell;
import database.PotSellDao;
import database.ServiceRequest;
import database.ServiceRequestDao;
import database.ServiceRequestFile;
import database.ServiceRequestFileDao;
import database.ServiceRequestLog;
import database.ServiceRequestLogDao;
import database.ServiceRequestServant;
import database.ServiceRequestServantDao;
import database.TSTask;
import database.TSTaskDao;
import database.TaskEmployee;
import database.TaskEmployeeDao;
import database.VidTask;
import database.VidTaskDao;
import database.WorkType;
import database.WorkTypeDao;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.CheckBlanksRepository;
import ru.facilicom24.manager.cache.CheckObjectsRepository;
import ru.facilicom24.manager.cache.CheckTypesRepository;
import ru.facilicom24.manager.cache.ChecksRepository;
import ru.facilicom24.manager.cache.DaoMobileRepository;
import ru.facilicom24.manager.cache.ElementMarksRepository;
import ru.facilicom24.manager.cache.PhotosRepository;
import ru.facilicom24.manager.cache.ZonesRepository;
import ru.facilicom24.manager.dialogs.AlertDialogFragment;
import ru.facilicom24.manager.dialogs.BaseDialogFragment;
import ru.facilicom24.manager.model.AuthorizationRequest;
import ru.facilicom24.manager.model.AuthorizationResponse;
import ru.facilicom24.manager.model.Check;
import ru.facilicom24.manager.model.CheckBlank;
import ru.facilicom24.manager.model.CheckObject;
import ru.facilicom24.manager.model.CheckRequest;
import ru.facilicom24.manager.model.CheckType;
import ru.facilicom24.manager.model.Element;
import ru.facilicom24.manager.model.ElementMark;
import ru.facilicom24.manager.model.Photo;
import ru.facilicom24.manager.model.Reason;
import ru.facilicom24.manager.model.Version;
import ru.facilicom24.manager.model.Zone;
import ru.facilicom24.manager.network.ApiRequestHelper;
import ru.facilicom24.manager.retrofit.ActContract;
import ru.facilicom24.manager.retrofit.CheckContract;
import ru.facilicom24.manager.retrofit.MobileContract;
import ru.facilicom24.manager.retrofit.RFService;
import ru.facilicom24.manager.retrofit.TaskContract;
import ru.facilicom24.manager.services.ImageUploadService;
import ru.facilicom24.manager.services.MockCheck;
import ru.facilicom24.manager.utils.Common;
import ru.facilicom24.manager.utils.NetworkHelper;
import ru.facilicom24.manager.utils.SessionManager;
import ru.facilicom24.manager.views.FontButton;

public class MainActivity
		extends BaseActivity
		implements
		Handler.Callback,
		View.OnClickListener,
		SwipeRefreshLayout.OnRefreshListener {

	static final String MAIN_ASYNC_TASK = "MainAsyncTask";

	List<MenuItem> menu;
	BroadcastReceiver broadcastReceiver;
	SwipeRefreshLayout swipeRefreshLayout;
	AuthorizationResponse authorizationResponse;

	boolean checkData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_main);

		((ImageView) findViewById(R.id.headerImageView)).setImageResource(FacilicomApplication.getLogoResId(this));

		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (ImageUploadService.IMAGE_UPLOAD_ONE_ACTION.equals(intent.getAction())) {
					Toast.makeText(context, R.string.uploading_photo, Toast.LENGTH_LONG).show();
				} else if (ImageUploadService.IMAGE_UPLOAD_STARTED_ACTION.equals(intent.getAction())) {
					Toast.makeText(context, R.string.uploading_photos, Toast.LENGTH_LONG).show();
				} else if (ImageUploadService.IMAGE_UPLOAD_FINISHED_ACTION.equals(intent.getAction())) {
					updateMessagesCount();
					Toast.makeText(context, R.string.sync_success, Toast.LENGTH_LONG).show();
				} else if (ImageUploadService.IMAGE_UPLOAD_FAILURE_ACTION.equals(intent.getAction())) {
					Toast.makeText(context, R.string.sync_failure, Toast.LENGTH_LONG).show();
				}
			}
		};

		menu = new ArrayList<>();

		menu.add(new MenuItem(R.string.facilityArrival, true));
		menu.add(new MenuItem(R.string.outlook_meeting, true));
		menu.add(new MenuItem(R.string.terrasoft_targets, true));
		menu.add(new MenuItem(R.string.facilicom_request, true));
		menu.add(new MenuItem(R.string.interactions, true));
		menu.add(new MenuItem(R.string.quality_checks, true));
		menu.add(new MenuItem(R.string.work_with_employees, true));

		((ListView) findViewById(R.id.main_menu)).setAdapter(new MainMenuAdapter());

		findViewById(R.id.lockImageButton).setOnClickListener(this);

		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
		swipeRefreshLayout.setOnRefreshListener(this);

		checkData = false;
		if (NetworkHelper.isConnected(this)) {
			RFService.log(Common.ANDROID_VERSION, Build.MODEL, Build.VERSION.RELEASE);
			loadDataFromNetworkIfNeeded();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!checkData) {
			versionCheck(new Runnable() {
				@Override
				public void run() {
					mockCheck();
					updateMessagesCount();
				}
			});
		}

		LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(ImageUploadService.IMAGE_UPLOAD_ONE_ACTION));
		LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(ImageUploadService.IMAGE_UPLOAD_STARTED_ACTION));
		LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(ImageUploadService.IMAGE_UPLOAD_FINISHED_ACTION));
		LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(ImageUploadService.IMAGE_UPLOAD_FAILURE_ACTION));
	}

	@Override
	public void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
		super.onPause();
	}

	void loadDataFromNetworkIfNeeded() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		final String username = preferences.getString(LoginActivity.USERNAME, null);
		String password = preferences.getString(LoginActivity.PASSWORD, null);

		if (username != null && password != null) {
			showProgressDialog(R.string.data_check);

			checkData = true;
			RFService.logon(new AuthorizationRequest(username, password), new Callback<AuthorizationResponse>() {
				@Override
				public void onResponse(Call<AuthorizationResponse> call, Response<AuthorizationResponse> response) {
					checkData = false;

					if (response != null) {
						authorizationResponse = response.body();

						if (authorizationResponse != null) {
							versionCheck(new Runnable() {
								@Override
								public void run() {
									new Handler(MainActivity.this).sendEmptyMessageDelayed(0, FacilicomApplication.TICK);
								}
							});
						} else {
							ResponseBody errorBody = response.errorBody();

							if (errorBody != null && errorBody.contentLength() > 0) {
								try {
									if (URLDecoder.decode(errorBody.string(), "UTF-8").toLowerCase().contains(getString(R.string.activity_password_change_key))) {
										hideProgressDialog();
										startActivityForResult(new Intent(MainActivity.this, PasswordChangeActivity.class)
														.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
														.putExtra(LoginActivity.USERNAME, username),
												LoginActivity.PASSWORD_CHANGE);
									} else {
										new Handler(MainActivity.this).sendEmptyMessageDelayed(0, FacilicomApplication.TICK);
									}
								} catch (Exception exception) {
									exception.printStackTrace();
									new Handler(MainActivity.this).sendEmptyMessageDelayed(0, FacilicomApplication.TICK);
								}
							} else {
								new Handler(MainActivity.this).sendEmptyMessageDelayed(0, FacilicomApplication.TICK);
							}
						}
					} else {
						new Handler(MainActivity.this).sendEmptyMessageDelayed(0, FacilicomApplication.TICK);
					}
				}

				@Override
				public void onFailure(Call<AuthorizationResponse> call, Throwable t) {
					checkData = false;
					new Handler(MainActivity.this).sendEmptyMessageDelayed(0, FacilicomApplication.TICK);
				}
			});
		} else {
			finish();
			goToActivity(LoginActivity.class);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case LoginActivity.PASSWORD_CHANGE: {
				}
				break;
			}
		}
	}

	@Override
	public boolean handleMessage(Message message) {
		hideProgressDialog();

		if (authorizationResponse != null) {
			SessionManager.getInstance().setToken(authorizationResponse.getToken());

			if (SessionManager.getInstance().getForceSync()) {
				SessionManager.getInstance().setForceSync(false);
				synchronize();
			} else {
				mockCheck();
				updateMessagesCount();
			}
		} else {
			finish();
			goToActivity(LoginActivity.class);
		}

		return false;
	}

	void versionCheck(final Runnable runnable) {
		if (NetworkHelper.isConnected(this)) {
			RFService.version(new Callback<Version>() {
				@Override
				public void onResponse(Call<Version> call, Response<Version> response) {
					boolean versionUpdate = false;

					if (response != null) {
						Version version = response.body();

						try {
							if (version != null && version.getVersion() > getPackageManager().getPackageInfo(getPackageName(), 0).versionCode) {
								versionUpdate = true;
								SessionManager.getInstance().setForceSync(true);
								showAlertDialog(R.id.update_dialog, R.string.message, R.string.update_title, R.string.btn_update);
							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}

					if (!versionUpdate && runnable != null) {
						runnable.run();
					}
				}

				@Override
				public void onFailure(Call<Version> call, Throwable t) {
					if (runnable != null) {
						runnable.run();
					}
				}
			});
		} else {
			if (runnable != null) {
				runnable.run();
			}
		}
	}

	void mockCheck() {
		ArrayList<Pair<String, String>> applications = MockCheck.getAllMockPermissionApplications(this);

		if (applications != null) {
			MockCheck.mockMessage(this, applications, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					moveTaskToBack(true);
				}
			});
		}
	}

	public void updateMessagesCount() {
		int count = 0;

		JSONArray acts = SessionManager.getInstance().getActs();
		if (acts != null && acts.length() > 0) {
			count += acts.length();
		}

		List<Check> checks = new ChecksRepository(this).getAll(Check.READY);
		if (checks != null && !checks.isEmpty()) {
			count += checks.size();
		}

		count += DaoMobileRepository.getCountSend(this);

		((TextView) findViewById(R.id.messages)).setText(count > 0
				? getString(R.string.messages_to_send, count)
				: getString(R.string.no_messages_for_sync)
		);
	}

	void synchronize() {
		System.gc();
		new MainAsyncTask().execute(null, null, null);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			// Reserved
			case R.id.lockImageButton: {
				startActivity(new Intent(this, PasswordChangeActivity.class));
			}
			break;

			default: {
				Class[] classes = {
						MapActivity.class,
						CalendarActivity.class,
						TaskMenuActivity.class,
						RequestMenuActivity.class,
						InteractionsActivity.class,
						QualityCheckActivity.class,
						HRMenuActivity.class
				};

				Integer tag = (Integer) view.getTag();
				if (tag != null && tag >= 0 && tag < classes.length) {
					startActivityForResult(new Intent(this, classes[tag]).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), tag);
				}
			}
		}
	}

	@Override
	public void onAlertPositiveBtnClicked(AlertDialogFragment dialog) {
		switch (dialog.getArguments().getInt(BaseDialogFragment.DIALOG_ID, 0)) {
			case R.id.update_dialog: {
				moveTaskToBack(true);

				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
				} catch (android.content.ActivityNotFoundException exception) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
				}
			}
			break;
		}
	}

	@Override
	public void onRefresh() {
		swipeRefreshLayout.setRefreshing(false);

		if (NetworkHelper.isConnected(this)) {
			synchronize();
		} else {
			showAlertDialog(R.id.alert_dialog, getString(R.string.errorLabel), getString(R.string.errorConnection));
		}
	}

	class MenuItem {
		int id;
		boolean enabled;

		MenuItem(int id, boolean enabled) {
			this.id = id;
			this.enabled = enabled;
		}
	}

	private class MainMenuAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return menu.size();
		}

		@Override
		public MenuItem getItem(int i) {
			return menu.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.main_menu_item, viewGroup, false);
			}

			view.setTag(i);

			MenuItem item = getItem(i);

			((FontButton) view).setText(item.id);
			view.setEnabled(item.enabled);

			view.setOnClickListener(MainActivity.this);

			return view;
		}
	}

	private class MainAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			showProgressDialog(R.string.sync);
		}

		@Override
		protected Void doInBackground(Void... voids) {
			final DaoSession daoSession = FacilicomApplication.getInstance().getDaoSession();

			// GreenDao

			try {
				Log.d(MAIN_ASYNC_TASK, "actTypes");
				final JsonArray actTypes = RFService.actTypes();

				if (actTypes != null) {
					final ActTypeDao actTypeDao = daoSession.getActTypeDao();

					actTypeDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : actTypes) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										actTypeDao.insert(new ActType(null,
												jsonObject.get("RecId").getAsInt(),
												jsonObject.get("Name").getAsString()
										));
									}
								}
							});
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "actReasons");
				final JsonArray actReasons = RFService.actReasons();

				if (actReasons != null) {
					final ActReasonDao actReasonDao = daoSession.getActReasonDao();

					actReasonDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : actReasons) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										actReasonDao.insert(new ActReason(null,
												jsonObject.get("RecId").getAsInt(),
												jsonObject.get("Name").getAsString()
										));
									}
								}
							});
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "clients");
				final JsonArray clients = RFService.clients();

				if (clients != null) {
					final ClientDao clientDao = daoSession.getClientDao();

					clientDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : clients) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										clientDao.insert(new Client(null,
												jsonObject.get("ClientId").getAsInt(),
												jsonObject.get("Name").getAsString(),
												jsonObject.get("Status").getAsInt()
										));
									}
								}
							});
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "accounts");
				final JsonArray accounts = RFService.accounts();

				if (accounts != null) {
					final ClientDao clientDao = daoSession.getClientDao();
					final ActAccountDao actAccountDao = daoSession.getActAccountDao();

					actAccountDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement clientJsonElement : accounts) {
										JsonObject clientJsonObject = clientJsonElement.getAsJsonObject();

										List<Client> clients = clientDao.queryBuilder()
												.where(ClientDao.Properties.ClientID.eq(clientJsonObject.get("ClientId").getAsInt()))
												.list();

										if (clients != null && !clients.isEmpty()) {
											for (JsonElement accountJsonElement : clientJsonObject.getAsJsonArray("Accounts")) {
												JsonObject accountJsonObject = accountJsonElement.getAsJsonObject();

												actAccountDao.insert(new ActAccount(null,
														accountJsonObject.get("DirectumId").getAsInt(),
														accountJsonObject.get("Name").getAsString(),
														accountJsonObject.get("Address").getAsString(),
														accountJsonObject.get("Status").getAsInt(),
														clients.get(0).getId()
												));
											}
										}
									}
								}
							});
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "clientContacts");
				final JsonArray clientContacts = RFService.clientContacts();

				if (clientContacts != null) {
					final ContactClientDao contactClientDao = daoSession.getContactClientDao();

					contactClientDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : clientContacts) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										contactClientDao.insert(new ContactClient(null,
												jsonObject.get("ClientId").getAsInt(),
												jsonObject.get("ContactId").getAsInt()
										));
									}
								}
							});
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "accountContacts");
				final JsonArray accountContacts = RFService.accountContacts();

				if (accountContacts != null) {
					final ContactAccountDao contactAccountDao = daoSession.getContactAccountDao();

					contactAccountDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : accountContacts) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										contactAccountDao.insert(new ContactAccount(null,
												jsonObject.get("DirectumId").getAsInt(),
												jsonObject.get("ContactId").getAsInt()
										));
									}
								}
							});
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "contacts");
				final JsonArray contacts = RFService.contacts();

				if (contacts != null) {
					final SparseArray<List<Long>> clientsSparseArray = new SparseArray<>();

					SparseArray<Long> clientsMap = new SparseArray<>();
					final List<Client> clients = daoSession.getClientDao().loadAll();

					if (clients != null && !clients.isEmpty()) {
						for (Client client : clients) {
							clientsMap.put(client.getClientID(), client.getId());
						}
					}

					final List<ContactClient> contactClients = daoSession.getContactClientDao().loadAll();

					if (contactClients != null && !contactClients.isEmpty()) {
						for (ContactClient contactClient : contactClients) {
							Long id = clientsMap.get(contactClient.getClientID());

							if (id != null) {
								List<Long> blueContactClients = clientsSparseArray.get(contactClient.getContactID());

								if (blueContactClients == null) {
									blueContactClients = new ArrayList<>();
									clientsSparseArray.put(contactClient.getContactID(), blueContactClients);
								}

								blueContactClients.add(id);
							}
						}
					}

					final SparseArray<List<Long>> accountsSparseArray = new SparseArray<>();

					SparseArray<Long> accountsMap = new SparseArray<>();
					final List<ActAccount> accounts = daoSession.getActAccountDao().loadAll();

					if (accounts != null && !accounts.isEmpty()) {
						for (ActAccount actAccount : accounts) {
							accountsMap.put(actAccount.getDirectumID(), actAccount.getId());
						}
					}

					final List<ContactAccount> contactAccounts = daoSession.getContactAccountDao().loadAll();

					if (contactAccounts != null && !contactAccounts.isEmpty()) {
						for (ContactAccount contactAccount : contactAccounts) {
							Long id = accountsMap.get(contactAccount.getDirectumID());

							if (id != null) {
								List<Long> blueContactAccounts = accountsSparseArray.get(contactAccount.getContactID());

								if (blueContactAccounts == null) {
									blueContactAccounts = new ArrayList<>();
									accountsSparseArray.put(contactAccount.getContactID(), blueContactAccounts);
								}

								blueContactAccounts.add(id);
							}
						}
					}

					final ActContactDao actContactDao = daoSession.getActContactDao();

					actContactDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : contacts) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										int contactId = jsonObject.get("Id").getAsInt();
										String contactName = jsonObject.get("Name").getAsString();

										List<Long> clients = clientsSparseArray.get(contactId);

										if (clients != null) {
											for (Long clientId : clients) {
												actContactDao.insert(new ActContact(null,
														contactId,
														contactName,
														clientId,
														null
												));
											}
										}

										List<Long> accounts = accountsSparseArray.get(contactId);

										if (accounts != null) {
											for (Long accountId : accounts) {
												actContactDao.insert(new ActContact(null,
														contactId,
														contactName,
														null,
														accountId
												));
											}
										}
									}
								}
							}
					);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "accountServiceTypes");
				final JsonArray accountServiceTypes = RFService.accountServiceTypes();

				if (accountServiceTypes != null) {
					final ActServiceTypeDao actServiceTypeDao = daoSession.getActServiceTypeDao();

					actServiceTypeDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : accountServiceTypes) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										actServiceTypeDao.insert(new ActServiceType(null,
												jsonObject.get("ServiceTypeId").getAsString(),
												jsonObject.get("Name").getAsString(),
												jsonObject.get("Status").getAsBoolean(),
												jsonObject.get("DirectumId").getAsLong(),
												jsonObject.get("ClientId").getAsLong()
										));
									}
								}
							});
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "nomenclatureGroups");
				final JsonArray nomenclatureGroups = RFService.nomenclatureGroups();

				if (nomenclatureGroups != null) {
					final NomenclatureGroupDao nomenclatureGroupDao = daoSession.getNomenclatureGroupDao();

					nomenclatureGroupDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : nomenclatureGroups) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										nomenclatureGroupDao.insert(new NomenclatureGroup(null,
												jsonObject.get("NomenclatureGroupID").getAsInt(),
												jsonObject.get("NomenclatureGroupCode").getAsString(),
												jsonObject.get("NomenclatureGroupName").getAsString()
										));
									}
								}
							});
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "taskTypes");
				final JsonArray taskTypes = RFService.taskTypes();

				if (taskTypes != null) {
					final VidTaskDao vidTaskDao = daoSession.getVidTaskDao();

					vidTaskDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : taskTypes) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										vidTaskDao.insert(new VidTask(null,
												jsonObject.get("ID").getAsInt(),
												jsonObject.get("Name").getAsString()
										));
									}
								}
							});
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "taskEmployees");
				final JsonArray taskEmployees = RFService.taskEmployees();

				if (taskEmployees != null) {
					final TaskEmployeeDao taskEmployeeDao = daoSession.getTaskEmployeeDao();

					taskEmployeeDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : taskEmployees) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										taskEmployeeDao.insert(new TaskEmployee(null,
												jsonObject.get("Login").getAsString(),
												jsonObject.get("Name").getAsString()
										));
									}
								}
							});
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "countries");
				final JsonArray countries = RFService.countries();

				if (countries != null) {
					final CountryDao countryDao = daoSession.getCountryDao();

					countryDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : countries) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										countryDao.insert(new Country(null,
												jsonObject.get("UID").getAsString(),
												jsonObject.get("Name").getAsString(),
												jsonObject.get("NeedPatentOrPermission").getAsInt()
										));
									}
								}
							});
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "tasks");
				final JsonArray tasks = RFService.tasks();

				if (tasks != null) {
					final TSTaskDao tsTaskDao = daoSession.getTSTaskDao();

					tsTaskDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : tasks) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										tsTaskDao.insert(new TSTask(null,
												jsonObject.get("ID").getAsString(),
												jsonObject.get("Title").getAsString(),
												jsonObject.get("DetailedResult").getAsString(),
												jsonObject.get("DueDate").getAsString(),
												jsonObject.get("ClientName").getAsString(),
												jsonObject.get("AccountName").getAsString(),
												jsonObject.get("AccountAddress").getAsString(),
												jsonObject.get("ContactName").getAsString(),
												jsonObject.get("Executor").getAsString(),
												jsonObject.get("Status").getAsString(),
												jsonObject.get("ControlComment").getAsString(),
												jsonObject.get("Author").getAsString()
										));
									}
								}
							});
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "appointments");
				final JsonArray appointments = RFService.appointments();

				if (appointments != null) {
					final AppointmentDao appointmentDao = daoSession.getAppointmentDao();
					final AppointmentAttenderDao appointmentAttenderDao = daoSession.getAppointmentAttenderDao();

					appointmentDao.deleteAll();
					appointmentAttenderDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : appointments) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										try {
											Appointment appointment = new Appointment(null,
													jsonObject.get("Subject").getAsString(),
													jsonObject.get("Body").getAsString(),
													jsonObject.get("Start").getAsString(),
													jsonObject.get("End").getAsString(),
													jsonObject.get("Place").getAsString(),
													jsonObject.get("ID").getAsString(),
													jsonObject.get("Status").getAsString(),
													jsonObject.get("UserIsOwner").getAsInt(),
													FacilicomApplication.dateTimeFormat2.parse(jsonObject.get("Date").getAsString())
											);

											appointmentDao.insert(appointment);

											JsonArray attenders = jsonObject.getAsJsonArray("Attenders");

											if (attenders != null && attenders.size() > 0) {
												for (JsonElement attenderJsonElement : attenders) {
													appointmentAttenderDao.insert(new AppointmentAttender(null,
															appointment.getId(),
															attenderJsonElement.getAsJsonObject().get("Email").getAsString()
													));
												}
											}
										} catch (Exception exception) {
											exception.printStackTrace();
										}
									}
								}
							});
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "potSells");
				final JsonArray potSells = RFService.potSells();

				if (potSells != null) {
					final PotSellDao potSellDao = daoSession.getPotSellDao();

					potSellDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : potSells) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										potSellDao.insert(new PotSell(
												null,
												jsonObject.get("ClientID").getAsInt(),
												jsonObject.get("Number").getAsInt(),
												jsonObject.get("Name").getAsString()
										));
									}
								}
							});
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "workTypes");
				final JsonArray workTypes = RFService.workTypes();

				if (workTypes != null) {
					final WorkTypeDao workTypeDao = daoSession.getWorkTypeDao();

					workTypeDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : workTypes) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										workTypeDao.insert(new WorkType(
												null,
												jsonObject.get("SystemGUID").getAsString(),
												jsonObject.get("SystemName").getAsString()
										));
									}
								}
							});
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "cleaningReasons");
				final JsonArray cleaningReasons = RFService.cleaningReasons();

				if (cleaningReasons != null) {
					final CleaningReasonDao cleaningReasonDao = daoSession.getCleaningReasonDao();

					cleaningReasonDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : cleaningReasons) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										cleaningReasonDao.insert(new CleaningReason(
												null,
												jsonObject.get("ReasonID").getAsInt(),
												jsonObject.get("ReasonName").getAsString()
										));
									}
								}
							});
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "facilityUrgencyTypes");
				final JsonArray facilityUrgencyTypes = RFService.facilityUrgencyTypes();

				if (facilityUrgencyTypes != null) {
					final FacilityUrgencyTypeDao facilityUrgencyTypeDao = daoSession.getFacilityUrgencyTypeDao();

					facilityUrgencyTypeDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : facilityUrgencyTypes) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										facilityUrgencyTypeDao.insert(new FacilityUrgencyType(
												null,
												jsonObject.get("ID").getAsInt(),
												jsonObject.get("UrgencyType").getAsString()
										));
									}
								}
							});
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "serviceRequests");
				final JsonObject serviceRequestsCountJsonObject = RFService.serviceRequestsCount();

				if (serviceRequestsCountJsonObject != null) {
					final ServiceRequestDao serviceRequestDao = daoSession.getServiceRequestDao();
					final ServiceRequestServantDao serviceRequestServantDao = daoSession.getServiceRequestServantDao();
					final ServiceRequestLogDao serviceRequestLogDao = daoSession.getServiceRequestLogDao();
					final ServiceRequestFileDao serviceRequestFileDao = daoSession.getServiceRequestFileDao();

					serviceRequestDao.deleteAll();
					serviceRequestServantDao.deleteAll();
					serviceRequestLogDao.deleteAll();
					serviceRequestFileDao.deleteAll();

					int serviceRequestsCount = serviceRequestsCountJsonObject.get("Count").getAsInt();

					int offset = 0;
					while (offset < serviceRequestsCount) {
						final JsonArray serviceRequests = RFService.serviceRequests(offset);

						if (serviceRequests == null) {
							break;
						}

						daoSession.runInTx(
								new Runnable() {
									public void run() {
										for (JsonElement jsonElement : serviceRequests) {
											JsonObject jsonObject = jsonElement.getAsJsonObject();

											JsonElement uidJsonElement = jsonObject.get("UID");

											ServiceRequest serviceRequest = new ServiceRequest(null,
													jsonObject.get("ID").getAsInt(),

													// ???
													uidJsonElement.isJsonNull() ? null : uidJsonElement.getAsString(),

													jsonObject.get("DueDate").getAsString(),
													jsonObject.get("CreatedOn").getAsString(),
													jsonObject.get("FacilityName").getAsString(),
													jsonObject.get("FacilityAddress").getAsString(),
													jsonObject.get("UrgencyType").getAsString(),
													jsonObject.get("Status").getAsString(),
													jsonObject.get("Content").getAsString(),
													jsonObject.get("ServiceTypeName").getAsString(),
													jsonObject.get("CanExecute").getAsInt(),
													jsonObject.get("NeedEvaluate").getAsInt(),
													jsonObject.get("Mine").getAsInt(),
													null
											);

											serviceRequestDao.insert(serviceRequest);

											JsonArray servants = jsonObject.getAsJsonArray("Servants");

											if (servants != null && servants.size() > 0) {
												for (JsonElement servantJsonElement : servants) {
													serviceRequestServantDao.insert(new ServiceRequestServant(
															null,
															serviceRequest.getId(),

															servantJsonElement.getAsString()
													));
												}
											}

											JsonArray history = jsonObject.getAsJsonArray("History");

											if (history != null && history.size() > 0) {
												for (JsonElement historyJsonElement : history) {
													JsonObject historyJsonObject = historyJsonElement.getAsJsonObject();

													serviceRequestLogDao.insert(new ServiceRequestLog(
															null,
															serviceRequest.getId(),

															historyJsonObject.get("StatusSetOn").getAsString(),
															historyJsonObject.get("Status").getAsString(),
															historyJsonObject.get("StatusSetByFullName").getAsString(),
															historyJsonObject.get("Comment").getAsString()
													));
												}
											}

											JsonArray files = jsonObject.getAsJsonArray("Files");

											if (files != null && files.size() > 0) {
												for (JsonElement fileJsonElement : files) {
													JsonObject file = fileJsonElement.getAsJsonObject();

													serviceRequestFileDao.insert(new ServiceRequestFile(
															null,
															serviceRequest.getId(),

															file.get("ServiceRequestFileID").getAsInt(),
															file.get("Type").getAsInt(),
															file.get("Ext").getAsString()
													));
												}
											}
										}
									}
								});

						offset += serviceRequests.size();
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "fakes");
				final JsonArray fakes = RFService.fakes();

				if (fakes != null) {
					final FakeDao fakeDao = daoSession.getFakeDao();

					fakeDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : fakes) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										fakeDao.insert(new Fake(
												null,
												jsonObject.get("Name").getAsString(),
												jsonObject.get("Pattern").getAsString()
										));
									}
								}
							});

					MockCheck.initializeApplications(MainActivity.this);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "orioTypes");
				final JsonArray orioTypes = RFService.orioTypes();

				if (orioTypes != null) {
					final OrioTypeDao orioTypeDao = daoSession.getOrioTypeDao();

					orioTypeDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : orioTypes) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										orioTypeDao.insert(new OrioType(null,
												jsonObject.get("UID").getAsString(),
												jsonObject.get("Name").getAsString()
										));
									}
								}
							});
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "orioArticles");
				final JsonArray orioArticles = RFService.orioArticles();

				if (orioArticles != null) {
					final OrioArticleDao orioArticleDao = daoSession.getOrioArticleDao();

					orioArticleDao.deleteAll();

					daoSession.runInTx(
							new Runnable() {
								public void run() {
									for (JsonElement jsonElement : orioArticles) {
										JsonObject jsonObject = jsonElement.getAsJsonObject();

										orioArticleDao.insert(new OrioArticle(null,
												jsonObject.get("UID").getAsString(),
												jsonObject.get("Name").getAsString()
										));
									}
								}
							});
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			// ORMLite

			ZonesRepository zonesRepository = new ZonesRepository(MainActivity.this);
			ChecksRepository checksRepository = new ChecksRepository(MainActivity.this);
			PhotosRepository photosRepository = new PhotosRepository(MainActivity.this);
			CheckTypesRepository checkTypesRepository = new CheckTypesRepository(MainActivity.this);
			CheckBlanksRepository checkBlanksRepository = new CheckBlanksRepository(MainActivity.this);
			CheckObjectsRepository checkObjectsRepository = new CheckObjectsRepository(MainActivity.this);
			ElementMarksRepository elementMarksRepository = new ElementMarksRepository(MainActivity.this);

			try {
				Log.d(MAIN_ASYNC_TASK, "photos");
				List<Photo> photos = photosRepository.getAll();

				if (photos != null) {
					while (!photos.isEmpty()) {
						Photo photo = photos.remove(0);

						if (photo.getCheck() == null || !new File(Uri.parse(photo.getUri()).getPath()).exists()) {
							photosRepository.delete(photo);
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "checkUpdate (Postpone)");
				List<Check> readyChecks = checksRepository.getAll(Check.READY);

				if (readyChecks != null) {
					while (!readyChecks.isEmpty()) {
						Check check = readyChecks.remove(0);

						if (check.getCheckType() != null
								&& check.getCheckObject() != null
								&& check.getCheckBlank() != null

								&& check.getMarks() != null
								&& !check.getMarks().isEmpty()) {

							CheckRequest checkRequest = ApiRequestHelper.buildCheckRequest(MainActivity.this, check);

							Response<Void> response = RFService.checkUpdate(check.getCheckId(), new CheckContract(
									checkRequest.getAccountId(),
									checkRequest.getFormId(),
									checkRequest.getCreatedOn(),
									checkRequest.getLongitude(),
									checkRequest.getLatitude(),
									checkRequest.getComments(),
									checkRequest.getMarks()
							));

							if (response != null && response.errorBody() == null) {
								if (check.getPhotos() == null || check.getPhotos().isEmpty()) {
									if (RFService.checkEnd(check.getCheckId()) != null) {
										checksRepository.delete(check);
									}
								}
							}
						} else {
							checksRepository.delete(check);
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "mobileRequestUpdate (Postpone)");
				List<Mobile> mobiles = DaoMobileRepository.getAllSend(MainActivity.this);

				if (mobiles != null && !mobiles.isEmpty()) {
					while (!mobiles.isEmpty()) {
						Mobile mobile = mobiles.remove(0);

						if (mobile.getJson() != null && !mobile.getJson().isEmpty()) {
							Response<Void> response = RFService.mobileRequestUpdate(new GsonBuilder().create().fromJson(mobile.getJson(), MobileContract.class));

							if (response != null && response.errorBody() == null) {
								DaoMobileRepository.delete(MainActivity.this, mobile);
							}
						} else {
							DaoMobileRepository.delete(MainActivity.this, mobile);
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "act (Postpone)");
				JSONArray acts = SessionManager.getInstance().getActs();

				if (acts != null && acts.length() > 0) {
					JSONArray failActs = new JSONArray();

					for (int index = 0; index < acts.length(); index++) {
						JSONObject act = acts.getJSONObject(index);

						JSONArray jsonArray = act.getJSONArray("TaskList");
						ArrayList<TaskContract> taskList = new ArrayList<>();

						for (index = 0; index < jsonArray.length(); index++) {
							JSONObject task = jsonArray.getJSONObject(index);

							taskList.add(new TaskContract(
									task.optString("ActUID"),
									task.optInt("ClientID"),
									task.optInt("AccountID"),
									task.optString("ServiceTypeUID"),
									task.optInt("ContactID"),
									task.optString("TaskUID"),
									task.optInt("TypeID"),
									task.optInt("VidID"),
									task.optString("ExecutorLogin"),
									task.optString("DueDate"),
									task.optString("Subject"),
									task.optString("Text"),
									task.optString("Result"),
									task.optString("Status"),
									task.optString("CreatedOn"),
									task.optInt("PotSellNumber"),
									""
							));
						}

						ActContract actContract = new ActContract(
								act.optString("ID"),
								act.optString("CreatedOn"),
								act.optInt("ActTypeId"),
								act.optInt("ReasonId"),
								act.optInt("ClientId"),
								act.optInt("AccountId"),
								act.optString("ServiceTypeId"),
								act.optInt("PotSellNumber"),
								act.optInt("ContactId"),
								act.optString("Questions"),
								act.optInt("ClientMark"),
								act.optInt("ExpandService"),
								act.optInt("QualityServiceClientMark"),
								act.optInt("ManagementClientMark"),
								act.optInt("NextWorkClientMark"),
								act.optInt("QualityServiceOurMark"),
								act.optInt("ManagementOurMark"),
								act.optInt("NextWorkOurMark"),
								act.optDouble("Longitude"),
								act.optDouble("Latitude"),
								taskList
						);

						Response<Void> response = RFService.act(actContract);

						if (response == null || response.errorBody() != null) {
							failActs.put(act);
						}
					}

					SessionManager.getInstance().saveActs(failActs.length() > 0 ? failActs : null);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			//

			Log.d(MAIN_ASYNC_TASK, "ormLite (Prepare)");

			boolean elementMarksCheck = false;

			List<Check> newChecks = checksRepository.getAll(Check.NEW);
			List<Check> readyChecks = checksRepository.getAll(Check.READY);

			HashSet<String> excludeCheckTypes = new HashSet<>();
			HashSet<Integer> excludeCheckObjects = new HashSet<>();
			HashSet<Integer> excludeCheckBlanks = new HashSet<>();

			if (newChecks != null && !newChecks.isEmpty()) {
				Check check = newChecks.get(0);

				if (check.getCheckType() != null || check.getCheckObject() != null || check.getCheckBlank() != null) {
					excludeCheckTypes.add(check.getCheckType().getCheckId());
					excludeCheckObjects.add(check.getCheckObject().getCheckObjectId());
					excludeCheckBlanks.add(check.getCheckBlank().getCheckBlankId());

					elementMarksCheck = true;
				} else {
					checksRepository.delete(check);
				}
			}

			if (readyChecks != null && !readyChecks.isEmpty()) {
				for (Check check : readyChecks) {
					if (check.getCheckType() != null || check.getCheckObject() != null || check.getCheckBlank() != null) {
						excludeCheckTypes.add(check.getCheckType().getCheckId());
						excludeCheckObjects.add(check.getCheckObject().getCheckObjectId());
						excludeCheckBlanks.add(check.getCheckBlank().getCheckBlankId());

						elementMarksCheck = true;
					} else {
						checksRepository.delete(check);
					}
				}
			}

			if (elementMarksCheck) {
				elementMarksRepository.elementMarksCheck();
			} else {
				elementMarksRepository.deleteAll();
			}

			//

			try {
				Log.d(MAIN_ASYNC_TASK, "serviceTypes");
				JsonArray serviceTypes = RFService.serviceTypes();

				if (serviceTypes != null) {
					ArrayList<CheckType> checkTypes = new ArrayList<>();

					for (JsonElement jsonElement : serviceTypes) {
						JsonObject jsonObject = jsonElement.getAsJsonObject();

						String checkTypeId = jsonObject.get("Id").getAsString();
						if (!excludeCheckTypes.contains(checkTypeId)) {
							checkTypes.add(new CheckType(
									checkTypeId,
									jsonObject.get("Name").getAsString()
							));
						}
					}

					if (checkTypesRepository.bulkCreate(checkTypes, excludeCheckTypes) != checkTypes.size()) {
						throw new Exception("bulkCreate");
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "accounts2");
				JsonArray accounts = RFService.accounts2();

				if (accounts != null) {
					ArrayList<CheckObject> checkObjects = new ArrayList<>();

					for (JsonElement jsonElement : accounts) {
						JsonObject jsonObject = jsonElement.getAsJsonObject();

						int checkObjectId = jsonObject.get("Id").getAsInt();
						if (!excludeCheckObjects.contains(checkObjectId)) {
							String name = jsonObject.get("Name").getAsString();
							String address = jsonObject.get("Address").getAsString();

							for (JsonElement serviceTypeId : jsonObject.get("ServiceTypeIds").getAsJsonArray()) {
								checkObjects.add(new CheckObject(
										checkObjectId,
										name,
										address,
										serviceTypeId.getAsString()
								));
							}
						}
					}

					if (checkObjectsRepository.bulkCreate(checkObjects, excludeCheckObjects) != checkObjects.size()) {
						throw new Exception("bulkCreate");
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "forms");
				List<CheckType> checkTypes = checkTypesRepository.getAll();

				if (checkTypes != null && !checkTypes.isEmpty()) {
					List<CheckBlank> checkBlanks = new ArrayList<>();

					boolean getTransaction = true;
					for (CheckType checkType : checkTypes) {
						JsonArray forms = RFService.forms(checkType.getCheckId());

						if (forms != null) {
							for (JsonElement jsonElement : forms) {
								JsonObject jsonObject = jsonElement.getAsJsonObject();

								int checkBlankId = jsonObject.get("Id").getAsInt();
								if (!excludeCheckBlanks.contains(checkBlankId)) {
									checkBlanks.add(new CheckBlank(
											checkBlankId,
											jsonObject.get("Name").getAsString(),
											checkType.getCheckId()
									));
								}
							}
						} else {
							getTransaction = false;
							break;
						}
					}

					if (getTransaction && checkBlanksRepository.bulkCreate(checkBlanks, excludeCheckBlanks) != checkBlanks.size()) {
						throw new Exception("bulkCreate");
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			try {
				Log.d(MAIN_ASYNC_TASK, "formDescriptions");
				List<CheckBlank> checkBlanks = checkBlanksRepository.getAll();

				if (checkBlanks != null && !checkBlanks.isEmpty()) {
					List<ElementMark> elementMarks = elementMarksRepository.getAll();

					zonesRepository.deleteAll();

					ArrayList<Zone> zoneList = new ArrayList<>();

					boolean getTransaction = true;
					for (CheckBlank checkBlank : checkBlanks) {
						JsonObject formDescriptions = RFService.formDescriptions(checkBlank.getCheckBlankId());

						if (formDescriptions != null) {
							JsonArray zones = formDescriptions.getAsJsonArray("Zones");

							if (zones != null && zones.size() > 0) {
								for (JsonElement zoneJsonElement : zones) {
									JsonObject zoneJsonObject = zoneJsonElement.getAsJsonObject();

									ArrayList<Element> elementList = new ArrayList<>();
									JsonArray elements = zoneJsonObject.getAsJsonArray("Elements");

									if (elements != null && elements.size() > 0) {
										for (JsonElement elementJsonElement : elements) {
											JsonObject elementJsonObject = elementJsonElement.getAsJsonObject();

											ArrayList<Reason> reasonList = new ArrayList<>();
											JsonArray reasons = elementJsonObject.getAsJsonArray("Reasons");

											if (reasons != null && reasons.size() > 0) {
												for (JsonElement reasonJsonElement : reasons) {
													reasonList.add(new Reason(reasonJsonElement.getAsString()));
												}
											}

											int elementId = elementJsonObject.get("Id").getAsInt();

											Element element = new Element(
													elementId,
													elementJsonObject.get("Name").getAsString(),
													elementJsonObject.get("Required").getAsBoolean(),
													reasonList
											);

											elementList.add(element);

											for (ElementMark elementMark : elementMarks) {
												if (elementMark.getElement().getElementId() == elementId) {
													elementMark.setElement(element);
												}
											}
										}
									}

									zoneList.add(new Zone(
											checkBlank,
											zoneJsonObject.get("Name").getAsString(),
											elementList
									));
								}
							}
						} else {
							getTransaction = false;
							break;
						}
					}

					if (getTransaction) {
						for (Zone zone : zoneList) {
							for (Element element : zone.getElements()) {
								element.setZone(zone);
								for (Reason reason : element.getDbReasons()) {
									reason.setElement(element);
								}
							}
						}

						if (zonesRepository.bulkCreate(MainActivity.this, zoneList, elementMarks) != zoneList.size()) {
							throw new Exception("bulkCreate");
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void _void) {
			hideProgressDialog();

			List<Check> readyChecks = new ChecksRepository(MainActivity.this).getAll(Check.READY);

			if (readyChecks != null && !readyChecks.isEmpty()) {
				for (Check check : readyChecks) {
					if (check.getPhotos() != null && !check.getPhotos().isEmpty()) {
						ImageUploadService.upload(MainActivity.this, ImageUploadService.READY_CHECKS_PHOTO);
						break;
					}
				}
			}

			loadDataFromNetworkIfNeeded();
		}
	}
}
