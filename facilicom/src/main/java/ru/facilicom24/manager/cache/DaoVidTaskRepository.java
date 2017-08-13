package ru.facilicom24.manager.cache;

import android.content.Context;

import java.util.List;

import database.VidTask;
import database.VidTaskDao;
import ru.facilicom24.manager.FacilicomApplication;

public class DaoVidTaskRepository {
	static VidTaskDao getVidTaskDao(Context context) {
		return ((FacilicomApplication) context.getApplicationContext()).getDaoSession().getVidTaskDao();
	}

	public static void clearVidTask(Context context) {
		getVidTaskDao(context).deleteAll();
	}

	public static void insertOrUpdate(Context context, VidTask task) {
		getVidTaskDao(context).insertOrReplace(task);
	}

	public static List<VidTask> getAllVidTask(Context context) {
		return getVidTaskDao(context).loadAll();
	}

	public static VidTask getVidTaskById(Context context, int vidTaskID) {
		List<VidTask> vidTasks = getVidTaskDao(context).queryBuilder().where(VidTaskDao.Properties.VidTaskID.eq(vidTaskID)).list();
		return vidTasks != null && vidTasks.size() > 0 ? vidTasks.get(0) : null;
	}
}
