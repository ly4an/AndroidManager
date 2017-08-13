package ru.facilicom24.manager.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@DatabaseTable(tableName = "check_types")
public class CheckType implements Serializable {

	@DatabaseField(generatedId = true)
	private int id;

	@JsonProperty("Id")
	@DatabaseField(columnName = "check_id")
	private String checkId;

	@JsonProperty("Name")
	@DatabaseField(columnName = "check_name")
	private String checkName;

	public CheckType() {
		super();
	}

	public CheckType(int id, String checkId, String checkName) {
		this.id = id;
		this.checkId = checkId;
		this.checkName = checkName;
	}

	public CheckType(String checkId, String checkName) {
		this.checkId = checkId;
		this.checkName = checkName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCheckId() {
		return checkId;
	}

	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}

	public String getCheckName() {
		return checkName;
	}

	public void setCheckName(String checkName) {
		this.checkName = checkName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CheckType)) return false;

		CheckType checkType = (CheckType) o;

		if (id != checkType.id) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + (checkId != null ? checkId.hashCode() : 0);
		result = 31 * result + (checkName != null ? checkName.hashCode() : 0);
		return result;
	}
}
