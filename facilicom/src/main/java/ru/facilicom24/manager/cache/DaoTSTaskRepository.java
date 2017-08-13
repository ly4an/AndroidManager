package ru.facilicom24.manager.cache;

import android.content.Context;

import java.util.List;

import database.TSTask;
import database.TSTaskDao;
import ru.facilicom24.manager.FacilicomApplication;

public class DaoTSTaskRepository {
	static TSTaskDao getTSTaskDao(Context context) {
		return ((FacilicomApplication) context.getApplicationContext()).getDaoSession().getTSTaskDao();
	}

	public static void deleteAll(Context context) {
		getTSTaskDao(context).deleteAll();
	}

	public static void insertOrReplace(Context context, TSTask tsTask) {
		getTSTaskDao(context).insertOrReplace(tsTask);
	}

	public static List<TSTask> loadAll(Context context) {
		return getTSTaskDao(context).loadAll();
	}

	public static List<TSTask> loadByStatus(Context context, String status) {
		return getTSTaskDao(context).queryBuilder().where(TSTaskDao.Properties.Status.eq(status)).list();
	}

	public static TSTask get(Context context, long id) {
		List<TSTask> tsTasks = getTSTaskDao(context).queryBuilder().where(TSTaskDao.Properties.Id.eq(id)).list();
		return tsTasks != null && tsTasks.size() > 0 ? tsTasks.get(0) : null;
	}

	public static void delete(Context context, TSTask tsTask) {
		getTSTaskDao(context).delete(tsTask);
	}
}
