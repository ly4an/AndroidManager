package ru.facilicom24.manager.retrofit;

public class LogContract {
	private int logTypeID;
	private String label;
	private String value;

	public LogContract(
			int logTypeID,
			String label,
			String value
	) {
		this.logTypeID = logTypeID;
		this.label = label;
		this.value = value;
	}
}
