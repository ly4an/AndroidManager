package ru.facilicom24.manager.network;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import database.Person;
import database.PersonPhoto;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.utils.SessionManager;

public class FacilicomNetworkClient {
	final static private int POST_GET_TIMEOUT = 40000;

	static private SyncHttpClient syncHttpClient = new SyncHttpClient();
	static private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

	//

	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		AsyncHttpClient client = Looper.myLooper() == Looper.getMainLooper() ? asyncHttpClient : syncHttpClient;

		client.setTimeout(POST_GET_TIMEOUT);
		client.addHeader("Authorization", SessionManager.getInstance().getToken());
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	static private void post(Context context, String url, HttpEntity entity, String contentType, AsyncHttpResponseHandler responseHandler) {
		AsyncHttpClient client = Looper.myLooper() == Looper.getMainLooper() ? asyncHttpClient : syncHttpClient;

		client.setTimeout(POST_GET_TIMEOUT);
		client.addHeader("Authorization", SessionManager.getInstance().getToken());
		client.post(context, getAbsoluteUrl(url), entity, contentType, responseHandler);
	}

	//

	public static void getSchedule(int accountId, String nomenclatureGroupCode, String fromDate, String toDate, AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();

		params.add("directumID", Integer.toString(accountId));
		params.add("nomenclatureGroupCode", nomenclatureGroupCode);
		params.add("from", fromDate);
		params.add("to", toDate);

		get("api/Schedule", params, handler);
	}

	public static void getSubdivision(AsyncHttpResponseHandler handler) {
		get("api/Object", null, handler);
	}

	public static void getCheckActiveCard(String personUID, AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.add("PersonUID", personUID);
		get("api/CheckActiveCard", params, handler);
	}

	public static void getEmployeeForBinding(String pattern, AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.add("partFIO", pattern);
		get("api/EmployeeForBinding", params, handler);
	}

	public static void getEmployeeForUnbinding(String subdivisionUID, AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.add("subdivisionUID", subdivisionUID);
		get("api/EmployeeForBinding", params, handler);
	}

	public static void getBank(AsyncHttpResponseHandler handler) {
		get("api/Bank", null, handler);
	}

	public static void getJobTitle(String subdivisionUID, AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.add("subdivisionUID", subdivisionUID);
		get("api/JobTitle", params, handler);
	}

	public static void getDismissReasons(AsyncHttpResponseHandler handler) {
		get("api/DismissReason", null, handler);
	}

	static public void checkDuplicatePerson(Context context, Person person, AsyncHttpResponseHandler handler) {
		JSONObject json = FacilicomNetworkParser.personToJSON(person);

		try {
			post(context, "api/CheckPerson", new ByteArrayEntity(json.toString().getBytes("UTF-8")), "application/json", handler);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void postMobile(Context context, String JSON, AsyncHttpResponseHandler handler) {
		try {
			post(context, "api/MobileRequest", new ByteArrayEntity(JSON.getBytes("UTF-8")), "application/json", handler);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void postAct(Context context, String JSON, AsyncHttpResponseHandler handler) {
		try {
			post(context, "api/Acts3", new ByteArrayEntity(JSON.getBytes("UTF-8")), "application/json", handler);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void postPerson(Context context, Person person, AsyncHttpResponseHandler handler) {
		JSONObject json = FacilicomNetworkParser.personToJSON(person);

		try {
			post(context, "api/SavePerson", new ByteArrayEntity(json.toString().getBytes("UTF-8")), "application/json", handler);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void postPersonPhoto(Context context, PersonPhoto personPhoto, AsyncHttpResponseHandler handler) {
		JSONObject json = FacilicomNetworkParser.personPhotoToJSON(personPhoto);

		try {
			post(context, "api/SavePersonImage", new ByteArrayEntity(json.toString().getBytes("UTF-8")), "application/json", handler);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void postPersonCommit(Context context, Person person, AsyncHttpResponseHandler handler) {
		JSONObject json = FacilicomNetworkParser.personToJSON(person);

		try {
			post(context, "api/SavePersonFinish", new ByteArrayEntity(json.toString().getBytes("UTF-8")), "application/json", handler);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	static public void postTask(Context context, JSONObject taskJSON, AsyncHttpResponseHandler handler) {
		try {
			post(context, "api/TaskPost", new ByteArrayEntity(taskJSON.toString().getBytes("UTF-8")), "application/json", handler);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	static public void getPhone(String accountUID, AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();

		params.add("SubdivisionUID", accountUID);
		params.add("PersonUID", accountUID);

		get("api/PhoneNumber", params, handler);
	}

	public static void getTaskToDo(AsyncHttpResponseHandler handler) {
		get("api/TaskToDo", null, handler);
	}

	public static void getServiceRequests(int offset, AsyncHttpResponseHandler handler) {
		RequestParams requestParams = new RequestParams();
		requestParams.add("offset", String.valueOf(offset));
		get("api/ServiceRequestSelect", requestParams, handler);
	}

	public static void getServiceRequestsCount(AsyncHttpResponseHandler handler) {
		get("api/ServiceRequestSelect", new RequestParams(), handler);
	}

	public static void getFile(int serviceRequestFileID, AsyncHttpResponseHandler handler) {
		RequestParams requestParams = new RequestParams();
		requestParams.add("ServiceRequestFileID", String.valueOf(serviceRequestFileID));
		get("GetFile.aspx", requestParams, handler);
	}

	public static void postRequest(Context context, JSONObject jsonObject, AsyncHttpResponseHandler handler) {
		try {
			post(context, "api/ServiceRequestPost", new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8")), "application/json", handler);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void postRequestFile(Context context, JSONObject jsonObject, AsyncHttpResponseHandler handler) {
		try {
			post(context, "api/ServiceRequestFilePost", new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8")), "application/json", handler);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void getAppointments(AsyncHttpResponseHandler handler) {
		get("api/Appointment", null, handler);
	}

	public static void postAppointment(Context context, JSONObject jsonObject, AsyncHttpResponseHandler handler) {
		try {
			post(context, "api/Appointment", new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8")), "application/json", handler);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	//

	static public String responseToString(byte[] responseBody) {
		String message = null;

		try {
			message = new String(responseBody, "UTF-8");

			if (!message.equals("null")) {
				if (message.startsWith("\"")) {
					message = message.substring(1);
				}

				if (message.endsWith("\"")) {
					message = message.substring(0, message.length() - 1);
				}
			} else {
				message = null;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return message;
	}

	static public String getAbsoluteUrl(String relativeUrl) {
		return TextUtils.concat(FacilicomApplication.getAPI(), relativeUrl).toString();
	}
}
