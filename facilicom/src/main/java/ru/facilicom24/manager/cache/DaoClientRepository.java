package ru.facilicom24.manager.cache;

import android.content.Context;

import java.util.List;

import database.Client;
import database.ClientDao;
import ru.facilicom24.manager.FacilicomApplication;

public class DaoClientRepository {
	final static public int ACTIVE_CLIENT_STATUS = 1;

	public static void insertOrUpdate(Context context, Client client) {
		getClientDao(context).insertOrReplace(client);
	}

	public static void clearClients(Context context) {
		getClientDao(context).deleteAll();
	}

	public static Client getClientForId(Context context, long id) {
		return getClientDao(context).load(id);
	}

	public static List<Client> getClientForClientId(Context context, long id) {
		return getClientDao(context).queryBuilder()
				.where(ClientDao.Properties.ClientID.eq(id))
				.list();
	}

	public static List<Client> getAllClients(Context context) {
		return getClientDao(context).loadAll();
	}

	public static List<Client> getAllClients(Context context, int status) {
		return getClientDao(context).queryBuilder()
				.where(ClientDao.Properties.Status.eq(status))
				.list();
	}

	private static ClientDao getClientDao(Context c) {
		return ((FacilicomApplication) c.getApplicationContext()).getDaoSession().getClientDao();
	}
}
