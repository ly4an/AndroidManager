package database;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "ACT_ACCOUNT".
 */
@Entity(active = true)
public class ActAccount {

	@Id
	private Long id;
    private Integer directumID;
    private String name;
    private String address;
    private Integer status;
    private long clientID;

	/**
	 * Used to resolve relations
	 */
	@Generated
	private transient DaoSession daoSession;

	/**
	 * Used for active entity operations.
	 */
	@Generated
	private transient ActAccountDao myDao;

	@ToOne(joinProperty = "clientID")
	private Client client;

	@Generated
	private transient Long client__resolvedKey;

    @Generated
    public ActAccount() {
    }

    public ActAccount(Long id) {
	    this.id = id;
    }

    @Generated
    public ActAccount(Long id, Integer directumID, String name, String address, Integer status, long clientID) {
        this.id = id;
        this.directumID = directumID;
        this.name = name;
        this.address = address;
        this.status = status;
        this.clientID = clientID;
    }

	/** called by internal mechanisms, do not call yourself. */
	@Generated
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getActAccountDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDirectumID() {
        return directumID;
    }

    public void setDirectumID(Integer directumID) {
        this.directumID = directumID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public long getClientID() {
        return clientID;
    }

    public void setClientID(long clientID) {
        this.clientID = clientID;
    }

	/** To-one relationship, resolved on first access. */
	@Generated
    public Client getClient() {
        long __key = this.clientID;
        if (client__resolvedKey == null || !client__resolvedKey.equals(__key)) {
	        __throwIfDetached();
	        ClientDao targetDao = daoSession.getClientDao();
            Client clientNew = targetDao.load(__key);
            synchronized (this) {
	            client = clientNew;
	            client__resolvedKey = __key;
            }
        }
	    return client;
    }

    @Generated
    public void setClient(Client client) {
        if (client == null) {
            throw new DaoException("To-one property 'clientID' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.client = client;
            clientID = client.getId();
            client__resolvedKey = clientID;
        }
    }

	/**
	 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
	 * Entity must attached to an entity context.
	 */
    @Generated
    public void delete() {
	    __throwIfDetached();
	    myDao.delete(this);
    }

	/**
	 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
	 * Entity must attached to an entity context.
	 */
    @Generated
    public void update() {
	    __throwIfDetached();
	    myDao.update(this);
    }

	/**
	 * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
	 * Entity must attached to an entity context.
	 */
    @Generated
    public void refresh() {
	    __throwIfDetached();
	    myDao.refresh(this);
    }

	@Generated
	private void __throwIfDetached() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
    }

}
