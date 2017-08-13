package ru.facilicom24.manager.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class FontEditText extends EditText {

	public FontEditText(Context context) {
		super(context);
	}

	public FontEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FontEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setTypeface(Typeface tf, int style) {
		super.setTypeface(TypefaceFactory.getTypeface(getContext(), style), style);
	}

}
