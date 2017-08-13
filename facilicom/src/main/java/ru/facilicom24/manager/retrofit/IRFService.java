package ru.facilicom24.manager.retrofit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.facilicom24.manager.activities.PasswordChangeActivity;
import ru.facilicom24.manager.activities.ServiceRequestActivity;
import ru.facilicom24.manager.model.AuthorizationRequest;
import ru.facilicom24.manager.model.AuthorizationResponse;
import ru.facilicom24.manager.model.LocationRequest;
import ru.facilicom24.manager.model.MapAccount;
import ru.facilicom24.manager.model.PartTimeWorkerEmployee;
import ru.facilicom24.manager.model.Version;

public interface IRFService {

	String AUTHORIZATION = "Authorization";

	@POST("Logon")
	Call<AuthorizationResponse> logon(@Body AuthorizationRequest jsonObject) throws Exception;

	@GET("Version")
	Call<Version> version(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("ReferenceInfo?section=ActType")
	Call<JsonArray> actTypes(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("ReferenceInfo?section=ActReason")
	Call<JsonArray> actReasons(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("Clients")
	Call<JsonArray> clients(@Header(AUTHORIZATION) String authorization, @Query("wideData") boolean wideData) throws Exception;

	@GET("SelectAccounts")
	Call<JsonArray> accounts(@Header(AUTHORIZATION) String authorization, @Query("wideData") boolean wideData) throws Exception;

	@GET("ContactClients")
	Call<JsonArray> clientContacts(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("ContactAccounts")
	Call<JsonArray> accountContacts(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("Contacts")
	Call<JsonArray> contacts(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("AccountServiceTypes")
	Call<JsonArray> accountServiceTypes(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("NomGroup")
	Call<JsonArray> nomenclatureGroups(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("TaskVid")
	Call<JsonArray> taskTypes(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("Employee")
	Call<JsonArray> taskEmployees(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("Country")
	Call<JsonArray> countries(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("TaskToDo")
	Call<JsonArray> tasks(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("Appointment")
	Call<JsonArray> appointments(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("PotSell")
	Call<JsonArray> potSells(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("WorkType")
	Call<JsonArray> workTypes(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("CleaningReason")
	Call<JsonArray> cleaningReasons(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("FacilityUrgencyType")
	Call<JsonArray> facilityUrgencyTypes(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("ServiceRequestSelect")
	Call<JsonObject> serviceRequestsCount(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("ServiceRequestSelect")
	Call<JsonArray> serviceRequests(@Header(AUTHORIZATION) String authorization, @Query("offset") int offset) throws Exception;

	@POST("Checks/{CheckId}")
	Call<Void> checkUpdate(@Header(AUTHORIZATION) String authorization, @Path("CheckId") String checkId, @Body CheckContract checkContract);

	@POST("MobileRequest")
	Call<Void> mobileRequestUpdate(@Header(AUTHORIZATION) String authorization, @Body MobileContract mobileContract);

	@GET("ServiceTypes")
	Call<JsonArray> serviceTypes(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("Accounts")
	Call<JsonArray> accounts2(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("Forms")
	Call<JsonArray> forms(@Header(AUTHORIZATION) String authorization, @Query("serviceTypeId") String serviceTypeId) throws Exception;

	@GET("FormDescriptions/{FormId}")
	Call<JsonObject> formDescriptions(@Header(AUTHORIZATION) String authorization, @Path("FormId") int formId) throws Exception;

	@POST("EndChecks/{CheckId}")
	Call<JsonObject> checkEnd(@Header(AUTHORIZATION) String authorization, @Path("CheckId") String checkId);

	@POST("Acts3")
	Call<Void> act(@Header(AUTHORIZATION) String authorization, @Body ActContract actContract);

	@POST("Log")
	Call<Void> log(@Header(AUTHORIZATION) String authorization, @Body LogContract logContract);

	@GET("Fake")
	Call<JsonArray> fakes(@Header(AUTHORIZATION) String authorization) throws Exception;

	@POST("Location")
	Call<Void> location(@Header(AUTHORIZATION) String authorization, @Body LocationRequest locationRequest);

	@GET("NearestAccount")
	Call<List<MapAccount>> nearestAccounts(@Header(AUTHORIZATION) String authorization, @Query("longitude") float longitude, @Query("latitude") float latitude) throws Exception;

	@GET("PartTimeWorkerEmployee")
	Call<List<PartTimeWorkerEmployee>> partTimeWorkerEmployee(@Header(AUTHORIZATION) String authorization, @Query("partName") String partName) throws Exception;

	@GET("Orio/Type")
	Call<JsonArray> orioTypes(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("Orio/Article")
	Call<JsonArray> orioArticles(@Header(AUTHORIZATION) String authorization) throws Exception;

	@GET("Orio/Inventory")
	Call<JsonArray> orioInventory(
			@Header(AUTHORIZATION) String authorization,
			@Query("nomGroupCode") String nomGroupCode,
			@Query("directumID") int directumID
	);

	@POST("Orio/CreateRequest")
	Call<Void> createRequest(@Header(AUTHORIZATION) String authorization, @Body ServiceRequestActivity.OrioRequest orioRequest);

	@POST("Authorize?token=token")
	Call<PasswordChangeActivity.AccessTokenModelContract> changePassword(
			@Header(AUTHORIZATION) String authorization,
			@Body PasswordChangeActivity.ChangePasswordModelContract changePasswordModelContract
	);
}
