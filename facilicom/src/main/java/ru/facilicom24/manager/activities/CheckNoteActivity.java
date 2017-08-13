package ru.facilicom24.manager.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;

public class CheckNoteActivity
		extends FragmentActivity
		implements
		AdapterView.OnClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_check_note);

		findViewById(R.id.next).setOnClickListener(this);

		((EditText) findViewById(R.id.text)).setText(getIntent().getStringExtra("Text"));
	}

	@Override
	public void onClick(View v) {
		setResult(RESULT_OK, getIntent().putExtra("Text", ((EditText) findViewById(R.id.text)).getText().toString().trim()));
		finish();
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}
}
