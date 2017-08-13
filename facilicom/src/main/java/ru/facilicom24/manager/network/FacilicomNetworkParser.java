package ru.facilicom24.manager.network;

import android.app.Activity;
import android.net.Uri;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import database.Appointment;
import database.AppointmentAttender;
import database.AppointmentAttenderDao;
import database.AppointmentDao;
import database.DaoSession;
import database.Person;
import database.PersonPhoto;
import database.ServiceRequest;
import database.ServiceRequestDao;
import database.ServiceRequestFile;
import database.ServiceRequestFileDao;
import database.ServiceRequestLog;
import database.ServiceRequestLogDao;
import database.ServiceRequestServant;
import database.ServiceRequestServantDao;
import database.TSTask;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.activities.PersonActivity;
import ru.facilicom24.manager.cache.DaoTSTaskRepository;
import ru.facilicom24.manager.model.Bank;
import ru.facilicom24.manager.model.DismissReason;
import ru.facilicom24.manager.model.JobTitle;
import ru.facilicom24.manager.model.PartTimeWorkerEmployee;
import ru.facilicom24.manager.model.Subdivision;

public class FacilicomNetworkParser {
	public static void parseTSTasks(final Activity activity, byte[] json) {
		final JSONArray finalJSONArray = FacilicomNetworkManager.toJSONArray(json);

		FacilicomApplication.getInstance().getDaoSession().runInTx(
				new Runnable() {
					public void run() {
						for (int i = 0; i < finalJSONArray.length(); i++) {
							JSONObject object = finalJSONArray.optJSONObject(i);

							TSTask tsTask = new TSTask();

							tsTask.setID(object.optString("ID"));
							tsTask.setTitle(object.optString("Title"));
							tsTask.setDetailedResult(object.optString("DetailedResult"));
							tsTask.setDueDate(object.optString("DueDate"));
							tsTask.setClientName(object.optString("ClientName"));
							tsTask.setAccountName(object.optString("AccountName"));
							tsTask.setAccountAddress(object.optString("AccountAddress"));
							tsTask.setContactName(object.optString("ContactName"));
							tsTask.setExecutor(object.optString("Executor"));
							tsTask.setStatus(object.optString("Status"));
							tsTask.setControlComment(object.optString("ControlComment"));
							tsTask.setAuthor(object.optString("Author"));

							DaoTSTaskRepository.insertOrReplace(activity, tsTask);
						}
					}
				}
		);
	}

	public static int parseServiceRequests(byte[] json) {

		final JSONArray jsonArray = FacilicomNetworkManager.toJSONArray(json);
		final DaoSession daoSession = FacilicomApplication.getInstance().getDaoSession();

		daoSession.runInTx(
				new Runnable() {
					public void run() {
						ServiceRequestDao serviceRequestDao = daoSession.getServiceRequestDao();

						ServiceRequestServantDao serviceRequestServantDao = daoSession.getServiceRequestServantDao();
						ServiceRequestLogDao serviceRequestLogDao = daoSession.getServiceRequestLogDao();
						ServiceRequestFileDao serviceRequestFileDao = daoSession.getServiceRequestFileDao();

						for (int index = 0; index < jsonArray.length(); index++) {
							JSONObject jsonObject = jsonArray.optJSONObject(index);

							ServiceRequest serviceRequest = new ServiceRequest();

							serviceRequest.setID(jsonObject.optInt("ID"));
							serviceRequest.setUID(jsonObject.optString("UID"));
							serviceRequest.setDueDate(jsonObject.optString("DueDate"));
							serviceRequest.setCreatedOn(jsonObject.optString("CreatedOn"));
							serviceRequest.setFacilityName(jsonObject.optString("FacilityName"));
							serviceRequest.setFacilityAddress(jsonObject.optString("FacilityAddress"));
							serviceRequest.setUrgencyType(jsonObject.optString("UrgencyType"));
							serviceRequest.setStatus(jsonObject.optString("Status"));
							serviceRequest.setContent(jsonObject.optString("Content"));
							serviceRequest.setServiceTypeName(jsonObject.optString("ServiceTypeName"));
							serviceRequest.setCanExecute(jsonObject.optInt("CanExecute"));
							serviceRequest.setNeedEvaluate(jsonObject.optInt("NeedEvaluate"));
							serviceRequest.setMine(jsonObject.optInt("Mine"));

							serviceRequestDao.insertOrReplace(serviceRequest);

							JSONArray servants = jsonObject.optJSONArray("Servants");

							if (servants != null && servants.length() > 0) {
								for (int servantIndex = 0; servantIndex < servants.length(); servantIndex++) {
									Object servant = servants.opt(servantIndex);
									if (servant != null && servant.getClass().equals(String.class) && ((String) servant).length() > 0) {
										serviceRequestServantDao.insert(new ServiceRequestServant(
												null,
												serviceRequest.getId(),

												(String) servant
										));
									}
								}
							}

							JSONArray history = jsonObject.optJSONArray("History");

							if (history != null && history.length() > 0) {
								for (int historyIndex = 0; historyIndex < history.length(); historyIndex++) {
									Object historyItem = history.opt(historyIndex);
									if (historyItem != null && historyItem.getClass().equals(JSONObject.class)) {
										JSONObject jsonHistory = (JSONObject) historyItem;

										serviceRequestLogDao.insert(new ServiceRequestLog(
												null,
												serviceRequest.getId(),

												jsonHistory.optString("StatusSetOn"),
												jsonHistory.optString("Status"),
												jsonHistory.optString("StatusSetByFullName"),
												jsonHistory.optString("Comment")
										));
									}
								}
							}

							JSONArray files = jsonObject.optJSONArray("Files");

							if (files != null && files.length() > 0) {
								for (int fileIndex = 0; fileIndex < files.length(); fileIndex++) {
									Object file = files.opt(fileIndex);
									if (file != null && file.getClass().equals(JSONObject.class)) {
										JSONObject jsonFile = (JSONObject) file;

										serviceRequestFileDao.insert(new ServiceRequestFile(
												null,
												serviceRequest.getId(),

												jsonFile.optInt("ServiceRequestFileID"),
												jsonFile.optInt("Type"),
												jsonFile.optString("Ext")
										));
									}
								}
							}
						}
					}
				});

		return jsonArray.length();
	}

	public static void parseAppointments(byte[] json) {
		final JSONArray finalJSONArray = FacilicomNetworkManager.toJSONArray(json);

		final DaoSession daoSession = FacilicomApplication.getInstance().getDaoSession();

		daoSession.runInTx(
				new Runnable() {
					public void run() {
						AppointmentDao appointmentDao = daoSession.getAppointmentDao();
						AppointmentAttenderDao appointmentAttenderDao = daoSession.getAppointmentAttenderDao();

						appointmentAttenderDao.deleteAll();

						for (int index = 0; index < finalJSONArray.length(); index++) {
							JSONObject jsonObject = finalJSONArray.optJSONObject(index);

							Appointment appointment = new Appointment();

							appointment.setSubject(jsonObject.optString("Subject"));
							appointment.setBody(jsonObject.optString("Body"));
							appointment.setStart(jsonObject.optString("Start"));
							appointment.setEnd(jsonObject.optString("End"));
							appointment.setPlace(jsonObject.optString("Place"));
							appointment.setID(jsonObject.optString("ID"));
							appointment.setStatus(jsonObject.optString("Status"));
							appointment.setUserIsOwner(jsonObject.optInt("UserIsOwner"));

							try {
								appointment.setDate(FacilicomApplication.dateTimeFormat2.parse(jsonObject.optString("Date")));
							} catch (Exception exception) {
								exception.printStackTrace();
							}

							appointmentDao.insertOrReplace(appointment);

							JSONArray attenders = jsonObject.optJSONArray("Attenders");

							if (attenders != null && attenders.length() > 0) {
								for (int attenderIndex = 0; attenderIndex < attenders.length(); attenderIndex++) {
									JSONObject attender = (JSONObject) attenders.opt(attenderIndex);

									if (attender != null) {
										appointmentAttenderDao.insert(new AppointmentAttender(null, appointment.getId(), attender.optString("Email")));
									}
								}
							}
						}
					}
				});
	}

	static public int parseServiceRequestCount(byte[] json) {
		int result = 0;

		JSONObject jsonObject = FacilicomNetworkManager.toJSONObject(json);

		if (jsonObject != null) {
			result = jsonObject.optInt("Count");
		}

		return result;
	}

	//

	public static ArrayList<Subdivision> parseSubdivision(byte[] json) {
		ArrayList<Subdivision> result = new ArrayList<>();

		JSONArray jsonArray = FacilicomNetworkManager.toJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject object = jsonArray.optJSONObject(i);

			Subdivision subdivision = new Subdivision();

			subdivision.setUID(object.optString("UID"));
			subdivision.setRegionUID(object.optString("RegionUID"));
			subdivision.setName(object.optString("Name"));

			result.add(subdivision);
		}

		return result;
	}

	public static ArrayList<PartTimeWorkerEmployee> parseEmployee(byte[] json) {
		ArrayList<PartTimeWorkerEmployee> result = new ArrayList<>();

		JSONArray jsonArray = FacilicomNetworkManager.toJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject object = jsonArray.optJSONObject(i);

			PartTimeWorkerEmployee employee = new PartTimeWorkerEmployee();

			employee.setEmpId(object.optString("EmpID"));
			employee.setEmpFIO(object.optString("EmpFIO"));

			result.add(employee);
		}

		return result;
	}

	public static ArrayList<JobTitle> parseJobTitle(byte[] json) {
		ArrayList<JobTitle> result = new ArrayList<>();

		JSONArray jsonArray = FacilicomNetworkManager.toJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject object = jsonArray.optJSONObject(i);

			JobTitle jobTitle = new JobTitle();

			jobTitle.setUID(object.optString("UID"));
			jobTitle.setName(object.optString("Name"));

			result.add(jobTitle);
		}

		return result;
	}

	public static ArrayList<DismissReason> parseDismissReasons(byte[] json) {
		ArrayList<DismissReason> result = new ArrayList<>();

		JSONArray jsonArray = FacilicomNetworkManager.toJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject object = jsonArray.optJSONObject(i);

			DismissReason dismissReason = new DismissReason();

			dismissReason.setUID(object.optString("UID"));
			dismissReason.setName(object.optString("Name"));

			result.add(dismissReason);
		}

		Collections.sort(result, new Comparator<DismissReason>() {

			@Override
			public int compare(DismissReason lhs, DismissReason rhs) {
				return lhs.getName().compareToIgnoreCase(rhs.getName());
			}
		});

		return result;
	}

	public static ArrayList<Bank> parseBank(byte[] json) {
		ArrayList<Bank> result = new ArrayList<>();

		JSONArray jsonArray = FacilicomNetworkManager.toJSONArray(json);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject object = jsonArray.optJSONObject(i);

			Bank bank = new Bank();

			bank.setUID(object.optString("UID"));
			bank.setName(object.optString("Name"));

			result.add(bank);
		}

		return result;
	}

	static JSONObject personToJSON(Person person) {
		JSONObject result = new JSONObject();

		try {
			result.put("DocumentType", person.getDocumentType());
			result.put("PersonLocalUID", person.getPersonLocalUID());

			result.put("LastName", person.getLastName());
			result.put("FirstName", person.getFirstName());
			result.put("FatherName", person.getFatherName());

			result.put("BirthDate", person.getBirthDate() != null ? FacilicomApplication.dateTimeFormat5.format(person.getBirthDate()) : JSONObject.NULL);
			result.put("CountryUID", person.getCountryUID());
			result.put("Sex", person.getSex());

			result.put("PassportNumber", person.getPassportNumber());
			result.put("PassportIssue", person.getPassportIssue() != null ? FacilicomApplication.dateTimeFormat5.format(person.getPassportIssue()) : JSONObject.NULL);

			result.put("PhoneNumber", person.getPhoneNumber());

			result.put("PermissionSeria", person.getPermissionSeria());
			result.put("PermissionNumber", person.getPermissionNumber());
			result.put("PermissionExpiry", person.getPermissionExpiry() != null ? FacilicomApplication.dateTimeFormat5.format(person.getPermissionExpiry()) : JSONObject.NULL);

			result.put("PatentNumber", person.getPatentNumber());
			result.put("PatentIssue", person.getPatentIssue() != null ? FacilicomApplication.dateTimeFormat5.format(person.getPatentIssue()) : JSONObject.NULL);
			result.put("PatentExpiry", person.getPatentExpiry() != null ? FacilicomApplication.dateTimeFormat5.format(person.getPatentExpiry()) : JSONObject.NULL);

			result.put("SubdivisionUID", person.getSubdivisionUID());
			result.put("JobTitleUID", person.getJobTitleUID());

			result.put("BankUID", person.getBankUID());
			result.put("BankDate", person.getBankDate() != null ? FacilicomApplication.dateTimeFormat5.format(person.getBankDate()) : JSONObject.NULL);

			result.put("PersonUID", person.getPersonUID());
			result.put("BindDate", person.getBindDate() != null ? FacilicomApplication.dateTimeFormat5.format(person.getBindDate()) : JSONObject.NULL);

			result.put("DismissDate", person.getDismissDate() != null ? FacilicomApplication.dateTimeFormat5.format(person.getDismissDate()) : JSONObject.NULL);
			result.put("DismissReason", person.getDismissReasonUID());
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return result;
	}

	static JSONObject personPhotoToJSON(PersonPhoto personPhoto) {
		JSONObject result = new JSONObject();

		try {
			String imageType = null;
			switch (personPhoto.getPersonPhotoType()) {
				case PersonActivity.PHOTO_1: {
					imageType = "Passport";
				}
				break;

				case PersonActivity.PHOTO_2: {
					imageType = "Permission";
				}
				break;

				case PersonActivity.PHOTO_3: {
					imageType = "Patent";
				}
				break;

				case PersonActivity.PHOTO_4:
				case PersonActivity.PHOTO_5: {
					imageType = "Card";
				}
				break;

				case PersonActivity.PHOTO_6: {
					imageType = "SNILS";
				}
				break;

				case PersonActivity.PHOTO_7: {
					imageType = "Polis";
				}
				break;

				case PersonActivity.PHOTO_8: {
					imageType = "INN";
				}
				break;

				case PersonActivity.PHOTO_9: {
					imageType = "Training";
				}
				break;

				case PersonActivity.PHOTO_10: {
					imageType = "Registration";
				}
				break;

				case PersonActivity.PHOTO_11: {
					imageType = "Migration";
				}
				break;
			}

			File file = new File(Uri.parse(personPhoto.getPersonPhotoUri()).getPath());
			byte[] data = new byte[(int) file.length()];

			BufferedInputStream stream = null;

			try {
				stream = new BufferedInputStream(new FileInputStream(file));
				stream.read(data);
			} finally {
				if (stream != null) {
					stream.close();
				}
			}

			//

			result.put("PersonLocalUID", personPhoto.getPersonLocalUID());

			result.put("ImageLocalUID", personPhoto.getImageLocalUID());

			result.put("ImageType", imageType);
			result.put("Path", personPhoto.getPersonPhotoUri());

			result.put("Data", Base64.encodeToString(data, Base64.DEFAULT));
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return result;
	}
}
