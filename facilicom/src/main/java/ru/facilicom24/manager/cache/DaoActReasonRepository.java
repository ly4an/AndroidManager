package ru.facilicom24.manager.cache;

import android.content.Context;

import java.util.List;

import database.ActReason;
import database.ActReasonDao;
import ru.facilicom24.manager.FacilicomApplication;

public class DaoActReasonRepository {

	public static void insertOrUpdate(Context context, ActReason actReason) {
		getActReasonDao(context).insertOrReplace(actReason);
	}

	public static void clearActReasons(Context context) {
		getActReasonDao(context).deleteAll();
	}

	public static void deleteActReasonWithId(Context context, long id) {
		getActReasonDao(context).delete(getActReasonForId(context, id));
	}

	public static ActReason getActReasonForId(Context context, long id) {
		return getActReasonDao(context).load(id);
	}

	public static List<ActReason> getAllActReasons(Context context) {
		return getActReasonDao(context).loadAll();
	}

	private static ActReasonDao getActReasonDao(Context c) {
		return ((FacilicomApplication) c.getApplicationContext()).getDaoSession().getActReasonDao();
	}
}
