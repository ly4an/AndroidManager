package ru.facilicom24.manager.model;

import com.google.gson.annotations.SerializedName;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapAccount {
	@JsonProperty("Id")
	@SerializedName("Id")
	int id;

	@JsonProperty("Name")
	@SerializedName("Name")
	String name;

	@JsonProperty("Address")
	@SerializedName("Address")
	private String address;

	@JsonProperty("Distance")
	@SerializedName("Distance")
	private float distance;

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}
}
