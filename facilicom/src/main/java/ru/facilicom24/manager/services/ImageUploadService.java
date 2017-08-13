package ru.facilicom24.manager.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import ru.facilicom24.manager.cache.ChecksRepository;
import ru.facilicom24.manager.cache.PhotosRepository;
import ru.facilicom24.manager.model.Check;
import ru.facilicom24.manager.model.Photo;
import ru.facilicom24.manager.retrofit.RFService;
import ru.facilicom24.manager.utils.SessionManager;

public class ImageUploadService extends Service {
	static final public String IMAGE_UPLOAD_ONE_ACTION = "IMAGE_UPLOAD_ONE_ACTION";
	static final public String IMAGE_UPLOAD_STARTED_ACTION = "IMAGE_UPLOAD_STARTED_ACTION";
	static final public String IMAGE_UPLOAD_FINISHED_ACTION = "IMAGE_UPLOAD_FINISHED_ACTION";
	static final public String IMAGE_UPLOAD_FAILURE_ACTION = "IMAGE_UPLOAD_FAILURE_ACTION";

	static final public String SYNC_MODE = "SYNC_MODE";

	static final public int ALL_CHECKS_PHOTO = 0;
	static final public int NEW_CHECKS_PHOTO = 1;
	static final public int READY_CHECKS_PHOTO = 2;

	static int PHOTO_WIDTH = 640;
	static int PHOTO_HEIGHT = 480;
	static int PHOTO_COMPRESS = 77;

	boolean failureSend;

	ChecksRepository checksRepository;
	PhotosRepository photosRepository;

	public static void upload(Context context, int syncMode) {
		context.startService(new Intent(context, ImageUploadService.class).putExtra(SYNC_MODE, syncMode));
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			checksRepository = new ChecksRepository(this);
			photosRepository = new PhotosRepository(this);

			//

			List<Photo> photos = photosRepository.getAll();

			if (photos != null) {
				while (!photos.isEmpty()) {
					Photo photo = photos.remove(0);

					if (photo.getCheck() == null || !new File(Uri.parse(photo.getUri()).getPath()).exists()) {
						photosRepository.delete(photo);
					}
				}
			}

			//

			photos = photosRepository.getAll();

			if (photos != null && !photos.isEmpty()) {
				switch (intent.getIntExtra(SYNC_MODE, ALL_CHECKS_PHOTO)) {
					case NEW_CHECKS_PHOTO: {
						List<Photo> removePhotos = new ArrayList<>();

						for (Photo photo : photos) {
							if (photo.getCheck().getState() != Check.NEW) {
								removePhotos.add(photo);
							}
						}

						photos.removeAll(removePhotos);
					}
					break;

					case READY_CHECKS_PHOTO: {
						List<Photo> removePhotos = new ArrayList<>();

						for (Photo photo : photos) {
							if (photo.getCheck().getState() != Check.READY) {
								removePhotos.add(photo);
							}
						}

						photos.removeAll(removePhotos);
					}
					break;
				}

				if (!photos.isEmpty()) {
					failureSend = false;

					AsyncHttpClient mClient = new AsyncHttpClient();
					mClient.addHeader("Authorization", SessionManager.getInstance().getToken());

					for (int i = 0; i < photos.size(); i++) {
						Photo photo = photos.get(i);
						byte[] photoBytes = getPhoto(new File(Uri.parse(photo.getUri()).getPath()), PHOTO_WIDTH, PHOTO_HEIGHT, PHOTO_COMPRESS);

						if (photoBytes != null) {
							RequestParams requestParams = new RequestParams();
							requestParams.put("file", new ByteArrayInputStream(photoBytes));

							String url = TextUtils.concat(SessionManager.getInstance().getHostUrl(), "api/Photos3?checkId=", photo.getCheck().getCheckId()).toString();

							if (i == 0 && photos.size() == 1) {
								LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(IMAGE_UPLOAD_ONE_ACTION));
								mClient.post(url, requestParams, new ImageUploadAsyncHttpResponseHandler(photo, true));
							} else if (i == 0) {
								LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(IMAGE_UPLOAD_STARTED_ACTION));
								mClient.post(url, requestParams, new ImageUploadAsyncHttpResponseHandler(photo, false));
							} else if (i == photos.size() - 1) {
								mClient.post(url, requestParams, new ImageUploadAsyncHttpResponseHandler(photo, true));
							} else {
								mClient.post(url, requestParams, new ImageUploadAsyncHttpResponseHandler(photo, false));
							}
						} else {
							if (i == 0 && photos.size() == 1) {
								endCheck(photo, true);
							} else if (i == 0) {
								endCheck(photo, false);
							} else if (i == photos.size() - 1) {
								endCheck(photo, true);
							} else {
								endCheck(photo, false);
							}
						}
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return START_NOT_STICKY;
	}

	void endCheck(final Photo photo, final boolean finished) {
		photosRepository.delete(photo);

		Collection<Photo> photos = photo.getCheck().getPhotos();

		if (photos == null || photos.isEmpty()) {
			RFService.checkEnd(photo.getCheck().getCheckId(), new Callback<JsonObject>() {
				@Override
				public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
					if (response != null && response.body() != null) {
						checksRepository.delete(photo.getCheck());

						if (finished) {
							LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(IMAGE_UPLOAD_FINISHED_ACTION));
						}
					} else {
						failure(photo);
					}
				}

				@Override
				public void onFailure(Call<JsonObject> call, Throwable t) {
					failure(photo);
				}
			});
		}
	}

	void failure(Photo photo) {
		Check check = photo.getCheck();
		check.setState(Check.READY);
		checksRepository.update(check);

		if (!failureSend) {
			failureSend = true;
			LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(IMAGE_UPLOAD_FAILURE_ACTION));
		}
	}

	byte[] getPhoto(File photoFile, int width, int height, int compress) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();

			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);

			options.inJustDecodeBounds = false;
			options.inSampleSize = calcInSampleSize(options, width, height);

			Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);

			// photoFile.delete();

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			byte[] bytes = bitmap.compress(Bitmap.CompressFormat.JPEG, compress, stream) ? stream.toByteArray() : null;
			bitmap.recycle();

			return bytes;
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	int calcInSampleSize(BitmapFactory.Options options, int width, int height) {
		int inSampleSize = 1;

		width = width / 2;
		height = height / 2;

		if (options.outHeight > height || options.outWidth > width) {
			if (options.outWidth > options.outHeight) {
				inSampleSize = Math.round((float) options.outHeight / (float) height);
			} else {
				inSampleSize = Math.round((float) options.outWidth / (float) width);
			}
		}

		return inSampleSize;
	}

	class ImageUploadAsyncHttpResponseHandler extends AsyncHttpResponseHandler {
		Photo photo;
		boolean finished;

		ImageUploadAsyncHttpResponseHandler(Photo photo, boolean finished) {
			this.photo = photo;
			this.finished = finished;
		}

		@Override
		public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
			endCheck(photo, finished);
		}

		@Override
		public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
			failure(photo);
		}
	}
}
