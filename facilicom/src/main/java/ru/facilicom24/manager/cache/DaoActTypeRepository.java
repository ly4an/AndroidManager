package ru.facilicom24.manager.cache;

import android.content.Context;

import java.util.List;

import database.ActType;
import database.ActTypeDao;
import ru.facilicom24.manager.FacilicomApplication;

public class DaoActTypeRepository {

	public static void insertOrUpdate(Context context, ActType actType) {
		getActTypeDao(context).insertOrReplace(actType);
	}

	public static void clearActTypes(Context context) {
		getActTypeDao(context).deleteAll();
	}

	public static void deleteActTypeWithId(Context context, long id) {
		getActTypeDao(context).delete(getActTypeForId(context, id));
	}

	public static ActType getActTypeForId(Context context, long id) {
		return getActTypeDao(context).load(id);
	}

	public static List<ActType> getAllActTypes(Context context) {
		return getActTypeDao(context).loadAll();
	}

	private static ActTypeDao getActTypeDao(Context c) {
		return ((FacilicomApplication) c.getApplicationContext()).getDaoSession().getActTypeDao();
	}
}
