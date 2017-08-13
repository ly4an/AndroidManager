package ru.facilicom24.manager.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.List;

import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.cache.ChecksRepository;
import ru.facilicom24.manager.dialogs.AlertDialogFragment;
import ru.facilicom24.manager.fragments.CaptionFragment;
import ru.facilicom24.manager.fragments.CheckFormFragment;
import ru.facilicom24.manager.fragments.CreateCheckFragment;
import ru.facilicom24.manager.model.Check;
import ru.facilicom24.manager.services.MaxCamera;

public class QualityCheckActivity
		extends BaseActivity
		implements
		CreateCheckFragment.ICreateCheckFragmentListener,
		CaptionFragment.OnFragmentInteractionListener {

	static final String QUALITY_CHECK_FRAGMENT_TAG = "quality_check_fragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_quality_check);

		List<Check> checks = new ChecksRepository(this).getAll(Check.NEW);

		if (checks != null && !checks.isEmpty()) {
			addUiFragment(R.id.content, new CheckFormFragment(), QUALITY_CHECK_FRAGMENT_TAG);
		} else {
			addUiFragment(R.id.content, new CreateCheckFragment(), QUALITY_CHECK_FRAGMENT_TAG);
		}
	}

	@Override
	public void onCheckFormCreated() {
		getSupportFragmentManager().beginTransaction().replace(R.id.content, new CheckFormFragment(), QUALITY_CHECK_FRAGMENT_TAG).commit();
	}

	@Override
	public void onBackPressed() {
		Fragment fragment = getCurrentFragment();
		if (fragment instanceof IQualityCheckFragment) {
			((IQualityCheckFragment) fragment).onBackPressed();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onAlertPositiveBtnClicked(AlertDialogFragment dialog) {
		switch (dialog.getTag()) {
			case CheckFormFragment.google_photo_install_dialog: {
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(TextUtils.concat("market://details?id=", CheckFormFragment.GOOGLE_PHOTO).toString())));
				} catch (android.content.ActivityNotFoundException exception) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(TextUtils.concat("https://play.google.com/store/apps/details?id=", CheckFormFragment.GOOGLE_PHOTO).toString())));
				}
			}
			break;

			case CheckFormFragment.google_photo_default_dialog: {
				startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
			}
			break;

			default: {
				super.onAlertPositiveBtnClicked(dialog);
				finish();
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(
			int requestCode,
			@NonNull String[] permissions,
			@NonNull int[] grantResults
	) {
		if (requestCode == MaxCamera.CAMERA_PERMISSION_REQUEST
				&& grantResults.length > 0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

			Fragment fragment = getCurrentFragment();
			if (fragment instanceof IQualityCheckFragment) {
				MaxCamera.snap(fragment);
			}
		} else if (requestCode == CheckFormFragment.GALLERY_PERMISSION_REQUEST
				&& grantResults.length > 0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

			Fragment fragment = getCurrentFragment();
			if (fragment instanceof IQualityCheckFragment) {
				((IQualityCheckFragment) fragment).getGallery();
			}
		}
	}

	Fragment getCurrentFragment() {
		return getSupportFragmentManager().findFragmentByTag(QUALITY_CHECK_FRAGMENT_TAG);
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	@Override
	public void captionFragmentOnSendPressed() {
		((CheckFormFragment) getCurrentFragment()).onSendBtnClicked();
	}

	@Override
	public void captionFragmentOnSavePressed() {
		((CheckFormFragment) getCurrentFragment()).onSaveBtnClicked();
	}

	@Override
	public void captionFragmentOnHistoryPressed() {
	}

	@Override
	public void captionFragmentOnAlbumPressed() {
		((CheckFormFragment) getCurrentFragment()).onPhotoAlbumPressed();
	}

	@Override
	public boolean backIcon() {
		return true;
	}

	@Override
	public boolean sendIcon() {
		return getCurrentFragment() instanceof IQualityCheckFragment;
	}

	@Override
	public boolean saveIcon() {
		return getCurrentFragment() instanceof IQualityCheckFragment;
	}

	@Override
	public boolean historyIcon() {
		return false;
	}

	@Override
	public boolean albumIcon() {
		return getCurrentFragment() instanceof IQualityCheckFragment;
	}

	public interface IQualityCheckFragment {
		void onBackPressed();

		void getGallery();
	}
}
