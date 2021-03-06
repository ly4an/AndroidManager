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
 * Entity mapped to table "SERVICE_REQUEST".
 */
@Entity(active = true)
public class ServiceRequest {

	@Id
	private Long id;
    private Integer ID;
    private String UID;
    private String DueDate;
    private String CreatedOn;
    private String FacilityName;
    private String FacilityAddress;
    private String UrgencyType;
    private String Status;
    private String Content;
    private String ServiceTypeName;
    private Integer CanExecute;
    private Integer NeedEvaluate;
    private Integer Mine;
    private String Comment;

	/**
	 * Used to resolve relations
	 */
	@Generated
	private transient DaoSession daoSession;

	/**
	 * Used for active entity operations.
	 */
	@Generated
	private transient ServiceRequestDao myDao;

	@ToMany(joinProperties = {
			@JoinProperty(name = "id", referencedName = "ServiceRequestID")
	})
    private List<ServiceRequestServant> Servants;

	@ToMany(joinProperties = {
			@JoinProperty(name = "id", referencedName = "ServiceRequestID")
	})
    private List<ServiceRequestLog> Log;

	@ToMany(joinProperties = {
			@JoinProperty(name = "id", referencedName = "ServiceRequestID")
	})
    private List<ServiceRequestFile> Files;

	@ToMany(joinProperties = {
			@JoinProperty(name = "id", referencedName = "ServiceRequestID")
	})
    private List<ServiceRequestPhoto> Photos;

    @Generated
    public ServiceRequest() {
    }

    public ServiceRequest(Long id) {
	    this.id = id;
    }

    @Generated
    public ServiceRequest(Long id, Integer ID, String UID, String DueDate, String CreatedOn, String FacilityName, String FacilityAddress, String UrgencyType, String Status, String Content, String ServiceTypeName, Integer CanExecute, Integer NeedEvaluate, Integer Mine, String Comment) {
        this.id = id;
        this.ID = ID;
        this.UID = UID;
        this.DueDate = DueDate;
        this.CreatedOn = CreatedOn;
        this.FacilityName = FacilityName;
        this.FacilityAddress = FacilityAddress;
        this.UrgencyType = UrgencyType;
        this.Status = Status;
        this.Content = Content;
        this.ServiceTypeName = ServiceTypeName;
        this.CanExecute = CanExecute;
        this.NeedEvaluate = NeedEvaluate;
        this.Mine = Mine;
        this.Comment = Comment;
    }

	/** called by internal mechanisms, do not call yourself. */
	@Generated
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getServiceRequestDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String DueDate) {
        this.DueDate = DueDate;
    }

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String CreatedOn) {
        this.CreatedOn = CreatedOn;
    }

    public String getFacilityName() {
        return FacilityName;
    }

    public void setFacilityName(String FacilityName) {
        this.FacilityName = FacilityName;
    }

    public String getFacilityAddress() {
        return FacilityAddress;
    }

    public void setFacilityAddress(String FacilityAddress) {
        this.FacilityAddress = FacilityAddress;
    }

    public String getUrgencyType() {
        return UrgencyType;
    }

    public void setUrgencyType(String UrgencyType) {
        this.UrgencyType = UrgencyType;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    public String getServiceTypeName() {
        return ServiceTypeName;
    }

    public void setServiceTypeName(String ServiceTypeName) {
        this.ServiceTypeName = ServiceTypeName;
    }

    public Integer getCanExecute() {
        return CanExecute;
    }

    public void setCanExecute(Integer CanExecute) {
        this.CanExecute = CanExecute;
    }

    public Integer getNeedEvaluate() {
        return NeedEvaluate;
    }

    public void setNeedEvaluate(Integer NeedEvaluate) {
        this.NeedEvaluate = NeedEvaluate;
    }

    public Integer getMine() {
        return Mine;
    }

    public void setMine(Integer Mine) {
        this.Mine = Mine;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String Comment) {
        this.Comment = Comment;
    }

	/** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
	@Generated
    public List<ServiceRequestServant> getServants() {
	    if (Servants == null) {
		    __throwIfDetached();
		    ServiceRequestServantDao targetDao = daoSession.getServiceRequestServantDao();
            List<ServiceRequestServant> ServantsNew = targetDao._queryServiceRequest_Servants(id);
		    synchronized (this) {
			    if(Servants == null) {
				    Servants = ServantsNew;
                }
            }
        }
	    return Servants;
    }

	/** Resets a to-many relationship, making the next get call to query for a fresh result. */
	@Generated
    public synchronized void resetServants() {
	    Servants = null;
    }

	/** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
	@Generated
    public List<ServiceRequestLog> getLog() {
	    if (Log == null) {
		    __throwIfDetached();
		    ServiceRequestLogDao targetDao = daoSession.getServiceRequestLogDao();
            List<ServiceRequestLog> LogNew = targetDao._queryServiceRequest_Log(id);
		    synchronized (this) {
			    if(Log == null) {
				    Log = LogNew;
                }
            }
        }
        return Log;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated
    public synchronized void resetLog() {
	    Log = null;
    }

	/** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
	@Generated
    public List<ServiceRequestFile> getFiles() {
	    if (Files == null) {
		    __throwIfDetached();
		    ServiceRequestFileDao targetDao = daoSession.getServiceRequestFileDao();
            List<ServiceRequestFile> FilesNew = targetDao._queryServiceRequest_Files(id);
		    synchronized (this) {
			    if(Files == null) {
				    Files = FilesNew;
                }
            }
        }
        return Files;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated
    public synchronized void resetFiles() {
	    Files = null;
    }

	/** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
	@Generated
    public List<ServiceRequestPhoto> getPhotos() {
	    if (Photos == null) {
		    __throwIfDetached();
		    ServiceRequestPhotoDao targetDao = daoSession.getServiceRequestPhotoDao();
            List<ServiceRequestPhoto> PhotosNew = targetDao._queryServiceRequest_Photos(id);
		    synchronized (this) {
			    if(Photos == null) {
				    Photos = PhotosNew;
                }
            }
        }
        return Photos;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated
    public synchronized void resetPhotos() {
        Photos = null;
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
