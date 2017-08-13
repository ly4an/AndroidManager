package ru.facilicom24.manager.cache;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import ru.facilicom24.manager.model.Element;

class ElementsRepository {
	private static final String TAG = ElementsRepository.class.getSimpleName();
	FacilicomDatabaseHelper mDatabaseHelper;
	private FacilicomDatabaseManager mDbManager;
	private Dao<Element, Integer> mDao;

	ElementsRepository(Context context) {
		mDbManager = new FacilicomDatabaseManager();
		mDatabaseHelper = mDbManager.getHelper(context);
		mDao = mDatabaseHelper.getElementsDao();
	}

	public int create(Element element) {
		try {
			return mDao.create(element);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public int delete(Element element) {
		try {
			return mDao.delete(element);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public int update(Element element) {
		try {
			return mDao.update(element);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}
		return -1;
	}

	public List<Element> getAll() {
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

	public Dao<Element, Integer> getDao() {
		return mDao;
	}

	public void close() {
		mDbManager.releaseHelper();
	}

	int bulkCreate(Context context, final List<Element> elements) {
		final ReasonsRepository reasonsRepository = new ReasonsRepository(context);

		try {
			return mDao.callBatchTasks(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					int counter = 0;
					for (Element element : elements) {
						mDao.create(element);
						reasonsRepository.bulkCreate(element.getDbReasons());

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
