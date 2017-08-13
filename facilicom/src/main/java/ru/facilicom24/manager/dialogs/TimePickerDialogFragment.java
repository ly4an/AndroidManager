package ru.facilicom24.manager.dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerDialogFragment
		extends DialogFragment
		implements TimePickerDialog.OnTimeSetListener {

	Calendar calendar;
	ITimePickerDialogListener listener;

	static public TimePickerDialogFragment newInstance(Calendar calendar, ITimePickerDialogListener listener) {
		TimePickerDialogFragment fragment = new TimePickerDialogFragment();

		fragment.calendar = calendar;
		fragment.listener = listener;

		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);

		listener.onTimeSet(calendar);
	}

	public interface ITimePickerDialogListener {
		void onTimeSet(Calendar calendar);
	}
}
