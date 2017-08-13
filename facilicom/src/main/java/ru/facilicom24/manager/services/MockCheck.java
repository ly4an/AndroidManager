package ru.facilicom24.manager.services;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Pair;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import database.Fake;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.retrofit.RFService;
import ru.facilicom24.manager.utils.Common;
import ru.facilicom24.manager.utils.NetworkHelper;

public class MockCheck {
	static private ArrayList<ApplicationInfo> applications;

	static public void initializeApplications(Context context) {
		applications = new ArrayList<>();

		List<Fake> fakes = FacilicomApplication.getInstance().getDaoSession().getFakeDao().loadAll();

		if (fakes != null && fakes.size() > 0) {
			String packageName = context.getPackageName();
			PackageManager manager = context.getPackageManager();

			for (ApplicationInfo applicationInfo : manager.getInstalledApplications(PackageManager.GET_META_DATA)) {
				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM) {
					try {
						String[] permissions = manager.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS).requestedPermissions;

						if (permissions != null) {
							for (String permission : permissions) {
								if (!applicationInfo.packageName.equals(packageName)
										&& permission.equals("android.permission.ACCESS_MOCK_LOCATION")) {

									boolean exists = false;

									for (Fake fake : fakes) {
										if (applicationInfo.packageName.toLowerCase().contains(fake.getPattern())) {
											exists = true;
											break;
										}
									}

									if (!exists) {
										applications.add(applicationInfo);
									}

									break;
								}
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
		}
	}

	static public ArrayList<Pair<String, String>> getAllMockPermissionApplications(Context context) {
		ArrayList<Pair<String, String>> result = null;

		for (ApplicationInfo applicationInfo : applications) {
			if (result == null) {
				result = new ArrayList<>();
			}

			result.add(new Pair<>(
					applicationInfo.packageName,
					applicationInfo.loadLabel(context.getPackageManager()).toString()
			));
		}

		return result;
	}

	static public ArrayList<Pair<String, String>> getActiveMockPermissionApplications(Context context) {
		ArrayList<Pair<String, String>> result = null;

		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

		for (ActivityManager.RunningAppProcessInfo info : activityManager.getRunningAppProcesses()) {
			for (ApplicationInfo applicationInfo : applications) {
				if (info.processName.equals(applicationInfo.processName)) {

					if (result == null) {
						result = new ArrayList<>();
					}

					result.add(new Pair<>(
							applicationInfo.packageName,
							applicationInfo.loadLabel(context.getPackageManager()).toString()
					));
				}
			}
		}

		return result;
	}

	static public boolean isMockLocationEnabled(Context context) {
		boolean result = false;

		final int Build_VERSION_CODES_M = 23;
		if (Build.VERSION.SDK_INT < Build_VERSION_CODES_M) {
			try {
				result = !Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		return result;
	}

	static public void mockMessage(Context context, ArrayList<Pair<String, String>> applications, DialogInterface.OnClickListener onClickListener) {
		if (applications.size() == 1) {
			new AlertDialog.Builder(context)
					.setTitle(R.string.message_label)
					.setMessage(String.format(context.getString(R.string.mock_install_label), applications.get(0).second))
					.setCancelable(false)
					.setPositiveButton(R.string.btn_ok, onClickListener)
					.show();
		} else {
			StringWriter writer = new StringWriter();

			for (int i = 0; i < applications.size(); i++) {
				writer.write(applications.get(i).second);

				if (i < applications.size() - 1) {
					writer.write(", ");
				}
			}

			new AlertDialog.Builder(context)
					.setTitle(R.string.message_label)
					.setMessage(String.format(context.getString(R.string.mocks_install_label), writer.toString()))
					.setCancelable(false)
					.setPositiveButton(R.string.btn_ok, onClickListener)
					.show();
		}

		if (NetworkHelper.isConnected(context)) {
			for (Pair<String, String> application : applications) {
				RFService.log(Common.LOG_MOCK, application.first, application.second);
			}
		}
	}
}
