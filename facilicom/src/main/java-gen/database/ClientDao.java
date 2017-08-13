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
 * DAO for table "CLIENT".
 */
public class ClientDao extends AbstractDao<Client, Long> {

    public static final String TABLENAME = "CLIENT";
    private DaoSession daoSession;

    public ClientDao(DaoConfig config) {
        super(config);
    }


    public ClientDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

	/**
	 * Creates the underlying database table.
	 */
	public static void createTable(Database db, boolean ifNotExists) {
		String constraint = ifNotExists ? "IF NOT EXISTS " : "";
		db.execSQL("CREATE TABLE " + constraint + "\"CLIENT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"CLIENT_ID\" INTEGER," + // 1: clientID
                "\"NAME\" TEXT," + // 2: name
                "\"STATUS\" INTEGER);"); // 3: status
	}

	/**
	 * Drops the underlying database table.
	 */
	public static void dropTable(Database db, boolean ifExists) {
		String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CLIENT\"";
        db.execSQL(sql);
	}

	@Override
	protected final void bindValues(DatabaseStatement stmt, Client entity) {
		stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        Integer clientID = entity.getClientID();
        if (clientID != null) {
            stmt.bindLong(2, clientID);
        }

        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }

        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(4, status);
        }
	}

	@Override
	protected final void bindValues(SQLiteStatement stmt, Client entity) {
		stmt.clearBindings();

		Long id = entity.getId();
		if (id != null) {
			stmt.bindLong(1, id);
		}

		Integer clientID = entity.getClientID();
		if (clientID != null) {
			stmt.bindLong(2, clientID);
		}

		String name = entity.getName();
		if (name != null) {
			stmt.bindString(3, name);
		}

		Integer status = entity.getStatus();
		if (status != null) {
			stmt.bindLong(4, status);
		}
	}

	@Override
	protected final void attachEntity(Client entity) {
		super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    @Override
    public Client readEntity(Cursor cursor, int offset) {
        Client entity = new Client( //
		        cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
		        cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // clientID
		        cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
		        cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3) // status
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, Client entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setClientID(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setStatus(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
    }

	@Override
	protected final Long updateKeyAfterInsert(Client entity, long rowId) {
		entity.setId(rowId);
		return rowId;
    }
    
    @Override
    public Long getKey(Client entity) {
	    if(entity != null) {
		    return entity.getId();
        } else {
            return null;
	    }
    }

	@Override
	public boolean hasKey(Client entity) {
		return entity.getId() != null;
	}

	@Override
	protected final boolean isEntityUpdateable() {
		return true;
    }

    /**
     * Properties of entity Client.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ClientID = new Property(1, Integer.class, "clientID", false, "CLIENT_ID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Status = new Property(3, Integer.class, "status", false, "STATUS");
    }
    
}
