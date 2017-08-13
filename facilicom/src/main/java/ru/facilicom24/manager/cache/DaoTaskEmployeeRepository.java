package ru.facilicom24.manager.cache;

import android.content.Context;

import java.util.List;

import database.TaskEmployee;
import database.TaskEmployeeDao;
import ru.facilicom24.manager.FacilicomApplication;

public class DaoTaskEmployeeRepository {
	static TaskEmployeeDao getTaskEmployeeDao(Context context) {
		return ((FacilicomApplication) context.getApplicationContext()).getDaoSession().getTaskEmployeeDao();
	}

	public static void clearTaskEmployee(Context context) {
		getTaskEmployeeDao(context).deleteAll();
	}

	public static void insertOrUpdate(Context context, TaskEmployee task) {
		getTaskEmployeeDao(context).insertOrReplace(task);
	}

	public static List<TaskEmployee> getAllTaskEmployee(Context context) {
		return getTaskEmployeeDao(context).loadAll();
	}

	public static TaskEmployee getTaskEmployeeById(Context context, String taskEmployeeLogin) {
		List<TaskEmployee> taskEmployees = getTaskEmployeeDao(context).queryBuilder().where(TaskEmployeeDao.Properties.TaskEmployeeLogin.eq(taskEmployeeLogin)).list();
		return taskEmployees != null && taskEmployees.size() > 0 ? taskEmployees.get(0) : null;
	}
}
