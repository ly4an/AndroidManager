package ru.facilicom24.manager.utils;

// Import

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import ru.facilicom24.manager.FacilicomApplication;

// SessionManager

public class SessionManager {

	static final private String SESSION_PREFS_NAME = "session";

	static final private String LAST_EDITED_ZONE = "last_edited_zone";
	static final private String LAST_EDITED_ELEMENT = "last_edited_element";

	static final private String HOST_URL = "host";
	static final private String TOKEN = "token";
	static final private String FORCE_SYNC = "force_sync";
	static final private String ACT = "Act";
	static final private String ACTS = "Acts";

	static private SessionManager sessionManager;

	private SharedPreferences sharedPreferences;

	private SessionManager(Context context) {
		sharedPreferences = context.getSharedPreferences(SESSION_PREFS_NAME, Context.MODE_PRIVATE);
	}

	// Instance

	public static SessionManager getInstance() {
		return sessionManager;
	}

	// Init

	public static void init(Context context) {
		sessionManager = new SessionManager(context);
		sessionManager.setHostUrl(FacilicomApplication.getAPI());
	}

	// Act

	public JSONObject getAct() {
		try {
			String jsonString = getString(sharedPreferences, ACT);

			if (jsonString == null) {
				return null;
			} else {
				return new JSONObject(getString(sharedPreferences, ACT));
			}
		} catch (Exception e) {
			return null;
		}
	}

	public void saveAct(String token) {
		putString(sharedPreferences, ACT, token);
	}

	// Acts

	public JSONArray getActs() {
		try {
			String jsonString = getString(sharedPreferences, ACTS);

			if (jsonString == null) {
				return null;
			} else {
				return new JSONArray(getString(sharedPreferences, ACTS));
			}
		} catch (Exception e) {
			return null;
		}
	}

	public void saveActs(JSONArray acts) {
		putString(sharedPreferences, ACTS, acts != null ? acts.toString() : null);
	}

	// Token

	public String getToken() {
		return getString(sharedPreferences, TOKEN);
	}

	public void setToken(String token) {
		putString(sharedPreferences, TOKEN, token);
	}

	// ForceSync

	public boolean getForceSync() {
		return getBoolean(sharedPreferences, FORCE_SYNC);
	}

	public void setForceSync(boolean forceSync) {
		putBoolean(sharedPreferences, FORCE_SYNC, forceSync);
	}

	// UserLoggedIn

	public boolean isUserLoggedIn() {
		return !TextUtils.isEmpty(getToken());
	}

	// LastEditedZone

	public int getLastEditedZone() {
		return getInt(sharedPreferences, LAST_EDITED_ZONE);
	}

	public void setLastEditedZone(int index) {
		putInt(sharedPreferences, LAST_EDITED_ZONE, index);
	}

	// LastEditedElement

	public int getLastEditedElement() {
		return getInt(sharedPreferences, LAST_EDITED_ELEMENT);
	}

	public void setLastEditedElement(int index) {
		putInt(sharedPreferences, LAST_EDITED_ELEMENT, index);
	}

	// HostUrl

	public String getHostUrl() {
		return getString(sharedPreferences, HOST_URL);
	}

	private void setHostUrl(String url) {
		putString(sharedPreferences, HOST_URL, url);
	}

	// String

	private void putString(SharedPreferences sharedPreferences, String key, String value) {
		sharedPreferences.edit().putString(key, value).apply();
	}

	protected String getString(SharedPreferences sharedPreferences, String key) {
		return sharedPreferences.getString(key, null);
	}

	// Boolean

	private void putBoolean(SharedPreferences sharedPreferences, String key, boolean value) {
		sharedPreferences.edit().putBoolean(key, value).apply();
	}

	private boolean getBoolean(SharedPreferences sharedPreferences, String key) {
		return sharedPreferences.getBoolean(key, false);
	}

	// Int

	private void putInt(SharedPreferences sharedPreferences, String key, int value) {
		sharedPreferences.edit().putInt(key, value).apply();
	}

	protected int getInt(SharedPreferences sharedPreferences, String key) {
		return sharedPreferences.getInt(key, -1);
	}
}
