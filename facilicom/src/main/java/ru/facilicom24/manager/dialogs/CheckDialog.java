package ru.facilicom24.manager.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import ru.facilicom24.manager.R;
import ru.facilicom24.manager.views.FontButton;

public class CheckDialog
		extends BaseDialogFragment
		implements View.OnClickListener {

	ICheckDialogListener listener;

	public static CheckDialog newInstance() {
		return newInstance(true);
	}

	public static CheckDialog newInstance(boolean sendVisible) {
		CheckDialog dialog = new CheckDialog();

		Bundle bundle = new Bundle();
		addId(bundle, R.id.check_dialog);
		bundle.putBoolean("SendVisible", sendVisible);
		dialog.setArguments(bundle);

		return dialog;
	}

	@Override
	public void onAttach(Context activity) {
		super.onAttach(activity);

		Fragment targetFragment = getTargetFragment();

		if (targetFragment instanceof ICheckDialogListener) {
			listener = (ICheckDialogListener) targetFragment;
		} else if (activity instanceof ICheckDialogListener) {
			listener = (ICheckDialogListener) activity;
		}
	}

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity(), R.style.CheckDialogStyle);

		dialog.setContentView(R.layout.dialog_check);

		dialog.findViewById(R.id.save_btn).setOnClickListener(this);
		dialog.findViewById(R.id.delete_btn).setOnClickListener(this);

		FontButton sendButton = (FontButton) dialog.findViewById(R.id.send_btn);

		sendButton.setOnClickListener(this);

		if (!getArguments().getBoolean("SendVisible")) {
			sendButton.setVisibility(View.GONE);
		}

		return dialog;
	}

	@Override
	public void onClick(View view) {
		dismiss();

		switch (view.getId()) {
			case R.id.send_btn: {
				if (listener != null) {
					listener.onSendBtnClicked();
				}
			}
			break;

			case R.id.save_btn: {
				if (listener != null) {
					listener.onSaveBtnClicked();
				}
			}
			break;

			case R.id.delete_btn: {
				if (listener != null) {
					listener.onDeleteBtnClicked();
				}
			}
			break;
		}
	}

	public interface ICheckDialogListener {
		void onSendBtnClicked();

		void onSaveBtnClicked();

		void onDeleteBtnClicked();
	}
}
