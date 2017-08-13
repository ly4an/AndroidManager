package ru.facilicom24.manager.cache;

import android.content.Context;

import java.util.List;

import database.ContactAccount;
import database.ContactAccountDao;
import ru.facilicom24.manager.FacilicomApplication;

public class DaoContactAccountRepository {
	static private ContactAccountDao getContactAccountDao(Context c) {
		return ((FacilicomApplication) c.getApplicationContext()).getDaoSession().getContactAccountDao();
	}

	static public void insertOrUpdate(Context context, ContactAccount client) {
		getContactAccountDao(context).insertOrReplace(client);
	}

	static public void clearContactAccounts(Context context) {
		getContactAccountDao(context).deleteAll();
	}

	static public List<ContactAccount> loadAll(Context context) {
		return getContactAccountDao(context).loadAll();
	}
}
