package ru.facilicom24.manager.activities;

import android.os.Bundle;

import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;

public class MobileEmployeeActivity
		extends BaseActivity
		implements CaptionSimpleFragment.OnFragmentInteractionListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_mobile_employee);
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}
}
