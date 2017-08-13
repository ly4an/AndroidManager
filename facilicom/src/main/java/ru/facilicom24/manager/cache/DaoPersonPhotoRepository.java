package ru.facilicom24.manager.cache;

import android.content.Context;

import java.util.List;

import database.PersonPhoto;
import database.PersonPhotoDao;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.activities.PersonActivity;

public class DaoPersonPhotoRepository {
	static PersonPhotoDao getPersonPhotoDao(Context context) {
		return ((FacilicomApplication) context.getApplicationContext()).getDaoSession().getPersonPhotoDao();
	}

	public static void clearPersonPhotoCreate(Context context) {
		PersonPhotoDao dao = getPersonPhotoDao(context);

		for (PersonPhoto personPhoto : getAllPersonPhotoCreate(context)) {
			dao.delete(personPhoto);
		}
	}

	public static void clearPersonPhotoBind(Context context) {
		PersonPhotoDao dao = getPersonPhotoDao(context);

		for (PersonPhoto personPhoto : getAllPersonPhotoBind(context)) {
			dao.delete(personPhoto);
		}
	}

	public static void clearPersonPhoto(Context context, PersonPhoto personPhoto) {
		getPersonPhotoDao(context).delete(personPhoto);
	}

	public static void insertOrUpdate(Context context, PersonPhoto PersonPhoto) {
		getPersonPhotoDao(context).insertOrReplace(PersonPhoto);
	}

	public static List<PersonPhoto> getAllPersonPhotoCreate(Context context) {
		return getPersonPhotoDao(context).queryBuilder()
				.where(PersonPhotoDao.Properties.PersonPhotoType.notEq(PersonActivity.PHOTO_5))
				.list();
	}

	public static List<PersonPhoto> getAllPersonPhotoBind(Context context) {
		return getPersonPhotoDao(context).queryBuilder()
				.where(PersonPhotoDao.Properties.PersonPhotoType.eq(PersonActivity.PHOTO_5))
				.list();
	}

	public static List<PersonPhoto> getPersonPhotoByType(Context context, int type) {
		return getPersonPhotoDao(context).queryBuilder().where(PersonPhotoDao.Properties.PersonPhotoType.eq(type)).list();
	}

	public static long getPersonPhotoCountByType(Context context, int type) {
		return getPersonPhotoDao(context).queryBuilder().where(PersonPhotoDao.Properties.PersonPhotoType.eq(type)).count();
	}
}
