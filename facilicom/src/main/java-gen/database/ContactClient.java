package database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "CONTACT_CLIENT".
 */
@Entity
public class ContactClient {

	@Id
	private Long id;
    private Integer clientID;
    private Integer contactID;

	@Generated
	public ContactClient() {
    }

    public ContactClient(Long id) {
        this.id = id;
    }

	@Generated
	public ContactClient(Long id, Integer clientID, Integer contactID) {
        this.id = id;
        this.clientID = clientID;
        this.contactID = contactID;
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

    public Integer getContactID() {
        return contactID;
    }

    public void setContactID(Integer contactID) {
        this.contactID = contactID;
    }

}
