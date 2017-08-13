package ru.facilicom24.manager.cache;

import android.content.Context;

import java.util.List;

import database.Schedule;
import database.ScheduleDao;
import ru.facilicom24.manager.FacilicomApplication;

public class DaoScheduleRepository {
	static ScheduleDao getScheduleDao(Context context) {
		return ((FacilicomApplication) context.getApplicationContext()).getDaoSession().getScheduleDao();
	}

	public static List<Schedule> getAll(Context context) {
		return getScheduleDao(context).loadAll();
	}

	public static void insertOrUpdate(Context context, Schedule schedule) {
		getScheduleDao(context).insertOrReplace(schedule);
	}

	public static void deleteAll(Context context) {
		getScheduleDao(context).deleteAll();
	}
}
