package database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "SERVICE_REQUEST_SERVANT".
 */
@Entity
public class ServiceRequestServant {

	@Id
	private Long id;
    private Long ServiceRequestID;
    private String Name;

	@Generated
	public ServiceRequestServant() {
    }

    public ServiceRequestServant(Long id) {
        this.id = id;
    }

	@Generated
	public ServiceRequestServant(Long id, Long ServiceRequestID, String Name) {
        this.id = id;
        this.ServiceRequestID = ServiceRequestID;
        this.Name = Name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServiceRequestID() {
        return ServiceRequestID;
    }

    public void setServiceRequestID(Long ServiceRequestID) {
        this.ServiceRequestID = ServiceRequestID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

}
