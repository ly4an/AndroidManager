package ru.facilicom24.manager.cache;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import ru.facilicom24.manager.model.Element;
import ru.facilicom24.manager.model.ElementMark;
import ru.facilicom24.manager.model.Reason;
import ru.facilicom24.manager.model.Zone;

public class ZonesRepository {
	private static final String TAG = ZonesRepository.class.getSimpleName();

	FacilicomDatabaseManager mDbManager;
	FacilicomDatabaseHelper mDatabaseHelper;

	private Dao<Zone, Integer> mDao;

	public ZonesRepository(Context context) {
		mDbManager = new FacilicomDatabaseManager();
		mDatabaseHelper = mDbManager.getHelper(context);
		mDao = mDatabaseHelper.getZonesDao();
	}

	public int create(Zone zone) {
		try {
			return mDao.create(zone);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public int delete(Zone zone) {
		try {
			return mDao.delete(zone);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public int update(Zone zone) {
		try {
			return mDao.update(zone);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public List<Zone> getAll() {
		try {
			return mDao.queryForAll();
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return null;
	}

	public long getCount() {
		try {
			return mDao.countOf();
		} catch (Exception e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1L;
	}

	public int deleteAll() {
		try {
			TableUtils.clearTable(mDatabaseHelper.getConnectionSource(), Reason.class);
			TableUtils.clearTable(mDatabaseHelper.getConnectionSource(), Element.class);

			return TableUtils.clearTable(mDatabaseHelper.getConnectionSource(), Zone.class);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return -1;
	}

	public Dao<Zone, Integer> getDao() {
		return mDao;
	}

	public void close() {
		mDbManager.releaseHelper();
	}

	public int bulkCreate(final Context context, final List<Zone> zones, final List<ElementMark> elementMarks) {
		final ElementsRepository elementsRepository = new ElementsRepository(context);
		final ElementMarksRepository elementMarksRepository = new ElementMarksRepository(context);

		try {
			return mDao.callBatchTasks(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					int counter = 0;
					for (Zone zone : zones) {
						mDao.create(zone);
						elementsRepository.bulkCreate(context, zone.getElements());

						counter++;
					}

					for (ElementMark elementMark : elementMarks) {
						elementMarksRepository.update(elementMark);
					}

					return counter;
				}
			});
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return -1;
	}
}
