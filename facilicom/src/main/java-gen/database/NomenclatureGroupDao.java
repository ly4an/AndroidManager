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
 * DAO for table "NOMENCLATURE_GROUP".
 */
public class NomenclatureGroupDao extends AbstractDao<NomenclatureGroup, Long> {

    public static final String TABLENAME = "NOMENCLATURE_GROUP";

    public NomenclatureGroupDao(DaoConfig config) {
        super(config);
    }


    public NomenclatureGroupDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

	/**
	 * Creates the underlying database table.
	 */
	public static void createTable(Database db, boolean ifNotExists) {
		String constraint = ifNotExists ? "IF NOT EXISTS " : "";
		db.execSQL("CREATE TABLE " + constraint + "\"NOMENCLATURE_GROUP\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"NOMENCLATURE_GROUP_ID\" INTEGER," + // 1: nomenclatureGroupId
                "\"CODE\" TEXT," + // 2: code
                "\"NAME\" TEXT);"); // 3: name
	}

	/**
	 * Drops the underlying database table.
	 */
	public static void dropTable(Database db, boolean ifExists) {
		String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NOMENCLATURE_GROUP\"";
        db.execSQL(sql);
	}

	@Override
	protected final void bindValues(DatabaseStatement stmt, NomenclatureGroup entity) {
		stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        Integer nomenclatureGroupId = entity.getNomenclatureGroupId();
        if (nomenclatureGroupId != null) {
            stmt.bindLong(2, nomenclatureGroupId);
        }

        String code = entity.getCode();
        if (code != null) {
            stmt.bindString(3, code);
        }

        String name = entity.getName();
        if (name != null) {
            stmt.bindString(4, name);
        }
	}

	@Override
	protected final void bindValues(SQLiteStatement stmt, NomenclatureGroup entity) {
		stmt.clearBindings();

		Long id = entity.getId();
		if (id != null) {
			stmt.bindLong(1, id);
		}

		Integer nomenclatureGroupId = entity.getNomenclatureGroupId();
		if (nomenclatureGroupId != null) {
			stmt.bindLong(2, nomenclatureGroupId);
		}

		String code = entity.getCode();
		if (code != null) {
			stmt.bindString(3, code);
		}

		String name = entity.getName();
		if (name != null) {
			stmt.bindString(4, name);
		}
	}

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    @Override
    public NomenclatureGroup readEntity(Cursor cursor, int offset) {
        NomenclatureGroup entity = new NomenclatureGroup( //
		        cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
		        cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // nomenclatureGroupId
		        cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // code
		        cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // name
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, NomenclatureGroup entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setNomenclatureGroupId(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setCode(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
    }

	@Override
	protected final Long updateKeyAfterInsert(NomenclatureGroup entity, long rowId) {
		entity.setId(rowId);
		return rowId;
    }
    
    @Override
    public Long getKey(NomenclatureGroup entity) {
	    if(entity != null) {
		    return entity.getId();
        } else {
            return null;
	    }
    }

	@Override
	public boolean hasKey(NomenclatureGroup entity) {
		return entity.getId() != null;
	}

	@Override
	protected final boolean isEntityUpdateable() {
		return true;
    }

    /**
     * Properties of entity NomenclatureGroup.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property NomenclatureGroupId = new Property(1, Integer.class, "nomenclatureGroupId", false, "NOMENCLATURE_GROUP_ID");
        public final static Property Code = new Property(2, String.class, "code", false, "CODE");
        public final static Property Name = new Property(3, String.class, "name", false, "NAME");
    }
    
}
