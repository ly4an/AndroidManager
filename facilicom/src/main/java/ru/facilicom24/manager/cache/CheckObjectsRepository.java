package ru.facilicom24.manager.cache;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import ru.facilicom24.manager.model.CheckObject;
import ru.facilicom24.manager.model.CheckType;

public class CheckObjectsRepository {
	private static final String TAG = CheckObjectsRepository.class.getSimpleName();

	FacilicomDatabaseManager mDbManager;
	FacilicomDatabaseHelper mDatabaseHelper;

	private Dao<CheckObject, Integer> mDao;

	public CheckObjectsRepository(Context context) {
		mDbManager = new FacilicomDatabaseManager();
		mDatabaseHelper = mDbManager.getHelper(context);
		mDao = mDatabaseHelper.getCheckObjectsDao();
	}

	public int create(CheckObject checkObject) {
		try {
			return mDao.create(checkObject);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return -1;
	}

	public int delete(CheckObject checkObject) {
		try {
			return mDao.delete(checkObject);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return -1;
	}

	public int deleteAll() {
		try {
			return TableUtils.clearTable(mDatabaseHelper.getConnectionSource(), CheckObject.class);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return -1;
	}

	public int deleteAll(HashSet<Integer> excludeCheckObjects) {
		try {
			ArrayList<String> values = new ArrayList<>();
			for (Integer checkObjectId : excludeCheckObjects) {
				values.add(String.valueOf(checkObjectId));
			}

			mDao.executeRawNoArgs(TextUtils.concat("DELETE FROM check_objects WHERE check_object_id NOT IN (", TextUtils.join(",", values), ")").toString());
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return -1;
	}

	public int update(CheckObject checkObject) {
		try {
			return mDao.update(checkObject);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return -1;
	}

	public List<CheckObject> getAll() {
		try {
			return mDao.queryForAll();
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return null;
	}

	public List<CheckObject> getAll(CheckType checkType) {
		try {
			return mDao.queryBuilder().where().eq("check_object_check_type_id", checkType.getCheckId()).query();
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return null;
	}

	public CheckObject getObjectById(int directumId) {
		try {
			return mDao.queryBuilder().where().eq("check_object_id", directumId).query().get(0);
		} catch (Exception exception) {
			Log.d(TAG, exception.getMessage(), exception);
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

	public int bulkCreate(final List<CheckObject> checkObjects, final HashSet<Integer> excludeCheckObjects) {
		if (excludeCheckObjects != null && !excludeCheckObjects.isEmpty()) {
			deleteAll(excludeCheckObjects);
		} else {
			deleteAll();
		}

		try {
			return mDao.callBatchTasks(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					int counter = 0;
					for (CheckObject checkObject : checkObjects) {
						mDao.create(checkObject);
						counter++;
					}

					return counter;
				}
			});
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return -1;
	}

	public Dao<CheckObject, Integer> getDao() {
		return mDao;
	}

	public void close() {
		mDbManager.releaseHelper();
	}
}
