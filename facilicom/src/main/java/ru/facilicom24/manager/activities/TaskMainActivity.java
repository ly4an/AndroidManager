package ru.facilicom24.manager.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import database.ActAccount;
import database.ActContact;
import database.ActServiceType;
import database.Client;
import database.PotSell;
import database.PotSellDao;
import database.Task;
import database.TaskEmployee;
import database.VidTask;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoActAccountRepository;
import ru.facilicom24.manager.cache.DaoActContactRepository;
import ru.facilicom24.manager.cache.DaoActServiceTypeRepository;
import ru.facilicom24.manager.cache.DaoClientRepository;
import ru.facilicom24.manager.cache.DaoTaskEmployeeRepository;
import ru.facilicom24.manager.cache.DaoTaskRepository;
import ru.facilicom24.manager.cache.DaoVidTaskRepository;
import ru.facilicom24.manager.dialogs.CategoryPotSellDialog;
import ru.facilicom24.manager.dialogs.CheckDialog;
import ru.facilicom24.manager.fragments.CaptionFragment;
import ru.facilicom24.manager.fragments.InteractionsFragment;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.utils.NFAdapter;
import ru.facilicom24.manager.utils.NFItem;

public class TaskMainActivity
		extends FragmentActivity
		implements
		View.OnClickListener,
		ListView.OnItemClickListener,
		CheckDialog.ICheckDialogListener,
		CaptionFragment.OnFragmentInteractionListener {

	static final int CLIENT = 0;
	static final int ACCOUNT = 1;
	static final int CATEGORY = 2;
	static final int CONTACT = 3;
	static final int TYPE = 4;
	static final int TASK = 5;
	static final int ASSIGNED = 6;
	static final int DATE = 7;

	static final int SUBJECT = 8;
	static final int TEXT = 9;

	static final int CATEGORY_POTSELL = 10;

	static final String CHECK_DIALOG_TAG = "TaskMainActivity_CheckDialog";

	Task task;
	NFAdapter adapter;
	ArrayList<NFItem> items;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_task_main);

		items = new ArrayList<>();

		items.add(new NFItem(NFItem.Type.Choose, "Клиент", "Выбрать"));
		items.add(new NFItem(NFItem.Type.Choose, "Объект", "Выбрать"));
		items.add(new NFItem(NFItem.Type.Choose, "Вид деятельности /\nПродажа", "Выбрать"));
		items.add(new NFItem(NFItem.Type.Choose, "Контактное лицо", "Выбрать"));
		items.add(new NFItem(NFItem.Type.Choose, "Тип", "Выбрать"));
		items.add(new NFItem(NFItem.Type.Choose, "Вид", "Выбрать"));
		items.add(new NFItem(NFItem.Type.Choose, "Исполнитель", "Выбрать"));
		items.add(new NFItem(NFItem.Type.Choose, "Срок", "Выбрать"));

		items.add(new NFItem(NFItem.Type.Layout, R.layout.fragment_task_text, new NFItem.ILayoutBase() {

			@Override
			public void OnCreate(View view) {
				TextView targetTextView = (TextView) view.findViewById(R.id.target);
				TextView textTextView = (TextView) view.findViewById(R.id.text);

				if (task.getSubject() != null && task.getSubject().length() > 0) {
					targetTextView.setText(task.getSubject());
				}

				if (task.getText() != null && task.getText().length() > 0) {
					textTextView.setText(task.getText());
				}

				targetTextView.setOnClickListener(TaskMainActivity.this);
				textTextView.setOnClickListener(TaskMainActivity.this);
			}
		}));

		List<Task> tasks = DaoTaskRepository.getAll(this);

		if (tasks != null && tasks.size() > 0) {
			task = tasks.get(0);
		} else {
			task = new Task();

			Calendar today = Calendar.getInstance();

			today.set(Calendar.HOUR_OF_DAY, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			today.set(Calendar.MILLISECOND, 0);

			task.setTaskUID(UUID.randomUUID().toString());
			task.setCreatedOn(new Date());
			task.setDueDate(today.getTime());
			task.setChanged(false);

			DaoTaskRepository.insertOrUpdate(this, task);
		}

		if (task.getClientID() != null && task.getClientID() > 0) {
			List<Client> client = DaoClientRepository.getClientForClientId(this, task.getClientID());

			if (client != null && client.size() > 0) {
				items.get(CLIENT).setText(client.get(0).getName());
			}
		}

		if (task.getAccountID() != null && task.getAccountID() > 0) {
			List<ActAccount> accounts = DaoActAccountRepository.getActAccountForDirectumId(this, task.getAccountID());

			if (accounts != null && accounts.size() > 0) {
				items.get(ACCOUNT).setText(TextUtils.concat(accounts.get(0).getName(), ". ", accounts.get(0).getAddress()).toString());
			}
		}

		if (task.getServiceTypeUID() != null) {
			ActServiceType actServiceType = DaoActServiceTypeRepository.getActServiceTypeById(this, task.getServiceTypeUID());

			if (actServiceType != null) {
				items.get(CATEGORY).setText(actServiceType.getName());
			}
		} else {
			if (task.getPotSellNumber() != null) {
				List<PotSell> potSells = FacilicomApplication.getInstance().getDaoSession().getPotSellDao().queryBuilder().
						where(PotSellDao.Properties.Number.eq(task.getPotSellNumber()))
						.list();

				if (potSells != null && !potSells.isEmpty()) {
					items.get(CATEGORY).setText(potSells.get(0).getName());
				}
			}
		}

		if (task.getContactID() != null) {
			ActContact actContact = DaoActContactRepository.getActContactById(this, task.getContactID());

			if (actContact != null) {
				items.get(CONTACT).setText(actContact.getName());
			}
		}

		if (task.getTypeID() != null) {
			switch (task.getTypeID()) {
				case 2: {
					items.get(TYPE).setText(getString(R.string.task_type_operational));
				}
				break;

				case 1: {
					items.get(TYPE).setText(getString(R.string.task_type_strategy));
				}
				break;
			}
		}

		if (task.getVidID() != null) {
			VidTask vidTask = DaoVidTaskRepository.getVidTaskById(this, task.getVidID());

			if (vidTask != null) {
				items.get(TASK).setText(vidTask.getVidTaskName());
			}
		}

		if (task.getExecutorLogin() != null) {
			TaskEmployee taskEmployee = DaoTaskEmployeeRepository.getTaskEmployeeById(this, task.getExecutorLogin());

			if (taskEmployee != null) {
				items.get(ASSIGNED).setText(taskEmployee.getTaskEmployeeName());
			}
		}

		if (task.getDueDate() != null) {
			items.get(DATE).setText(MobileActivity.dateToString(task.getDueDate()));
		}

		//

		adapter = new NFAdapter(this, items);

		ListView form = (ListView) findViewById(R.id.form);

		form.setAdapter(adapter);
		form.setOnItemClickListener(this);
	}

	@Override
	public void onBackPressed() {
		if (task.getChanged() != null && task.getChanged()) {
			CheckDialog.newInstance().show(getSupportFragmentManager(), CHECK_DIALOG_TAG);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
			case CLIENT: {
				startActivityForResult(new Intent(this, CheckClientActivity.class), CLIENT);
			}
			break;

			case ACCOUNT: {
				Intent intent = new Intent(this, CheckActAccountActivity.class);

				if (task.getClientID() != null && task.getClientID() > 0) {
					intent.putExtra("ClientId", task.getClientID());
				}

				startActivityForResult(intent, ACCOUNT);
			}
			break;

			case CATEGORY: {
				if (task.getClientID() != null || task.getAccountID() != null) {

					CategoryPotSellDialog.newInstance(new CategoryPotSellDialog.IListener() {
						@Override
						public void onCategory() {
							Intent intent = new Intent(TaskMainActivity.this, CheckActServiceTypeActivity.class);

							if (task.getClientID() != null) {
								intent.putExtra("ClientId", task.getClientID());
							}

							if (task.getAccountID() != null) {
								intent.putExtra("AccountId", task.getAccountID());
							}

							startActivityForResult(intent, CATEGORY);
						}

						@Override
						public void onPotSell() {
							startActivityForResult(new Intent(TaskMainActivity.this, PotSellActivity.class)
											.putExtra(InteractionsFragment.CLIENT_ID, task.getClientID()),
									CATEGORY_POTSELL);
						}
					}).show(getSupportFragmentManager(), InteractionsFragment.CATEGORY_POTSELL_DIALOG);
				} else {
					Toast.makeText(this, R.string.task_main_no_client_account, Toast.LENGTH_LONG).show();
				}
			}
			break;

			case CONTACT: {
				if (task.getClientID() != null || task.getAccountID() != null) {
					Intent intent = new Intent(this, CheckActContactActivity.class);

					if (task.getClientID() != null) {
						intent.putExtra("ClientId", task.getClientID());
					}

					if (task.getAccountID() != null) {
						intent.putExtra("AccountId", task.getAccountID());
					}

					startActivityForResult(intent, CONTACT);
				} else {
					Toast.makeText(this, R.string.task_main_no_client_account, Toast.LENGTH_LONG).show();
				}
			}
			break;

			case TYPE: {
				startActivityForResult(new Intent(this, TaskTypeActivity.class), TYPE);
			}
			break;

			case TASK: {
				startActivityForResult(new Intent(this, VidTaskActivity.class), TASK);
			}
			break;

			case ASSIGNED: {
				startActivityForResult(new Intent(this, EmployeeActivity.class), ASSIGNED);
			}
			break;

			case DATE: {
				Calendar calendar = Calendar.getInstance();

				if (task.getDueDate() != null) {
					calendar.setTime(task.getDueDate());
				}

				new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						Calendar _calendar = Calendar.getInstance();

						_calendar.set(Calendar.YEAR, year);
						_calendar.set(Calendar.MONTH, monthOfYear);
						_calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

						_calendar.set(Calendar.HOUR_OF_DAY, 0);
						_calendar.set(Calendar.MINUTE, 0);
						_calendar.set(Calendar.SECOND, 0);
						_calendar.set(Calendar.MILLISECOND, 0);

						task.setDueDate(_calendar.getTime());
						task.setChanged(true);

						items.get(DATE).setText(MobileActivity.dateToString(task.getDueDate()));
						adapter.notifyDataSetChanged();
					}
				}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.target: {
				Intent intent = new Intent(this, TextActivity.class);

				intent.putExtra("Caption", getString(R.string.task_main_subject));
				intent.putExtra("Text", task.getSubject());

				startActivityForResult(intent, SUBJECT);
			}
			break;

			case R.id.text: {
				Intent intent = new Intent(this, TextActivity.class);

				intent.putExtra("Caption", getString(R.string.task_main_text));
				intent.putExtra("Text", task.getText());

				startActivityForResult(intent, TEXT);
			}
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
				case CLIENT: {
					task.setClientID(data.getIntExtra("ClientId", 0));
					task.setChanged(true);

					items.get(CLIENT).setText(data.getStringExtra("Title"));
					adapter.notifyDataSetChanged();
				}
				break;

				case ACCOUNT: {
					int accountId = data.getIntExtra("AccountId", 0);

					List<ActAccount> accounts = DaoActAccountRepository.getActAccountForDirectumId(this, accountId);

					if (accounts != null && !accounts.isEmpty()) {
						Client client = accounts.get(0).getClient();

						if (client != null) {
							task.setClientID(client.getClientID());
							items.get(CLIENT).setText(client.getName());
						}
					}

					task.setAccountID(accountId);
					task.setChanged(true);

					items.get(ACCOUNT).setText(TextUtils.concat(data.getStringExtra("Title"), ". ", data.getStringExtra("Address")).toString());
					adapter.notifyDataSetChanged();
				}
				break;

				case CATEGORY: {
					task.setServiceTypeUID(data.getStringExtra("ActTypeId"));
					task.setPotSellNumber(null);
					task.setChanged(true);

					items.get(CATEGORY).setText(data.getStringExtra("Title"));
					adapter.notifyDataSetChanged();
				}
				break;

				case CATEGORY_POTSELL: {
					task.setPotSellNumber(data.getIntExtra(PotSellActivity.POT_SELL_NUMBER, 0));
					task.setServiceTypeUID(null);
					task.setChanged(true);

					items.get(CATEGORY).setText(data.getStringExtra(PotSellActivity.POT_SELL_NAME));
					adapter.notifyDataSetChanged();
				}
				break;

				case CONTACT: {
					task.setContactID(data.getIntExtra("ContactId", 0));
					task.setChanged(true);

					items.get(CONTACT).setText(data.getStringExtra("Title"));
					adapter.notifyDataSetChanged();
				}
				break;

				case TYPE: {
					task.setTypeID(data.getIntExtra("Type", 0));
					task.setChanged(true);

					items.get(TYPE).setText(data.getStringExtra("Name"));
					adapter.notifyDataSetChanged();
				}
				break;

				case TASK: {
					task.setVidID(data.getIntExtra("Kind", 0));
					task.setChanged(true);

					items.get(TASK).setText(data.getStringExtra("Name"));
					adapter.notifyDataSetChanged();
				}
				break;

				case ASSIGNED: {
					task.setExecutorLogin(data.getStringExtra("Employee"));
					task.setChanged(true);

					items.get(ASSIGNED).setText(data.getStringExtra("Name"));
					adapter.notifyDataSetChanged();
				}
				break;

				case SUBJECT: {
					task.setSubject(data.getStringExtra("Text"));
					task.setChanged(true);

					adapter.notifyDataSetChanged();
				}
				break;

				case TEXT: {
					task.setText(data.getStringExtra("Text"));
					task.setChanged(true);

					adapter.notifyDataSetChanged();
				}
				break;
			}
		}
	}

	@Override
	public void onSendBtnClicked() {
		send();
	}

	@Override
	public void onSaveBtnClicked() {
		save();
	}

	@Override
	public void onDeleteBtnClicked() {
		delete();
	}

	void save() {
		task.setChanged(false);

		DaoTaskRepository.insertOrUpdate(this, task);
		finish();
	}

	void send() {
		if (validate()) {
			JSONObject taskJSON = getTaskJson();

			if (taskJSON != null) {
				final ProgressDialog progressDialog = new ProgressDialog(this);

				progressDialog.setMessage(getString(R.string.task_main_task_post_send));
				progressDialog.setCancelable(false);
				progressDialog.setCanceledOnTouchOutside(false);

				progressDialog.show();

				FacilicomNetworkClient.postTask(this, taskJSON, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						progressDialog.dismiss();
						Toast.makeText(TaskMainActivity.this, R.string.task_main_task_post_done, Toast.LENGTH_LONG).show();

						delete();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						progressDialog.dismiss();
						Toast.makeText(TaskMainActivity.this, R.string.task_main_task_post_data_2, Toast.LENGTH_LONG).show();
					}
				});
			} else {
				Toast.makeText(this, R.string.task_main_task_post_data_1, Toast.LENGTH_LONG).show();
			}
		}
	}

	void delete() {
		DaoTaskRepository.deleteAll(this);
		finish();
	}

	boolean validate() {
		boolean result = true;

		ArrayList<String> titles = new ArrayList<>();

		if (task.getClientID() == null && task.getAccountID() == null) {
			titles.add("Клиент или объект");
		}

		if (task.getServiceTypeUID() == null && task.getPotSellNumber() == null) {
			titles.add("Вид деятельности / Продажа");
		}

		if (task.getContactID() == null) {
			titles.add("Контактное лицо");
		}

		if (task.getTypeID() == null) {
			titles.add("Тип");
		}

		if (task.getVidID() == null) {
			titles.add("Вид");
		}

		if (task.getExecutorLogin() == null) {
			titles.add("Исполнитель");
		}

		if (task.getDueDate() == null) {
			titles.add("Срок");
		} else {
			Calendar today = Calendar.getInstance();

			today.set(Calendar.HOUR_OF_DAY, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			today.set(Calendar.MILLISECOND, 0);

			if (task.getDueDate().compareTo(today.getTime()) < 0) {
				titles.add("Срок (Меньше текущей даты)");
			}
		}

		if (task.getSubject() == null) {
			titles.add(getString(R.string.task_main_subject));
		}

		if (task.getText() == null) {
			titles.add(getString(R.string.task_main_text));
		}

		if (titles.size() > 0) {
			new AlertDialog.Builder(this)
					.setIcon(R.drawable.ic_info_white_48dp)
					.setTitle(R.string.task_main_form)
					.setMessage(TextUtils.join(", ", titles))
					.setPositiveButton(R.string.next_button, null)
					.show();

			result = false;
		}

		return result;
	}

	JSONObject getTaskJson() {
		JSONObject result = null;

		if (task != null) {
			String _dueDate = FacilicomApplication.dateTimeFormat5.format(task.getDueDate());

			Calendar createdOn = Calendar.getInstance();
			String _createdOn = FacilicomApplication.dateTimeFormat7.format(createdOn.getTime());

			result = new JSONObject();

			try {
				result.put("ActUID", "");

				result.put("ClientID", task.getClientID());
				result.put("AccountID", task.getAccountID());
				result.put("ServiceTypeUID", task.getServiceTypeUID());
				result.put("PotSellNumber", task.getPotSellNumber());
				result.put("ContactID", task.getContactID());

				result.put("TaskUID", task.getTaskUID());

				result.put("TypeID", task.getTypeID());
				result.put("VidID", task.getVidID());
				result.put("ExecutorLogin", task.getExecutorLogin());
				result.put("DueDate", _dueDate);

				result.put("Subject", task.getSubject());
				result.put("Text", task.getText());

				result.put("Result", "");
				result.put("Status", "Running");
				result.put("CreatedOn", _createdOn);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		return result;
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	@Override
	public void captionFragmentOnSendPressed() {
		send();
	}

	@Override
	public void captionFragmentOnSavePressed() {
		save();
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
		return true;
	}

	@Override
	public boolean saveIcon() {
		return true;
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
