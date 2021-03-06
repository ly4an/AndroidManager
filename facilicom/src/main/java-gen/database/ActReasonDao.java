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
 * DAO for table "ACT_REASON".
 */
public class ActReasonDao extends AbstractDao<ActReason, Long> {

    public static final String TABLENAME = "ACT_REASON";

    public ActReasonDao(DaoConfig config) {
        super(config);
    }


    public ActReasonDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

	/**
	 * Creates the underlying database table.
	 */
	public static void createTable(Database db, boolean ifNotExists) {
		String constraint = ifNotExists ? "IF NOT EXISTS " : "";
		db.execSQL("CREATE TABLE " + constraint + "\"ACT_REASON\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"ACT_REASON_ID\" INTEGER," + // 1: actReasonID
                "\"NAME\" TEXT);"); // 2: name
	}

	/**
	 * Drops the underlying database table.
	 */
	public static void dropTable(Database db, boolean ifExists) {
		String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ACT_REASON\"";
        db.execSQL(sql);
	}

	@Override
	protected final void bindValues(DatabaseStatement stmt, ActReason entity) {
		stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        Integer actReasonID = entity.getActReasonID();
        if (actReasonID != null) {
            stmt.bindLong(2, actReasonID);
        }

        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
	}

	@Override
	protected final void bindValues(SQLiteStatement stmt, ActReason entity) {
		stmt.clearBindings();

		Long id = entity.getId();
		if (id != null) {
			stmt.bindLong(1, id);
		}

		Integer actReasonID = entity.getActReasonID();
		if (actReasonID != null) {
			stmt.bindLong(2, actReasonID);
		}

		String name = entity.getName();
		if (name != null) {
			stmt.bindString(3, name);
		}
	}

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    @Override
    public ActReason readEntity(Cursor cursor, int offset) {
        ActReason entity = new ActReason( //
		        cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
		        cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // actReasonID
		        cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // name
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, ActReason entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setActReasonID(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
    }

	@Override
	protected final Long updateKeyAfterInsert(ActReason entity, long rowId) {
		entity.setId(rowId);
		return rowId;
    }
    
    @Override
    public Long getKey(ActReason entity) {
	    if(entity != null) {
		    return entity.getId();
        } else {
            return null;
	    }
    }

	@Override
	public boolean hasKey(ActReason entity) {
		return entity.getId() != null;
	}

	@Override
	protected final boolean isEntityUpdateable() {
		return true;
    }

    /**
     * Properties of entity ActReason.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ActReasonID = new Property(1, Integer.class, "actReasonID", false, "ACT_REASON_ID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
    }
    
}
