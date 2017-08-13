package ru.facilicom24.manager.cache;

import android.content.Context;

import java.util.List;

import database.Task;
import database.TaskDao;
import ru.facilicom24.manager.FacilicomApplication;

public class DaoTaskRepository {
	static TaskDao getTaskDao(Context context) {
		return ((FacilicomApplication) context.getApplicationContext()).getDaoSession().getTaskDao();
	}

	public static List<Task> getAll(Context context) {
		return getTaskDao(context).loadAll();
	}

	public static void insertOrUpdate(Context context, Task task) {
		getTaskDao(context).insertOrReplace(task);
	}

	public static void deleteAll(Context context) {
		getTaskDao(context).deleteAll();
	}
}
