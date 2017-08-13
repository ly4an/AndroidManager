package ru.facilicom24.manager.fragments;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Calendar;

import ru.facilicom24.manager.R;
import ru.facilicom24.manager.dialogs.AlertDialogFragment;
import ru.facilicom24.manager.dialogs.DatePickerDialogFragment;
import ru.facilicom24.manager.dialogs.ProgressDialogFragment;

public abstract class BaseFragment
		extends Fragment
		implements
		AlertDialogFragment.IAlertDialogListener,
		DatePickerDialogFragment.IDatePickerDialogListener {

	public static final String ALERT_DIALOG = "alert_dialog";
	public static final String PROGRESS_DIALOG = "progress_dialog";
	public static final String DATE_PICKER_DIALOG = "date_picker_dialog";

	public BaseFragment() {
	}

	public void showDialog(DialogFragment dialog, String tag) {
		if (getActivity() != null && isAdded()) {
			dialog.setTargetFragment(this, 0);

			Fragment fragment;
			while ((fragment = getFragmentManager().findFragmentByTag(tag)) != null) {
				getFragmentManager().beginTransaction().remove(fragment).commit();
			}

			dialog.show(getFragmentManager(), tag);
		}
	}

	public void removeFragment(String tag) {
		Fragment fragment = getFragmentManager().findFragmentByTag(tag);

		if (fragment != null) {
			getFragmentManager().beginTransaction().remove(fragment).commit();
		}
	}

	public void showProgressDialog(String message) {
		showDialog(ProgressDialogFragment.newInstance(message), PROGRESS_DIALOG);
	}

	public void showProgressDialog(int messageResId) {
		showProgressDialog(getString(messageResId));
	}

	public void hideProgressDialog() {
		try {
			removeFragment(PROGRESS_DIALOG);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void showAlertDialog(int id, String title, String message, String posBtn, String negBtn, String neutrBtn) {
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

	public void showAlertDialog(int id, String title, String message, String posBtn, String negBtn) {
		showAlertDialog(id, title, message, posBtn, negBtn, null);
	}

	public void showAlertDialog(int id, int title, int message, int posBtn, int negBtn) {
		showAlertDialog(id, getString(title), getString(message), getString(posBtn), getString(negBtn));
	}

	@Override
	public void onAlertPositiveBtnClicked(AlertDialogFragment dialog) {
		dialog.dismiss();
	}

	@Override
	public void onAlertNegativeBtnClicked(AlertDialogFragment dialog) {
	}

	@Override
	public void onAlertNeutralBtnClicked(AlertDialogFragment dialog) {
	}

	@Override
	public void onAlertDialogCancel(AlertDialogFragment dialog) {
	}

	protected void hideKeyboard() {
		View view = getView();

		if (view != null) {
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	@Override
	public void onDateSet(Calendar calendar) {
	}
}
