package ru.facilicom24.manager.activities;

import android.app.Activity;
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
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import database.Person;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoPersonRepository;
import ru.facilicom24.manager.dialogs.CheckDialog;
import ru.facilicom24.manager.fragments.CaptionFragment;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.utils.NFAdapter;
import ru.facilicom24.manager.utils.NFItem;
import ru.facilicom24.manager.utils.NetworkHelper;

public class DismissActivity
		extends FragmentActivity
		implements
		ListView.OnItemClickListener,
		CheckDialog.ICheckDialogListener,
		DatePickerDialog.OnDateSetListener,
		CaptionFragment.OnFragmentInteractionListener {

	final static int ACCOUNT_INDEX = 0;
	final static int NAME_INDEX = 1;
	final static int DATE_INDEX = 2;
	final static int REASON_INDEX = 3;

	final static String CHECK_DIALOG_TAG = "DismissActivity_CheckDialog";

	Person person;

	ListView form;
	NFAdapter adapter;

	ArrayList<NFItem> items = new ArrayList<>();

	int datePickerDialogIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_dismiss);

		items.add(new NFItem(NFItem.Type.Choose, "Объект", "Выбрать"));
		items.add(new NFItem(NFItem.Type.Choose, "ФИО", "Выбрать"));
		items.add(new NFItem(NFItem.Type.Date, "Дата", "Выбрать"));
		items.add(new NFItem(NFItem.Type.Choose, "Причина", "Выбрать"));

		// Data

		List<Person> persons = DaoPersonRepository.getPersonsByType(this, PersonActivity.PERSON_TYPE_UNBIND);

		if (persons.size() == 0) {
			person = new Person();

			person.setDocumentType(PersonActivity.PERSON_TYPE_UNBIND);

			Calendar calendar = Calendar.getInstance();

			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			items.get(DATE_INDEX).setText(MobileActivity.dateToString(calendar.getTime()));
			person.setDismissDate(calendar.getTime());
		} else {
			person = persons.get(0);

			if (person.getSubdivisionName() != null) {
				items.get(ACCOUNT_INDEX).setText(person.getSubdivisionName());
			}

			if (person.getPersonName() != null) {
				items.get(NAME_INDEX).setText(person.getPersonName());
			}

			if (person.getDismissDate() != null) {
				items.get(DATE_INDEX).setText(MobileActivity.dateToString(person.getDismissDate()));
			}

			if (person.getDismissReasonName() != null) {
				items.get(REASON_INDEX).setText(person.getDismissReasonName());
			}
		}

		//

		form = (ListView) findViewById(R.id.form);

		adapter = new NFAdapter(this, items);

		form.setAdapter(adapter);
		form.setOnItemClickListener(this);
	}

	@Override
	public void onBackPressed() {
		CheckDialog.newInstance().show(getSupportFragmentManager(), CHECK_DIALOG_TAG);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
			case ACCOUNT_INDEX: {
				startActivityForResult(new Intent(this, SubdivisionActivity.class), ACCOUNT_INDEX);
			}
			break;

			case NAME_INDEX: {
				if (person.getSubdivisionUID() != null) {
					startActivityForResult(new Intent(this, PersonGetActivity.class).putExtra(PersonGetActivity.SUBDIVISION_UID, person.getSubdivisionUID()), NAME_INDEX);
				} else {
					new AlertDialog.Builder(this)
							.setTitle(R.string.message_label)
							.setMessage(R.string.personal_no_subdivision)
							.setNegativeButton(R.string.next_button, null)
							.show();
				}
			}
			break;

			case DATE_INDEX: {
				datePickerDialogIndex = DATE_INDEX;

				Calendar calendar = Calendar.getInstance();

				if (person.getDismissDate() != null) {
					calendar.setTime(person.getDismissDate());
				} else {
					calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
					calendar.set(Calendar.MILLISECOND, 0);
				}

				new DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
			}
			break;

			case REASON_INDEX: {
				startActivityForResult(new Intent(this, DismissReasonActivity.class), REASON_INDEX);
			}
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case ACCOUNT_INDEX: {
					items.get(ACCOUNT_INDEX).setText(data.getStringExtra("Name"));

					person.setSubdivisionUID(data.getStringExtra("UID"));
					person.setSubdivisionName(data.getStringExtra("Name"));
				}
				break;

				case NAME_INDEX: {
					items.get(NAME_INDEX).setText(data.getStringExtra("Name"));

					person.setPersonUID(data.getStringExtra("UID"));
					person.setPersonLocalUID(data.getStringExtra("UID"));

					person.setPersonName(data.getStringExtra("Name"));
				}
				break;

				case REASON_INDEX: {
					items.get(REASON_INDEX).setText(data.getStringExtra("Name"));

					person.setDismissReasonUID(data.getStringExtra("UID"));
					person.setDismissReasonName(data.getStringExtra("Name"));
				}
				break;
			}

			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();

		calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		switch (datePickerDialogIndex) {
			case DATE_INDEX: {
				items.get(DATE_INDEX).setText(MobileActivity.dateToString(calendar.getTime()));
				person.setDismissDate(calendar.getTime());
			}
			break;
		}

		adapter.notifyDataSetChanged();
	}

	@Override
	public void onSendBtnClicked() {
		captionFragmentOnSendPressed();
	}

	@Override
	public void onSaveBtnClicked() {
		captionFragmentOnSavePressed();
	}

	@Override
	public void onDeleteBtnClicked() {
		delete();
	}

	void delete() {
		DaoPersonRepository.clearPersonUnbind(this);
		finish();
	}

	void serverErrorMessage(String message) {
		new AlertDialog.Builder(this)
				.setTitle(R.string.message_label)
				.setMessage(message != null && !message.isEmpty() ? message : getString(R.string.server_error))
				.setNegativeButton(R.string.next_button, null)
				.show();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	@Override
	public void captionFragmentOnSendPressed() {
		ArrayList<String> messages = new ArrayList<>();

		if (person.getSubdivisionUID() == null) {
			messages.add(items.get(ACCOUNT_INDEX).getName());
		}

		if (person.getPersonUID() == null) {
			messages.add(items.get(NAME_INDEX).getName());
		}

		if (person.getDismissDate() == null) {
			messages.add(items.get(DATE_INDEX).getName());
		}

		if (person.getDismissReasonUID() == null) {
			messages.add(items.get(REASON_INDEX).getName());
		}

		if (!messages.isEmpty()) {
			String message = TextUtils.concat("Заполните: ", TextUtils.join(", ", messages)).toString();

			new AlertDialog.Builder(this)
					.setTitle(R.string.message_label)
					.setMessage(message)
					.setNegativeButton(R.string.next_button, null)
					.show();
		} else {
			DaoPersonRepository.insertOrUpdate(this, person);

			if (NetworkHelper.isConnected(this)) {
				final ProgressDialog dialog = new ProgressDialog(this);

				dialog.setMessage(getString(R.string.personal_dismiss_send));
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);

				dialog.show();

				FacilicomNetworkClient.postPerson(this, person, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
						dialog.dismiss();

						String message = FacilicomNetworkClient.responseToString(responseBody);

						if (message != null && message.length() > 0) {
							new AlertDialog.Builder(DismissActivity.this)
									.setTitle(R.string.message_label)
									.setMessage(message)
									.setNegativeButton(R.string.back_button, null)
									.show();
						} else {
							delete();
							Toast.makeText(DismissActivity.this, R.string.personal_dismiss_send_done, Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
						dialog.dismiss();
						serverErrorMessage(FacilicomNetworkClient.responseToString(responseBody));
					}
				});
			} else {
				new AlertDialog.Builder(this)
						.setTitle(R.string.message_label)
						.setMessage(R.string.errorConnection)
						.setNegativeButton(R.string.next_button, null)
						.show();
			}
		}
	}

	@Override
	public void captionFragmentOnSavePressed() {
		DaoPersonRepository.insertOrUpdate(this, person);
		finish();
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
