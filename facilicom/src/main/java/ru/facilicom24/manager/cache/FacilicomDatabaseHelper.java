package ru.facilicom24.manager.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import ru.facilicom24.manager.model.Check;
import ru.facilicom24.manager.model.CheckBlank;
import ru.facilicom24.manager.model.CheckObject;
import ru.facilicom24.manager.model.CheckType;
import ru.facilicom24.manager.model.Element;
import ru.facilicom24.manager.model.ElementMark;
import ru.facilicom24.manager.model.Photo;
import ru.facilicom24.manager.model.Reason;
import ru.facilicom24.manager.model.Zone;

public class FacilicomDatabaseHelper extends OrmLiteSqliteOpenHelper {
	static final String TAG = FacilicomDatabaseHelper.class.getSimpleName();

	static final private int DATABASE_VERSION = 5;
	static final private String DATABASE_NAME = "facilicom.db";

	private Dao<CheckType, Integer> checkTypesDao;
	private Dao<CheckObject, Integer> checkObjectsDao;
	private Dao<CheckBlank, Integer> checkBlanksDao;
	private Dao<Check, Integer> checksDao;
	private Dao<Zone, Integer> zonesDao;
	private Dao<Element, Integer> elementsDao;
	private Dao<Reason, Integer> reasonsDao;
	private Dao<Photo, Integer> photosDao;
	private Dao<ElementMark, Integer> marksDao;

	public FacilicomDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, CheckType.class);
			TableUtils.createTable(connectionSource, CheckObject.class);

			TableUtils.createTable(connectionSource, CheckBlank.class);
			TableUtils.createTable(connectionSource, Zone.class);
			TableUtils.createTable(connectionSource, Element.class);
			TableUtils.createTable(connectionSource, Reason.class);

			TableUtils.createTable(connectionSource, Check.class);
			TableUtils.createTable(connectionSource, ElementMark.class);
			TableUtils.createTable(connectionSource, Photo.class);
		} catch (Exception e) {
			Log.d(TAG, e.getMessage(), e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int fromVersion, int toVersion) {
	}

	Dao<CheckType, Integer> getCheckTypesDao() {
		if (checkTypesDao == null) {
			try {
				checkTypesDao = getDao(CheckType.class);
			} catch (SQLException e) {
				Log.d(TAG, e.getMessage(), e);
			}
		}
		return checkTypesDao;
	}

	Dao<CheckObject, Integer> getCheckObjectsDao() {
		if (checkObjectsDao == null) {
			try {
				checkObjectsDao = getDao(CheckObject.class);
			} catch (SQLException e) {
				Log.d(TAG, e.getMessage(), e);
			}
		}
		return checkObjectsDao;
	}

	Dao<CheckBlank, Integer> getCheckBlanksDao() {
		if (checkBlanksDao == null) {
			try {
				checkBlanksDao = getDao(CheckBlank.class);
			} catch (SQLException e) {
				Log.d(TAG, e.getMessage(), e);
			}
		}
		return checkBlanksDao;
	}

	Dao<Check, Integer> getChecksDao() {
		if (checksDao == null) {
			try {
				checksDao = getDao(Check.class);
			} catch (SQLException e) {
				Log.d(TAG, e.getMessage(), e);
			}
		}
		return checksDao;
	}

	Dao<Zone, Integer> getZonesDao() {
		if (zonesDao == null) {
			try {
				zonesDao = getDao(Zone.class);
			} catch (Exception e) {
				Log.d(TAG, e.getMessage(), e);
			}
		}
		return zonesDao;
	}

	Dao<Element, Integer> getElementsDao() {
		if (elementsDao == null) {
			try {
				elementsDao = getDao(Element.class);
			} catch (Exception e) {
				Log.d(TAG, e.getMessage(), e);
			}
		}
		return elementsDao;
	}

	Dao<Reason, Integer> getReasonsDao() {
		if (reasonsDao == null) {
			try {
				reasonsDao = getDao(Reason.class);
			} catch (Exception e) {
				Log.d(TAG, e.getMessage(), e);
			}
		}
		return reasonsDao;
	}

	Dao<Photo, Integer> getPhotosDao() {
		if (photosDao == null) {
			try {
				photosDao = getDao(Photo.class);
			} catch (Exception e) {
				Log.d(TAG, e.getMessage(), e);
			}
		}
		return photosDao;
	}

	Dao<ElementMark, Integer> getMarksDao() {
		if (marksDao == null) {
			try {
				marksDao = getDao(ElementMark.class);
			} catch (Exception e) {
				Log.d(TAG, e.getMessage(), e);
			}
		}
		return marksDao;
	}

	@Override
	public void close() {
		super.close();
		checkTypesDao = null;
	}
}
