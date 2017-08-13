package ru.facilicom24.manager.cache;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import ru.facilicom24.manager.model.Reason;

public class ReasonsRepository {
	private static final String TAG = ReasonsRepository.class.getSimpleName();

	FacilicomDatabaseManager mDbManager;
	FacilicomDatabaseHelper mDatabaseHelper;

	private Dao<Reason, Integer> mDao;

	public ReasonsRepository(Context context) {
		mDbManager = new FacilicomDatabaseManager();
		mDatabaseHelper = mDbManager.getHelper(context);
		mDao = mDatabaseHelper.getReasonsDao();
	}

	public int create(Reason reason) {
		try {
			return mDao.create(reason);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public int delete(Reason reason) {
		try {
			return mDao.delete(reason);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public int update(Reason reason) {
		try {
			return mDao.update(reason);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public List<Reason> getAll() {
		try {
			return mDao.queryForAll();
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return null;
	}

	public int deleteAll() {
		try {
			return mDao.delete(mDao.queryForAll());
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public Dao<Reason, Integer> getDao() {
		return mDao;
	}

	public void close() {
		mDbManager.releaseHelper();
	}

	int bulkCreate(final Collection<Reason> reasons) {
		try {
			return mDao.callBatchTasks(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					int counter = 0;
					for (Reason reason : reasons) {
						mDao.create(reason);
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
