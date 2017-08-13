package ru.facilicom24.manager.views;

import android.content.Context;
import android.util.AttributeSet;

public class ElementNumberView extends FontTextView {

	public ElementNumberView(Context context) {
		super(context);
	}

	public ElementNumberView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ElementNumberView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
