package ru.facilicom24.manager.cache;

import android.content.Context;

import java.util.List;

import database.ActContact;
import database.ActContactDao;
import ru.facilicom24.manager.FacilicomApplication;

public class DaoActContactRepository {

	public static void insertOrUpdate(Context context, ActContact actContact) {
		getActContactDao(context).insertOrReplace(actContact);
	}

	public static void clearActContacts(Context context) {
		getActContactDao(context).deleteAll();
	}

	public static ActContact getActContactById(Context context, int ActContactID) {
		List<ActContact> actContacts = getActContactDao(context).queryBuilder().where(ActContactDao.Properties.ActContactID.eq(ActContactID)).list();
		return actContacts != null && actContacts.size() > 0 ? actContacts.get(0) : null;
	}

	public static List<ActContact> getActContactForClientId(Context context, long id) {
		List contactAccounts = getActContactDao(context).queryBuilder()
				.where(ActContactDao.Properties.ClientID.eq(id))
				.list();

		return contactAccounts;
	}

	public static List<ActContact> getActContactForAccountId(Context context, long id) {
		List contactAccounts = getActContactDao(context).queryBuilder()
				.where(ActContactDao.Properties.AccountID.eq(id))
				.list();

		return contactAccounts;
	}

	static ActContactDao getActContactDao(Context c) {
		return ((FacilicomApplication) c.getApplicationContext()).getDaoSession().getActContactDao();
	}
}
