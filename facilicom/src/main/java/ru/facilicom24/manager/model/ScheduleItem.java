package ru.facilicom24.manager.model;

public class ScheduleItem {
	String requestDate;

	String scheduleId;
	String scheduleCode;
	String scheduleName;

	int sexId;

	String fromTime;
	String toTime;

	int quantity;
	int maxQuantity;

	public ScheduleItem(
			String requestDate,

			String scheduleId,
			String scheduleCode,
			String scheduleName,

			int sexId,

			String fromTime,
			String toTime,

			int quantity,
			int maxQuantity
	) {
		this.requestDate = requestDate;

		this.scheduleId = scheduleId;
		this.scheduleCode = scheduleCode;
		this.scheduleName = scheduleName;

		this.sexId = sexId;

		this.fromTime = fromTime;
		this.toTime = toTime;

		this.quantity = quantity;
		this.maxQuantity = maxQuantity;
	}

	public String getRequestDate() {
		return requestDate;
	}

	public String getScheduleId() {
		return scheduleId;
	}

	public String getScheduleCode() {
		return scheduleCode;
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public int getSexId() {
		return sexId;
	}

	public String getFromTime() {
		return fromTime;
	}

	public String getToTime() {
		return toTime;
	}

	public int getQuantity() {
		return quantity;
	}

	public int getMaxQuantity() {
		return maxQuantity;
	}
}
