package ru.facilicom24.manager.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import database.ServiceRequest;
import database.ServiceRequestFile;
import database.ServiceRequestPhoto;
import database.ServiceRequestPhotoDao;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.dialogs.ImageDialog;
import ru.facilicom24.manager.fragments.CaptionFragment;
import ru.facilicom24.manager.model.Photo;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.utils.NFAdapter;
import ru.facilicom24.manager.utils.NFItem;
import ru.facilicom24.manager.utils.NetworkHelper;

public class RequestEditActivity
		extends FragmentActivity
		implements
		View.OnClickListener,
		ListView.OnItemClickListener,
		ImageDialog.IImageDialogListener,
		CaptionFragment.OnFragmentInteractionListener {

	final static public String REQUEST_ID = "REQUEST_ID";
	final static public String REQUEST_MODE = "REQUEST_MODE";

	final static int REQUEST_NEW_NOTE = 1;
	final static int REQUEST_PHOTO = 2;
	final static int REQUEST_ALBUM = 3;
	final static int ALBUM_VIEW = 4;

	final static String TYPE_SERVICED = "Serviced";
	final static String TYPE_ACCEPT = "Accept";
	final static String TYPE_REWORK = "Rework";

	File photoFile;
	NFAdapter adapter;
	ArrayList<NFItem> items;
	ServiceRequest serviceRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_request_edit);

		serviceRequest = FacilicomApplication.getInstance().getDaoSession().getServiceRequestDao().load(getIntent().getLongExtra(REQUEST_ID, 0));

		if (serviceRequest != null) {
			((TextView) findViewById(R.id.titleFontTextView)).setText(String.format(getString(R.string.request_edit_caption), serviceRequest.getID()));

			items = new ArrayList<>();

			items.add(new NFItem(NFItem.Type.Value, "Объект", serviceRequest.getFacilityName()));
			items.add(new NFItem(NFItem.Type.Value, "Адрес", serviceRequest.getFacilityAddress()));
			items.add(new NFItem(NFItem.Type.Value, "Дата создания", serviceRequest.getCreatedOn()));
			items.add(new NFItem(NFItem.Type.Value, "Крайний срок", serviceRequest.getDueDate()));
			items.add(new NFItem(NFItem.Type.Value, "Тип", serviceRequest.getServiceTypeName()));

			items.add(new NFItem(NFItem.Type.Title, "Содержание"));
			items.add(new NFItem(NFItem.Type.Layout, R.layout.fragment_request_text, new NFItem.ILayoutBase() {

				@Override
				public void OnCreate(View view) {
					((TextView) view.findViewById(R.id.noteTextView)).setText(serviceRequest.getContent());
				}
			}));

			ListView serviceRequestListView = (ListView) findViewById(R.id.serviceRequestListView);

			serviceRequest.resetFiles();
			if (serviceRequest.getFiles() != null && serviceRequest.getFiles().size() > 0) {

				boolean document = false;
				for (ServiceRequestFile serviceRequestFile : serviceRequest.getFiles()) {

					if (serviceRequestFile != null
							&& serviceRequestFile.getExt() != null
							&& serviceRequestFile.getServiceRequestFileID() != null

							&& !serviceRequestFile.getExt().toLowerCase().equals(".jpg")
							&& !serviceRequestFile.getExt().toLowerCase().equals(".jpeg")
							&& !serviceRequestFile.getExt().toLowerCase().equals(".png")) {

						items.add(new NFItem(
								NFItem.Type.Link,
								TextUtils.concat("Документ (", serviceRequestFile.getExt().substring(1).toUpperCase(), ")").toString(),
								"Загрузить",
								serviceRequestFile.getServiceRequestFileID()
						));

						document = true;
					}
				}

				if (document) {
					serviceRequestListView.setOnItemClickListener(this);
				}
			}

			FacilicomApplication.getInstance().getDaoSession().getServiceRequestPhotoDao().queryBuilder()
					.where(ServiceRequestPhotoDao.Properties.ServiceRequestFileID.isNull())
					.buildDelete()
					.executeDeleteWithoutDetachingEntities();

			final RequestListActivity.Mode mode = (RequestListActivity.Mode) getIntent().getSerializableExtra(REQUEST_MODE);

			if (mode.equals(RequestListActivity.Mode.Execute) || mode.equals(RequestListActivity.Mode.Mark)) {
				items.add(new NFItem(NFItem.Type.Layout, R.layout.fragment_request_edit, new NFItem.ILayoutBase() {

					@Override
					public void OnCreate(View view) {
						TextView noteTextView = (TextView) view.findViewById(R.id.noteTextView);

						noteTextView.setText(serviceRequest.getComment());
						noteTextView.setOnClickListener(RequestEditActivity.this);

						View applyFontButton = view.findViewById(R.id.applyFontButton);
						View allowFontButton = view.findViewById(R.id.allowFontButton);
						View disallowFontButton = view.findViewById(R.id.disallowFontButton);

						if (mode.equals(RequestListActivity.Mode.Execute)) {
							applyFontButton.setVisibility(View.VISIBLE);
							applyFontButton.setOnClickListener(RequestEditActivity.this);

							allowFontButton.setVisibility(View.GONE);
							disallowFontButton.setVisibility(View.GONE);
						} else if (mode.equals(RequestListActivity.Mode.Mark)) {
							applyFontButton.setVisibility(View.GONE);

							allowFontButton.setVisibility(View.VISIBLE);
							allowFontButton.setOnClickListener(RequestEditActivity.this);

							disallowFontButton.setVisibility(View.VISIBLE);
							disallowFontButton.setOnClickListener(RequestEditActivity.this);
						}

						ImageButton photoImageButton = (ImageButton) view.findViewById(R.id.photoImageButton);

						photoImageButton.setOnClickListener(RequestEditActivity.this);
						photoImageButton.setImageResource(FacilicomApplication.getThemeIcon(RequestEditActivity.this, R.drawable.photo_btn));
					}
				}));
			}

			adapter = new NFAdapter(this, items);
			serviceRequestListView.setAdapter(adapter);
		} else {
			findViewById(R.id.titleFontTextView).setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.noteTextView: {
				Intent intent = new Intent(this, TextActivity.class);

				intent.putExtra("Caption", getString(R.string.fragment_request_edit_caption));
				intent.putExtra("Text", serviceRequest.getComment());

				startActivityForResult(intent, REQUEST_NEW_NOTE);
			}
			break;

			case R.id.photoImageButton: {
				ImageDialog.newInstance().show(getSupportFragmentManager(), "ImageDialog");
			}
			break;

			case R.id.applyFontButton: {
				apply(TYPE_SERVICED, R.string.request_edit_progress, R.string.request_edit_error);
			}
			break;

			case R.id.allowFontButton: {
				apply(TYPE_ACCEPT, R.string.request_accept_progress, R.string.request_accept_error);
			}
			break;

			case R.id.disallowFontButton: {
				apply(TYPE_REWORK, R.string.request_rework_progress, R.string.request_rework_error);
			}
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (data != null) {
				switch (requestCode) {
					case REQUEST_NEW_NOTE: {
						serviceRequest.setComment(data.getStringExtra("Text"));
						adapter.notifyDataSetChanged();
					}
					break;

					case REQUEST_ALBUM: {
						Cursor cursor = getContentResolver().query(data.getData(), new String[]{MediaStore.Images.Media.DATA}, null, null, null);

						if (cursor != null && cursor.moveToFirst()) {
							FacilicomApplication.getInstance().getDaoSession().getServiceRequestPhotoDao().insert(new ServiceRequestPhoto(
									null,
									serviceRequest.getId(),
									null,
									cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
							));

							cursor.close();
						}
					}
					break;
				}
			} else {
				switch (requestCode) {
					case REQUEST_PHOTO: {
						if (photoFile != null && photoFile.exists() && photoFile.length() > 0) {
							FacilicomApplication.getInstance().getDaoSession().getServiceRequestPhotoDao().insert(new ServiceRequestPhoto(
									null,
									serviceRequest.getId(),
									null,
									photoFile.toString()
							));
						}
					}
					break;
				}
			}
		}
	}

	@Override
	public void onCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (intent.resolveActivity(getPackageManager()) != null) {
			try {
				photoFile = FacilicomApplication.photoFileGenerate();

				System.gc();
				startActivityForResult(intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile)), REQUEST_PHOTO);
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

	void apply(final String type, int messageId, final int errorMessageId) {
		ArrayList<String> messages = new ArrayList<>();

		if ((type.equals(TYPE_SERVICED) || type.equals(TYPE_REWORK))
				&& (serviceRequest.getComment() == null || serviceRequest.getComment().length() == 0)) {

			messages.add(getString(R.string.fragment_request_edit_caption));
		}

		if (messages.size() == 0) {
			if (NetworkHelper.isConnected(this)) {
				JSONObject jsonObject = new JSONObject();

				try {
					jsonObject.put("Type", type);
					jsonObject.put("InternalServiceRequestID", serviceRequest.getID());

					if (serviceRequest.getComment() != null && serviceRequest.getComment().length() > 0) {
						jsonObject.put("Comment", serviceRequest.getComment());
					}

					//

					final ProgressDialog progressDialog = new ProgressDialog(this);

					progressDialog.setCancelable(false);
					progressDialog.setCanceledOnTouchOutside(false);

					progressDialog.setMessage(getString(messageId));

					progressDialog.show();
					FacilicomNetworkClient.postRequest(this, jsonObject, new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
							try {
								applyPhoto(
										type,
										progressDialog,
										new JSONObject(new String(responseBody, "UTF-8")).getInt("ID"),
										serviceRequest.getPhotos()
								);
							} catch (Exception exception) {
								exception.printStackTrace();
								applyEnd(type, progressDialog);
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
							progressDialog.dismiss();
							Toast.makeText(RequestEditActivity.this, errorMessageId, Toast.LENGTH_LONG).show();
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

	void applyPhoto(final String type, final ProgressDialog progressDialog, final int internalServiceRequestID, final List<ServiceRequestPhoto> photos) {
		if (photos != null && photos.size() > 0) {
			ServiceRequestPhoto requestPhoto = photos.remove(0);

			if (requestPhoto != null && requestPhoto.getServiceRequestFileID() == null && requestPhoto.getPhotoFileName() != null) {
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
									applyPhoto(type, progressDialog, internalServiceRequestID, photos);
								}

								@Override
								public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
									// Error
									applyPhoto(type, progressDialog, internalServiceRequestID, photos);
								}
							});
						} else {
							// Error
							applyPhoto(type, progressDialog, internalServiceRequestID, photos);
						}
					} catch (Exception exception) {
						exception.printStackTrace();

						// Error
						applyPhoto(type, progressDialog, internalServiceRequestID, photos);
					}
				} else {
					applyPhoto(type, progressDialog, internalServiceRequestID, photos);
				}
			} else {
				applyPhoto(type, progressDialog, internalServiceRequestID, photos);
			}
		} else {
			applyEnd(type, progressDialog);
		}
	}

	void applyEnd(String type, ProgressDialog progressDialog) {
		progressDialog.dismiss();
		Toast.makeText(RequestEditActivity.this, R.string.request_edit_done, Toast.LENGTH_LONG).show();

		if (type.equals(TYPE_SERVICED)) {
			serviceRequest.setCanExecute(0);
			serviceRequest.setStatus(RequestListActivity.TYPE_SERVICED);
		} else if (type.equals(TYPE_ACCEPT)) {
			serviceRequest.setNeedEvaluate(0);
		} else if (type.equals(TYPE_REWORK)) {
			serviceRequest.setNeedEvaluate(0);
			serviceRequest.setStatus(RequestListActivity.TYPE_REWORK);
		}

		FacilicomApplication.getInstance().getDaoSession().getServiceRequestDao().update(serviceRequest);

		setResult(Activity.RESULT_OK, new Intent().putExtra(REQUEST_ID, serviceRequest.getId()));

		finish();
	}

	void loadFiles(final ProgressDialog progressDialog, final List<ServiceRequestFile> files) {
		if (files != null && files.size() > 0) {
			final ServiceRequestFile serviceRequestFile = files.remove(0);

			if (serviceRequestFile != null && serviceRequestFile.getServiceRequestFileID() != null && serviceRequestFile.getExt() != null
					&& (serviceRequestFile.getExt().toLowerCase().equals(".jpg") || serviceRequestFile.getExt().toLowerCase().equals(".jpeg") || serviceRequestFile.getExt().toLowerCase().equals(".png"))) {

				final ServiceRequestPhotoDao serviceRequestPhotoDao = FacilicomApplication.getInstance().getDaoSession().getServiceRequestPhotoDao();
				List<ServiceRequestPhoto> serviceRequestPhotos = serviceRequestPhotoDao.queryBuilder().where(ServiceRequestPhotoDao.Properties.ServiceRequestFileID.eq(serviceRequestFile.getServiceRequestFileID())).list();

				if (serviceRequestPhotos == null || serviceRequestPhotos.size() == 0) {
					if (!progressDialog.isShowing()) {
						progressDialog.show();
					}

					FacilicomNetworkClient.getFile(serviceRequestFile.getServiceRequestFileID(), new AsyncHttpResponseHandler() {

						@Override
						public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
							if (responseBody != null && responseBody.length > 0) {
								File photoFile = FacilicomApplication.photoFileGenerate();

								if (photoFile != null) {
									try {
										BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(photoFile));
										bufferedOutputStream.write(responseBody);
										bufferedOutputStream.close();

										serviceRequestPhotoDao.insert(new ServiceRequestPhoto(
												null,
												serviceRequest.getId(),
												serviceRequestFile.getServiceRequestFileID(),
												photoFile.toString()
										));
									} catch (Exception exception) {
										exception.printStackTrace();
									}
								}
							}

							loadFiles(progressDialog, files);
						}

						@Override
						public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
							loadFiles(progressDialog, files);
						}
					});
				} else {
					ServiceRequestPhoto serviceRequestPhoto = serviceRequestPhotos.get(0);

					if (!serviceRequestPhoto.getServiceRequestID().equals(serviceRequest.getId())) {
						serviceRequestPhoto.setServiceRequestID(serviceRequest.getId());
						serviceRequestPhotoDao.update(serviceRequestPhoto);
					}

					loadFiles(progressDialog, files);
				}
			} else {
				loadFiles(progressDialog, files);
			}
		} else {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}

			ArrayList<Photo> extraPhotos = new ArrayList<>();

			serviceRequest.resetPhotos();
			List<ServiceRequestPhoto> photos = serviceRequest.getPhotos();

			if (photos != null && photos.size() > 0) {
				for (ServiceRequestPhoto photo : photos) {
					extraPhotos.add(new Photo(TextUtils.concat("file://", photo.getPhotoFileName()).toString()));
				}
			}

			Intent intent = new Intent(this, PhotoAlbumActivity.class);

			intent.putExtra(PhotoAlbumActivity.FORM_EXTRA, getString(R.string.request_new_photo));
			intent.putExtra(PhotoAlbumActivity.OBJECT_EXTRA, TextUtils.concat(serviceRequest.getFacilityName(), ". ", serviceRequest.getFacilityAddress()));
			intent.putExtra(PhotoAlbumActivity.PHOTOS_EXTRA, extraPhotos);

			startActivityForResult(intent, ALBUM_VIEW);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		NFItem item = items.get(i);

		if (item.getType().equals(NFItem.Type.Link)) {
			startActivity(new Intent(
					Intent.ACTION_VIEW,
					Uri.parse(FacilicomNetworkClient.getAbsoluteUrl(TextUtils.concat("GetFile.aspx?ServiceRequestFileID=", String.valueOf(item.getTag())).toString()))
			));
		}
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
		startActivity(new Intent(this, RequestHistoryActivity.class).putExtra(REQUEST_ID, serviceRequest.getId()));
	}

	@Override
	public void captionFragmentOnAlbumPressed() {
		final ProgressDialog progressDialog = new ProgressDialog(RequestEditActivity.this);

		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage(getString(R.string.request_edit_download));

		serviceRequest.resetFiles();
		loadFiles(progressDialog, serviceRequest.getFiles());
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
		return true;
	}

	@Override
	public boolean albumIcon() {
		return true;
	}
}
