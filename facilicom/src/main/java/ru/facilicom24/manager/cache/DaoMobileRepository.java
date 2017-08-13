package ru.facilicom24.manager.cache;

import android.content.Context;

import java.util.List;

import database.Mobile;
import database.MobileDao;
import ru.facilicom24.manager.FacilicomApplication;

public class DaoMobileRepository {
	static private MobileDao getMobileDao(Context context) {
		return ((FacilicomApplication) context.getApplicationContext()).getDaoSession().getMobileDao();
	}

	public static List<Mobile> getAllNotSend(Context context) {
		return getMobileDao(context).queryBuilder().where(MobileDao.Properties.Send.eq(false)).list();
	}

	public static List<Mobile> getAllSend(Context context) {
		return getMobileDao(context).queryBuilder().where(MobileDao.Properties.Send.eq(true)).list();
	}

	public static int getCountSend(Context context) {
		return getAllSend(context).size();
	}

	public static void insertOrUpdate(Context context, Mobile mobile) {
		getMobileDao(context).insertOrReplace(mobile);
	}

	public static void delete(Context context, Mobile mobile) {
		getMobileDao(context).delete(mobile);
	}
}
