package ru.facilicom24.manager.utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkHelper {

	public static boolean isConnected(Context context) {
		if (context == null) {
			return false;
		}

		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isConnected();
	}
}
