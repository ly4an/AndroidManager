package ru.facilicom24.manager.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.facilicom24.manager.FacilicomApplication;
import ru.facilicom24.manager.R;
import ru.facilicom24.manager.fragments.CaptionSimpleFragment;
import ru.facilicom24.manager.fragments.InteractionItem;
import ru.facilicom24.manager.model.LocationRequest;
import ru.facilicom24.manager.model.MapAccount;
import ru.facilicom24.manager.retrofit.RFService;
import ru.facilicom24.manager.services.MaxLocationTrackingManager;
import ru.facilicom24.manager.services.MockCheck;
import ru.facilicom24.manager.utils.NetworkHelper;
import ru.facilicom24.manager.views.FontButton;

public class MapActivity
		extends FragmentActivity
		implements
		MaxLocationTrackingManager.IRequestPermissions,
		CaptionSimpleFragment.OnFragmentInteractionListener {

	final public static String OBJECT_FIXED_X = "OBJECT_FIXED_X";
	final public static String OBJECT_FIXED_Y = "OBJECT_FIXED_Y";

	final static String mapFileName = "file:///android_asset/map/map.html";
	final static String failFileName = "file:///android_asset/map/fail.html";

	Integer directumId;
	MaxLocationTrackingManager maxLocationTrackingManager;

	InteractionItem accountSelector;

	InteractionItem getAccountSelector() {
		if (accountSelector == null) {
			accountSelector = (InteractionItem) getSupportFragmentManager().findFragmentById(R.id.accountSelector);
		}

		return accountSelector;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(FacilicomApplication.getThemeResId(this));

		setContentView(R.layout.activity_map);

		MockCheck.initializeApplications(this);

		maxLocationTrackingManager = new MaxLocationTrackingManager(this);

		//

		getAccountSelector().setVisibility(View.INVISIBLE);
		getAccountSelector().setTitle(getString(R.string.map_account));
		getAccountSelector().setClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				startActivityForResult(new Intent(MapActivity.this, MapAccountActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
			}
		});

		//

		FontButton mapApplyButton = (FontButton) findViewById(R.id.mapApplyButton);

		mapApplyButton.setVisibility(View.INVISIBLE);
		mapApplyButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				apply();
			}
		});

		//

		FontButton mapRetryButton = (FontButton) findViewById(R.id.mapRetryButton);

		mapRetryButton.setVisibility(View.INVISIBLE);
		mapRetryButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mapRender();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();

		ArrayList<Pair<String, String>> applications = MockCheck.getActiveMockPermissionApplications(this);

		if (applications != null) {
			MockCheck.mockMessage(this, applications, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					moveTaskToBack(true);
				}
			});
		}

		if (directumId == null) {
			mapRender();
		}
	}

	@Override
	public void onPause() {
		if (maxLocationTrackingManager != null) {
			maxLocationTrackingManager.stopLocationListeners();
		}

		super.onPause();
	}

	@Override
	public void onDestroy() {
		maxLocationTrackingManager = null;
		super.onDestroy();
	}

	void mapRender() {
		directumId = null;

		FacilicomApplication.setAccount(this, 0);
		FacilicomApplication.getInstance().setMapAccounts(null);

		getAccountSelector().setOption(getString(R.string.map_account_default));

		if (maxLocationTrackingManager != null
				&& maxLocationTrackingManager.startLocationListeners()
				&& maxLocationTrackingManager.locationListenersEnabled()) {

			PointF coordinates = maxLocationTrackingManager.getLocation().getCoordinates();

			if (NetworkHelper.isConnected(this)) {
				if (coordinates.x != 0 && coordinates.y != 0) {
					viewMap("map/map.html", coordinates);

					final ProgressDialog progressDialog = new ProgressDialog(this);

					progressDialog.setIndeterminate(true);
					progressDialog.setMessage(getString(R.string.activity_map_accounts_progress));

					progressDialog.setCancelable(false);
					progressDialog.setCanceledOnTouchOutside(false);

					progressDialog.show();

					RFService.nearestAccounts(coordinates.y, coordinates.x, new Callback<List<MapAccount>>() {

						@Override
						public void onResponse(Call<List<MapAccount>> call, Response<List<MapAccount>> response) {
							progressDialog.dismiss();

							if (response != null && response.isSuccessful()) {
								List<MapAccount> mapAccounts = response.body();

								if (mapAccounts != null && !mapAccounts.isEmpty()) {
									setApplyRetry();
									FacilicomApplication.getInstance().setMapAccounts(mapAccounts);
								} else {
									setRetry();
									Toast.makeText(MapActivity.this, R.string.activity_map_accounts_nolist, Toast.LENGTH_LONG).show();
								}
							} else {
								setRetry();
								Toast.makeText(MapActivity.this, R.string.activity_map_accounts_nolist, Toast.LENGTH_LONG).show();
							}
						}

						@Override
						public void onFailure(Call<List<MapAccount>> call, Throwable t) {
							progressDialog.dismiss();

							setRetry();
							Toast.makeText(MapActivity.this, R.string.activity_map_account_list_fail, Toast.LENGTH_LONG).show();
						}
					});
				} else {
					setRetry();
					Toast.makeText(this, R.string.activity_map_get_no_location, Toast.LENGTH_LONG).show();
				}
			} else {
				if (coordinates.x != 0 && coordinates.y != 0) {
					setRetry();
					viewMap("map/map2.html", coordinates);
					Toast.makeText(this, R.string.activity_map_account_list_fail_no_internet, Toast.LENGTH_LONG).show();
				} else {
					setRetry();
					Toast.makeText(this, R.string.activity_map_get_no_location, Toast.LENGTH_LONG).show();
				}
			}
		} else {
			setRetry();
			Toast.makeText(this, R.string.activity_map_no_location, Toast.LENGTH_LONG).show();
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	void viewMap(String template, PointF coordinates) {
		byte[] fileBytes = readFileBytesFromAssets(template);

		if (fileBytes != null) {
			String html = new String(fileBytes)
					.replace("[Lat]", Float.toString(coordinates.x))
					.replace("[Lng]", Float.toString(coordinates.y));

			WebView mapWebView = (WebView) findViewById(R.id.mapWebView);

			// mapWebView.clearCache(true);
			mapWebView.getSettings().setJavaScriptEnabled(true);

			System.gc();
			mapWebView.loadDataWithBaseURL(
					mapFileName,
					html,
					"text/html",
					"utf-8",
					failFileName
			);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			directumId = data.getIntExtra("Id", 0);

			int distance = (int) (data.getFloatExtra("Distance", 0) * 20) * 50;

			if (distance > 0) {
				getAccountSelector().setOption(String.format(getString(R.string.activity_map_account_format), data.getStringExtra("Name"), distance));
			} else {
				getAccountSelector().setOption(data.getStringExtra("Name"));
			}
		}
	}

	void apply() {
		if (FacilicomApplication.getInstance().getMapAccounts() == null) {
			new AlertDialog.Builder(this)
					.setIcon(R.drawable.ic_info_white_48dp)
					.setTitle(R.string.activity_map_accounts_choose_title)
					.setMessage(R.string.activity_map_accounts_not_find)
					.setPositiveButton(R.string.activity_map_accounts_choose_ok, null)
					.show();

			return;
		}

		if (directumId == null) {
			new AlertDialog.Builder(this)
					.setIcon(R.drawable.ic_info_white_48dp)
					.setTitle(R.string.activity_map_accounts_choose_title)
					.setMessage(R.string.activity_map_accounts_choose)
					.setPositiveButton(R.string.activity_map_accounts_choose_ok, null)
					.show();

			return;
		}

		PointF coordinates = maxLocationTrackingManager.getLocation().getCoordinates();
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();

		editor.putFloat(OBJECT_FIXED_X, coordinates.x);
		editor.putFloat(OBJECT_FIXED_Y, coordinates.y);

		editor.apply();

		FacilicomApplication.setAccount(this, directumId);

		if (NetworkHelper.isConnected(this)) {
			RFService.location(new LocationRequest(
					coordinates.y,
					coordinates.x,
					0,
					0,
					directumId
			), new Callback<Void>() {

				@Override
				public void onResponse(Call<Void> call, Response<Void> response) {
					if (response != null && response.isSuccessful()) {
						finish();
						Toast.makeText(MapActivity.this, R.string.activity_map_arrived, Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(MapActivity.this, R.string.activity_map_arrived_no_internet, Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public void onFailure(Call<Void> call, Throwable t) {
					Toast.makeText(MapActivity.this, R.string.activity_map_arrived_no_internet, Toast.LENGTH_LONG).show();
				}
			});
		} else {
			Toast.makeText(this, R.string.activity_map_arrived_no_internet, Toast.LENGTH_LONG).show();
		}
	}

	byte[] readFileBytesFromAssets(String fileName) {
		AssetManager.AssetInputStream assetInputStream = null;

		try {
			assetInputStream = (AssetManager.AssetInputStream) getAssets().open(fileName);

			byte[] buffer = new byte[1024];
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			int length;
			while ((length = assetInputStream.read(buffer)) > 0) {
				byteArrayOutputStream.write(buffer, 0, length);
			}

			return byteArrayOutputStream.toByteArray();
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			if (assetInputStream != null) {
				try {
					assetInputStream.close();
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}

		return null;
	}

	void setApplyRetry() {
		findViewById(R.id.mapApplyButton).setVisibility(View.VISIBLE);
		findViewById(R.id.mapRetryButton).setVisibility(View.VISIBLE);

		getAccountSelector().setVisibility(View.VISIBLE);
	}

	void setRetry() {
		findViewById(R.id.mapApplyButton).setVisibility(View.GONE);
		findViewById(R.id.mapRetryButton).setVisibility(View.VISIBLE);

		getAccountSelector().setVisibility(View.INVISIBLE);
	}

	// Permission request

	@Override
	public void MaxLocationTrackingManagerRequestPermissions() {
		ActivityCompat.requestPermissions(
				this,
				new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
				MaxLocationTrackingManager.ACCESS_FINE_LOCATION_PERMISSION_REQUEST
		);
	}

	@Override
	public void onRequestPermissionsResult(
			int requestCode,
			@NonNull String[] permissions,
			@NonNull int[] grantResults
	) {
		if (requestCode == MaxLocationTrackingManager.ACCESS_FINE_LOCATION_PERMISSION_REQUEST
				&& grantResults.length > 0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

			mapRender();
		} else {
			finish();
		}
	}

	@Override
	public void captionFragmentOnBackPressed() {
		finish();
	}
}
