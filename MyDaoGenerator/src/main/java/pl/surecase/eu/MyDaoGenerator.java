package pl.surecase.eu;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;
import org.greenrobot.greendao.generator.ToMany;

public class MyDaoGenerator {
	static final private String DATABASE_OUTDIR = "facilicom/src/main/java-gen";

	static final private int DATABASE_VERSION = 59;
	static final private String DATABASE_MODEL_NAME = "database";

	public static void main(String args[]) {
		Schema schema = new Schema(DATABASE_VERSION, DATABASE_MODEL_NAME);

		// ActType

		Entity actType = schema.addEntity("ActType");

		actType.addIdProperty();
		actType.addIntProperty("actTypeID");

		actType.addStringProperty("name");

		// ActReason

		Entity actReason = schema.addEntity("ActReason");

		actReason.addIdProperty();
		actReason.addIntProperty("actReasonID");

		actReason.addStringProperty("name");

		// ContactClient

		Entity contactClient = schema.addEntity("ContactClient");

		contactClient.addIdProperty();
		contactClient.addIntProperty("clientID");

		contactClient.addIntProperty("contactID");

		// ContactAccount

		Entity contactAccount = schema.addEntity("ContactAccount");

		contactAccount.addIdProperty();
		contactAccount.addIntProperty("directumID");

		contactAccount.addIntProperty("contactID");

		// Client

		Entity client = schema.addEntity("Client");

		client.addIdProperty();
		client.addIntProperty("clientID");

		client.addStringProperty("name");
		client.addIntProperty("status");

		// ActAccount

		Entity actAccount = schema.addEntity("ActAccount");

		actAccount.addIdProperty();
		actAccount.addIntProperty("directumID");

		actAccount.addStringProperty("name");
		actAccount.addStringProperty("address");
		actAccount.addIntProperty("status");

		Property clientProperty = actAccount.addLongProperty("clientID").notNull().getProperty();
		actAccount.addToOne(client, clientProperty);

		ToMany clientToAccounts = client.addToMany(actAccount, clientProperty);
		clientToAccounts.setName("accounts");

		// ActServiceType

		Entity actServiceType = schema.addEntity("ActServiceType");

		actServiceType.addIdProperty();
		actServiceType.addStringProperty("serviceTypeId");

		actServiceType.addStringProperty("name");
		actServiceType.addBooleanProperty("status");

		Property serviceTypeActAccountProperty = actServiceType.addLongProperty("directumID").getProperty();
		Property serviceTypeClientProperty = actServiceType.addLongProperty("clientID").getProperty();

		actServiceType.addToOne(actAccount, serviceTypeActAccountProperty);
		actServiceType.addToOne(client, serviceTypeClientProperty);

		ToMany clientToActServiceTypes = client.addToMany(actServiceType, serviceTypeClientProperty);
		clientToActServiceTypes.setName("actServiceTypes");

		// ActContact

		Entity actContact = schema.addEntity("ActContact");

		actContact.addIdProperty();
		actContact.addIntProperty("actContactID");

		actContact.addStringProperty("name");

		Property actContactToClientProperty = actContact.addLongProperty("clientID").getProperty();
		Property actContactToAccountProperty = actContact.addLongProperty("accountID").getProperty();

		actContact.addToOne(client, actContactToClientProperty);
		actContact.addToOne(actAccount, actContactToAccountProperty);

		// NomenclatureGroup

		Entity nomenclatureGroup = schema.addEntity("NomenclatureGroup");

		nomenclatureGroup.addIdProperty();
		nomenclatureGroup.addIntProperty("nomenclatureGroupId");

		nomenclatureGroup.addStringProperty("code");
		nomenclatureGroup.addStringProperty("name");

		// Mobile

		Entity mobile = schema.addEntity("Mobile");

		mobile.addIdProperty();
		mobile.addIntProperty("mobileId");

		mobile.addStringProperty("json");
		mobile.addBooleanProperty("send");

		// PartTime

		Entity partTime = schema.addEntity("PartTime");

		partTime.addIdProperty();
		partTime.addIntProperty("partTimeId");

		partTime.addStringProperty("json");
		partTime.addBooleanProperty("send");

		// Schedule

		Entity schedule = schema.addEntity("Schedule");

		schedule.addIdProperty();

		schedule.addStringProperty("ScheduleID");
		schedule.addStringProperty("ScheduleCode");
		schedule.addStringProperty("Position");

		schedule.addIntProperty("Quantity");

		// VidTask

		Entity vidTask = schema.addEntity("VidTask");

		vidTask.addIdProperty();

		vidTask.addIntProperty("VidTaskID");
		vidTask.addStringProperty("VidTaskName");

		// TaskEmployee

		Entity taskEmployee = schema.addEntity("TaskEmployee");

		taskEmployee.addIdProperty();

		taskEmployee.addStringProperty("TaskEmployeeLogin");
		taskEmployee.addStringProperty("TaskEmployeeName");

		// Country

		Entity country = schema.addEntity("Country");

		country.addIdProperty();

		country.addStringProperty("CountryUID");
		country.addStringProperty("CountryName");
		country.addIntProperty("NeedPatentOrPermission");

		// Person

		Entity person = schema.addEntity("Person");

		person.addIdProperty();

		person.addStringProperty("DocumentType");
		person.addStringProperty("PersonLocalUID");

		person.addStringProperty("LastName");
		person.addStringProperty("FirstName");
		person.addStringProperty("FatherName");

		person.addDateProperty("BirthDate");

		person.addStringProperty("CountryUID");
		person.addIntProperty("NeedPatentOrPermission");

		person.addStringProperty("Sex");

		person.addStringProperty("PassportNumber");
		person.addDateProperty("PassportIssue");

		person.addStringProperty("PhoneNumber");

		person.addStringProperty("PermissionSeria");
		person.addStringProperty("PermissionNumber");
		person.addDateProperty("PermissionExpiry");

		person.addStringProperty("PatentNumber");
		person.addDateProperty("PatentIssue");
		person.addDateProperty("PatentExpiry");

		person.addStringProperty("SubdivisionUID");
		person.addStringProperty("RegionUID");
		person.addStringProperty("SubdivisionName");

		person.addStringProperty("JobTitleUID");
		person.addStringProperty("JobTitleName");

		person.addStringProperty("BankUID");
		person.addStringProperty("BankName");
		person.addDateProperty("BankDate");

		person.addDateProperty("BindDate");

		person.addStringProperty("PersonUID");
		person.addStringProperty("PersonName");

		person.addBooleanProperty("Card");

		person.addDateProperty("DismissDate");

		person.addStringProperty("DismissReasonUID");
		person.addStringProperty("DismissReasonName");

		// PersonPhoto

		Entity personPhoto = schema.addEntity("PersonPhoto");

		personPhoto.addIdProperty();

		personPhoto.addLongProperty("PersonId");
		personPhoto.addStringProperty("PersonLocalUID");

		personPhoto.addStringProperty("ImageLocalUID");

		personPhoto.addIntProperty("PersonPhotoType");
		personPhoto.addStringProperty("PersonPhotoUri");

		// Task

		Entity task = schema.addEntity("Task");

		task.addIdProperty();

		task.addStringProperty("ActUID");
		task.addIntProperty("ClientID");
		task.addIntProperty("AccountID");
		task.addDateProperty("DueDate");
		task.addStringProperty("ExecutorLogin");
		task.addStringProperty("Result");
		task.addStringProperty("Status");
		task.addStringProperty("Subject");
		task.addStringProperty("Text");
		task.addIntProperty("TypeID");
		task.addIntProperty("VidID");
		task.addStringProperty("TaskUID");
		task.addDateProperty("CreatedOn");
		task.addStringProperty("ServiceTypeUID");
		task.addIntProperty("PotSellNumber");
		task.addIntProperty("ContactID");
		task.addStringProperty("ControlComment");
		task.addBooleanProperty("Changed");

		// TSTask

		Entity tsTask = schema.addEntity("TSTask");

		tsTask.addIdProperty();

		tsTask.addStringProperty("ID");
		tsTask.addStringProperty("Title");
		tsTask.addStringProperty("DetailedResult");
		tsTask.addStringProperty("DueDate");
		tsTask.addStringProperty("ClientName");
		tsTask.addStringProperty("AccountName");
		tsTask.addStringProperty("AccountAddress");
		tsTask.addStringProperty("ContactName");
		tsTask.addStringProperty("Executor");
		tsTask.addStringProperty("Status");
		tsTask.addStringProperty("ControlComment");
		tsTask.addStringProperty("Author");

		// CheckInLog

		Entity checkInLog = schema.addEntity("CheckInLog");

		checkInLog.addIdProperty();

		checkInLog.addDateProperty("DateTime");
		checkInLog.addIntProperty("DirectumID");

		// Request

		Entity request = schema.addEntity("Request");

		request.addIdProperty();

		request.addIntProperty("InternalServiceRequestID");
		request.addStringProperty("ServiceRequestUID");
		request.addIntProperty("ClientID");
		request.addIntProperty("DirectumID");
		request.addStringProperty("ServiceTypeUID");
		request.addStringProperty("WorkTypeUID");
		request.addStringProperty("UrgencyTypeName");
		request.addIntProperty("CleaningReasonID");
		request.addStringProperty("Content");
		request.addDateProperty("DesiredDate");
		request.addStringProperty("Comment");
		request.addStringProperty("Type");
		request.addIntProperty("ArrivalTimeRequired");
		request.addBooleanProperty("Changed");

		// RequestPhoto

		Entity requestPhoto = schema.addEntity("RequestPhoto");

		requestPhoto.addIdProperty();

		requestPhoto.addLongProperty("RequestID");
		requestPhoto.addStringProperty("PhotoFileName");

		// ServiceRequestServant

		Entity serviceRequestServant = schema.addEntity("ServiceRequestServant");

		serviceRequestServant.addIdProperty();
		Property serviceRequestServantIdProperty = serviceRequestServant.addLongProperty("ServiceRequestID").getProperty();

		serviceRequestServant.addStringProperty("Name");

		// ServiceRequestLog

		Entity serviceRequestLog = schema.addEntity("ServiceRequestLog");

		serviceRequestLog.addIdProperty();
		Property serviceRequestLogIdProperty = serviceRequestLog.addLongProperty("ServiceRequestID").getProperty();

		serviceRequestLog.addStringProperty("StatusSetOn");
		serviceRequestLog.addStringProperty("Status");
		serviceRequestLog.addStringProperty("StatusSetByFullName");
		serviceRequestLog.addStringProperty("Comment");

		// ServiceRequestFile

		Entity serviceRequestFile = schema.addEntity("ServiceRequestFile");

		serviceRequestFile.addIdProperty();
		Property serviceRequestFileIdProperty = serviceRequestFile.addLongProperty("ServiceRequestID").getProperty();

		serviceRequestFile.addIntProperty("ServiceRequestFileID");
		serviceRequestFile.addIntProperty("Type");
		serviceRequestFile.addStringProperty("Ext");

		// ServiceRequestPhoto

		Entity serviceRequestPhoto = schema.addEntity("ServiceRequestPhoto");

		serviceRequestPhoto.addIdProperty();
		Property serviceRequestPhotoIdProperty = serviceRequestPhoto.addLongProperty("ServiceRequestID").getProperty();

		serviceRequestPhoto.addIntProperty("ServiceRequestFileID");
		serviceRequestPhoto.addStringProperty("PhotoFileName");

		// ServiceRequest

		Entity serviceRequest = schema.addEntity("ServiceRequest");

		serviceRequest.addIdProperty();

		serviceRequest.addIntProperty("ID");
		serviceRequest.addStringProperty("UID");
		serviceRequest.addStringProperty("DueDate");
		serviceRequest.addStringProperty("CreatedOn");
		serviceRequest.addStringProperty("FacilityName");
		serviceRequest.addStringProperty("FacilityAddress");
		serviceRequest.addStringProperty("UrgencyType");
		serviceRequest.addStringProperty("Status");
		serviceRequest.addStringProperty("Content");
		serviceRequest.addStringProperty("ServiceTypeName");
		serviceRequest.addIntProperty("CanExecute");
		serviceRequest.addIntProperty("NeedEvaluate");
		serviceRequest.addIntProperty("Mine");

		serviceRequest.addToMany(serviceRequestServant, serviceRequestServantIdProperty, "Servants");
		serviceRequest.addToMany(serviceRequestLog, serviceRequestLogIdProperty, "Log");
		serviceRequest.addToMany(serviceRequestFile, serviceRequestFileIdProperty, "Files");
		serviceRequest.addToMany(serviceRequestPhoto, serviceRequestPhotoIdProperty, "Photos");

		serviceRequest.addStringProperty("Comment");

		// AppointmentAttender

		Entity appointmentAttender = schema.addEntity("AppointmentAttender");

		appointmentAttender.addIdProperty();

		Property appointmentIdProperty = appointmentAttender.addLongProperty("AppointmentId").getProperty();
		appointmentAttender.addStringProperty("Email");

		// Appointment

		Entity appointment = schema.addEntity("Appointment");

		appointment.addIdProperty();

		appointment.addStringProperty("Subject");
		appointment.addStringProperty("Body");
		appointment.addStringProperty("Start");
		appointment.addStringProperty("End");
		appointment.addStringProperty("Place");
		appointment.addStringProperty("ID");
		appointment.addStringProperty("Status");
		appointment.addIntProperty("UserIsOwner");
		appointment.addToMany(appointmentAttender, appointmentIdProperty, "Attenders");
		appointment.addDateProperty("Date");

		// PotSell

		Entity potSell = schema.addEntity("PotSell");

		potSell.addIdProperty();

		potSell.addIntProperty("ClientID");
		potSell.addIntProperty("Number");
		potSell.addStringProperty("Name");

		// WorkType

		Entity workType = schema.addEntity("WorkType");

		workType.addIdProperty();

		workType.addStringProperty("SystemGUID");
		workType.addStringProperty("SystemName");

		// CleaningReason

		Entity cleaningReason = schema.addEntity("CleaningReason");

		cleaningReason.addIdProperty();

		cleaningReason.addIntProperty("ReasonID");
		cleaningReason.addStringProperty("ReasonName");

		// FacilityUrgencyType

		Entity facilityUrgencyType = schema.addEntity("FacilityUrgencyType");

		facilityUrgencyType.addIdProperty();

		facilityUrgencyType.addIntProperty("ID");
		facilityUrgencyType.addStringProperty("UrgencyType");

		// Fake

		Entity fake = schema.addEntity("Fake");

		fake.addIdProperty();

		fake.addStringProperty("Name");
		fake.addStringProperty("Pattern");

		// OrioType

		Entity orioType = schema.addEntity("OrioType");

		orioType.addIdProperty();

		orioType.addStringProperty("UID");
		orioType.addStringProperty("Name");

		// OrioArticle

		Entity orioArticle = schema.addEntity("OrioArticle");

		orioArticle.addIdProperty();

		orioArticle.addStringProperty("UID");
		orioArticle.addStringProperty("Name");

		// QED

		try {
			new DaoGenerator().generateAll(schema, DATABASE_OUTDIR);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
