package ru.facilicom24.manager.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Collection;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties({"zones"})
@DatabaseTable(tableName = "check_blanks")

public class CheckBlank implements Serializable {

	@DatabaseField(generatedId = true)
	int id;

	@JsonProperty("Id")
	@DatabaseField(columnName = "check_blank_id")
	int checkBlankId;

	@JsonProperty("Name")
	@DatabaseField(columnName = "check_blank_name")
	String name;

	@ForeignCollectionField
	Collection<Zone> zones;

	@DatabaseField(columnName = "check_blank_check_type_id")
	String checkTypeId;

	public CheckBlank() {
		super();
	}

	public CheckBlank(int checkBlankId, String checkBlankName) {
		this.checkBlankId = checkBlankId;
		this.name = checkBlankName;
	}

	public CheckBlank(int checkBlankId, String checkBlankName, String checkTypeId) {
		this.checkBlankId = checkBlankId;
		this.name = checkBlankName;
		this.checkTypeId = checkTypeId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCheckBlankId() {
		return checkBlankId;
	}

	public void setCheckBlankId(int checkBlankId) {
		this.checkBlankId = checkBlankId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Zone> getZones() {
		return zones;
	}

	public void setZones(Collection<Zone> zones) {
		this.zones = zones;
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
		if (!(o instanceof CheckBlank)) return false;

		CheckBlank that = (CheckBlank) o;

		if (id != that.id) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + checkBlankId;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (zones != null ? zones.hashCode() : 0);
		result = 31 * result + (checkTypeId != null ? checkTypeId.hashCode() : 0);
		return result;
	}
}
