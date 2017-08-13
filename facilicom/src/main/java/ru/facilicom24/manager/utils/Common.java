package ru.facilicom24.manager.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Common {

	final static public int LOG_MOCK = 1;
	final static public int ANDROID_VERSION = 2;

	final static private String DECIMAL_FORMAT = "#.############";

	static public String floatToString(float value) {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');

		DecimalFormat format = new DecimalFormat(DECIMAL_FORMAT);
		format.setDecimalFormatSymbols(symbols);

		return format.format(value);
	}
}
