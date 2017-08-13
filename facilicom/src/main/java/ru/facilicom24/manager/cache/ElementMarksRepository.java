package ru.facilicom24.manager.cache;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import ru.facilicom24.manager.model.ElementMark;

public class ElementMarksRepository {
	private static final String TAG = ElementMarksRepository.class.getSimpleName();

	private FacilicomDatabaseManager mDbManager;
	private FacilicomDatabaseHelper mDatabaseHelper;

	private Dao<ElementMark, Integer> mDao;

	public ElementMarksRepository(Context context) {
		mDbManager = new FacilicomDatabaseManager();
		mDatabaseHelper = mDbManager.getHelper(context);
		mDao = mDatabaseHelper.getMarksDao();
	}

	public int create(ElementMark mark) {
		try {
			return mDao.create(mark);
		} catch (Exception e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public int delete(ElementMark mark) {
		try {
			return mDao.delete(mark);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public int update(ElementMark mark) {
		try {
			return mDao.update(mark);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public List<ElementMark> getAll() {
		try {
			return mDao.queryForAll();
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return null;
	}

	public int deleteAll() {
		try {
			return TableUtils.clearTable(mDatabaseHelper.getConnectionSource(), ElementMark.class);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public void close() {
		mDbManager.releaseHelper();
	}

	public Void elementMarksCheck() {
		try {
			return mDao.callBatchTasks(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					for (ElementMark elementMark : mDao.queryForAll()) {
						if (elementMark.getElement() == null) {
							mDao.delete(elementMark);
						}
					}

					return null;
				}
			});
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}
}
