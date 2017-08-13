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

import ru.facilicom24.manager.model.CheckBlank;
import ru.facilicom24.manager.model.CheckType;

public class CheckBlanksRepository {
	private static final String TAG = CheckBlanksRepository.class.getSimpleName();

	FacilicomDatabaseManager mDbManager;
	FacilicomDatabaseHelper mDatabaseHelper;

	private Dao<CheckBlank, Integer> mDao;

	public CheckBlanksRepository(Context context) {
		mDbManager = new FacilicomDatabaseManager();
		mDatabaseHelper = mDbManager.getHelper(context);
		mDao = mDatabaseHelper.getCheckBlanksDao();
	}

	public int create(CheckBlank checkObject) {
		try {
			return mDao.create(checkObject);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public int delete(CheckBlank checkBlank) {
		try {
			return mDao.delete(checkBlank);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public int update(CheckBlank checkBlank) {
		try {
			return mDao.update(checkBlank);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public List<CheckBlank> getAll() {
		try {
			return mDao.queryForAll();
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return null;
	}

	public int deleteAll() {
		try {
			return TableUtils.clearTable(mDatabaseHelper.getConnectionSource(), CheckBlank.class);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return -1;
	}

	public int deleteAll(HashSet<Integer> excludeCheckBlanks) {
		try {
			ArrayList<String> values = new ArrayList<>();
			for (Integer checkBlankId : excludeCheckBlanks) {
				values.add(String.valueOf(checkBlankId));
			}

			mDao.executeRawNoArgs(TextUtils.concat("DELETE FROM check_blanks WHERE check_blank_id NOT IN (", TextUtils.join(",", values), ")").toString());
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return -1;
	}

	public List<CheckBlank> getAll(CheckType checkType) {
		try {
			return mDao.queryBuilder().where().eq("check_blank_check_type_id", checkType.getCheckId()).query();
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

	public CheckBlank getByCheckBlankId(int checkBlankId) {
		try {
			return mDao.queryBuilder().where().eq("check_blank_id", checkBlankId).query().get(0);
		} catch (Exception e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return null;
	}

	public int bulkCreate(final List<CheckBlank> checkBlanks, final HashSet<Integer> excludeCheckBlanks) {
		if (excludeCheckBlanks != null && !excludeCheckBlanks.isEmpty()) {
			deleteAll(excludeCheckBlanks);
		} else {
			deleteAll();
		}

		try {
			return mDao.callBatchTasks(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					int counter = 0;
					for (CheckBlank checkBlank : checkBlanks) {
						mDao.create(checkBlank);
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

	public Dao<CheckBlank, Integer> getDao() {
		return mDao;
	}

	public void close() {
		mDbManager.releaseHelper();
	}
}
