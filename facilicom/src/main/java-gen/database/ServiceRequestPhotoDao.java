package database;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "SERVICE_REQUEST_PHOTO".
 */
public class ServiceRequestPhotoDao extends AbstractDao<ServiceRequestPhoto, Long> {

    public static final String TABLENAME = "SERVICE_REQUEST_PHOTO";
    private Query<ServiceRequestPhoto> serviceRequest_PhotosQuery;

    public ServiceRequestPhotoDao(DaoConfig config) {
        super(config);
    }

    public ServiceRequestPhotoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

	/**
	 * Creates the underlying database table.
	 */
	public static void createTable(Database db, boolean ifNotExists) {
		String constraint = ifNotExists ? "IF NOT EXISTS " : "";
		db.execSQL("CREATE TABLE " + constraint + "\"SERVICE_REQUEST_PHOTO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"SERVICE_REQUEST_ID\" INTEGER," + // 1: ServiceRequestID
                "\"SERVICE_REQUEST_FILE_ID\" INTEGER," + // 2: ServiceRequestFileID
                "\"PHOTO_FILE_NAME\" TEXT);"); // 3: PhotoFileName
	}

	/**
	 * Drops the underlying database table.
	 */
	public static void dropTable(Database db, boolean ifExists) {
		String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SERVICE_REQUEST_PHOTO\"";
        db.execSQL(sql);
	}

	@Override
	protected final void bindValues(DatabaseStatement stmt, ServiceRequestPhoto entity) {
		stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        Long ServiceRequestID = entity.getServiceRequestID();
        if (ServiceRequestID != null) {
            stmt.bindLong(2, ServiceRequestID);
        }

        Integer ServiceRequestFileID = entity.getServiceRequestFileID();
        if (ServiceRequestFileID != null) {
            stmt.bindLong(3, ServiceRequestFileID);
        }

        String PhotoFileName = entity.getPhotoFileName();
        if (PhotoFileName != null) {
            stmt.bindString(4, PhotoFileName);
        }
	}

	@Override
	protected final void bindValues(SQLiteStatement stmt, ServiceRequestPhoto entity) {
		stmt.clearBindings();

		Long id = entity.getId();
		if (id != null) {
			stmt.bindLong(1, id);
		}

		Long ServiceRequestID = entity.getServiceRequestID();
		if (ServiceRequestID != null) {
			stmt.bindLong(2, ServiceRequestID);
		}

		Integer ServiceRequestFileID = entity.getServiceRequestFileID();
		if (ServiceRequestFileID != null) {
			stmt.bindLong(3, ServiceRequestFileID);
		}

		String PhotoFileName = entity.getPhotoFileName();
		if (PhotoFileName != null) {
			stmt.bindString(4, PhotoFileName);
		}
	}

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    @Override
    public ServiceRequestPhoto readEntity(Cursor cursor, int offset) {
        ServiceRequestPhoto entity = new ServiceRequestPhoto( //
		        cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
		        cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // ServiceRequestID
		        cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // ServiceRequestFileID
		        cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // PhotoFileName
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, ServiceRequestPhoto entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setServiceRequestID(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setServiceRequestFileID(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setPhotoFileName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
    }

	@Override
	protected final Long updateKeyAfterInsert(ServiceRequestPhoto entity, long rowId) {
		entity.setId(rowId);
		return rowId;
    }
    
    @Override
    public Long getKey(ServiceRequestPhoto entity) {
	    if(entity != null) {
		    return entity.getId();
        } else {
            return null;
	    }
    }

	@Override
	public boolean hasKey(ServiceRequestPhoto entity) {
		return entity.getId() != null;
	}

	@Override
	protected final boolean isEntityUpdateable() {
		return true;
	}

	/** Internal query to resolve the "Photos" to-many relationship of ServiceRequest. */
	public List<ServiceRequestPhoto> _queryServiceRequest_Photos(Long ServiceRequestID) {
        synchronized (this) {
            if (serviceRequest_PhotosQuery == null) {
                QueryBuilder<ServiceRequestPhoto> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.ServiceRequestID.eq(null));
                serviceRequest_PhotosQuery = queryBuilder.build();
            }
        }
        Query<ServiceRequestPhoto> query = serviceRequest_PhotosQuery.forCurrentThread();
        query.setParameter(0, ServiceRequestID);
        return query.list();
    }
    
    /**
     * Properties of entity ServiceRequestPhoto.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ServiceRequestID = new Property(1, Long.class, "ServiceRequestID", false, "SERVICE_REQUEST_ID");
        public final static Property ServiceRequestFileID = new Property(2, Integer.class, "ServiceRequestFileID", false, "SERVICE_REQUEST_FILE_ID");
        public final static Property PhotoFileName = new Property(3, String.class, "PhotoFileName", false, "PHOTO_FILE_NAME");
    }

}