package ru.facilicom24.manager.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;

import ru.facilicom24.manager.R;

public class CategoryPotSellDialog
		extends DialogFragment
		implements View.OnClickListener {

	IListener listener;

	static public CategoryPotSellDialog newInstance(IListener listener) {
		CategoryPotSellDialog dialog = new CategoryPotSellDialog();
		dialog.setListener(listener);
		return dialog;
	}

	public void setListener(IListener listener) {
		this.listener = listener;
	}

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity(), R.style.ImageDialogStyle);

		dialog.setContentView(R.layout.dialog_category_potsell);

		dialog.findViewById(R.id.categoryFontButton).setOnClickListener(this);
		dialog.findViewById(R.id.potsellFontButton).setOnClickListener(this);

		return dialog;
	}

	@Override
	public void onClick(View view) {
		dismiss();

		if (listener != null) {
			switch (view.getId()) {
				case R.id.categoryFontButton: {
					listener.onCategory();
				}
				break;

				case R.id.potsellFontButton: {
					listener.onPotSell();
				}
				break;
			}
		}
	}

	public interface IListener {
		void onCategory();

		void onPotSell();
	}
}
