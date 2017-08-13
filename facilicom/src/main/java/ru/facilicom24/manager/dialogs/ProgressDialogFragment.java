package ru.facilicom24.manager.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;

import ru.facilicom24.manager.R;

public class ProgressDialogFragment extends BaseDialogFragment {
	public static final String TITLE_KEY = "title";
	public static final String MESSAGE_KEY = "message";
	public static final String IS_CANCELABLE_KEY = "is_cancelable";

	public static ProgressDialogFragment newInstance(String title, String message, boolean isCancelable) {
		ProgressDialogFragment dialog = new ProgressDialogFragment();

		Bundle args = new Bundle();

		if (title != null) args.putString(TITLE_KEY, title);
		if (message != null) args.putString(MESSAGE_KEY, message);

		args.putBoolean(IS_CANCELABLE_KEY, isCancelable);

		dialog.setArguments(args);

		return dialog;
	}

	public static ProgressDialogFragment newInstance(String message) {
		return newInstance(null, message, false);
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();

		String title = args.getString(TITLE_KEY);
		String message = args.getString(MESSAGE_KEY);
		boolean isCancelable = args.getBoolean(IS_CANCELABLE_KEY);

		setCancelable(isCancelable);

		ProgressDialog dialog = new ProgressDialog(getActivity());

		if (title != null) {
			dialog.setTitle(title);
		}

		if (message != null) {
			dialog.setMessage(message);
		} else {
			dialog.setMessage(getString(R.string.loading));
		}

		dialog.setCancelable(isCancelable);
		dialog.setCanceledOnTouchOutside(isCancelable);

		dialog.setIndeterminate(true);

		return dialog;
	}
}
