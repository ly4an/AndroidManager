package ru.facilicom24.manager.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.fragments.CheckFormFragment;
import ru.facilicom24.manager.fragments.CreateCheckFragment;
import ru.facilicom24.manager.fragments.InteractionsFragment;

public class InteractionsActivity
		extends BaseActivity
		implements
		CreateCheckFragment.ICreateCheckFragmentListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	static final String QUALITY_CHECK_FRAGMENT_TAG = "quality_check_fragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_quality_check);
		addUiFragment(R.id.content, new InteractionsFragment(), QUALITY_CHECK_FRAGMENT_TAG);
	}

	@Override
	public void onCheckFormCreated() {
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content, new CheckFormFragment(), QUALITY_CHECK_FRAGMENT_TAG)
				.commit();
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

	Fragment getCurrentFragment() {
		return getSupportFragmentManager().findFragmentByTag(QUALITY_CHECK_FRAGMENT_TAG);
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}

	interface IQualityCheckFragment {
		void onBackPressed();
	}
}
