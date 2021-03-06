package database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "WORK_TYPE".
 */
@Entity
public class WorkType {

	@Id
	private Long id;
    private String SystemGUID;
    private String SystemName;

	@Generated
	public WorkType() {
    }

    public WorkType(Long id) {
        this.id = id;
    }

	@Generated
	public WorkType(Long id, String SystemGUID, String SystemName) {
        this.id = id;
        this.SystemGUID = SystemGUID;
        this.SystemName = SystemName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSystemGUID() {
        return SystemGUID;
    }

    public void setSystemGUID(String SystemGUID) {
        this.SystemGUID = SystemGUID;
    }

    public String getSystemName() {
        return SystemName;
    }

    public void setSystemName(String SystemName) {
        this.SystemName = SystemName;
    }

}
