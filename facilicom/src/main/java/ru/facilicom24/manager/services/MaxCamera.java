package ru.facilicom24.manager.services;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.facilicom24.manager.R;

public class MaxCamera {
	final static public int SNAP_REQUEST_CODE = 22650;
	final static public int CAMERA_PERMISSION_REQUEST = 13122;

	static private String snapFileAbsolutePath;

	static public String getSnapFileAbsolutePath() {
		return snapFileAbsolutePath;
	}

	static public void snap(Object context) {
		Activity activity = context instanceof android.support.v4.app.Fragment
				? ((android.support.v4.app.Fragment) context).getActivity()
				: (Activity) context;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
				if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
					snapPermission(context);
				} else {
					if (context instanceof MaxCamera.IRequestPermissions) {
						((MaxCamera.IRequestPermissions) context).MaxCameraRequestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE);
					}
				}
			} else {
				if (context instanceof MaxCamera.IRequestPermissions) {
					((MaxCamera.IRequestPermissions) context).MaxCameraRequestPermissions(Manifest.permission.CAMERA);
				}
			}
		} else {
			snapPermission(context);
		}
	}

	static private void snapPermission(Object context) {
		Activity activity = context instanceof android.support.v4.app.Fragment
				? ((android.support.v4.app.Fragment) context).getActivity()
				: (Activity) context;

		final String snapPrefix = "FM-";
		final String snapDatePattern = "yyyyMMdd-HHmmss";
		final String snapExtension = ".jpg";

		Intent snapIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if (snapIntent.resolveActivity(activity.getPackageManager()) != null) {
			File snapFile = null;

			try {
				snapFile = File.createTempFile(
						TextUtils.concat(snapPrefix, new SimpleDateFormat(snapDatePattern, Locale.US).format(new Date())).toString(),
						snapExtension,
						activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
				);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			if (snapFile != null) {
				snapFileAbsolutePath = snapFile.getAbsolutePath();

				System.gc();

				try {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						String authorities = TextUtils.concat(activity.getApplicationContext().getPackageName(), ".fileprovider").toString();
						snapIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(activity, authorities, snapFile));
					} else {
						snapIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(snapFile));
					}

					if (context instanceof android.support.v4.app.Fragment) {
						((android.support.v4.app.Fragment) context).startActivityForResult(snapIntent, SNAP_REQUEST_CODE);
					} else {
						activity.startActivityForResult(snapIntent, SNAP_REQUEST_CODE);
					}
				} catch (Exception exception) {
					exception.printStackTrace();

					snapFile.delete();
					Toast.makeText(activity, R.string.create_image_file_error, Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(activity, R.string.create_image_file_error, Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(activity, R.string.error_no_camera, Toast.LENGTH_LONG).show();
		}
	}

	public interface IRequestPermissions {
		void MaxCameraRequestPermissions(String permission);
	}
}
