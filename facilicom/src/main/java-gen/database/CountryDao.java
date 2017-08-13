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
 * DAO for table "COUNTRY".
 */
public class CountryDao extends AbstractDao<Country, Long> {

    public static final String TABLENAME = "COUNTRY";

    public CountryDao(DaoConfig config) {
        super(config);
    }


    public CountryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

	/**
	 * Creates the underlying database table.
	 */
	public static void createTable(Database db, boolean ifNotExists) {
		String constraint = ifNotExists ? "IF NOT EXISTS " : "";
		db.execSQL("CREATE TABLE " + constraint + "\"COUNTRY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"COUNTRY_UID\" TEXT," + // 1: CountryUID
                "\"COUNTRY_NAME\" TEXT," + // 2: CountryName
                "\"NEED_PATENT_OR_PERMISSION\" INTEGER);"); // 3: NeedPatentOrPermission
	}

	/**
	 * Drops the underlying database table.
	 */
	public static void dropTable(Database db, boolean ifExists) {
		String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"COUNTRY\"";
        db.execSQL(sql);
	}

	@Override
	protected final void bindValues(DatabaseStatement stmt, Country entity) {
		stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        String CountryUID = entity.getCountryUID();
        if (CountryUID != null) {
            stmt.bindString(2, CountryUID);
        }

        String CountryName = entity.getCountryName();
        if (CountryName != null) {
            stmt.bindString(3, CountryName);
        }

        Integer NeedPatentOrPermission = entity.getNeedPatentOrPermission();
        if (NeedPatentOrPermission != null) {
            stmt.bindLong(4, NeedPatentOrPermission);
        }
	}

	@Override
	protected final void bindValues(SQLiteStatement stmt, Country entity) {
		stmt.clearBindings();

		Long id = entity.getId();
		if (id != null) {
			stmt.bindLong(1, id);
		}

		String CountryUID = entity.getCountryUID();
		if (CountryUID != null) {
			stmt.bindString(2, CountryUID);
		}

		String CountryName = entity.getCountryName();
		if (CountryName != null) {
			stmt.bindString(3, CountryName);
		}

		Integer NeedPatentOrPermission = entity.getNeedPatentOrPermission();
		if (NeedPatentOrPermission != null) {
			stmt.bindLong(4, NeedPatentOrPermission);
		}
	}

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    @Override
    public Country readEntity(Cursor cursor, int offset) {
        Country entity = new Country( //
		        cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
		        cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // CountryUID
		        cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // CountryName
		        cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3) // NeedPatentOrPermission
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, Country entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCountryUID(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCountryName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setNeedPatentOrPermission(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
    }

	@Override
	protected final Long updateKeyAfterInsert(Country entity, long rowId) {
		entity.setId(rowId);
		return rowId;
    }
    
    @Override
    public Long getKey(Country entity) {
	    if(entity != null) {
		    return entity.getId();
        } else {
            return null;
	    }
    }

	@Override
	public boolean hasKey(Country entity) {
		return entity.getId() != null;
	}

	@Override
	protected final boolean isEntityUpdateable() {
		return true;
    }

    /**
     * Properties of entity Country.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property CountryUID = new Property(1, String.class, "CountryUID", false, "COUNTRY_UID");
        public final static Property CountryName = new Property(2, String.class, "CountryName", false, "COUNTRY_NAME");
        public final static Property NeedPatentOrPermission = new Property(3, Integer.class, "NeedPatentOrPermission", false, "NEED_PATENT_OR_PERMISSION");
    }
    
}
