package ru.facilicom24.manager.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

class FacilicomNetworkManager {
	static final private String TAG = "FacilicomNetworkManager";

	static JSONObject toJSONObject(byte[] response) {
		JSONObject jsonObject = new JSONObject();

		try {
			String json = new String(response, "UTF-8");
			jsonObject = json.equals("null") ? new JSONObject() : new JSONObject(json);
		} catch (Exception e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return jsonObject;
	}

	static JSONArray toJSONArray(byte[] response) {
		JSONArray jsonArray = new JSONArray();

		try {
			String json = new String(response, "UTF-8");
			jsonArray = json.equals("null") ? new JSONArray() : new JSONArray(json);
		} catch (Exception e) {
			Log.d(TAG, e.getMessage(), e);
		}

		return jsonArray;
	}
}
