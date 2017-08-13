package ru.facilicom24.manager.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.dialogs.AlertDialogFragment;
import ru.facilicom24.manager.utils.NetworkHelper;
import ru.facilicom24.manager.utils.SessionManager;

public class SplashActivity
		extends BaseActivity
		implements Handler.Callback {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		((ImageView) findViewById(R.id.logoImageView)).setImageResource(FacilicomApplication.getBigLogoResId(this));
	}

	@Override
	protected void onResume() {
		super.onResume();
		new Handler(this).sendEmptyMessageDelayed(0, FacilicomApplication.TICK);
	}

	@Override
	public boolean handleMessage(Message message) {
		if (NetworkHelper.isConnected(this)) {
			finish();

			goToActivity(SessionManager.getInstance().isUserLoggedIn()
					? MainActivity.class
					: LoginActivity.class
			);
		} else {
			showAlertDialog(R.id.error_no_connection_dialog, R.string.warning, R.string.no_network, R.string.btn_ok, R.string.btn_cancel);
		}

		return true;
	}

	@Override
	public void onAlertPositiveBtnClicked(AlertDialogFragment dialog) {
		super.onAlertPositiveBtnClicked(dialog);

		switch (dialog.getDialogId()) {
			case R.id.error_no_connection_dialog: {
				finish();
				goToActivity(SessionManager.getInstance().isUserLoggedIn() ? MainActivity.class : LoginActivity.class);
			}
			break;
		}
	}

	@Override
	public void onAlertNegativeBtnClicked(AlertDialogFragment dialog) {
		super.onAlertNegativeBtnClicked(dialog);

		switch (dialog.getDialogId()) {
			case R.id.error_no_connection_dialog: {
				finish();
			}
			break;
		}
	}
}
