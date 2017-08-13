package ru.facilicom24.manager.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URLDecoder;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.ChecksRepository;
import ru.facilicom24.manager.cache.PhotosRepository;
import ru.facilicom24.manager.model.AuthorizationRequest;
import ru.facilicom24.manager.model.AuthorizationResponse;
import ru.facilicom24.manager.retrofit.RFService;
import ru.facilicom24.manager.utils.NetworkHelper;
import ru.facilicom24.manager.utils.SessionManager;

public class LoginActivity
		extends BaseActivity
		implements View.OnClickListener, Handler.Callback {

	static final public String USERNAME = "USERNAME";
	static final public String PASSWORD = "PASSWORD";

	static final int PASSWORD_CHANGE = 6546;

	EditText mUsernameView;
	EditText mPasswordView;

	SharedPreferences preferences;
	AuthorizationResponse authorizationResponse;

	String username;
	String password;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		findViewById(R.id.enter).setOnClickListener(this);

		mUsernameView = (EditText) findViewById(R.id.username);
		mPasswordView = (EditText) findViewById(R.id.password);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		mUsernameView.setText(preferences.getString(USERNAME, ""));
		mPasswordView.setText(preferences.getString(PASSWORD, ""));

		mPasswordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (hasFocus) {
					mPasswordView.setText("");
				}
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.enter: {
				hideKeyboard();

				username = mUsernameView.getText().toString().trim();
				password = mPasswordView.getText().toString().trim();

				if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
					if (NetworkHelper.isConnected(this)) {
						showProgressDialog("Авторизация");

						RFService.logon(new AuthorizationRequest(username, password), new Callback<AuthorizationResponse>() {
							@Override
							public void onResponse(Call<AuthorizationResponse> call, Response<AuthorizationResponse> response) {
								if (response != null) {
									authorizationResponse = response.body();

									if (authorizationResponse != null) {
										new Handler(LoginActivity.this).sendEmptyMessageDelayed(0, FacilicomApplication.TICK);
									} else {
										ResponseBody errorBody = response.errorBody();

										if (errorBody != null && errorBody.contentLength() > 0) {
											try {
												if (URLDecoder.decode(errorBody.string(), "UTF-8").toLowerCase().contains(getString(R.string.activity_password_change_key))) {
													hideProgressDialog();
													startActivityForResult(new Intent(LoginActivity.this, PasswordChangeActivity.class)
																	.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
																	.putExtra(USERNAME, username),
															PASSWORD_CHANGE);
												} else {
													new Handler(LoginActivity.this).sendEmptyMessageDelayed(0, FacilicomApplication.TICK);
												}
											} catch (Exception exception) {
												exception.printStackTrace();
												new Handler(LoginActivity.this).sendEmptyMessageDelayed(0, FacilicomApplication.TICK);
											}
										} else {
											new Handler(LoginActivity.this).sendEmptyMessageDelayed(0, FacilicomApplication.TICK);
										}
									}
								}
							}

							@Override
							public void onFailure(Call<AuthorizationResponse> call, Throwable t) {
								new Handler(LoginActivity.this).sendEmptyMessageDelayed(0, FacilicomApplication.TICK);
							}
						});
					} else {
						showAlertDialog(R.id.error_data_dialog, R.string.message_label, R.string.errorConnection);
					}
				} else {
					showAlertDialog(R.id.error_data_dialog, R.string.error, R.string.error_data);
				}
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case PASSWORD_CHANGE: {
					SessionManager.getInstance().setForceSync(true);

					finish();
					goToActivity(MainActivity.class);
				}
				break;
			}
		}
	}

	@Override
	public boolean handleMessage(Message message) {
		hideProgressDialog();

		if (authorizationResponse != null) {
			if (!username.equalsIgnoreCase(PreferenceManager.getDefaultSharedPreferences(this).getString(USERNAME, null))) {
				SessionManager.getInstance().saveActs(null);

				new PhotosRepository(this).deleteAll();
				new ChecksRepository(this).deleteAll();
			}

			PreferenceManager
					.getDefaultSharedPreferences(this)
					.edit()
					.putString(USERNAME, username)
					.putString(PASSWORD, password)
					.apply();

			SessionManager sessionManager = SessionManager.getInstance();

			String token = sessionManager.getToken();

			sessionManager.setToken(authorizationResponse.getToken());
			sessionManager.setForceSync(!authorizationResponse.getToken().equals(token));

			finish();
			goToActivity(MainActivity.class);
		} else {
			mPasswordView.setText(preferences.getString(PASSWORD, ""));
			Toast.makeText(this, R.string.error_login, Toast.LENGTH_SHORT).show();
		}

		return false;
	}
}
