package ru.facilicom24.manager.cache;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class FacilicomDatabaseManager {
	private FacilicomDatabaseHelper mDatabaseHelper;

	public FacilicomDatabaseHelper getHelper(Context context) {
		if (mDatabaseHelper == null) {
			mDatabaseHelper = OpenHelperManager.getHelper(context, FacilicomDatabaseHelper.class);
		}
		return mDatabaseHelper;
	}

	public void releaseHelper() {
		if (mDatabaseHelper != null) {
			OpenHelperManager.releaseHelper();
			mDatabaseHelper = null;
		}
	}

}
