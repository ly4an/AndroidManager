package ru.facilicom24.manager.cache;

import android.content.Context;

import java.util.List;

import database.NomenclatureGroup;
import database.NomenclatureGroupDao;
import ru.facilicom24.manager.FacilicomApplication;

public class DaoNomenclatureGroupRepository {
	static NomenclatureGroupDao getNomenclatureGroupDao(Context context) {
		return ((FacilicomApplication) context.getApplicationContext()).getDaoSession().getNomenclatureGroupDao();
	}

	public static void clearNomenclatureGroups(Context context) {
		getNomenclatureGroupDao(context).deleteAll();
	}

	public static void insertOrUpdate(Context context, NomenclatureGroup group) {
		getNomenclatureGroupDao(context).insertOrReplace(group);
	}

	public static List<NomenclatureGroup> getAllNomenclatureGroups(Context context) {
		return getNomenclatureGroupDao(context).loadAll();
	}

	public static List<NomenclatureGroup> getNomenclatureGroupsByCode(Context context, String code) {
		return getNomenclatureGroupDao(context).queryBuilder().where(NomenclatureGroupDao.Properties.Code.eq(code)).list();
	}
}
