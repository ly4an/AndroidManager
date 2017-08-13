package ru.facilicom24.manager.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import database.Person;
import database.PersonPhoto;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoPersonPhotoRepository;
import ru.facilicom24.manager.cache.DaoPersonRepository;
import ru.facilicom24.manager.dialogs.CheckDialog;
import ru.facilicom24.manager.dialogs.ImageDialog;
import ru.facilicom24.manager.fragments.CaptionFragment;
import ru.facilicom24.manager.fragments.PersonStage2Fragment;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.utils.NFAdapter;
import ru.facilicom24.manager.utils.NFItem;
import ru.facilicom24.manager.utils.NetworkHelper;
import ru.facilicom24.manager.views.FontButton;

public class AccessActivity
		extends FragmentActivity
		implements
		View.OnClickListener,
		ListView.OnItemClickListener,
		ImageDialog.IImageDialogListener,
		CheckDialog.ICheckDialogListener,
		DatePickerDialog.OnDateSetListener,
		CaptionFragment.OnFragmentInteractionListener {

	final static public String IMAGE_DIALOG_TAG = "AccessActivity_ImageDialog";

	final static String TAKE_IMAGE_MODE = "TakeImageMode";
	final static String TEMP_IMAGE_FILE_NAME = "TempImageFileName";
	final static String CHECK_DIALOG_TAG = "AccessActivity_CheckDialog";

	final static int ACCOUNT_INDEX = 0;
	final static int JOB_TITLE_INDEX = 1;
	final static int NAME_INDEX = 2;
	final static int DATE_INDEX = 3;

	final static int BANK_TITLE = 4;
	final static int BANK_INDEX = 5;
	final static int BANK_DATE_INDEX = 6;

	int photoRequestCode;
	int imageSemaphoreCounter;
	int datePickerDialogIndex;

	Person person;

	ListView form;
	NFAdapter adapter;
	FontButton photo5;

	Semaphore imageSemaphore;

	ArrayList<NFItem> items;

	void setTakeImageMode(Activity activity, TakeImageMode takeImageMode) {
		activity.getIntent().putExtra(TAKE_IMAGE_MODE, takeImageMode);
	}

	TakeImageMode getTakeImageMode(Activity activity) {
		return (TakeImageMode) activity.getIntent().getSerializableExtra(TAKE_IMAGE_MODE);
	}

	void setTempImageFileName(Activity activity, Uri tempImageFileName) {
		activity.getIntent().putExtra(TEMP_IMAGE_FILE_NAME, tempImageFileName);
	}

	Uri getTempImageFileName(Activity activity) {
		return (Uri) activity.getIntent().getParcelableExtra(TEMP_IMAGE_FILE_NAME);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_access);

		items = new ArrayList<>();

		items.add(new NFItem(NFItem.Type.Choose, "Объект", "Выбрать"));
		items.add(new NFItem(NFItem.Type.Choose, "Должность", "Выбрать"));
		items.add(new NFItem(NFItem.Type.Choose, "ФИО", "Выбрать"));
		items.add(new NFItem(NFItem.Type.Date, "Дата", "Выбрать"));

		items.add(new NFItem(NFItem.Type.Title, "Банковская карта", false));
		items.add(new NFItem(NFItem.Type.Choose, "Пункт выдачи карт", "Выбрать", false));
		items.add(new NFItem(NFItem.Type.Date, "Дата/время записи", "Нет", false));

		// Data

		List<Person> persons = DaoPersonRepository.getPersonsByType(this, PersonActivity.PERSON_TYPE_BIND);
		long photoCount5 = DaoPersonPhotoRepository.getPersonPhotoCountByType(this, PersonActivity.PHOTO_5);

		photo5 = (FontButton) findViewById(R.id.photo5);
		photo5.setText(TextUtils.concat(getString(R.string.personal_stage_photo5), " (", Long.toString(photoCount5), ")"));

		if (persons.size() == 0) {
			person = new Person();

			person.setDocumentType(PersonActivity.PERSON_TYPE_BIND);

			Calendar calendar = Calendar.getInstance();

			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			items.get(DATE_INDEX).setText(MobileActivity.dateToString(calendar.getTime()));
			person.setBindDate(calendar.getTime());

			person.setCard(false);
		} else {
			person = persons.get(0);

			if (person.getSubdivisionName() != null) {
				items.get(ACCOUNT_INDEX).setText(person.getSubdivisionName());
			}

			if (person.getJobTitleName() != null) {
				items.get(JOB_TITLE_INDEX).setText(person.getJobTitleName());
			}

			if (person.getPersonName() != null) {
				items.get(NAME_INDEX).setText(person.getPersonName());
			}

			if (person.getBindDate() != null) {
				items.get(DATE_INDEX).setText(MobileActivity.dateToString(person.getBindDate()));
			}

			if (person.getRegionUID() != null) {
				boolean visible = person.getRegionUID().equals(PersonStage2Fragment.MOSCOW_REGION_UID) && !person.getCard();

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

					photo5.setVisibility(View.VISIBLE);
				} else {
					photo5.setVisibility(View.GONE);
				}
			}
		}

		photo5.setOnClickListener(this);

		form = (ListView) findViewById(R.id.form);

		adapter = new NFAdapter(this, items);

		form.setAdapter(adapter);
		form.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.photo5: {
				photoRequestCode = PersonActivity.PHOTO_5;
				ImageDialog.newInstance().show(getSupportFragmentManager(), IMAGE_DIALOG_TAG);
			}
			break;
		}
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

			case JOB_TITLE_INDEX: {
				if (person.getSubdivisionUID() == null) {
					new AlertDialog.Builder(this)
							.setTitle(R.string.message_label)
							.setMessage(R.string.personal_no_subdivision)
							.setNegativeButton(R.string.next_button, null)
							.show();
				} else {
					Intent intent = new Intent(this, JobTitleActivity.class);
					intent.putExtra("SubdivisionUID", person.getSubdivisionUID());
					startActivityForResult(intent, JOB_TITLE_INDEX);
				}
			}
			break;

			case NAME_INDEX: {
				startActivityForResult(new Intent(this, PersonGetActivity.class), NAME_INDEX);
			}
			break;

			case DATE_INDEX: {
				datePickerDialogIndex = DATE_INDEX;

				Calendar calendar = Calendar.getInstance();

				if (person.getBindDate() != null) {
					calendar.setTime(person.getBindDate());
				} else {
					calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
					calendar.set(Calendar.MILLISECOND, 0);
				}

				new DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
			}
			break;

			case BANK_INDEX: {
				startActivityForResult(new Intent(this, BankActivity.class), BANK_INDEX);
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

				new DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
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
					items.get(JOB_TITLE_INDEX).setText("Выбрать");

					boolean cardBind = person.getRegionUID() != null;

					person.setSubdivisionUID(data.getStringExtra("UID"));
					person.setRegionUID(data.getStringExtra("RegionUID"));
					person.setSubdivisionName(data.getStringExtra("Name"));

					person.setJobTitleUID(null);
					person.setJobTitleName(null);

					if (cardBind) {
						cardBind();
					}
				}
				break;

				case JOB_TITLE_INDEX: {
					items.get(JOB_TITLE_INDEX).setText(data.getStringExtra("Name"));

					person.setJobTitleUID(data.getStringExtra("UID"));
					person.setJobTitleName(data.getStringExtra("Name"));
				}
				break;

				case NAME_INDEX: {
					items.get(NAME_INDEX).setText(data.getStringExtra("Name"));

					person.setPersonUID(data.getStringExtra("UID"));
					person.setPersonLocalUID(data.getStringExtra("UID"));

					person.setPersonName(data.getStringExtra("Name"));

					final ProgressDialog dialog = new ProgressDialog(this);

					dialog.setMessage(getString(R.string.personal_check_card));
					dialog.setCancelable(false);
					dialog.setCanceledOnTouchOutside(false);

					dialog.show();

					FacilicomNetworkClient.getCheckActiveCard(person.getPersonUID(), new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
							dialog.dismiss();

							try {
								person.setCard(new String(responseBody, "UTF-8").equals("true"));
								Toast.makeText(AccessActivity.this, person.getCard() ? R.string.personal_check_card_present : R.string.personal_check_card_not_present, Toast.LENGTH_LONG).show();

								cardBind();
							} catch (Exception exception) {
								exception.printStackTrace();
							}
						}

						@Override
						public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
							dialog.dismiss();

							String message = FacilicomNetworkClient.responseToString(responseBody);

							new AlertDialog.Builder(AccessActivity.this)
									.setTitle(R.string.message_label)
									.setMessage(message != null && !message.isEmpty() ? message : getString(R.string.server_error))
									.setNegativeButton(R.string.next_button, null)
									.show();
						}
					});
				}
				break;

				case BANK_INDEX: {
					items.get(BANK_INDEX).setText(data.getStringExtra("Name"));

					person.setBankUID(data.getStringExtra("UID"));
					person.setBankName(data.getStringExtra("Name"));
				}
				break;

				case PersonActivity.PHOTO_5: {
					personPhotoApply(PersonActivity.PHOTO_5, data);
				}
				break;
			}

			adapter.notifyDataSetChanged();
		}
	}

	void cardBind() {
		if (person.getPersonUID() != null) {
			boolean visible = person.getRegionUID().equals(PersonStage2Fragment.MOSCOW_REGION_UID) && !person.getCard();

			items.get(BANK_TITLE).setVisible(visible);
			items.get(BANK_INDEX).setVisible(visible);
			items.get(BANK_DATE_INDEX).setVisible(visible);

			if (visible) {
				photo5.setVisibility(View.VISIBLE);
			} else {
				items.get(BANK_INDEX).setText("Выбрать");
				items.get(BANK_DATE_INDEX).setText("Нет");

				person.setBankUID(null);
				person.setBankName(null);
				person.setBankDate(null);

				List<PersonPhoto> photos = DaoPersonPhotoRepository.getPersonPhotoByType(this, PersonActivity.PHOTO_5);

				for (PersonPhoto photo : photos) {
					DaoPersonPhotoRepository.clearPersonPhoto(this, photo);
				}

				photo5.setVisibility(View.GONE);
				photo5.setText(TextUtils.concat(getString(R.string.personal_stage_photo5), " (0)"));
			}
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
				person.setBindDate(calendar.getTime());
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
	public void onCamera() {
		setTakeImageMode(this, TakeImageMode.Photo);

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (intent.resolveActivity(this.getPackageManager()) != null) {
			File photoFile = FacilicomApplication.photoFileGenerate();

			if (photoFile != null) {
				Uri photoFileUri = Uri.fromFile(photoFile);

				setTempImageFileName(this, photoFileUri);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri);

				System.gc();
				startActivityForResult(intent, photoRequestCode);
			} else {
				Toast.makeText(this, R.string.create_image_file_error, Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(this, R.string.error_no_camera, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onGallery() {
		setTakeImageMode(this, TakeImageMode.Gallery);
		startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), photoRequestCode);
	}

	void personPhotoApply(int personPhotoType, Intent data) {
		switch (getTakeImageMode(this)) {
			case Photo: {
				PersonPhotoSave(personPhotoType);
			}
			break;

			case Gallery: {
				Uri selectedImage = data.getData();
				String[] filePathColumn = {MediaStore.Images.Media.DATA};

				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

				if (cursor != null && cursor.moveToFirst()) {
					String path = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
					cursor.close();

					setTempImageFileName(this, Uri.fromFile(new File(path)));

					PersonPhotoSave(personPhotoType);
				}
			}
			break;
		}
	}

	void PersonPhotoSave(int personPhotoType) {
		Uri tempImageFileName = getTempImageFileName(this);

		if (tempImageFileName != null && new File(tempImageFileName.getPath()).length() > 0) {
			PersonPhoto personPhoto = new PersonPhoto();

			personPhoto.setPersonId(person.getId());
			personPhoto.setPersonLocalUID(person.getPersonLocalUID());

			personPhoto.setImageLocalUID(UUID.randomUUID().toString());

			personPhoto.setPersonPhotoType(personPhotoType);
			personPhoto.setPersonPhotoUri(tempImageFileName.toString());

			DaoPersonPhotoRepository.insertOrUpdate(this, personPhoto);
			long photoCount = DaoPersonPhotoRepository.getPersonPhotoCountByType(this, personPhotoType);

			switch (personPhotoType) {
				case PersonActivity.PHOTO_5: {
					photo5.setText(TextUtils.concat(getString(R.string.personal_stage_photo5), " (", Long.toString(photoCount), ")"));
				}
				break;
			}
		} else {
			Toast.makeText(this, R.string.error_photo, Toast.LENGTH_LONG).show();
		}

		setTempImageFileName(this, null);
	}

	@Override
	public void onSaveBtnClicked() {
		captionFragmentOnSavePressed();
	}

	@Override
	public void onSendBtnClicked() {
		captionFragmentOnSendPressed();
	}

	@Override
	public void onDeleteBtnClicked() {
		delete();
	}

	void delete() {
		DaoPersonPhotoRepository.clearPersonPhotoBind(this);
		DaoPersonRepository.clearPersonBind(this);

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

		if (person.getJobTitleUID() == null) {
			messages.add(items.get(JOB_TITLE_INDEX).getName());
		}

		if (person.getPersonUID() == null) {
			messages.add(items.get(NAME_INDEX).getName());
		}

		if (person.getBindDate() == null) {
			messages.add(items.get(DATE_INDEX).getName());
		}

		if (items.get(BANK_TITLE).getVisible()) {
			if (person.getBankUID() == null) {
				if (DaoPersonPhotoRepository.getPersonPhotoCountByType(this, PersonActivity.PHOTO_5) == 0) {
					messages.add(getString(R.string.personal_no_photo_4));
				}
			} else {
				if (person.getBankDate() == null) {
					messages.add(items.get(BANK_DATE_INDEX).getName());
				}
			}
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

				dialog.setMessage(getString(R.string.personal_access_send));
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);

				dialog.show();

				FacilicomNetworkClient.postPerson(this, person, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
						imageSemaphore = new Semaphore(0);

						List<PersonPhoto> photos = DaoPersonPhotoRepository.getAllPersonPhotoBind(AccessActivity.this);

						new ImageAsyncTask().execute(photos);

						try {
							imageSemaphore.acquire(photos.size());
						} catch (Exception exception) {
							exception.printStackTrace();
						}

						if (imageSemaphoreCounter == photos.size()) {
							FacilicomNetworkClient.postPersonCommit(AccessActivity.this, person, new AsyncHttpResponseHandler() {

								@Override
								public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
									dialog.dismiss();

									String message = FacilicomNetworkClient.responseToString(responseBody);

									if (message != null && message.length() > 0) {
										new AlertDialog.Builder(AccessActivity.this)
												.setTitle(R.string.message_label)
												.setMessage(message)
												.setNegativeButton(R.string.back_button, null)
												.show();
									} else {
										delete();
										Toast.makeText(AccessActivity.this, R.string.personal_access_send_done, Toast.LENGTH_LONG).show();
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
		Intent intent = new Intent(this, PersonAlbumActivity.class);

		intent.putExtra(PersonAlbumActivity.NAME_PARAM, photo5.getText());
		intent.putExtra(PersonAlbumActivity.TYPE_PARAM, PersonActivity.PERSON_TYPE_BIND);

		startActivity(intent);
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
		return true;
	}

	private enum TakeImageMode {
		Photo,
		Gallery
	}

	private class ImageAsyncTask
			extends AsyncTask<List<PersonPhoto>, Void, Void> {

		@Override
		protected Void doInBackground(List<PersonPhoto>... params) {
			List<PersonPhoto> photos = params[0];

			imageSemaphoreCounter = 0;
			for (PersonPhoto photo : photos) {
				FacilicomNetworkClient.postPersonPhoto(AccessActivity.this, photo, new AsyncHttpResponseHandler() {

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
