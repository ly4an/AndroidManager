package ru.facilicom24.manager.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import database.ActAccount;
import database.ActServiceType;
import database.ActServiceTypeDao;
import database.CleaningReason;
import database.CleaningReasonDao;
import database.Client;
import database.DaoSession;
import database.Request;
import database.RequestPhoto;
import database.RequestPhotoDao;
import database.WorkType;
import database.WorkTypeDao;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.DaoActAccountRepository;
import ru.facilicom24.manager.cache.DaoClientRepository;
import ru.facilicom24.manager.dialogs.CheckDialog;
import ru.facilicom24.manager.dialogs.ImageDialog;
import ru.facilicom24.manager.fragments.CaptionFragment;
import ru.facilicom24.manager.model.Photo;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.utils.NFAdapter;
import ru.facilicom24.manager.utils.NFItem;
import ru.facilicom24.manager.utils.NetworkHelper;

public class RequestNewActivity
		extends FragmentActivity
		implements
		View.OnClickListener,
		AdapterView.OnItemClickListener,
		CheckDialog.ICheckDialogListener,
		ImageDialog.IImageDialogListener,
		CaptionFragment.OnFragmentInteractionListener {

	final static public int URGENCY = 3;
	final static public int WORK_TYPE = 4;
	final static public int CLEANING_REASON = 5;

	final static int CLIENT = 0;
	final static int ACCOUNT = 1;
	final static int CATEGORY = 2;
	final static int DESIRED_DATE = 6;
	final static int ARRIVAL_TIME = 7;
	final static int REQUEST_NEW_NOTE = 8;

	final static int REQUEST_PHOTO = 9;
	final static int REQUEST_ALBUM = 10;
	final static int ALBUM_VIEW = 11;

	final static String TYPE_CREATE = "Create";

	final static String CATEGORY_TO = "ea64052f-16ee-4160-a124-9fa9d5099976";
	final static String CATEGORY_EU = "412c9370-6678-4e05-bd78-2c121892b667";
	final static String CATEGORY_OS = "85693b90-7090-4529-af24-985019c8f9fa";

	File photoFile;
	Request request;
	NFAdapter adapter;
	ArrayList<NFItem> items;

	String defaultNo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_request_new);

		List<Request> requests = FacilicomApplication.getInstance().getDaoSession().getRequestDao().loadAll();

		if (requests != null && requests.size() > 0) {
			request = requests.get(0);
		} else {
			request = new Request();

			request.setType(TYPE_CREATE);
			request.setServiceRequestUID(UUID.randomUUID().toString());

			FacilicomApplication.getInstance().getDaoSession().getRequestDao().insertOrReplace(request);
		}

		String clientName = null;
		String accountName = null;
		String serviceTypeName = null;
		String workTypeName = null;
		String cleaningReasonName = null;
		String arrivalTimeRequired = null;

		if (request.getClientID() != null) {
			List<Client> clients = DaoClientRepository.getClientForClientId(this, request.getClientID());

			if (clients != null && clients.size() > 0) {
				clientName = clients.get(0).getName();
			}
		}

		if (request.getDirectumID() != null) {
			List<ActAccount> accounts = DaoActAccountRepository.getActAccountForDirectumId(this, request.getDirectumID());

			if (accounts != null && accounts.size() > 0) {
				ActAccount account = accounts.get(0);
				accountName = TextUtils.concat(account.getName(), ". ", account.getAddress()).toString();
			}
		}

		DaoSession daoSession = FacilicomApplication.getInstance().getDaoSession();

		if (request.getServiceTypeUID() != null) {
			List<ActServiceType> serviceTypes = daoSession.getActServiceTypeDao().queryBuilder()
					.where(ActServiceTypeDao.Properties.ServiceTypeId.eq(request.getServiceTypeUID()))
					.list();

			if (serviceTypes != null && serviceTypes.size() > 0) {
				serviceTypeName = serviceTypes.get(0).getName();
			}
		}

		if (request.getWorkTypeUID() != null) {
			List<WorkType> workTypes = daoSession.getWorkTypeDao().queryBuilder()
					.where(WorkTypeDao.Properties.SystemGUID.eq(request.getWorkTypeUID()))
					.list();

			if (workTypes != null && !workTypes.isEmpty()) {
				workTypeName = workTypes.get(0).getSystemName();
			}
		}

		if (request.getCleaningReasonID() != null) {
			List<CleaningReason> cleaningReasons = daoSession.getCleaningReasonDao().queryBuilder()
					.where(CleaningReasonDao.Properties.ReasonID.eq(request.getCleaningReasonID()))
					.list();

			if (cleaningReasons != null && !cleaningReasons.isEmpty()) {
				cleaningReasonName = cleaningReasons.get(0).getReasonName();
			}
		}

		if (request.getDesiredDate() != null && request.getArrivalTimeRequired() != null && request.getArrivalTimeRequired() == 1) {
			arrivalTimeRequired = FacilicomApplication.dateTimeFormat8.format(request.getDesiredDate());
		}

		defaultNo = getString(R.string.default_no);

		items = new ArrayList<>();

		items.add(new NFItem(NFItem.Type.Choose, "Клиент", clientName != null ? clientName : defaultNo));
		items.add(new NFItem(NFItem.Type.Choose, "Объект", accountName != null ? accountName : defaultNo));
		items.add(new NFItem(NFItem.Type.Choose, "Вид деятельности", serviceTypeName != null ? serviceTypeName : defaultNo));

		items.add(new NFItem(NFItem.Type.Choose, "Тип", request.getUrgencyTypeName() != null ? request.getUrgencyTypeName() : defaultNo));
		items.add(new NFItem(NFItem.Type.Choose, "Вид работ", workTypeName != null ? workTypeName : defaultNo));

		items.add(new NFItem(NFItem.Type.Choose, "Причина", cleaningReasonName != null ? cleaningReasonName : defaultNo));

		items.add(new NFItem(NFItem.Type.Choose, "Желаемая дата", request.getDesiredDate() != null ? FacilicomApplication.dateTimeFormat4.format(request.getDesiredDate()) : defaultNo));
		items.add(new NFItem(NFItem.Type.Choose, "Требуется строгое\nприбытие ко времени", arrivalTimeRequired != null ? arrivalTimeRequired : defaultNo));

		items.add(new NFItem(NFItem.Type.Layout, R.layout.fragment_request_new, new NFItem.ILayoutBase() {

			@Override
			public void OnCreate(View view) {
				TextView noteTextView = (TextView) view.findViewById(R.id.noteTextView);

				noteTextView.setText(request.getContent());
				noteTextView.setOnClickListener(RequestNewActivity.this);

				view.findViewById(R.id.applyFontButton).setOnClickListener(RequestNewActivity.this);

				ImageButton photoImageButton = (ImageButton) view.findViewById(R.id.photoImageButton);

				photoImageButton.setOnClickListener(RequestNewActivity.this);
				photoImageButton.setImageResource(FacilicomApplication.getThemeIcon(RequestNewActivity.this, R.drawable.photo_btn));
			}
		}));

		adapter = new NFAdapter(this, items);

		ListView taskListView = (ListView) findViewById(R.id.requestListView);

		taskListView.setAdapter(adapter);
		taskListView.setOnItemClickListener(this);

		menuReset(false);
	}

	@Override
	public void onBackPressed() {
		if (request.getChanged() != null && request.getChanged()) {
			CheckDialog.newInstance(false).show(getSupportFragmentManager(), "CheckDialog");
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.noteTextView: {
				Intent intent = new Intent(this, TextActivity.class);

				intent.putExtra("Caption", getString(R.string.fragment_request_new_caption));
				intent.putExtra("Text", request.getContent());

				startActivityForResult(intent, REQUEST_NEW_NOTE);
			}
			break;

			case R.id.applyFontButton: {
				apply();
			}
			break;

			case R.id.photoImageButton: {
				ImageDialog.newInstance().show(getSupportFragmentManager(), "ImageDialog");
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		switch (i) {
			case CLIENT: {
				startActivityForResult(new Intent(this, CheckClientActivity.class), CLIENT);
			}
			break;

			case ACCOUNT: {
				Intent intent = new Intent(this, CheckActAccountActivity.class);

				if (request.getClientID() != null) {
					intent.putExtra("ClientId", request.getClientID());
				}

				startActivityForResult(intent, ACCOUNT);
			}
			break;

			case CATEGORY: {
				if (request.getDirectumID() != null) {
					Intent intent = new Intent(this, CheckActServiceTypeActivity.class);

					if (request.getClientID() != null) {
						intent.putExtra("ClientId", request.getClientID());
					}

					if (request.getDirectumID() != null) {
						intent.putExtra("AccountId", request.getDirectumID());
					}

					startActivityForResult(intent, CATEGORY);
				} else {
					Toast.makeText(this, R.string.task_main_no_client_account, Toast.LENGTH_LONG).show();
				}
			}
			break;

			case URGENCY: {
				if (request.getDirectumID() != null) {
					startActivityForResult(new Intent(this, DictionaryActivity.class)
									.putExtra(DictionaryActivity.REQUEST_CODE, URGENCY)
									.putExtra(DictionaryActivity.PARAMETER_DIRECTUM_ID, request.getDirectumID()),
							URGENCY);
				} else {
					Toast.makeText(this, R.string.task_main_no_client_account, Toast.LENGTH_LONG).show();
				}
			}
			break;

			case WORK_TYPE: {
				startActivityForResult(new Intent(this, DictionaryActivity.class)
								.putExtra(DictionaryActivity.REQUEST_CODE, WORK_TYPE),
						WORK_TYPE);
			}
			break;

			case CLEANING_REASON: {
				startActivityForResult(new Intent(this, DictionaryActivity.class)
								.putExtra(DictionaryActivity.REQUEST_CODE, CLEANING_REASON),
						CLEANING_REASON);
			}
			break;

			case DESIRED_DATE: {
				final Calendar calendar = Calendar.getInstance();

				if (request.getDesiredDate() != null) {
					calendar.setTime(request.getDesiredDate());
				}

				new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
						calendar.set(year, month, dayOfMonth, 0, 0, 0);
						calendar.set(Calendar.MILLISECOND, 0);

						request.setDesiredDate(calendar.getTime());
						request.setArrivalTimeRequired(null);

						items.get(DESIRED_DATE).setText(FacilicomApplication.dateTimeFormat4.format(request.getDesiredDate()));
						items.get(ARRIVAL_TIME).setText(defaultNo);
						request.setChanged(true);

						adapter.notifyDataSetChanged();
					}
				}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
			}
			break;

			case ARRIVAL_TIME: {
				if (request.getDesiredDate() != null) {
					final Calendar calendar = Calendar.getInstance();
					calendar.setTime(request.getDesiredDate());

					new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

						@Override
						public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
							calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
							calendar.set(Calendar.MINUTE, minute);

							request.setDesiredDate(calendar.getTime());
							request.setArrivalTimeRequired(1);
							request.setChanged(true);

							items.get(ARRIVAL_TIME).setText(FacilicomApplication.dateTimeFormat8.format(request.getDesiredDate()));

							adapter.notifyDataSetChanged();
						}
					}, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
				} else {
					Toast.makeText(this, R.string.request_new_arrival_time_message, Toast.LENGTH_LONG).show();
				}
			}
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (data != null) {
				switch (requestCode) {
					case CLIENT: {
						request.setClientID(data.getIntExtra("ClientId", 0));
						request.setChanged(true);

						items.get(CLIENT).setText(data.getStringExtra("Title"));

						request.setDirectumID(null);
						request.setServiceTypeUID(null);

						items.get(ACCOUNT).setText(defaultNo);
						items.get(CATEGORY).setText(defaultNo);

						menuReset();

						adapter.notifyDataSetChanged();
					}
					break;

					case ACCOUNT: {
						request.setDirectumID(data.getIntExtra("AccountId", 0));
						request.setChanged(true);

						items.get(ACCOUNT).setText(TextUtils.concat(data.getStringExtra("Title"), ". ", data.getStringExtra("Address")).toString());

						request.setServiceTypeUID(null);
						items.get(CATEGORY).setText(defaultNo);

						menuReset();

						adapter.notifyDataSetChanged();
					}
					break;

					case CATEGORY: {
						request.setServiceTypeUID(data.getStringExtra("ActTypeId"));
						request.setChanged(true);

						menuReset();

						items.get(CATEGORY).setText(data.getStringExtra("Title"));
						adapter.notifyDataSetChanged();
					}
					break;

					case URGENCY: {
						request.setUrgencyTypeName(data.getStringExtra(DictionaryActivity.KEY));
						request.setChanged(true);

						items.get(URGENCY).setText(data.getStringExtra(DictionaryActivity.VALUE));
						adapter.notifyDataSetChanged();
					}
					break;

					case WORK_TYPE: {
						request.setWorkTypeUID(data.getStringExtra(DictionaryActivity.KEY));
						request.setChanged(true);

						items.get(WORK_TYPE).setText(data.getStringExtra(DictionaryActivity.VALUE));
						adapter.notifyDataSetChanged();
					}
					break;

					case CLEANING_REASON: {
						request.setCleaningReasonID((Integer) data.getSerializableExtra(DictionaryActivity.KEY));
						request.setChanged(true);

						items.get(CLEANING_REASON).setText(data.getStringExtra(DictionaryActivity.VALUE));
						adapter.notifyDataSetChanged();
					}
					break;

					case REQUEST_NEW_NOTE: {
						request.setContent(data.getStringExtra("Text"));
						request.setChanged(true);

						adapter.notifyDataSetChanged();
					}
					break;

					case REQUEST_ALBUM: {
						Cursor cursor = getContentResolver().query(data.getData(), new String[]{MediaStore.Images.Media.DATA}, null, null, null);

						if (cursor != null && cursor.moveToFirst()) {
							FacilicomApplication.getInstance().getDaoSession().getRequestPhotoDao().insert(new RequestPhoto(
									null,
									request.getId(),
									cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
							));

							cursor.close();
							request.setChanged(true);
						}
					}
					break;
				}
			} else {
				switch (requestCode) {
					case REQUEST_PHOTO: {
						if (photoFile != null && photoFile.exists() && photoFile.length() > 0) {
							FacilicomApplication.getInstance().getDaoSession().getRequestPhotoDao().insert(new RequestPhoto(
									null,
									request.getId(),
									photoFile.toString()
							));

							request.setChanged(true);
						}
					}
					break;
				}
			}
		}
	}

	void menuReset() {
		menuReset(true);
	}

	void menuReset(boolean resetValues) {
		if (resetValues) {
			request.setUrgencyTypeName(null);
			request.setWorkTypeUID(null);
			request.setCleaningReasonID(null);

			request.setDesiredDate(null);
			request.setArrivalTimeRequired(null);

			items.get(URGENCY).setText(defaultNo);
			items.get(WORK_TYPE).setText(defaultNo);
			items.get(CLEANING_REASON).setText(defaultNo);

			items.get(DESIRED_DATE).setText(defaultNo);
			items.get(ARRIVAL_TIME).setText(defaultNo);

			adapter.notifyDataSetChanged();
		}

		if (request.getServiceTypeUID() != null) {
			switch (request.getServiceTypeUID()) {
				case CATEGORY_TO: {
					items.get(URGENCY).setVisible(true);
					items.get(WORK_TYPE).setVisible(true);
					items.get(CLEANING_REASON).setVisible(false);

				}
				break;

				case CATEGORY_EU:
				case CATEGORY_OS: {
					items.get(URGENCY).setVisible(false);
					items.get(WORK_TYPE).setVisible(false);
					items.get(CLEANING_REASON).setVisible(true);
				}
				break;
			}

			items.get(DESIRED_DATE).setVisible(true);
			items.get(ARRIVAL_TIME).setVisible(true);
		} else {
			items.get(URGENCY).setVisible(false);
			items.get(WORK_TYPE).setVisible(false);
			items.get(CLEANING_REASON).setVisible(false);

			items.get(DESIRED_DATE).setVisible(false);
			items.get(ARRIVAL_TIME).setVisible(false);
		}
	}

	@Override
	public void onSendBtnClicked() {
	}

	@Override
	public void onSaveBtnClicked() {
		FacilicomApplication.getInstance().getDaoSession().getRequestDao().insertOrReplace(request);
		finish();
	}

	@Override
	public void onDeleteBtnClicked() {
		delete();
	}

	void delete() {
		FacilicomApplication.getInstance().getDaoSession().getRequestPhotoDao().queryBuilder()
				.where(RequestPhotoDao.Properties.RequestID.eq(request.getId()))
				.buildDelete()
				.executeDeleteWithoutDetachingEntities();

		FacilicomApplication.getInstance().getDaoSession().getRequestDao().delete(request);

		finish();
	}

	@Override
	public void onCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (intent.resolveActivity(getPackageManager()) != null) {
			try {
				photoFile = FacilicomApplication.photoFileGenerate();

				if (photoFile != null) {
					System.gc();
					startActivityForResult(intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile)), REQUEST_PHOTO);
				} else {
					Toast.makeText(this, R.string.create_image_file_error, Toast.LENGTH_LONG).show();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} else {
			Toast.makeText(this, R.string.error_no_camera, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onGallery() {
		startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), REQUEST_ALBUM);
	}

	void apply() {
		ArrayList<String> messages = new ArrayList<>();

		if (request.getDirectumID() == null) {
			messages.add(items.get(ACCOUNT).getName());
		}

		if (request.getServiceTypeUID() == null) {
			messages.add(items.get(CATEGORY).getName());
		} else {
			switch (request.getServiceTypeUID()) {
				case CATEGORY_TO: {
					if (request.getUrgencyTypeName() == null) {
						messages.add(items.get(URGENCY).getName());
					}

					if (request.getWorkTypeUID() == null) {
						messages.add(items.get(WORK_TYPE).getName());
					}
				}
				break;

				case CATEGORY_EU:
				case CATEGORY_OS: {
					if (request.getCleaningReasonID() == null) {
						messages.add(items.get(CLEANING_REASON).getName());
					}
				}
				break;
			}
		}

		if (request.getContent() == null || request.getContent().length() == 0) {
			messages.add(getString(R.string.fragment_request_new_caption));
		}

		if (messages.size() == 0) {
			if (NetworkHelper.isConnected(this)) {
				JSONObject jsonObject = new JSONObject();

				Date desiredDate = request.getDesiredDate();

				if (desiredDate != null) {
					desiredDate.setTime(desiredDate.getTime() - TimeZone.getDefault().getOffset(new Date().getTime()));
				}

				try {
					jsonObject.put("Type", request.getType());
					jsonObject.put("ServiceRequestUID", request.getServiceRequestUID());
					jsonObject.put("DirectumID", request.getDirectumID());
					jsonObject.put("ServiceTypeUID", request.getServiceTypeUID());

					jsonObject.put("UrgencyTypeName", request.getUrgencyTypeName());
					jsonObject.put("WorkTypeUID", request.getWorkTypeUID());
					jsonObject.put("CleaningReasonID", request.getCleaningReasonID());

					jsonObject.put("DesiredDate", FacilicomApplication.dateTimeFormat2.format(desiredDate));
					jsonObject.put("ArrivalTimeRequired", request.getArrivalTimeRequired());

					jsonObject.put("Content", request.getContent());

					//

					final ProgressDialog progressDialog = new ProgressDialog(this);

					progressDialog.setCancelable(false);
					progressDialog.setCanceledOnTouchOutside(false);

					progressDialog.setMessage(getString(R.string.request_new_progress));

					progressDialog.show();
					FacilicomNetworkClient.postRequest(this, jsonObject, new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
							try {
								int internalServiceRequestID = new JSONObject(new String(responseBody, "UTF-8")).getInt("ID");

								List<RequestPhoto> photos = FacilicomApplication.getInstance().getDaoSession().getRequestPhotoDao().queryBuilder()
										.where(RequestPhotoDao.Properties.RequestID.eq(request.getId()))
										.list();

								applyPhoto(progressDialog, internalServiceRequestID, photos);
							} catch (Exception exception) {
								exception.printStackTrace();
								applyEnd(progressDialog);
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
							progressDialog.dismiss();
							Toast.makeText(RequestNewActivity.this, R.string.request_new_error, Toast.LENGTH_LONG).show();
						}
					});
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			} else {
				new AlertDialog.Builder(this)
						.setIcon(R.drawable.ic_info_white_48dp)
						.setTitle(R.string.message)
						.setMessage(R.string.errorConnection)
						.setPositiveButton(R.string.mobile_next, null)
						.show();
			}
		} else {
			new AlertDialog.Builder(this)
					.setIcon(R.drawable.ic_info_white_48dp)
					.setTitle(R.string.request_new_validate_title)
					.setMessage(TextUtils.join(", ", messages))
					.setPositiveButton(R.string.mobile_next, null)
					.show();
		}
	}

	void applyPhoto(final ProgressDialog progressDialog, final int internalServiceRequestID, final List<RequestPhoto> photos) {
		if (photos != null && photos.size() > 0) {
			RequestPhoto requestPhoto = photos.remove(0);

			if (requestPhoto != null && requestPhoto.getPhotoFileName() != null) {
				File photoFile = new File(requestPhoto.getPhotoFileName());

				long length;
				if (photoFile.exists() && (length = photoFile.length()) > 0) {
					byte[] photoBytes = new byte[(int) length];

					try {
						BufferedInputStream stream = new BufferedInputStream(new FileInputStream(photoFile));
						int readed = stream.read(photoBytes);
						stream.close();

						if (readed == photoBytes.length) {
							JSONObject jsonObject = new JSONObject();

							jsonObject.put("Data", Base64.encodeToString(photoBytes, Base64.DEFAULT));
							jsonObject.put("Path", requestPhoto.getPhotoFileName());
							jsonObject.put("FileLocalUID", UUID.randomUUID().toString());
							jsonObject.put("InternalServiceRequestID", internalServiceRequestID);

							FacilicomNetworkClient.postRequestFile(this, jsonObject, new AsyncHttpResponseHandler() {
								@Override
								public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
									applyPhoto(progressDialog, internalServiceRequestID, photos);
								}

								@Override
								public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
									// Error
									applyPhoto(progressDialog, internalServiceRequestID, photos);
								}
							});
						} else {
							// Error
							applyPhoto(progressDialog, internalServiceRequestID, photos);
						}
					} catch (Exception exception) {
						exception.printStackTrace();

						// Error
						applyPhoto(progressDialog, internalServiceRequestID, photos);
					}
				} else {
					applyPhoto(progressDialog, internalServiceRequestID, photos);
				}
			} else {
				applyPhoto(progressDialog, internalServiceRequestID, photos);
			}
		} else {
			applyEnd(progressDialog);
		}
	}

	void applyEnd(ProgressDialog progressDialog) {
		progressDialog.dismiss();
		Toast.makeText(RequestNewActivity.this, R.string.request_new_done, Toast.LENGTH_LONG).show();

		delete();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	@Override
	public void captionFragmentOnSendPressed() {
	}

	@Override
	public void captionFragmentOnSavePressed() {
	}

	@Override
	public void captionFragmentOnHistoryPressed() {
	}

	@Override
	public void captionFragmentOnAlbumPressed() {
		List<RequestPhoto> photos = FacilicomApplication.getInstance().getDaoSession().getRequestPhotoDao().queryBuilder()
				.where(RequestPhotoDao.Properties.RequestID.eq(request.getId()))
				.list();

		ArrayList<Photo> extraPhotos = new ArrayList<>();

		if (photos != null && photos.size() > 0) {
			for (RequestPhoto photo : photos) {
				extraPhotos.add(new Photo(TextUtils.concat("file://", photo.getPhotoFileName()).toString()));
			}
		}

		Intent intent = new Intent(this, PhotoAlbumActivity.class);

		intent.putExtra(PhotoAlbumActivity.FORM_EXTRA, getString(R.string.request_new_photo));
		intent.putExtra(PhotoAlbumActivity.OBJECT_EXTRA, items.get(ACCOUNT).getText());
		intent.putExtra(PhotoAlbumActivity.PHOTOS_EXTRA, extraPhotos);

		startActivityForResult(intent, ALBUM_VIEW);
	}

	@Override
	public boolean backIcon() {
		return true;
	}

	@Override
	public boolean sendIcon() {
		return false;
	}

	@Override
	public boolean saveIcon() {
		return false;
	}

	@Override
	public boolean historyIcon() {
		return false;
	}

	@Override
	public boolean albumIcon() {
		return true;
	}
}
