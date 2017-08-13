package ru.facilicom24.manager.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import database.Country;
import database.Person;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.activities.CountryActivity;
import ru.facilicom24.manager.activities.MobileActivity;
import ru.facilicom24.manager.activities.PersonActivity;
import ru.facilicom24.manager.activities.SexActivity;
import ru.facilicom24.manager.cache.DaoCountryRepository;
import ru.facilicom24.manager.cache.DaoPersonRepository;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.utils.NFAdapter;
import ru.facilicom24.manager.utils.NFItem;
import ru.facilicom24.manager.utils.NetworkHelper;

public class PersonStage1Fragment
		extends Fragment
		implements
		View.OnClickListener,
		AdapterView.OnItemClickListener,
		DatePickerDialog.OnDateSetListener {

	final static int LAST_NAME_INDEX = 0;
	final static int FIRST_NAME_INDEX = 1;
	final static int FATHER_NAME_INDEX = 2;
	final static int DATE_1_INDEX = 3;
	final static int COUNTRY_INDEX = 4;
	final static int SEX_INDEX = 5;
	final static int PASSPORT_NUMBER_INDEX = 7;
	final static int DATE_2_INDEX = 8;

	ListView form;
	NFAdapter adapter;

	ArrayList<NFItem> items;

	int datePickerDialogIndex;

	Person person;

	// Fragment

	public PersonStage1Fragment() {
		items = new ArrayList<>();

		items.add(new NFItem(NFItem.Type.Text, "Фамилия", 250));
		items.add(new NFItem(NFItem.Type.Text, "Имя", 250));
		items.add(new NFItem(NFItem.Type.Text, "Отчество", 250));
		items.add(new NFItem(NFItem.Type.Date, "Дата рождения", "Нет"));
		items.add(new NFItem(NFItem.Type.Choose, "Гражданство", "Выбрать"));
		items.add(new NFItem(NFItem.Type.Choose, "Пол", "Выбрать"));

		items.add(new NFItem(NFItem.Type.Title, "Паспортные данные"));

		items.add(new NFItem(NFItem.Type.Text, "Номер паспорта", 50));
		items.add(new NFItem(NFItem.Type.Date, "Дата выдачи", "Нет"));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_person_stage_1, container, false);

		// Data

		List<Person> persons = DaoPersonRepository.getPersonsByType(getActivity(), PersonActivity.PERSON_TYPE_CREATE);

		if (persons == null || persons.isEmpty()) {
			person = new Person();

			person.setDocumentType(PersonActivity.PERSON_TYPE_CREATE);
			person.setPersonLocalUID(UUID.randomUUID().toString());
		} else {
			person = persons.get(0);
		}

		if (person.getLastName() != null) {
			items.get(LAST_NAME_INDEX).setText(person.getLastName());
		}

		if (person.getFirstName() != null) {
			items.get(FIRST_NAME_INDEX).setText(person.getFirstName());
		}

		if (person.getFatherName() != null) {
			items.get(FATHER_NAME_INDEX).setText(person.getFatherName());
		}

		if (person.getBirthDate() != null) {
			items.get(DATE_1_INDEX).setText(MobileActivity.dateToString(person.getBirthDate()));
		}

		if (person.getCountryUID() != null) {
			List<Country> countries = DaoCountryRepository.getCountryByUID(getActivity(), person.getCountryUID());
			items.get(COUNTRY_INDEX).setText(countries.size() > 0 ? countries.get(0).getCountryName() : "Выбрать");
		}

		if (person.getSex() != null) {
			items.get(SEX_INDEX).setText(person.getSex());
		}

		if (person.getPassportNumber() != null) {
			items.get(PASSPORT_NUMBER_INDEX).setText(person.getPassportNumber());
		}

		if (person.getPassportIssue() != null) {
			items.get(DATE_2_INDEX).setText(MobileActivity.dateToString(person.getPassportIssue()));
		}

		// View

		adapter = new NFAdapter(getActivity(), items);

		form = (ListView) view.findViewById(R.id.form);

		form.setAdapter(adapter);
		form.setOnItemClickListener(this);

		view.findViewById(R.id.next).setOnClickListener(this);

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
			case DATE_1_INDEX: {
				datePickerDialogIndex = DATE_1_INDEX;

				Calendar calendar = Calendar.getInstance();

				if (person.getBirthDate() != null) {
					calendar.setTime(person.getBirthDate());
				} else {
					calendar.set(calendar.get(Calendar.YEAR) - 20, 0, 1, 0, 0, 0);
					calendar.set(Calendar.MILLISECOND, 0);
				}

				(new DatePickerDialog(getActivity(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))).show();
			}
			break;

			case COUNTRY_INDEX: {
				startActivityForResult(new Intent(getActivity(), CountryActivity.class), COUNTRY_INDEX);
			}
			break;

			case SEX_INDEX: {
				startActivityForResult(new Intent(getActivity(), SexActivity.class), SEX_INDEX);
			}
			break;

			case DATE_2_INDEX: {
				datePickerDialogIndex = DATE_2_INDEX;

				Calendar calendar = Calendar.getInstance();

				if (person.getPassportIssue() != null) {
					calendar.setTime(person.getPassportIssue());
				} else {
					calendar.set(calendar.get(Calendar.YEAR) - 10, 0, 1, 0, 0, 0);
					calendar.set(Calendar.MILLISECOND, 0);
				}

				(new DatePickerDialog(getActivity(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))).show();
			}
			break;
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();

		calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		switch (datePickerDialogIndex) {
			case DATE_1_INDEX: {
				items.get(DATE_1_INDEX).setText(MobileActivity.dateToString(calendar.getTime()));
				person.setBirthDate(calendar.getTime());
			}
			break;

			case DATE_2_INDEX: {
				items.get(DATE_2_INDEX).setText(MobileActivity.dateToString(calendar.getTime()));
				person.setPassportIssue(calendar.getTime());
			}
			break;
		}

		adapter.notifyDataSetChanged();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case COUNTRY_INDEX: {
					items.get(COUNTRY_INDEX).setText(data.getStringExtra("CountryName"));

					person.setCountryUID(data.getStringExtra("CountryUID"));
					person.setNeedPatentOrPermission(data.getIntExtra("NeedPatentOrPermission", 0));
				}
				break;

				case SEX_INDEX: {
					items.get(SEX_INDEX).setText(data.getStringExtra("Title"));
					person.setSex(data.getStringExtra("Title"));
				}
				break;
			}

			adapter.notifyDataSetChanged();
		}
	}

	void apply() {
		person.setLastName(items.get(LAST_NAME_INDEX).getText().trim());
		person.setFirstName(items.get(FIRST_NAME_INDEX).getText().trim());
		person.setFatherName(items.get(FATHER_NAME_INDEX).getText().trim());

		person.setPassportNumber(items.get(PASSPORT_NUMBER_INDEX).getText().trim());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.next: {
				apply();

				ArrayList<String> messages = new ArrayList<>();

				if (person.getLastName().length() == 0) {
					messages.add(items.get(LAST_NAME_INDEX).getName());
				}

				if (person.getFirstName().length() == 0) {
					messages.add(items.get(FIRST_NAME_INDEX).getName());
				}

				if (person.getBirthDate() == null) {
					messages.add(items.get(DATE_1_INDEX).getName());
				}

				if (person.getCountryUID() == null) {
					messages.add(items.get(COUNTRY_INDEX).getName());
				}

				if (person.getSex() == null) {
					messages.add(items.get(SEX_INDEX).getName());
				}

				if (person.getPassportNumber().length() == 0) {
					messages.add(items.get(PASSPORT_NUMBER_INDEX).getName());
				}

				if (person.getPassportIssue() == null) {
					messages.add(items.get(DATE_2_INDEX).getName());
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

						dialog.setMessage(getString(R.string.personal_stage_1_check_person));
						dialog.setCancelable(false);
						dialog.setCanceledOnTouchOutside(false);

						dialog.show();

						FacilicomNetworkClient.checkDuplicatePerson(getActivity(), person, new AsyncHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
								dialog.dismiss();

								String message = FacilicomNetworkClient.responseToString(responseBody);

								if (message != null && message.length() > 0) {
									new AlertDialog.Builder(getActivity())
											.setTitle(R.string.message_label)
											.setMessage(message)
											.setNegativeButton(R.string.next_button, null)
											.show();
								} else {
									getActivity().getSupportFragmentManager().beginTransaction().add(
											R.id.person_stages_fragment,
											new PersonStage2Fragment(),
											PersonStage2Fragment.TAG
									).commit();

									((PersonActivity) getActivity()).setMode(PersonActivity.ModeType.Stage2);
								}
							}

							@Override
							public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
								dialog.dismiss();

								String message = FacilicomNetworkClient.responseToString(responseBody);

								new AlertDialog.Builder(getActivity())
										.setTitle(R.string.message_label)
										.setMessage(message != null && !message.isEmpty() ? message : getString(R.string.server_error))
										.setNegativeButton(R.string.next_button, null)
										.show();
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
			break;
		}
	}

	public void save() {
		apply();
		DaoPersonRepository.insertOrUpdate(getActivity(), person);
		getActivity().finish();
	}
}
