package ru.facilicom24.manager.cache;

import android.content.Context;

import java.util.List;

import database.PartTime;
import database.PartTimeDao;
import ru.facilicom24.manager.FacilicomApplication;

public class DaoPartTimeRepository {
	static PartTimeDao getPartTimeDao(Context context) {
		return ((FacilicomApplication) context.getApplicationContext()).getDaoSession().getPartTimeDao();
	}

	public static List<PartTime> getAllNotSend(Context context) {
		return getPartTimeDao(context).queryBuilder().where(PartTimeDao.Properties.Send.eq(false)).list();
	}

	public static List<PartTime> getAllSend(Context context) {
		return getPartTimeDao(context).queryBuilder().where(PartTimeDao.Properties.Send.eq(true)).list();
	}

	public static int getCountSend(Context context) {
		return getAllSend(context).size();
	}

	public static void insertOrUpdate(Context context, PartTime partTime) {
		getPartTimeDao(context).insertOrReplace(partTime);
	}

	public static void delete(Context context, PartTime partTime) {
		getPartTimeDao(context).delete(partTime);
	}
}
