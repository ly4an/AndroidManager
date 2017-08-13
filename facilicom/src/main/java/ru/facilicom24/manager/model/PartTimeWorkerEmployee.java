package ru.facilicom24.manager.model;

import com.google.gson.annotations.SerializedName;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PartTimeWorkerEmployee {
	@JsonProperty("EmpID")
	@SerializedName("EmpID")
	private String empId;

	@JsonProperty("EmpFIO")
	@SerializedName("EmpFIO")
	private String empFIO;

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getEmpFIO() {
		return empFIO;
	}

	public void setEmpFIO(String empFIO) {
		this.empFIO = empFIO;
	}
}
