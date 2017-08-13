package ru.facilicom24.manager.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionFragment;
import ru.facilicom24.manager.fragments.CheckFormFragment;
import ru.facilicom24.manager.fragments.ConfirmationFragment;
import ru.facilicom24.manager.fragments.CreateCheckFragment;

public class ConfirmationActivity
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
		addUiFragment(R.id.content, new ConfirmationFragment(), QUALITY_CHECK_FRAGMENT_TAG);
	}

	@Override
	public void onCheckFormCreated() {
		getSupportFragmentManager().beginTransaction().replace(R.id.content, new CheckFormFragment(), QUALITY_CHECK_FRAGMENT_TAG).commit();
	}

	@Override
	public void onBackPressed() {
		Fragment fragment = getCurrentFragment();
		if (fragment instanceof ConfirmationFragment) {
			((ConfirmationFragment) fragment).onBackPressed();
		} else {
			super.onBackPressed();
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
		((ConfirmationFragment) getCurrentFragment()).onSendBtnClicked();
	}

	@Override
	public void captionFragmentOnSavePressed() {
		((ConfirmationFragment) getCurrentFragment()).onSaveBtnClicked();
	}

	@Override
	public void captionFragmentOnHistoryPressed() {
	}

	@Override
	public void captionFragmentOnAlbumPressed() {
	}

	@Override
	public boolean backIcon() {
		return true;
	}

	@Override
	public boolean sendIcon() {
		return getCurrentFragment() instanceof ConfirmationFragment;
	}

	@Override
	public boolean saveIcon() {
		return getCurrentFragment() instanceof ConfirmationFragment;
	}

	@Override
	public boolean historyIcon() {
		return false;
	}

	@Override
	public boolean albumIcon() {
		return false;
	}
}
