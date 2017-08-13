package ru.facilicom24.manager.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class BaseDialogFragment extends DialogFragment {

	public static final String DIALOG_ID = "id";

	int id;

	public static void addId(Bundle args, int id) {
		args.putInt(DIALOG_ID, id);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		id = getArguments().getInt(DIALOG_ID, -1);
	}

	public int getDialogId() {
		return id;
	}
}
