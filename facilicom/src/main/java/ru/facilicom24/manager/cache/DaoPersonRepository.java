package ru.facilicom24.manager.cache;

import android.content.Context;

import java.util.List;

import database.Person;
import database.PersonDao;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.activities.PersonActivity;

public class DaoPersonRepository {
	static PersonDao getPersonDao(Context context) {
		return ((FacilicomApplication) context.getApplicationContext()).getDaoSession().getPersonDao();
	}

	public static void clearPersonCreate(Context context) {
		PersonDao dao = getPersonDao(context);

		for (Person person : getPersonsByType(context, PersonActivity.PERSON_TYPE_CREATE)) {
			dao.delete(person);
		}
	}

	public static void clearPersonBind(Context context) {
		PersonDao dao = getPersonDao(context);

		for (Person person : getPersonsByType(context, PersonActivity.PERSON_TYPE_BIND)) {
			dao.delete(person);
		}
	}

	public static void clearPersonUnbind(Context context) {
		PersonDao dao = getPersonDao(context);

		for (Person person : getPersonsByType(context, PersonActivity.PERSON_TYPE_UNBIND)) {
			dao.delete(person);
		}
	}

	public static void insertOrUpdate(Context context, Person person) {
		getPersonDao(context).insertOrReplace(person);
	}

	public static List<Person> getPersonsByType(Context context, String type) {
		return getPersonDao(context).queryBuilder()
				.where(PersonDao.Properties.DocumentType.eq(type))
				.list();
	}
}
