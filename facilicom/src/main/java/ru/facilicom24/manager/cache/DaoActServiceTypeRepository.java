package ru.facilicom24.manager.cache;

import android.content.Context;

import java.util.List;

import database.ActServiceType;
import database.ActServiceTypeDao;
import ru.facilicom24.manager.FacilicomApplication;

public class DaoActServiceTypeRepository {

	public static void insertOrUpdate(Context context, ActServiceType actServiceType) {
		getActServiceTypeDao(context).insertOrReplace(actServiceType);
	}

	public static void clearActServiceTypes(Context context) {
		getActServiceTypeDao(context).deleteAll();
	}

	public static ActServiceType getActServiceTypeById(Context context, String serviceTypeID) {
		List<ActServiceType> actServiceTypes = getActServiceTypeDao(context).queryBuilder().where(ActServiceTypeDao.Properties.ServiceTypeId.eq(serviceTypeID)).list();
		return actServiceTypes != null && actServiceTypes.size() > 0 ? actServiceTypes.get(0) : null;
	}

	public static List<ActServiceType> getAllActServiceTypes(Context context) {
		return getActServiceTypeDao(context).loadAll();
	}

	public static List<ActServiceType> getActServiceTypesForCliendId(Context context, long id) {
		List serviceTypes = getActServiceTypeDao(context).queryBuilder()
				.where(ActServiceTypeDao.Properties.ClientID.eq(id))
				.list();

		return serviceTypes;
	}

	public static List<ActServiceType> getActServiceTypesForAccountId(Context context, long id) {
		List serviceTypes = getActServiceTypeDao(context).queryBuilder()
				.where(ActServiceTypeDao.Properties.DirectumID.eq(id))
				.list();

		return serviceTypes;
	}

	static ActServiceTypeDao getActServiceTypeDao(Context c) {
		return ((FacilicomApplication) c.getApplicationContext()).getDaoSession().getActServiceTypeDao();
	}
}
