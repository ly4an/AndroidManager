package ru.facilicom24.manager.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import ru.facilicom24.manager.R;

public class ImageDialog
		extends BaseDialogFragment
		implements View.OnClickListener {

	IImageDialogListener listener;

	public static ImageDialog newInstance() {
		ImageDialog dialog = new ImageDialog();

		Bundle bundle = new Bundle();
		addId(bundle, R.id.image_dialog);
		dialog.setArguments(bundle);

		return dialog;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		Fragment targetFragment = getTargetFragment();

		if (targetFragment instanceof IImageDialogListener) {
			listener = (IImageDialogListener) targetFragment;
		} else if (context instanceof IImageDialogListener) {
			listener = (IImageDialogListener) context;
		}
	}

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity(), R.style.ImageDialogStyle);

		dialog.setContentView(R.layout.dialog_image);

		dialog.findViewById(R.id.camera).setOnClickListener(this);
		dialog.findViewById(R.id.gallery).setOnClickListener(this);

		return dialog;
	}

	@Override
	public void onClick(View view) {
		dismiss();

		switch (view.getId()) {
			case R.id.camera: {
				if (listener != null) {
					listener.onCamera();
				}
			}
			break;

			case R.id.gallery: {
				if (listener != null) {
					listener.onGallery();
				}
			}
			break;
		}
	}

	public interface IImageDialogListener {
		void onCamera();

		void onGallery();
	}
}
