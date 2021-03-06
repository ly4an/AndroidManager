package database;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "CHECK_IN_LOG".
 */
public class CheckInLogDao extends AbstractDao<CheckInLog, Long> {

    public static final String TABLENAME = "CHECK_IN_LOG";

    public CheckInLogDao(DaoConfig config) {
        super(config);
    }


    public CheckInLogDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

	/**
	 * Creates the underlying database table.
	 */
	public static void createTable(Database db, boolean ifNotExists) {
		String constraint = ifNotExists ? "IF NOT EXISTS " : "";
		db.execSQL("CREATE TABLE " + constraint + "\"CHECK_IN_LOG\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"DATE_TIME\" INTEGER," + // 1: DateTime
                "\"DIRECTUM_ID\" INTEGER);"); // 2: DirectumID
	}

	/**
	 * Drops the underlying database table.
	 */
	public static void dropTable(Database db, boolean ifExists) {
		String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CHECK_IN_LOG\"";
        db.execSQL(sql);
	}

	@Override
	protected final void bindValues(DatabaseStatement stmt, CheckInLog entity) {
		stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        java.util.Date DateTime = entity.getDateTime();
        if (DateTime != null) {
            stmt.bindLong(2, DateTime.getTime());
        }

        Integer DirectumID = entity.getDirectumID();
        if (DirectumID != null) {
            stmt.bindLong(3, DirectumID);
        }
	}

	@Override
	protected final void bindValues(SQLiteStatement stmt, CheckInLog entity) {
		stmt.clearBindings();

		Long id = entity.getId();
		if (id != null) {
			stmt.bindLong(1, id);
		}

		java.util.Date DateTime = entity.getDateTime();
		if (DateTime != null) {
			stmt.bindLong(2, DateTime.getTime());
		}

		Integer DirectumID = entity.getDirectumID();
		if (DirectumID != null) {
			stmt.bindLong(3, DirectumID);
		}
	}

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    @Override
    public CheckInLog readEntity(Cursor cursor, int offset) {
        CheckInLog entity = new CheckInLog( //
		        cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
		        cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)), // DateTime
		        cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2) // DirectumID
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, CheckInLog entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDateTime(cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)));
        entity.setDirectumID(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
    }

	@Override
	protected final Long updateKeyAfterInsert(CheckInLog entity, long rowId) {
		entity.setId(rowId);
		return rowId;
    }
    
    @Override
    public Long getKey(CheckInLog entity) {
	    if(entity != null) {
		    return entity.getId();
        } else {
            return null;
	    }
    }

	@Override
	public boolean hasKey(CheckInLog entity) {
		return entity.getId() != null;
	}

	@Override
	protected final boolean isEntityUpdateable() {
		return true;
    }

    /**
     * Properties of entity CheckInLog.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DateTime = new Property(1, java.util.Date.class, "DateTime", false, "DATE_TIME");
        public final static Property DirectumID = new Property(2, Integer.class, "DirectumID", false, "DIRECTUM_ID");
    }
    
}
