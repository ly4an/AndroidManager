package database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "CONTACT_ACCOUNT".
 */
@Entity
public class ContactAccount {

	@Id
	private Long id;
    private Integer directumID;
    private Integer contactID;

	@Generated
	public ContactAccount() {
    }

    public ContactAccount(Long id) {
        this.id = id;
    }

	@Generated
	public ContactAccount(Long id, Integer directumID, Integer contactID) {
        this.id = id;
        this.directumID = directumID;
        this.contactID = contactID;
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

    public Integer getContactID() {
        return contactID;
    }

    public void setContactID(Integer contactID) {
        this.contactID = contactID;
    }

}
