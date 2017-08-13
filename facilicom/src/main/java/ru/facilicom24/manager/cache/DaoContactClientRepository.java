package ru.facilicom24.manager.cache;

import android.content.Context;

import java.util.List;

import database.ContactClient;
import database.ContactClientDao;
import ru.facilicom24.manager.FacilicomApplication;

public class DaoContactClientRepository {
	static private ContactClientDao getContactClientDao(Context c) {
		return ((FacilicomApplication) c.getApplicationContext()).getDaoSession().getContactClientDao();
	}

	static public void insertOrUpdate(Context context, ContactClient client) {
		getContactClientDao(context).insertOrReplace(client);
	}

	static public void clearContactClients(Context context) {
		getContactClientDao(context).deleteAll();
	}

	static public List<ContactClient> loadAll(Context context) {
		return getContactClientDao(context).loadAll();
	}
}
