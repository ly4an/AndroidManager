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
@JsonIgnoreProperties({"dbReasons", "marks"})
@DatabaseTable(tableName = "check_elements")
public class Element implements Serializable, Comparable<Element> {

	@DatabaseField(generatedId = true)
	private int id;

	@JsonProperty("Id")
	@DatabaseField(columnName = "check_element_id")
	private int elementId;

	@JsonProperty("Name")
	@DatabaseField(columnName = "check_element_name")
	private String name;

	@JsonProperty("Required")
	@DatabaseField(columnName = "element_required")
	private boolean required;

	@JsonProperty("Reasons")
	private Collection<String> reasons;

	@ForeignCollectionField
	private Collection<Reason> dbReasons;

	@DatabaseField(foreign = true)
	private Zone zone;

	public Element() {
	}

	public Element(int elementId, String name, boolean required, Collection<Reason> dbReasons) {
		this.elementId = elementId;
		this.name = name;
		this.required = required;
		this.dbReasons = dbReasons;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getElementId() {
		return elementId;
	}

	public void setElementId(int elementId) {
		this.elementId = elementId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public Collection<String> getReasons() {
		return reasons;
	}

	public void setReasons(Collection<String> reasons) {
		this.reasons = reasons;
	}


	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public Collection<Reason> getDbReasons() {
		return dbReasons;
	}

	public void setDbReasons(Collection<Reason> dbReasons) {
		this.dbReasons = dbReasons;
	}

	@Override
	public int compareTo(Element element) {
		return required == element.isRequired() ? 0 : (required ? -1 : 1);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Element)) return false;

		Element element = (Element) o;

		if (id != element.id) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + elementId;
		result = 31 * result + name.hashCode();
		result = 31 * result + (required ? 1 : 0);
		result = 31 * result + zone.hashCode();
		return result;
	}
}
