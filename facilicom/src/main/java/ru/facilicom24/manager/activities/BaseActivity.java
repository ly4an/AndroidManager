package ru.facilicom24.manager.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONObject;

import java.util.List;

import ru.facilicom24.manager.R;
import ru.facilicom24.manager.dialogs.AlertDialogFragment;
import ru.facilicom24.manager.dialogs.AlertDialogFragment.IAlertDialogListener;
import ru.facilicom24.manager.dialogs.ProgressDialogFragment;

public abstract class BaseActivity
		extends FragmentActivity
		implements IAlertDialogListener {

	public static final String PROGRESS_DIALOG = "progress_dialog";
	public static final String ALERT_DIALOG = "alert_dialog";

	public JSONObject savedDialog;

	protected void goToActivity(Class<?> activity) {
		startActivity(new Intent(this, activity).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
	}

	public void showDialog(DialogFragment dialog, String tag) {
		try {
			dialog.show(getSupportFragmentManager(), tag);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void removeFragment(String tag) {
		Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);

		if (fragment != null) {
			getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
		}
	}

	public void showProgressDialog(String message) {
		showDialog(ProgressDialogFragment.newInstance(message), PROGRESS_DIALOG);
	}

	public void showProgressDialog(int messageResId) {
		showProgressDialog(getString(messageResId));
	}

	public void hideProgressDialog() {
		removeFragment(PROGRESS_DIALOG);
	}

	public void showAlertDialog(int id, String title, String message, String posBtn, String negBtn, String neutrBtn) {
		if (isApplicationBroughtToBackground()) {
			savedDialog = new JSONObject();

			try {
				savedDialog.put("id", id);
				savedDialog.put("title", title);
				savedDialog.put("message", message);
				savedDialog.put("posBtn", posBtn);
				savedDialog.put("negBtn", negBtn);
				savedDialog.put("neutrBtn", neutrBtn);
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			return;
		}

		if (posBtn == null) {
			posBtn = getString(R.string.btn_ok);
		}

		if (negBtn != null) {
			if (neutrBtn != null) {
				showDialog(AlertDialogFragment.newInstance(id, title, message, posBtn, negBtn, neutrBtn), ALERT_DIALOG);
			} else {
				showDialog(AlertDialogFragment.newInstance(id, title, message, posBtn, negBtn), ALERT_DIALOG);
			}
		} else {
			if (neutrBtn != null) {
				showDialog(AlertDialogFragment.newInstance(id, title, message, posBtn, neutrBtn), ALERT_DIALOG);
			} else {
				showDialog(AlertDialogFragment.newInstance(id, title, message, posBtn), ALERT_DIALOG);
			}
		}
	}

	public void showAlertDialog(int id, String title, String message) {
		showAlertDialog(id, title, message, null, null, null);
	}

	public void showAlertDialog(int id, int titleResId, int messageResId) {
		showAlertDialog(id, getString(titleResId), getString(messageResId));
	}

	public void showAlertDialog(int id, String title, String message, String posBtn) {
		showAlertDialog(id, title, message, posBtn, null, null);
	}

	public void showAlertDialog(int id, int title, int message, int posBtn) {
		showAlertDialog(id, getString(title), getString(message), getString(posBtn));
	}

	public void showAlertDialog(int id, String title, String message, String posBtn, String negBtn) {
		showAlertDialog(id, title, message, posBtn, negBtn, null);
	}

	public void showAlertDialog(int id, int title, int message, int posBtn, int negBtn) {
		showAlertDialog(id, getString(title), getString(message), getString(posBtn), getString(negBtn));
	}

	@Override
	public void onAlertPositiveBtnClicked(AlertDialogFragment dialog) {
		dismissProgress();
		dialog.dismiss();
	}

	@Override
	public void onAlertNegativeBtnClicked(AlertDialogFragment dialog) {
		dismissProgress();
	}

	@Override
	public void onAlertNeutralBtnClicked(AlertDialogFragment dialog) {
		dismissProgress();
	}

	@Override
	public void onAlertDialogCancel(AlertDialogFragment dialog) {
		dismissProgress();
	}

	public void dismissProgress() {
		hideProgressDialog();
	}

	public void addUiFragment(int containerId, Fragment fragment, String tag) {
		getSupportFragmentManager().beginTransaction().add(containerId, fragment, tag).commit();
	}

	public void setTitle(int titleResId) {
		((IBaseHeader) getSupportFragmentManager().findFragmentById(R.id.header)).setTitle(titleResId);
	}

	protected void hideKeyboard() {
		View view = getCurrentFocus();

		if (view != null) {
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public boolean isApplicationBroughtToBackground() {
		List<ActivityManager.RunningTaskInfo> tasks = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
		return !tasks.isEmpty() && !tasks.get(0).topActivity.getPackageName().equals(getApplicationContext().getPackageName());
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (savedDialog != null) {
			showAlertDialog(
					savedDialog.optInt("id"),
					savedDialog.optString("title"),
					savedDialog.optString("message"),
					savedDialog.optString("posBtn"),
					savedDialog.optString("negBtn"),
					savedDialog.optString("neutrBtn")
			);

			savedDialog = new JSONObject();
		}
	}

	interface IBaseHeader {
		void setTitle(int titleResId);
	}
}
