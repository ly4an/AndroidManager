package ru.facilicom24.manager.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class FontButton extends Button {

	public FontButton(Context context) {
		super(context);
	}

	public FontButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FontButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setTypeface(Typeface tf, int style) {
		super.setTypeface(TypefaceFactory.getTypeface(getContext(), style), style);
	}
}
