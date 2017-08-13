package ru.facilicom24.manager.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@DatabaseTable(tableName = "checks")
public class Check implements Serializable {

	public static final int NEW = 0;
	public static final int READY = 2;

	@ForeignCollectionField
	Collection<Photo> photos;

	@DatabaseField(generatedId = true)
	int id;

	@DatabaseField(columnName = "check_id")
	String checkId;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 4)
	CheckType checkType;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 4)
	CheckObject checkObject;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 4)
	CheckBlank checkBlank;

	@DatabaseField(columnName = "check_date", dataType = DataType.DATE_LONG)
	Date date;

	@DatabaseField(columnName = "state")
	int state;

	@DatabaseField(columnName = "comment")
	String comment;

	@ForeignCollectionField
	Collection<ElementMark> marks;

	public Check() {
		super();
		state = NEW;
	}

	public Check(String checkId, CheckType checkType, CheckObject checkObject, CheckBlank checkBlank, Date date, int state, String comment) {
		this.checkId = checkId;
		this.checkType = checkType;
		this.checkObject = checkObject;
		this.checkBlank = checkBlank;
		this.date = date;
		this.state = state;
		this.comment = comment;
	}

	public String getCheckId() {
		return checkId;
	}

	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}

	public CheckType getCheckType() {
		return checkType;
	}

	public void setCheckType(CheckType checkType) {
		this.checkType = checkType;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public CheckObject getCheckObject() {
		return checkObject;
	}

	public void setCheckObject(CheckObject checkObject) {
		this.checkObject = checkObject;
	}

	public CheckBlank getCheckBlank() {
		return checkBlank;
	}

	public void setCheckBlank(CheckBlank checkBlank) {
		this.checkBlank = checkBlank;
	}

	public Collection<ElementMark> getMarks() {
		return marks;
	}

	public void setMarks(Collection<ElementMark> marks) {
		this.marks = marks;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Collection<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(Collection<Photo> photos) {
		this.photos = photos;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Check)) return false;

		Check check = (Check) o;

		if (id != check.id) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + checkId.hashCode();
		result = 31 * result + checkType.hashCode();
		result = 31 * result + checkObject.hashCode();
		result = 31 * result + checkBlank.hashCode();
		result = 31 * result + date.hashCode();
		result = 31 * result + state;
		result = 31 * result + (photos != null ? photos.hashCode() : 0);
		return result;
	}
}
