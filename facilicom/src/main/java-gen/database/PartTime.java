package database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "PART_TIME".
 */
@Entity
public class PartTime {

	@Id
	private Long id;
    private Integer partTimeId;
    private String json;
    private Boolean send;

	@Generated
	public PartTime() {
    }

    public PartTime(Long id) {
        this.id = id;
    }

	@Generated
	public PartTime(Long id, Integer partTimeId, String json, Boolean send) {
        this.id = id;
        this.partTimeId = partTimeId;
        this.json = json;
        this.send = send;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPartTimeId() {
        return partTimeId;
    }

    public void setPartTimeId(Integer partTimeId) {
        this.partTimeId = partTimeId;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public Boolean getSend() {
        return send;
    }

    public void setSend(Boolean send) {
        this.send = send;
    }

}
