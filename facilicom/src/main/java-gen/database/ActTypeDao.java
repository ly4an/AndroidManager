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
 * DAO for table "ACT_TYPE".
 */
public class ActTypeDao extends AbstractDao<ActType, Long> {

    public static final String TABLENAME = "ACT_TYPE";

    public ActTypeDao(DaoConfig config) {
        super(config);
    }


    public ActTypeDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

	/**
	 * Creates the underlying database table.
	 */
	public static void createTable(Database db, boolean ifNotExists) {
		String constraint = ifNotExists ? "IF NOT EXISTS " : "";
		db.execSQL("CREATE TABLE " + constraint + "\"ACT_TYPE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"ACT_TYPE_ID\" INTEGER," + // 1: actTypeID
                "\"NAME\" TEXT);"); // 2: name
	}

	/**
	 * Drops the underlying database table.
	 */
	public static void dropTable(Database db, boolean ifExists) {
		String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ACT_TYPE\"";
        db.execSQL(sql);
	}

	@Override
	protected final void bindValues(DatabaseStatement stmt, ActType entity) {
		stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        Integer actTypeID = entity.getActTypeID();
        if (actTypeID != null) {
            stmt.bindLong(2, actTypeID);
        }

        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
	}

	@Override
	protected final void bindValues(SQLiteStatement stmt, ActType entity) {
		stmt.clearBindings();

		Long id = entity.getId();
		if (id != null) {
			stmt.bindLong(1, id);
		}

		Integer actTypeID = entity.getActTypeID();
		if (actTypeID != null) {
			stmt.bindLong(2, actTypeID);
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
    public ActType readEntity(Cursor cursor, int offset) {
        ActType entity = new ActType( //
		        cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
		        cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // actTypeID
		        cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // name
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, ActType entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setActTypeID(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
    }

	@Override
	protected final Long updateKeyAfterInsert(ActType entity, long rowId) {
		entity.setId(rowId);
		return rowId;
    }
    
    @Override
    public Long getKey(ActType entity) {
	    if(entity != null) {
		    return entity.getId();
        } else {
            return null;
	    }
    }

	@Override
	public boolean hasKey(ActType entity) {
		return entity.getId() != null;
	}

	@Override
	protected final boolean isEntityUpdateable() {
		return true;
    }

    /**
     * Properties of entity ActType.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ActTypeID = new Property(1, Integer.class, "actTypeID", false, "ACT_TYPE_ID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
    }
    
}