package ru.facilicom24.manager.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationRequest {
	@JsonProperty("Longitude")
	private float longitude;

	@JsonProperty("Latitude")
	private float latitude;

	@JsonProperty("Accuracy")
	private float accuracy;

	@JsonProperty("Offset")
	private float offset;

	@JsonProperty("DirectumID")
	private Integer directumId;

	public LocationRequest(float longitude, float latitude, float accuracy, float offset, Integer directumId) {
		this.longitude = longitude;
		this.latitude = latitude;

		this.accuracy = accuracy;
		this.offset = offset;

		this.directumId = directumId;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public float getOffset() {
		return offset;
	}

	public void setOffset(float offset) {
		this.offset = offset;
	}

	public Integer getDirectumId() {
		return directumId;
	}

	public void setDirectumId(Integer directumId) {
		this.directumId = directumId;
	}
}
