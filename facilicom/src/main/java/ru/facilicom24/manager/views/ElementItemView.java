package ru.facilicom24.manager.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class ElementItemView extends RelativeLayout implements Checkable {

	public static final int[] CHECKED_STATE = {android.R.attr.state_checked};

	private boolean isChecked;

	public ElementItemView(Context context) {
		super(context);
	}

	public ElementItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ElementItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isChecked() {
		return isChecked;
	}

	@Override
	public void setChecked(boolean checked) {
		isChecked = checked;
		refreshDrawableState();
	}

	@Override
	public void toggle() {
		isChecked = !isChecked;
		refreshDrawableState();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		int[] states = super.onCreateDrawableState(extraSpace + 1);
		if (isChecked) {
			mergeDrawableStates(states, CHECKED_STATE);
		}
		return states;
	}
}
