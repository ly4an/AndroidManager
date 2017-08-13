package ru.facilicom24.manager.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class AlertDialogFragment extends BaseDialogFragment implements DialogInterface.OnCancelListener {

	public static final String TITLE_KEY = "title";

	public static final String MESSAGE_KEY = "message";

	public static final String POSITIVE_BTN_KEY = "key";

	public static final String NEGATIVE_BTN_KEY = "negative_key";

	public static final String NEUTRAL_BTN_KEY = "neutral_btn";

	private IAlertDialogListener listener;

	public static AlertDialogFragment newInstance(int id, String title, String message, String btn) {
		AlertDialogFragment dialog = new AlertDialogFragment();

		Bundle args = new Bundle();
		args.putString(TITLE_KEY, title);
		args.putString(MESSAGE_KEY, message);
		args.putString(POSITIVE_BTN_KEY, btn);
		addId(args, id);
		dialog.setArguments(args);

		return dialog;
	}

	public static AlertDialogFragment newInstance(int id, String title, String message, String positiveBtn, String negativeBtn) {
		AlertDialogFragment dialog = new AlertDialogFragment();

		Bundle args = new Bundle();
		args.putString(TITLE_KEY, title);
		args.putString(MESSAGE_KEY, message);
		args.putString(POSITIVE_BTN_KEY, positiveBtn);
		args.putString(NEGATIVE_BTN_KEY, negativeBtn);
		addId(args, id);
		dialog.setArguments(args);

		return dialog;
	}

	public static AlertDialogFragment newInstance(int id, String title, String message, String positiveBtn, String negativeBtn, String neutralBtn) {
		AlertDialogFragment dialog = new AlertDialogFragment();

		Bundle args = new Bundle();

		args.putString(TITLE_KEY, title);
		args.putString(MESSAGE_KEY, message);
		args.putString(POSITIVE_BTN_KEY, positiveBtn);
		args.putString(NEGATIVE_BTN_KEY, negativeBtn);
		args.putString(NEUTRAL_BTN_KEY, neutralBtn);

		addId(args, id);

		dialog.setArguments(args);

		return dialog;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Fragment targetFragment = getTargetFragment();
		if (targetFragment instanceof IAlertDialogListener) {
			listener = (IAlertDialogListener) targetFragment;
		} else if (activity instanceof IAlertDialogListener) {
			listener = (IAlertDialogListener) activity;
		}
	}

	public Dialog onCreateDialog(android.os.Bundle savedInstanceState) {
		Bundle args = getArguments();

		String title = args.getString(TITLE_KEY);
		String message = args.getString(MESSAGE_KEY);
		String positiveBtn = args.getString(POSITIVE_BTN_KEY);

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

		dialogBuilder
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(positiveBtn, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (listener != null) {
							listener.onAlertPositiveBtnClicked(AlertDialogFragment.this);
						} else {
							dismiss();
						}
					}
				});

		String negativeBtn = args.getString(NEGATIVE_BTN_KEY);

		if (negativeBtn != null) {
			dialogBuilder.setNegativeButton(negativeBtn, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (listener != null) {
						listener.onAlertNegativeBtnClicked(AlertDialogFragment.this);
					} else {
						dismiss();
					}
				}
			});
		}

		String neutralBtn = args.getString(NEUTRAL_BTN_KEY);

		if (neutralBtn != null) {
			dialogBuilder.setNegativeButton(negativeBtn, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (listener != null) {
						listener.onAlertNeutralBtnClicked(AlertDialogFragment.this);
					} else {
						dismiss();
					}
				}
			});
		}

		AlertDialog dialog = dialogBuilder.create();

		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		return dialog;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		if (listener != null) {
			listener.onAlertDialogCancel(AlertDialogFragment.this);
		}
	}

	public interface IAlertDialogListener {

		void onAlertPositiveBtnClicked(AlertDialogFragment dialog);

		void onAlertNegativeBtnClicked(AlertDialogFragment dialog);

		void onAlertNeutralBtnClicked(AlertDialogFragment dialog);

		void onAlertDialogCancel(AlertDialogFragment dialog);

	}

}
