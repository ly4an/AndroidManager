package ru.facilicom24.manager.cache;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

import ru.facilicom24.manager.model.Photo;

public class PhotosRepository {
	static final String TAG = PhotosRepository.class.getSimpleName();
	FacilicomDatabaseHelper mDatabaseHelper;
	private Dao<Photo, Integer> mDao;
	private FacilicomDatabaseManager mDbManager;

	public PhotosRepository(Context context) {
		mDbManager = new FacilicomDatabaseManager();
		mDatabaseHelper = mDbManager.getHelper(context);
		mDao = mDatabaseHelper.getPhotosDao();
	}

	public int create(Photo photo) {
		try {
			return mDao.create(photo);
		} catch (Exception e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return -1;
	}

	public int delete(Photo photo) {
		try {
			return mDao.delete(photo);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return -1;
	}

	public int update(Photo photo) {
		try {
			return mDao.update(photo);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return -1;
	}

	public List<Photo> getAll() {
		try {
			return mDao.queryForAll();
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return null;
	}

	public int deleteAll() {
		try {
			TableUtils.clearTable(mDatabaseHelper.getConnectionSource(), Photo.class);
		} catch (SQLException e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return -1;
	}

	public void close() {
		mDbManager.releaseHelper();
	}
}
