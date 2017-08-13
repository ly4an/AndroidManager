package ru.facilicom24.manager.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Collection;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckRequest {

	@JsonProperty("AccountId")
	int accountId;

	@JsonProperty("FormId")
	int formId;

	@JsonProperty("CreatedOn")
	String createdOn;

	@JsonProperty("Longitude")
	float longitude;

	@JsonProperty("Latitude")
	float latitude;

	@JsonProperty("ElementMarks")
	Collection<ElementMarks> marks;

	@JsonProperty("Comments")
	String comment;

	public CheckRequest() {
		super();
	}

	public CheckRequest(int accountId, int formId, String createdOn, float longitude, float latitude, Collection<ElementMarks> marks, String comment) {
		this.accountId = accountId;
		this.formId = formId;
		this.createdOn = createdOn;
		this.marks = marks;

		this.longitude = longitude;
		this.latitude = latitude;

		this.comment = comment;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
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

	public Collection<ElementMarks> getMarks() {
		return marks;
	}

	public void setMarks(Collection<ElementMarks> marks) {
		this.marks = marks;
	}

	public String getComments() {
		return comment;
	}

	public void setComments(String comment) {
		this.comment = comment;
	}
}
