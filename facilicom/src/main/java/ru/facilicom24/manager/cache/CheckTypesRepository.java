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

import ru.facilicom24.manager.model.CheckType;

public class CheckTypesRepository {
	static final String TAG = CheckTypesRepository.class.getSimpleName();

	FacilicomDatabaseManager mDbManager;
	FacilicomDatabaseHelper mDatabaseHelper;

	private Dao<CheckType, Integer> mDao;

	public CheckTypesRepository(Context context) {
		mDbManager = new FacilicomDatabaseManager();
		mDatabaseHelper = mDbManager.getHelper(context);
		mDao = mDatabaseHelper.getCheckTypesDao();
	}

	public int create(CheckType checkType) {
		try {
			return mDao.create(checkType);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public int delete(CheckType checkType) {
		try {
			return mDao.delete(checkType);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public int update(CheckType checkType) {
		try {
			return mDao.update(checkType);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public List<CheckType> getAll() {
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
			return TableUtils.clearTable(mDatabaseHelper.getConnectionSource(), CheckType.class);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return -1;
	}

	public int deleteAll(HashSet<String> excludeCheckTypes) {
		try {
			ArrayList<String> values = new ArrayList<>();
			for (String checkTypeId : excludeCheckTypes) {
				values.add(TextUtils.concat("\"", checkTypeId, "\"").toString());
			}

			mDao.executeRawNoArgs(TextUtils.concat("DELETE FROM check_types WHERE check_id NOT IN (", TextUtils.join(",", values), ")").toString());
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return -1;
	}

	public Dao<CheckType, Integer> getDao() {
		return mDao;
	}

	public void close() {
		mDbManager.releaseHelper();
	}

	public CheckType getTypeById(String typeId) {
		try {
			return mDao.queryBuilder().where().eq("check_id", typeId).query().get(0);
		} catch (Exception e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return null;
	}

	public int bulkCreate(final List<CheckType> checkTypes, final HashSet<String> excludeCheckTypes) {
		if (excludeCheckTypes != null && !excludeCheckTypes.isEmpty()) {
			deleteAll(excludeCheckTypes);
		} else {
			deleteAll();
		}

		try {
			return mDao.callBatchTasks(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					int counter = 0;
					for (CheckType checkType : checkTypes) {
						mDao.create(checkType);
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
}
