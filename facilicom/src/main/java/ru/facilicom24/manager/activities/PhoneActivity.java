package ru.facilicom24.manager.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;

import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.network.FacilicomNetworkClient;
import ru.facilicom24.manager.utils.NFAdapter;
import ru.facilicom24.manager.utils.NFItem;
import ru.facilicom24.manager.utils.NetworkHelper;

public class PhoneActivity
		extends FragmentActivity
		implements
		View.OnClickListener,
		AdapterView.OnItemClickListener,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	final static int ACCOUNT_INDEX = 0;

	ListView form;
	NFAdapter adapter;

	String accountUID;

	ArrayList<NFItem> items = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_phone);

		findViewById(R.id.search).setOnClickListener(this);

		form = (ListView) findViewById(R.id.form);

		items.add(new NFItem(NFItem.Type.Choose, getString(R.string.phone_search_account), getString(R.string.phone_search_choose)));

		adapter = new NFAdapter(this, items);

		form.setAdapter(adapter);
		form.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.search: {
				ArrayList<String> messages = new ArrayList<>();

				if (accountUID == null) {
					messages.add(getString(R.string.phone_search_account));
				}

				if (!messages.isEmpty()) {
					String message = TextUtils.concat("Заполните: ", TextUtils.join(", ", messages)).toString();

					new AlertDialog.Builder(this)
							.setTitle(R.string.message_label)
							.setMessage(message)
							.setNegativeButton(R.string.next_button, null)
							.show();
				} else {
					if (NetworkHelper.isConnected(this)) {

						final ProgressDialog dialog = new ProgressDialog(this);

						dialog.setMessage(getString(R.string.phone_search_message));
						dialog.setCancelable(false);
						dialog.setCanceledOnTouchOutside(false);

						dialog.show();

						FacilicomNetworkClient.getPhone(accountUID, new AsyncHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
								dialog.dismiss();

								String json = FacilicomNetworkClient.responseToString(responseBody);

								if (json != null) {
									try {
										JSONObject phoneJSON = new JSONObject(json);
										String phoneNumber = phoneJSON.optString("PhoneNumber");

										if (phoneNumber.length() > 0) {
											new AlertDialog.Builder(PhoneActivity.this)
													.setTitle(R.string.phone_number)
													.setMessage(phoneNumber)
													.setNegativeButton(R.string.next_button, null)
													.show();
										} else {
											noPhoneMessage();
										}
									} catch (Exception exception) {
										exception.printStackTrace();
										noPhoneMessage();
									}
								} else {
									noPhoneMessage();
								}
							}

							@Override
							public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
								dialog.dismiss();
								serverErrorMessage(FacilicomNetworkClient.responseToString(responseBody));
							}
						});
					}
				}
			}
			break;
		}
	}

	void noPhoneMessage() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.message_label)
				.setMessage(String.format(getString(R.string.phone_no_empty), items.get(ACCOUNT_INDEX).getText()))
				.setNegativeButton(R.string.next_button, null)
				.show();
	}

	void serverErrorMessage(String message) {
		new AlertDialog.Builder(this)
				.setTitle(R.string.message_label)
				.setMessage(message != null && !message.isEmpty() ? message : getString(R.string.server_error))
				.setNegativeButton(R.string.next_button, null)
				.show();
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
			case ACCOUNT_INDEX: {
				Intent intent = new Intent(this, SubdivisionActivity.class);
				startActivityForResult(intent, ACCOUNT_INDEX);
			}
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case ACCOUNT_INDEX: {
					accountUID = data.getStringExtra("UID");

					items.get(ACCOUNT_INDEX).setText(data.getStringExtra("Name"));
					adapter.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	public void captionFragmentOnBackPressed() {
		onBackPressed();
	}
}
