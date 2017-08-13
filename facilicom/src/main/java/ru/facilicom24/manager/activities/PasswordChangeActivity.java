package ru.facilicom24.manager.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLDecoder;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionFragment;
import ru.facilicom24.manager.retrofit.RFService;
import ru.facilicom24.manager.services.MaxCheck;
import ru.facilicom24.manager.utils.NetworkHelper;
import ru.facilicom24.manager.utils.SessionManager;

public class PasswordChangeActivity
		extends FragmentActivity
		implements
		View.OnClickListener,
		CaptionFragment.OnFragmentInteractionListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_change);

		findViewById(R.id.errorTextView).setVisibility(View.GONE);

		findViewById(R.id.applyButton).setOnClickListener(this);

		((EditText) findViewById(R.id.emailEditText)).setText(getIntent().getStringExtra(LoginActivity.USERNAME));
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	@Override
	public void captionFragmentOnSendPressed() {
	}

	@Override
	public void captionFragmentOnSavePressed() {
	}

	@Override
	public void captionFragmentOnHistoryPressed() {
	}

	@Override
	public void captionFragmentOnAlbumPressed() {
	}

	@Override
	public boolean backIcon() {
		return false;
	}

	@Override
	public boolean sendIcon() {
		return false;
	}

	@Override
	public boolean saveIcon() {
		return false;
	}

	@Override
	public boolean historyIcon() {
		return false;
	}

	@Override
	public boolean albumIcon() {
		return false;
	}

	@Override
	public void onBackPressed() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.applyButton: {
				apply();
			}
			break;
		}
	}

	void apply() {
		EditText emailEditText = (EditText) findViewById(R.id.emailEditText);

		final String email = emailEditText.getText().toString().trim().toLowerCase();
		emailEditText.setText(email);

		final String passwordFrom = ((EditText) findViewById(R.id.passwordFromEditText)).getText().toString();
		final String passwordTo = ((EditText) findViewById(R.id.passwordToEditText)).getText().toString();

		final String passwordCheck = ((EditText) findViewById(R.id.passwordCheckEditText)).getText().toString();

		//

		ArrayList<String> labels = new ArrayList<>();

		if (!MaxCheck.required(email)) {
			labels.add(getString(R.string.activity_password_change_email));
		}

		if (MaxCheck.required(email) && !MaxCheck.email(email)) {
			labels.add(getString(R.string.activity_password_change_email_error));
		}

		if (!MaxCheck.required(passwordFrom)) {
			labels.add(getString(R.string.activity_password_change_password_from));
		}

		if (!MaxCheck.required(passwordTo)) {
			labels.add(getString(R.string.activity_password_change_password_to));
		}

		if (!MaxCheck.required(passwordCheck)) {
			labels.add(getString(R.string.activity_password_change_password_check));
		}

		if (MaxCheck.required(passwordTo) && MaxCheck.required(passwordCheck) && !passwordTo.equals(passwordCheck)) {
			labels.add(getString(R.string.activity_password_change_password_check_error));
		}

		//

		TextView errorTextView = (TextView) findViewById(R.id.errorTextView);

		errorTextView.setVisibility(View.GONE);

		if (labels.isEmpty()) {
			if (NetworkHelper.isConnected(this)) {
				new AlertDialog.Builder(this)
						.setIcon(R.drawable.ic_info_white_48dp)
						.setTitle(R.string.activity_password_change_message)
						.setMessage(R.string.activity_password_change_confirm)
						.setPositiveButton(R.string.activity_password_change_message_yes, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								final ProgressDialog progressDialog = new ProgressDialog(PasswordChangeActivity.this);

								progressDialog.setIndeterminate(true);
								progressDialog.setMessage(getString(R.string.activity_password_change_progress));

								progressDialog.setCancelable(false);
								progressDialog.setCanceledOnTouchOutside(false);

								progressDialog.show();
								RFService.changePassword(new PasswordChangeActivity.ChangePasswordModelContract(email, passwordFrom, passwordTo), new Callback<AccessTokenModelContract>() {
									@Override
									public void onResponse(Call<AccessTokenModelContract> call, Response<AccessTokenModelContract> response) {
										progressDialog.dismiss();

										if (response != null) {
											AccessTokenModelContract accessTokenModelContract = response.body();

											if (accessTokenModelContract != null && accessTokenModelContract.Token != null && !accessTokenModelContract.Token.isEmpty()) {
												PreferenceManager
														.getDefaultSharedPreferences(PasswordChangeActivity.this)
														.edit()
														.putString(LoginActivity.USERNAME, email)
														.putString(LoginActivity.PASSWORD, passwordTo)
														.apply();

												SessionManager.getInstance().setToken(accessTokenModelContract.Token);

												Toast.makeText(PasswordChangeActivity.this, R.string.activity_password_change_done, Toast.LENGTH_LONG).show();

												setResult(RESULT_OK);
												finish();
											} else {
												ResponseBody errorBody = response.errorBody();

												if (errorBody != null && errorBody.contentLength() > 0) {
													try {
														new AlertDialog.Builder(PasswordChangeActivity.this)
																.setIcon(R.drawable.ic_info_white_48dp)
																.setTitle(R.string.activity_password_change_message)
																.setMessage(URLDecoder.decode(errorBody.string(), "UTF-8"))
																.setPositiveButton(R.string.activity_password_change_message_yes, null)
																.show();
													} catch (Exception exception) {
														exception.printStackTrace();
														Toast.makeText(PasswordChangeActivity.this, R.string.activity_password_change_error, Toast.LENGTH_LONG).show();
													}
												} else {
													Toast.makeText(PasswordChangeActivity.this, R.string.activity_password_change_error, Toast.LENGTH_LONG).show();
												}
											}
										} else {
											Toast.makeText(PasswordChangeActivity.this, R.string.activity_password_change_error, Toast.LENGTH_LONG).show();
										}
									}

									@Override
									public void onFailure(Call<AccessTokenModelContract> call, Throwable t) {
										progressDialog.dismiss();
										Toast.makeText(PasswordChangeActivity.this, R.string.activity_password_change_error, Toast.LENGTH_LONG).show();
									}
								});
							}
						})
						.setNegativeButton(R.string.activity_password_change_message_no, null)
						.show();
			} else {
				Toast.makeText(PasswordChangeActivity.this, R.string.activity_password_change_internet, Toast.LENGTH_LONG).show();
			}
		} else {
			errorTextView.setVisibility(View.VISIBLE);
			errorTextView.setText(TextUtils.join(", ", labels));
		}
	}

	public class ChangePasswordModelContract {
		String Email;
		String OldPassword;
		String NewPassword;

		ChangePasswordModelContract(
				String email,
				String oldPassword,
				String newPassword
		) {
			this.Email = email;
			this.OldPassword = oldPassword;
			this.NewPassword = newPassword;
		}
	}

	public class AccessTokenModelContract {
		String Token;
	}
}
