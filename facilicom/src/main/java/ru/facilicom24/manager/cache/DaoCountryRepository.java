package ru.facilicom24.manager.cache;

import android.content.Context;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import database.Country;
import database.CountryDao;
import ru.facilicom24.manager.FacilicomApplication;

public class DaoCountryRepository {
	static private CountryDao getCountryDao(Context context) {
		return ((FacilicomApplication) context.getApplicationContext()).getDaoSession().getCountryDao();
	}

	public static void clearCountry(Context context) {
		getCountryDao(context).deleteAll();
	}

	public static void insertOrUpdate(Context context, Country country) {
		getCountryDao(context).insertOrReplace(country);
	}

	public static List<Country> getAllCountry(Context context) {
		List<Country> result = getCountryDao(context).loadAll();

		Collections.sort(result, new Comparator<Country>() {

			@Override
			public int compare(Country lhs, Country rhs) {
				return lhs.getCountryName().compareToIgnoreCase(rhs.getCountryName());
			}
		});

		return result;
	}

	public static List<Country> getCountryByUID(Context context, String countryUID) {
		return getCountryDao(context).queryBuilder().where(CountryDao.Properties.CountryUID.eq(countryUID)).list();
	}
}
