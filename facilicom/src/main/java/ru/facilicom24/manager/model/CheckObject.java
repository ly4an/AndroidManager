package ru.facilicom24.manager.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Arrays;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@DatabaseTable(tableName = "check_objects")
public class CheckObject
		implements Serializable, Cloneable {

	@DatabaseField(generatedId = true)
	private int id;

	@JsonProperty("Id")
	@DatabaseField(columnName = "check_object_id")
	private int checkObjectId;

	@JsonProperty("Name")
	@DatabaseField(columnName = "check_object_name")
	private String checkObjectName;

	@JsonProperty("Address")
	@DatabaseField(columnName = "check_object_address")
	private String checkObjectAddress;

	@JsonProperty("ServiceTypeIds")
	private String[] checkTypesIds;

	@DatabaseField(columnName = "check_object_check_type_id")
	private String checkTypeId;

	public CheckObject() {
		super();
	}

	public CheckObject(int checkObjectId, String checkObjectName, String checkObjectAddress, String checkTypeId) {
		this.checkObjectId = checkObjectId;
		this.checkObjectName = checkObjectName;
		this.checkObjectAddress = checkObjectAddress;
		this.checkTypeId = checkTypeId;
	}

	public int getCheckObjectId() {
		return checkObjectId;
	}

	public void setCheckObjectId(int checkObjectId) {
		this.checkObjectId = checkObjectId;
	}

	public String getCheckObjectName() {
		return checkObjectName;
	}

	public void setCheckObjectName(String checkObjectName) {
		this.checkObjectName = checkObjectName;
	}

	public String getCheckObjectAddress() {
		return checkObjectAddress;
	}

	public void setCheckObjectAddress(String checkObjectAddress) {
		this.checkObjectAddress = checkObjectAddress;
	}

	public String[] getCheckTypesIds() {
		return checkTypesIds;
	}

	public void setCheckTypesIds(String[] checkTypesIds) {
		this.checkTypesIds = checkTypesIds;
	}

	public String getCheckTypeId() {
		return checkTypeId;
	}

	public void setCheckTypeId(String checkTypeId) {
		this.checkTypeId = checkTypeId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CheckObject)) return false;

		CheckObject that = (CheckObject) o;

		if (id != that.id) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;

		result = 31 * result + checkObjectId;
		result = 31 * result + checkObjectName.hashCode();
		result = 31 * result + checkObjectAddress.hashCode();
		result = 31 * result + (checkTypesIds != null ? Arrays.hashCode(checkTypesIds) : 0);
		result = 31 * result + checkTypeId.hashCode();

		return result;
	}

	public CheckObject clone() {
		try {
			return (CheckObject) super.clone();
		} catch (Exception exception) {
			return null;
		}
	}
}
