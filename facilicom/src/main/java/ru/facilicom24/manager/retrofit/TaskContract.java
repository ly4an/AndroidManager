package ru.facilicom24.manager.retrofit;

public class TaskContract {
	private String actUID;
	private int clientID;
	private int accountID;
	private String dueDate;
	private String executorLogin;
	private String result;
	private String status;
	private String subject;
	private String text;
	private int typeID;
	private int vidID;
	private String taskUID;
	private String createdOn;
	private String serviceTypeUID;
	private int contactID;
	private String controlComment;
	private int potSellNumber;

	public TaskContract(
			String actUID,
			int clientID,
			int accountID,
			String serviceTypeUID,
			int contactID,
			String taskUID,
			int typeID,
			int vidID,
			String executorLogin,
			String dueDate,
			String subject,
			String text,
			String result,
			String status,
			String createdOn,
			int potSellNumber,
			String controlComment
	) {
		this.actUID = actUID;
		this.clientID = clientID;
		this.accountID = accountID;
		this.serviceTypeUID = serviceTypeUID;
		this.contactID = contactID;
		this.taskUID = taskUID;
		this.typeID = typeID;
		this.vidID = vidID;
		this.executorLogin = executorLogin;
		this.dueDate = dueDate;
		this.subject = subject;
		this.text = text;
		this.result = result;
		this.status = status;
		this.createdOn = createdOn;
		this.potSellNumber = potSellNumber;
		this.controlComment = controlComment;
	}
}
