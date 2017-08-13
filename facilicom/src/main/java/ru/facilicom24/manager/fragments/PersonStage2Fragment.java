package ru.facilicom24.manager.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Semaphore;

import database.Person;
import database.PersonPhoto;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.activities.BankActivity;
import ru.facilicom24.manager.activities.JobTitleActivity;
import ru.facilicom24.manager.activities.MobileActivity;
import ru.facilicom24.manager.activities.PersonActivity;
import ru.facilicom24.manager.activities.SubdivisionActivity;
import ru.facilicom24.manager.cache.DaoPersonPhotoRepository;
import ru.facilicom24.manager.cache.DaoPersonRepository;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.utils.NFAdapter;
import ru.facilicom24.manager.utils.NFItem;
import ru.facilicom24.manager.utils.NetworkHelper;

public class PersonStage2Fragment
		extends Fragment
		implements
		View.OnClickListener,
		AdapterView.OnItemClickListener,
		DatePickerDialog.OnDateSetListener {

	final static public String TAG = "PersonStage2Fragment";
	final static public String MOSCOW_REGION_UID = "a2151a79-8883-11dc-b942-003048314500";

	final static int PHONE_NUMBER_INDEX = 0;
	final static int PERMISSION_SERIA_INDEX = 2;
	final static int PERMISSION_NUMBER_INDEX = 3;
	final static int PERMISSION_EXPIRY_INDEX = 4;
	final static int PATENT_NUMBER_INDEX = 6;
	final static int PATENT_ISSUE_INDEX = 7;
	final static int PATENT_EXPIRY_INDEX = 8;
	final static int SUBDIVISION_INDEX = 10;
	final static int JOB_TITLE_INDEX = 11;
	final static int BANK_TITLE = 12;
	final static int BANK_INDEX = 13;
	final static int BANK_DATE_INDEX = 14;

	ListView form;
	Person person;
	NFAdapter adapter;
	ArrayList<NFItem> items;

	int datePickerDialogIndex;
	int imageSemaphoreCounter;

	Semaphore imageSemaphore;

	public PersonStage2Fragment() {
		items = new ArrayList<>();

		items.add(new NFItem(NFItem.Type.Phone, "Номер телефона", 16));

		items.add(new NFItem(NFItem.Type.Title, "Разрешение на работу"));
		items.add(new NFItem(NFItem.Type.Text, "Серия разрешения", 50));
		items.add(new NFItem(NFItem.Type.Text, "Номер разрешения", 50));
		items.add(new NFItem(NFItem.Type.Date, "Дата окончания", "Нет"));

		items.add(new NFItem(NFItem.Type.Title, "Патент"));
		items.add(new NFItem(NFItem.Type.Text, "Номер патента", 50));
		items.add(new NFItem(NFItem.Type.Date, "Дата выдачи патента", "Нет"));
		items.add(new NFItem(NFItem.Type.Date, "Дата окончания патента", "Нет"));

		items.add(new NFItem(NFItem.Type.Title, "Привязка к подразделению"));
		items.add(new NFItem(NFItem.Type.Choose, "Объект", "Выбрать"));
		items.add(new NFItem(NFItem.Type.Choose, "Должность", "Выбрать"));

		items.add(new NFItem(NFItem.Type.Title, "Банковская карта", false));
		items.add(new NFItem(NFItem.Type.Choose, "Пункт выдачи карт", "Выбрать", false));
		items.add(new NFItem(NFItem.Type.Date, "Дата/время записи", "Нет", false));
	}

	// Fragment

	public Person getPerson() {
		return person;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_person_stage_2, container, false);

		// Data

		List<Person> persons = DaoPersonRepository.getPersonsByType(getActivity(), PersonActivity.PERSON_TYPE_CREATE);

		if (persons != null && !persons.isEmpty()) {
			person = persons.get(0);

			if (person.getPhoneNumber() != null) {
				items.get(PHONE_NUMBER_INDEX).setText(person.getPhoneNumber());
			}

			if (person.getPermissionSeria() != null) {
				items.get(PERMISSION_SERIA_INDEX).setText(person.getPermissionSeria());
			}

			if (person.getPermissionNumber() != null) {
				items.get(PERMISSION_NUMBER_INDEX).setText(person.getPermissionNumber());
			}

			if (person.getPermissionExpiry() != null) {
				items.get(PERMISSION_EXPIRY_INDEX).setText(MobileActivity.dateToString(person.getPermissionExpiry()));
			}

			if (person.getPatentNumber() != null) {
				items.get(PATENT_NUMBER_INDEX).setText(person.getPatentNumber());
			}

			if (person.getPatentIssue() != null) {
				items.get(PATENT_ISSUE_INDEX).setText(MobileActivity.dateToString(person.getPatentIssue()));
			}

			if (person.getPatentExpiry() != null) {
				items.get(PATENT_EXPIRY_INDEX).setText(MobileActivity.dateToString(person.getPatentExpiry()));
			}

			if (person.getSubdivisionName() != null) {
				items.get(SUBDIVISION_INDEX).setText(person.getSubdivisionName());
			}

			if (person.getJobTitleName() != null) {
				items.get(JOB_TITLE_INDEX).setText(person.getJobTitleName());
			}

			//

			if (person.getRegionUID() != null) {
				boolean visible = person.getRegionUID().equals(MOSCOW_REGION_UID);

				items.get(BANK_TITLE).setVisible(visible);
				items.get(BANK_INDEX).setVisible(visible);
				items.get(BANK_DATE_INDEX).setVisible(visible);

				if (visible) {
					if (person.getBankName() != null) {
						items.get(BANK_INDEX).setText(person.getBankName());
					}

					if (person.getBankDate() != null) {
						items.get(BANK_DATE_INDEX).setText(MobileActivity.dateToString(person.getBankDate()));
					}
				}
			} else {
				items.get(BANK_TITLE).setVisible(false);
				items.get(BANK_INDEX).setVisible(false);
				items.get(BANK_DATE_INDEX).setVisible(false);
			}

			// View

			adapter = new NFAdapter(getActivity(), items);

			form = (ListView) view.findViewById(R.id.form);

			form.setAdapter(adapter);
			form.setOnItemClickListener(this);

			view.findViewById(R.id.documentFontButton).setOnClickListener(this);
		}

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
			case PERMISSION_EXPIRY_INDEX: {
				datePickerDialogIndex = PERMISSION_EXPIRY_INDEX;

				Calendar calendar = Calendar.getInstance();

				if (person.getPermissionExpiry() != null) {
					calendar.setTime(person.getPermissionExpiry());
				} else {
					calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0, 0, 0);
					calendar.set(Calendar.MILLISECOND, 0);
				}

				(new DatePickerDialog(getActivity(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))).show();
			}
			break;

			case PATENT_ISSUE_INDEX: {
				datePickerDialogIndex = PATENT_ISSUE_INDEX;

				Calendar calendar = Calendar.getInstance();

				if (person.getPatentIssue() != null) {
					calendar.setTime(person.getPatentIssue());
				} else {
					calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0, 0, 0);
					calendar.set(Calendar.MILLISECOND, 0);
				}

				(new DatePickerDialog(getActivity(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))).show();
			}
			break;

			case PATENT_EXPIRY_INDEX: {
				datePickerDialogIndex = PATENT_EXPIRY_INDEX;

				Calendar calendar = Calendar.getInstance();

				if (person.getPatentExpiry() != null) {
					calendar.setTime(person.getPatentExpiry());
				} else {
					calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0, 0, 0);
					calendar.set(Calendar.MILLISECOND, 0);
				}

				(new DatePickerDialog(getActivity(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))).show();
			}
			break;

			case SUBDIVISION_INDEX: {
				Intent intent = new Intent(getActivity(), SubdivisionActivity.class);
				startActivityForResult(intent, SUBDIVISION_INDEX);
			}
			break;

			case JOB_TITLE_INDEX: {
				if (person.getSubdivisionUID() == null) {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

					builder.setTitle(R.string.message_label);
					builder.setMessage(R.string.personal_no_subdivision);
					builder.setNegativeButton(R.string.next_button, null);

					builder.create().show();
				} else {
					Intent intent = new Intent(getActivity(), JobTitleActivity.class);
					intent.putExtra("SubdivisionUID", person.getSubdivisionUID());
					startActivityForResult(intent, JOB_TITLE_INDEX);
				}
			}
			break;

			case BANK_INDEX: {
				Intent intent = new Intent(getActivity(), BankActivity.class);
				startActivityForResult(intent, BANK_INDEX);
			}
			break;

			case BANK_DATE_INDEX: {
				datePickerDialogIndex = BANK_DATE_INDEX;

				Calendar calendar = Calendar.getInstance();

				if (person.getBankDate() != null) {
					calendar.setTime(person.getBankDate());
				} else {
					calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0, 0, 0);
					calendar.set(Calendar.MILLISECOND, 0);
				}

				(new DatePickerDialog(getActivity(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))).show();
			}
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case SUBDIVISION_INDEX: {
					items.get(SUBDIVISION_INDEX).setText(data.getStringExtra("Name"));
					items.get(JOB_TITLE_INDEX).setText("Выбрать");

					person.setSubdivisionUID(data.getStringExtra("UID"));
					person.setRegionUID(data.getStringExtra("RegionUID"));
					person.setSubdivisionName(data.getStringExtra("Name"));

					person.setJobTitleUID(null);
					person.setJobTitleName(null);

					boolean visible = person.getRegionUID().equals(MOSCOW_REGION_UID);

					items.get(BANK_TITLE).setVisible(visible);
					items.get(BANK_INDEX).setVisible(visible);
					items.get(BANK_DATE_INDEX).setVisible(visible);

					if (!visible) {
						items.get(BANK_INDEX).setText("Выбрать");
						items.get(BANK_DATE_INDEX).setText("Нет");

						person.setBankUID(null);
						person.setBankName(null);
						person.setBankDate(null);

						List<PersonPhoto> photos = DaoPersonPhotoRepository.getPersonPhotoByType(getActivity(), PersonActivity.PHOTO_4);

						for (PersonPhoto photo : photos) {
							DaoPersonPhotoRepository.clearPersonPhoto(getActivity(), photo);
						}
					}
				}
				break;

				case JOB_TITLE_INDEX: {
					items.get(JOB_TITLE_INDEX).setText(data.getStringExtra("Name"));

					person.setJobTitleUID(data.getStringExtra("UID"));
					person.setJobTitleName(data.getStringExtra("Name"));
				}
				break;

				case BANK_INDEX: {
					items.get(BANK_INDEX).setText(data.getStringExtra("Name"));

					person.setBankUID(data.getStringExtra("UID"));
					person.setBankName(data.getStringExtra("Name"));
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
			case PERMISSION_EXPIRY_INDEX: {
				items.get(PERMISSION_EXPIRY_INDEX).setText(MobileActivity.dateToString(calendar.getTime()));
				person.setPermissionExpiry(calendar.getTime());
			}
			break;

			case PATENT_ISSUE_INDEX: {
				items.get(PATENT_ISSUE_INDEX).setText(MobileActivity.dateToString(calendar.getTime()));
				person.setPatentIssue(calendar.getTime());
			}
			break;

			case PATENT_EXPIRY_INDEX: {
				items.get(PATENT_EXPIRY_INDEX).setText(MobileActivity.dateToString(calendar.getTime()));
				person.setPatentExpiry(calendar.getTime());
			}
			break;

			case BANK_DATE_INDEX: {
				items.get(BANK_DATE_INDEX).setText(MobileActivity.dateToString(calendar.getTime()));
				person.setBankDate(calendar.getTime());
			}
			break;
		}

		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.documentFontButton: {
				getActivity().getSupportFragmentManager().beginTransaction().add(
						R.id.person_stages_fragment,
						PersonDocumentFragment.newInstance(person),
						PersonDocumentFragment.TAG
				).commit();

				((PersonActivity) getActivity()).setMode(PersonActivity.ModeType.Document);
			}
			break;
		}
	}

	void apply() {
		person.setPhoneNumber(items.get(PHONE_NUMBER_INDEX).getText().trim());
		person.setPermissionSeria(items.get(PERMISSION_SERIA_INDEX).getText().trim());
		person.setPermissionNumber(items.get(PERMISSION_NUMBER_INDEX).getText().trim());
		person.setPatentNumber(items.get(PATENT_NUMBER_INDEX).getText().trim());
	}

	public void save() {
		apply();
		DaoPersonRepository.insertOrUpdate(getActivity(), person);
		getActivity().finish();
	}

	public void send() {
		apply();

		ArrayList<String> messages = new ArrayList<>();

		if (person.getPhoneNumber().length() == 0) {
			messages.add(items.get(PHONE_NUMBER_INDEX).getName());
		}

		//

		boolean permissionSeria = false;
		boolean permissionNumber = false;
		boolean permissionExpiry = false;

		boolean patentNumber = false;
		boolean patentIssue = false;
		boolean patentExpiry = false;

		if (person.getNeedPatentOrPermission() == 1) {

			// Permission

			if (!(person.getPermissionSeria().length() == 0)) {
				permissionSeria = true;
			}

			if (!(person.getPermissionNumber().length() == 0)) {
				permissionNumber = true;
			}

			if (!(person.getPermissionExpiry() == null)) {
				permissionExpiry = true;
			}

			// Patent

			if (!(person.getPatentNumber().length() == 0)) {
				patentNumber = true;
			}

			if (!(person.getPatentIssue() == null)) {
				patentIssue = true;
			}

			if (!(person.getPatentExpiry() == null)) {
				patentExpiry = true;
			}

			if (!permissionSeria && !permissionNumber && !permissionExpiry
					&& !patentNumber && !patentIssue && !patentExpiry) {

				messages.add(getString(R.string.permission_patent_message));
			} else {
				if (!patentNumber && !patentIssue && !patentExpiry) {
					if (!permissionSeria) {
						messages.add(items.get(PERMISSION_SERIA_INDEX).getName());
					}

					if (!permissionNumber) {
						messages.add(items.get(PERMISSION_NUMBER_INDEX).getName());
					}

					if (!permissionExpiry) {
						messages.add(items.get(PERMISSION_EXPIRY_INDEX).getName());
					}
				} else {
					if (!patentNumber) {
						messages.add(items.get(PATENT_NUMBER_INDEX).getName());
					}

					if (!patentIssue) {
						messages.add(items.get(PATENT_ISSUE_INDEX).getName());
					}

					if (!patentExpiry) {
						messages.add(items.get(PATENT_EXPIRY_INDEX).getName());
					}
				}
			}
		}

		//

		if (person.getSubdivisionUID() == null) {
			messages.add(items.get(SUBDIVISION_INDEX).getName());
		}

		if (person.getJobTitleUID() == null) {
			messages.add(items.get(JOB_TITLE_INDEX).getName());
		}

		if (DaoPersonPhotoRepository.getPersonPhotoCountByType(getActivity(), PersonActivity.PHOTO_1) == 0) {
			messages.add(getString(R.string.personal_no_photo_1));
		}

		if (person.getNeedPatentOrPermission() == 1) {
			if ((permissionSeria || permissionNumber || permissionExpiry)
					&& DaoPersonPhotoRepository.getPersonPhotoCountByType(getActivity(), PersonActivity.PHOTO_2) == 0) {

				messages.add(getString(R.string.personal_no_photo_2));
			}

			if ((patentNumber || patentIssue || patentExpiry)
					&& DaoPersonPhotoRepository.getPersonPhotoCountByType(getActivity(), PersonActivity.PHOTO_3) == 0) {

				messages.add(getString(R.string.personal_no_photo_3));
			}
		}

		if (items.get(BANK_TITLE).getVisible()) {
			if (person.getBankUID() == null) {
				if (DaoPersonPhotoRepository.getPersonPhotoCountByType(getActivity(), PersonActivity.PHOTO_4) == 0) {
					messages.add(getString(R.string.personal_no_photo_4));
				}
			} else {
				if (person.getBankDate() == null) {
					messages.add(items.get(BANK_DATE_INDEX).getName());
				}
			}
		}

		if (!messages.isEmpty()) {
			new AlertDialog.Builder(getActivity())
					.setTitle(R.string.message_label)
					.setMessage(TextUtils.concat("Заполните: ", TextUtils.join(", ", messages)).toString())
					.setNegativeButton(R.string.next_button, null)
					.show();
		} else {
			DaoPersonRepository.insertOrUpdate(getActivity(), person);

			if (NetworkHelper.isConnected(getActivity())) {
				final ProgressDialog dialog = new ProgressDialog(getActivity());

				dialog.setMessage(getString(R.string.personal_send));
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);

				dialog.show();

				FacilicomNetworkClient.postPerson(getActivity(), person, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
						imageSemaphore = new Semaphore(0);

						List<PersonPhoto> photos = DaoPersonPhotoRepository.getAllPersonPhotoCreate(getActivity());

						new ImageAsyncTask().execute(photos);

						try {
							imageSemaphore.acquire(photos.size());
						} catch (Exception exception) {
							exception.printStackTrace();
						}

						if (imageSemaphoreCounter == photos.size()) {
							FacilicomNetworkClient.postPersonCommit(getActivity(), person, new AsyncHttpResponseHandler() {
								@Override
								public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
									dialog.dismiss();

									String message = FacilicomNetworkClient.responseToString(responseBody);

									if (message != null && !message.isEmpty()) {
										new AlertDialog.Builder(getActivity())
												.setTitle(R.string.message_label)
												.setMessage(message)
												.setNegativeButton(R.string.back_button, null)
												.show();
									} else {
										((PersonActivity) getActivity()).delete();
										Toast.makeText(getActivity(), R.string.personal_send_done, Toast.LENGTH_LONG).show();
									}
								}

								@Override
								public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
									dialog.dismiss();
									serverErrorMessage(FacilicomNetworkClient.responseToString(responseBody));
								}
							});
						} else {
							dialog.dismiss();
							serverErrorMessage(getString(R.string.imagePostError));
						}
					}

					@Override
					public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
						dialog.dismiss();
						serverErrorMessage(FacilicomNetworkClient.responseToString(responseBody));
					}
				});
			} else {
				new AlertDialog.Builder(getActivity())
						.setTitle(R.string.message_label)
						.setMessage(R.string.errorConnection)
						.setNegativeButton(R.string.next_button, null)
						.show();
			}
		}
	}

	void serverErrorMessage(String message) {
		new AlertDialog.Builder(getActivity())
				.setTitle(R.string.message_label)
				.setMessage(message != null && !message.isEmpty() ? message : getString(R.string.server_error))
				.setNegativeButton(R.string.next_button, null)
				.show();
	}

	private class ImageAsyncTask extends AsyncTask<List<PersonPhoto>, Void, Void> {

		@Override
		protected Void doInBackground(List<PersonPhoto>... params) {
			List<PersonPhoto> photos = params[0];

			imageSemaphoreCounter = 0;
			for (PersonPhoto photo : photos) {
				FacilicomNetworkClient.postPersonPhoto(getActivity(), photo, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
						imageSemaphoreCounter++;

						try {
							imageSemaphore.release();
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}

					@Override
					public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
						try {
							imageSemaphore.release();
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
				});
			}

			return null;
		}
	}
}
