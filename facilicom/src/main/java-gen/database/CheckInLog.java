package database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "CHECK_IN_LOG".
 */
@Entity
public class CheckInLog {

	@Id
	private Long id;
    private java.util.Date DateTime;
    private Integer DirectumID;

	@Generated
	public CheckInLog() {
    }

    public CheckInLog(Long id) {
        this.id = id;
    }

	@Generated
	public CheckInLog(Long id, java.util.Date DateTime, Integer DirectumID) {
        this.id = id;
        this.DateTime = DateTime;
        this.DirectumID = DirectumID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public java.util.Date getDateTime() {
        return DateTime;
    }

    public void setDateTime(java.util.Date DateTime) {
        this.DateTime = DateTime;
    }

    public Integer getDirectumID() {
        return DirectumID;
    }

    public void setDirectumID(Integer DirectumID) {
        this.DirectumID = DirectumID;
    }

}
