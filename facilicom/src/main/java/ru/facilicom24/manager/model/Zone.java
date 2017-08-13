package ru.facilicom24.manager.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@DatabaseTable(tableName = "check_form_zones")
public class Zone implements Serializable {

	@DatabaseField(generatedId = true, columnName = "zone_id")
	private int id;

	@DatabaseField(foreign = true)
	private CheckBlank checkBlank;

	@JsonProperty("Name")
	@DatabaseField(columnName = "zone_name")
	private String name;

	@JsonProperty("Elements")
	@ForeignCollectionField
	private Collection<Element> elements;

	public Zone() {
	}

	public Zone(CheckBlank checkBlank, String name, Collection<Element> elements) {
		this.checkBlank = checkBlank;
		this.name = name;
		this.elements = elements;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Element> getElements() {
		List<Element> result = new ArrayList<Element>(elements);
		Collections.sort(result);
		return result;
	}

	public void setElements(Collection<Element> elements) {
		this.elements = elements;
	}

	public CheckBlank getCheckBlank() {
		return checkBlank;
	}

	public void setCheckBlank(CheckBlank checkBlank) {
		this.checkBlank = checkBlank;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Zone)) return false;

		Zone zone = (Zone) o;

		if (id != zone.id) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + (checkBlank != null ? checkBlank.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (elements != null ? elements.hashCode() : 0);
		return result;
	}
}
