package ru.facilicom24.manager.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MobileContract {
	@SerializedName("DirectumID")
	int directumID;

	@SerializedName("NomGroupCode")
	String nomGroupCode;

	@SerializedName("ScheduleCode")
	String scheduleCode;

	@SerializedName("Email")
	String email;

	@SerializedName("Records")
	List<MobileItem> records;

	private class MobileItem {
		@SerializedName("Quantity")
		int quantity;

		@SerializedName("Date")
		String date;

		@SerializedName("WorkOn")
		String workOn;

		@SerializedName("WorkOff")
		String workOff;

		@SerializedName("Sex")
		int sex;

		@SerializedName("ScheduleID")
		String scheduleID;
	}
}
