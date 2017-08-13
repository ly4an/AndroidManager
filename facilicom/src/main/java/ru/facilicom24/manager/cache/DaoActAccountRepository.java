package ru.facilicom24.manager.cache;

import android.content.Context;

import java.util.List;

import database.ActAccount;
import database.ActAccountDao;
import ru.facilicom24.manager.FacilicomApplication;

public class DaoActAccountRepository {

	final static public int ACTIVE_ACCOUNT_STATUS = 1;

	public static void insertOrUpdate(Context context, ActAccount actAccount) {
		getActAccountDao(context).insertOrReplace(actAccount);
	}

	public static void clearActAccounts(Context context) {
		getActAccountDao(context).deleteAll();
	}

	public static List<ActAccount> getActAccountForDirectumId(Context context, long id) {
		return getActAccountDao(context).queryBuilder()
				.where(ActAccountDao.Properties.DirectumID.eq(id))
				.list();
	}

	public static List<ActAccount> getActAccountForClientId(Context context, long id) {
		return getActAccountDao(context).queryBuilder()
				.where(ActAccountDao.Properties.ClientID.eq(id))
				.list();
	}

	public static List<ActAccount> getActAccountForClientId(Context context, long id, int status) {
		return getActAccountDao(context).queryBuilder()
				.where(
						ActAccountDao.Properties.ClientID.eq(id),
						ActAccountDao.Properties.Status.eq(status)
				).list();
	}

	static ActAccount getActAccountForId(Context context, long id) {
		return getActAccountDao(context).load(id);
	}

	public static List<ActAccount> getAllActAccounts(Context context) {
		return getActAccountDao(context).loadAll();
	}

	public static List<ActAccount> getAllActAccounts(Context context, int status) {
		return getActAccountDao(context).queryBuilder()
				.where(ActAccountDao.Properties.Status.eq(status))
				.list();
	}

	static ActAccountDao getActAccountDao(Context c) {
		return ((FacilicomApplication) c.getApplicationContext()).getDaoSession().getActAccountDao();
	}
}
