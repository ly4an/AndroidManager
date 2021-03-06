package database;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "CLIENT".
 */
@Entity(active = true)
public class Client {

	@Id
	private Long id;
    private Integer clientID;
    private String name;
    private Integer status;

	/**
	 * Used to resolve relations
	 */
	@Generated
	private transient DaoSession daoSession;

	/**
	 * Used for active entity operations.
	 */
	@Generated
	private transient ClientDao myDao;

	@ToMany(joinProperties = {
			@JoinProperty(name = "id", referencedName = "clientID")
	})
    private List<ActAccount> accounts;

	@ToMany(joinProperties = {
			@JoinProperty(name = "id", referencedName = "clientID")
	})
    private List<ActServiceType> actServiceTypes;

    @Generated
    public Client() {
    }

    public Client(Long id) {
	    this.id = id;
    }

    @Generated
    public Client(Long id, Integer clientID, String name, Integer status) {
        this.id = id;
        this.clientID = clientID;
        this.name = name;
        this.status = status;
    }

	/** called by internal mechanisms, do not call yourself. */
	@Generated
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getClientDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getClientID() {
        return clientID;
    }

    public void setClientID(Integer clientID) {
        this.clientID = clientID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

	/** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
	@Generated
    public List<ActAccount> getAccounts() {
	    if (accounts == null) {
		    __throwIfDetached();
		    ActAccountDao targetDao = daoSession.getActAccountDao();
            List<ActAccount> accountsNew = targetDao._queryClient_Accounts(id);
		    synchronized (this) {
			    if(accounts == null) {
				    accounts = accountsNew;
                }
            }
        }
	    return accounts;
    }

	/** Resets a to-many relationship, making the next get call to query for a fresh result. */
	@Generated
    public synchronized void resetAccounts() {
	    accounts = null;
    }

	/** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
	@Generated
    public List<ActServiceType> getActServiceTypes() {
        if (actServiceTypes == null) {
	        __throwIfDetached();
	        ActServiceTypeDao targetDao = daoSession.getActServiceTypeDao();
            List<ActServiceType> actServiceTypesNew = targetDao._queryClient_ActServiceTypes(id);
	        synchronized (this) {
		        if (actServiceTypes == null) {
			        actServiceTypes = actServiceTypesNew;
                }
            }
        }
        return actServiceTypes;
    }

	/** Resets a to-many relationship, making the next get call to query for a fresh result. */
	@Generated
    public synchronized void resetActServiceTypes() {
        actServiceTypes = null;
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
