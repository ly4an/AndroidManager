package ru.facilicom24.manager.retrofit;

import java.util.List;

public class ActContract {
	private String createdOn;
	private int reasonId;
	private int actTypeId;
	private String serviceTypeId;
	private int clientId;
	private int accountId;
	private int contactId;
	private String questions;
	private double longitude;
	private double latitude;
	private int clientMark;
	private int expandService;
	private int qualityServiceClientMark;
	private int managementClientMark;
	private int nextWorkClientMark;
	private int qualityServiceOurMark;
	private int managementOurMark;
	private int nextWorkOurMark;
	private String iD;
	private List<TaskContract> taskList;
	private int potSellNumber;

	public ActContract(
			String iD,
			String createdOn,
			int actTypeId,
			int reasonId,
			int clientId,
			int accountId,
			String serviceTypeId,
			int potSellNumber,
			int contactId,
			String questions,
			int clientMark,
			int expandService,
			int qualityServiceClientMark,
			int managementClientMark,
			int nextWorkClientMark,
			int qualityServiceOurMark,
			int managementOurMark,
			int nextWorkOurMark,
			double longitude,
			double latitude,
			List<TaskContract> taskList
	) {
		this.iD = iD;
		this.createdOn = createdOn;
		this.actTypeId = actTypeId;
		this.reasonId = reasonId;
		this.clientId = clientId;
		this.accountId = accountId;
		this.serviceTypeId = serviceTypeId;
		this.potSellNumber = potSellNumber;
		this.contactId = contactId;
		this.questions = questions;
		this.clientMark = clientMark;
		this.expandService = expandService;
		this.qualityServiceClientMark = qualityServiceClientMark;
		this.managementClientMark = managementClientMark;
		this.nextWorkClientMark = nextWorkClientMark;
		this.qualityServiceOurMark = qualityServiceOurMark;
		this.managementOurMark = managementOurMark;
		this.nextWorkOurMark = nextWorkOurMark;
		this.longitude = longitude;
		this.latitude = latitude;
		this.taskList = taskList;
	}
}
