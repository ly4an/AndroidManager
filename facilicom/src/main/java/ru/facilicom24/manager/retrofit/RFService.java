package ru.facilicom24.manager.retrofit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.activities.PasswordChangeActivity;
import ru.facilicom24.manager.activities.ServiceRequestActivity;
import ru.facilicom24.manager.model.AuthorizationRequest;
import ru.facilicom24.manager.model.AuthorizationResponse;
import ru.facilicom24.manager.model.LocationRequest;
import ru.facilicom24.manager.model.MapAccount;
import ru.facilicom24.manager.model.PartTimeWorkerEmployee;
import ru.facilicom24.manager.model.Version;
import ru.facilicom24.manager.utils.SessionManager;

public class RFService {
	static public void logon(AuthorizationRequest authorizationRequest, Callback<AuthorizationResponse> callback) {
		try {
			FacilicomApplication.rfService.logon(authorizationRequest).enqueue(callback);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	static public void version(Callback<Version> callback) {
		try {
			FacilicomApplication.rfService.version(SessionManager.getInstance().getToken()).enqueue(callback);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	static public JsonArray actTypes() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.actTypes(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray actReasons() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.actReasons(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray clients() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.clients(SessionManager.getInstance().getToken(), true).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray accounts() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.accounts(SessionManager.getInstance().getToken(), true).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray clientContacts() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.clientContacts(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray accountContacts() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.accountContacts(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray contacts() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.contacts(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray accountServiceTypes() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.accountServiceTypes(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray nomenclatureGroups() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.nomenclatureGroups(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray taskTypes() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.taskTypes(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray taskEmployees() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.taskEmployees(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray countries() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.countries(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray tasks() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.tasks(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray appointments() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.appointments(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray potSells() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.potSells(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray workTypes() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.workTypes(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray cleaningReasons() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.cleaningReasons(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray facilityUrgencyTypes() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.facilityUrgencyTypes(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonObject serviceRequestsCount() {
		try {
			retrofit2.Response<JsonObject> response = FacilicomApplication.rfService.serviceRequestsCount(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray serviceRequests(int offset) {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.serviceRequests(SessionManager.getInstance().getToken(), offset).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public retrofit2.Response<Void> checkUpdate(String checkId, CheckContract checkContract) {
		try {
			return FacilicomApplication.rfService.checkUpdate(SessionManager.getInstance().getToken(), checkId, checkContract).execute();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public void checkUpdate(String checkId, CheckContract checkContract, Callback<Void> callback) {
		try {
			FacilicomApplication.rfService.checkUpdate(SessionManager.getInstance().getToken(), checkId, checkContract).enqueue(callback);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	static public retrofit2.Response<Void> mobileRequestUpdate(MobileContract mobileContract) {
		try {
			return FacilicomApplication.rfService.mobileRequestUpdate(SessionManager.getInstance().getToken(), mobileContract).execute();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray serviceTypes() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.serviceTypes(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray accounts2() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.accounts2(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray forms(String serviceTypeId) {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.forms(SessionManager.getInstance().getToken(), serviceTypeId).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonObject formDescriptions(int formId) {
		try {
			retrofit2.Response<JsonObject> response = FacilicomApplication.rfService.formDescriptions(SessionManager.getInstance().getToken(), formId).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonObject checkEnd(String checkId) {
		try {
			retrofit2.Response<JsonObject> response = FacilicomApplication.rfService.checkEnd(SessionManager.getInstance().getToken(), checkId).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public void checkEnd(String checkId, Callback<JsonObject> callback) {
		try {
			FacilicomApplication.rfService.checkEnd(SessionManager.getInstance().getToken(), checkId).enqueue(callback);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	static public retrofit2.Response<Void> act(ActContract actContract) {
		try {
			return FacilicomApplication.rfService.act(SessionManager.getInstance().getToken(), actContract).execute();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public void log(
			int logTypeID,
			String label,
			String value
	) {
		try {
			FacilicomApplication.rfService.log(SessionManager.getInstance().getToken(), new LogContract(logTypeID, label, value)).enqueue(new Callback<Void>() {
				@Override
				public void onResponse(Call<Void> call, Response<Void> response) {
					// NOP
				}

				@Override
				public void onFailure(Call<Void> call, Throwable t) {
					// NOP
				}
			});
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	static public JsonArray fakes() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.fakes(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public void location(LocationRequest locationRequest, Callback<Void> callback) {
		try {
			FacilicomApplication.rfService.location(SessionManager.getInstance().getToken(), locationRequest).enqueue(callback);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	static public void nearestAccounts(float longitude, float latitude, Callback<List<MapAccount>> callback) {
		try {
			FacilicomApplication.rfService.nearestAccounts(SessionManager.getInstance().getToken(), longitude, latitude).enqueue(callback);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	static public void partTimeWorkerEmployee(String partName, Callback<List<PartTimeWorkerEmployee>> callback) {
		try {
			FacilicomApplication.rfService.partTimeWorkerEmployee(SessionManager.getInstance().getToken(), partName).enqueue(callback);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	static public JsonArray orioTypes() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.orioTypes(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public JsonArray orioArticles() {
		try {
			retrofit2.Response<JsonArray> response = FacilicomApplication.rfService.orioArticles(SessionManager.getInstance().getToken()).execute();

			if (response != null && response.body() != null) {
				return response.body();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	static public void orioInventory(String nomGroupCode, int directumID, Callback<JsonArray> callback) {
		try {
			FacilicomApplication.rfService.orioInventory(
					SessionManager.getInstance().getToken(),
					nomGroupCode,
					directumID
			).enqueue(callback);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	static public void createRequest(ServiceRequestActivity.OrioRequest orioRequest, Callback<Void> callback) {
		try {
			FacilicomApplication.rfService.createRequest(SessionManager.getInstance().getToken(), orioRequest).enqueue(callback);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	static public void changePassword(PasswordChangeActivity.ChangePasswordModelContract changePasswordContract, Callback<PasswordChangeActivity.AccessTokenModelContract> callback) {
		try {
			FacilicomApplication.rfService.changePassword(
					SessionManager.getInstance().getToken(),
					changePasswordContract
			).enqueue(callback);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
