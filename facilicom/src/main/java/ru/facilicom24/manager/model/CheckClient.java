package ru.facilicom24.manager.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@DatabaseTable(tableName = "check_clients")
public class CheckClient implements Serializable {

	@DatabaseField(generatedId = true)
	private int id;

	@JsonProperty("ClientId")
	@DatabaseField(columnName = "client_id")
	private String clientId;

	@JsonProperty("Name")
	@DatabaseField(columnName = "client_name")
	private String clientName;

	public CheckClient() {
		super();
	}

	public CheckClient(int id, String checkId, String checkName) {
		this.id = id;
		this.clientId = checkId;
		this.clientName = checkName;
	}

	public CheckClient(String checkId, String checkName) {
		this.clientId = checkId;
		this.clientName = checkName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String checkId) {
		this.clientId = checkId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String checkName) {
		this.clientName = checkName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CheckClient)) return false;

		CheckClient checkType = (CheckClient) o;
		if (id != checkType.id) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
		result = 31 * result + (clientName != null ? clientName.hashCode() : 0);
		return result;
	}
}
