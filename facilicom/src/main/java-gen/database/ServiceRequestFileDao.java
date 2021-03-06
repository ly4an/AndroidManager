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
 * DAO for table "SERVICE_REQUEST_FILE".
 */
public class ServiceRequestFileDao extends AbstractDao<ServiceRequestFile, Long> {

    public static final String TABLENAME = "SERVICE_REQUEST_FILE";
    private Query<ServiceRequestFile> serviceRequest_FilesQuery;

    public ServiceRequestFileDao(DaoConfig config) {
        super(config);
    }

    public ServiceRequestFileDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

	/**
	 * Creates the underlying database table.
	 */
	public static void createTable(Database db, boolean ifNotExists) {
		String constraint = ifNotExists ? "IF NOT EXISTS " : "";
		db.execSQL("CREATE TABLE " + constraint + "\"SERVICE_REQUEST_FILE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"SERVICE_REQUEST_ID\" INTEGER," + // 1: ServiceRequestID
                "\"SERVICE_REQUEST_FILE_ID\" INTEGER," + // 2: ServiceRequestFileID
                "\"TYPE\" INTEGER," + // 3: Type
                "\"EXT\" TEXT);"); // 4: Ext
	}

	/**
	 * Drops the underlying database table.
	 */
	public static void dropTable(Database db, boolean ifExists) {
		String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SERVICE_REQUEST_FILE\"";
        db.execSQL(sql);
	}

	@Override
	protected final void bindValues(DatabaseStatement stmt, ServiceRequestFile entity) {
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

        Integer Type = entity.getType();
        if (Type != null) {
            stmt.bindLong(4, Type);
        }

        String Ext = entity.getExt();
        if (Ext != null) {
            stmt.bindString(5, Ext);
        }
	}

	@Override
	protected final void bindValues(SQLiteStatement stmt, ServiceRequestFile entity) {
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

		Integer Type = entity.getType();
		if (Type != null) {
			stmt.bindLong(4, Type);
		}

		String Ext = entity.getExt();
		if (Ext != null) {
			stmt.bindString(5, Ext);
		}
	}

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    @Override
    public ServiceRequestFile readEntity(Cursor cursor, int offset) {
        ServiceRequestFile entity = new ServiceRequestFile( //
		        cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
		        cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // ServiceRequestID
		        cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // ServiceRequestFileID
		        cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // Type
		        cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // Ext
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, ServiceRequestFile entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setServiceRequestID(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setServiceRequestFileID(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setType(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setExt(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
    }

	@Override
	protected final Long updateKeyAfterInsert(ServiceRequestFile entity, long rowId) {
		entity.setId(rowId);
		return rowId;
    }
    
    @Override
    public Long getKey(ServiceRequestFile entity) {
	    if(entity != null) {
		    return entity.getId();
        } else {
            return null;
	    }
    }

	@Override
	public boolean hasKey(ServiceRequestFile entity) {
		return entity.getId() != null;
	}

	@Override
	protected final boolean isEntityUpdateable() {
		return true;
	}

	/** Internal query to resolve the "Files" to-many relationship of ServiceRequest. */
	public List<ServiceRequestFile> _queryServiceRequest_Files(Long ServiceRequestID) {
        synchronized (this) {
            if (serviceRequest_FilesQuery == null) {
                QueryBuilder<ServiceRequestFile> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.ServiceRequestID.eq(null));
                serviceRequest_FilesQuery = queryBuilder.build();
            }
        }
        Query<ServiceRequestFile> query = serviceRequest_FilesQuery.forCurrentThread();
        query.setParameter(0, ServiceRequestID);
        return query.list();
    }
    
    /**
     * Properties of entity ServiceRequestFile.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ServiceRequestID = new Property(1, Long.class, "ServiceRequestID", false, "SERVICE_REQUEST_ID");
        public final static Property ServiceRequestFileID = new Property(2, Integer.class, "ServiceRequestFileID", false, "SERVICE_REQUEST_FILE_ID");
        public final static Property Type = new Property(3, Integer.class, "Type", false, "TYPE");
        public final static Property Ext = new Property(4, String.class, "Ext", false, "EXT");
    }

}
