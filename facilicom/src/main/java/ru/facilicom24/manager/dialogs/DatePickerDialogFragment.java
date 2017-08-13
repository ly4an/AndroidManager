package ru.facilicom24.manager.dialogs;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerDialogFragment extends BaseDialogFragment implements DatePickerDialog.OnDateSetListener {

	public static final String CALENDAR_EXTRA = "calendar";
	private Calendar mCalendar;
	private IDatePickerDialogListener mListener;

	public static DatePickerDialogFragment newInstance(Calendar calendar) {
		DatePickerDialogFragment dialog = new DatePickerDialogFragment();

		Bundle args = new Bundle();
		args.putSerializable(CALENDAR_EXTRA, calendar);
		dialog.setArguments(args);

		return dialog;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Fragment targetFragment = getTargetFragment();
		if (targetFragment instanceof IDatePickerDialogListener) {
			mListener = (IDatePickerDialogListener) targetFragment;
		} else if (activity instanceof IDatePickerDialogListener) {
			mListener = (IDatePickerDialogListener) activity;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCalendar = (Calendar) getArguments().getSerializable(CALENDAR_EXTRA);
		mCalendar = mCalendar == null ? Calendar.getInstance() : mCalendar;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int year = mCalendar.get(Calendar.YEAR);
		int month = mCalendar.get(Calendar.MONTH);
		int day = mCalendar.get(Calendar.DAY_OF_MONTH);

		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	@Override
	public void onDateSet(DatePicker datePicker, int year, int month, int day) {
		if (mListener != null) {
			mCalendar.set(Calendar.YEAR, year);
			mCalendar.set(Calendar.MONTH, month);
			mCalendar.set(Calendar.DAY_OF_MONTH, day);
			mListener.onDateSet(mCalendar);
		}
	}

	public interface IDatePickerDialogListener {
		void onDateSet(Calendar calendar);
	}
}
