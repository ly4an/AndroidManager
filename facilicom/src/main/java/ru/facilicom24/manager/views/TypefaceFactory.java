package ru.facilicom24.manager.views;

import android.content.Context;
import android.graphics.Typeface;

import ru.facilicom24.manager.R;

public class TypefaceFactory {

	public static Typeface getTypeface(Context context, int style) {
		Typeface typeface = null;

		if (style == Typeface.BOLD) {
			typeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.bold_font));
		} else if (style == Typeface.ITALIC) {
			typeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.italic_font));
		} else if (style == Typeface.BOLD_ITALIC) {
			typeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.bold_italic_font));
		} else {
			typeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.regular_font));
		}

		return typeface;
	}

}
