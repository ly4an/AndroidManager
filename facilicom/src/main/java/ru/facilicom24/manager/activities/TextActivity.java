package ru.facilicom24.manager.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.views.FontTextView;

public class TextActivity
		extends BaseActivity
		implements CaptionSimpleFragment.OnFragmentInteractionListener {

	final static public String CAPTION = "Caption";
	final static public String TEXT = "Text";
	final static public String VALIDATE_EXPRESSION = "VALIDATE_EXPRESSION";
	final static public String VALIDATE_MESSAGE = "VALIDATE_MESSAGE";

	EditText editText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_text);

		((FontTextView) findViewById(R.id.view)).setText(getIntent().getStringExtra(CAPTION));

		editText = (EditText) findViewById(R.id.editText);
		editText.setText(getIntent().getStringExtra(TEXT));

		findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String text = editText.getText().toString().trim();

				String pattern = getIntent().getStringExtra(VALIDATE_EXPRESSION);

				if (pattern != null) {
					if (text.matches(pattern)) {
						setResult(RESULT_OK, new Intent().putExtra(TEXT, text));
						finish();
					} else {
						new AlertDialog.Builder(TextActivity.this)
								.setIcon(R.drawable.ic_info_white_48dp)
								.setTitle(R.string.mobile_alert_title)
								.setMessage(getIntent().getIntExtra(VALIDATE_MESSAGE, R.string.confirmation_no_value))
								.setPositiveButton(R.string.mobile_next, null)
								.show();
					}
				} else {
					setResult(RESULT_OK, new Intent().putExtra(TEXT, text));
					finish();
				}
			}
		});
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}
}
