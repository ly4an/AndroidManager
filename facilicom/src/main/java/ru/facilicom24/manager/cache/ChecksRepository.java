package ru.facilicom24.manager.cache;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

import ru.facilicom24.manager.model.Check;

public class ChecksRepository {
	static final String TAG = ChecksRepository.class.getSimpleName();

	FacilicomDatabaseHelper mDatabaseHelper;

	private Dao<Check, Integer> mDao;
	private FacilicomDatabaseManager mDbManager;

	public ChecksRepository(Context context) {
		mDbManager = new FacilicomDatabaseManager();
		mDatabaseHelper = mDbManager.getHelper(context);
		mDao = mDatabaseHelper.getChecksDao();
	}

	public int create(Check check) {
		try {
			return mDao.create(check);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return -1;
	}

	public int delete(Check check) {
		try {
			return mDao.delete(check);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return -1;
	}

	public int deleteAll() {
		try {
			TableUtils.clearTable(mDatabaseHelper.getConnectionSource(), Check.class);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return -1;
	}

	public int update(Check check) {
		try {
			return mDao.update(check);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return -1;
	}

	public List<Check> getAll() {
		try {
			return mDao.queryForAll();
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return null;
	}

	public List<Check> getAll(int state) {
		try {
			return mDao.queryBuilder().where().eq("state", state).query();
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return null;
	}

	public void close() {
		mDbManager.releaseHelper();
	}
}
