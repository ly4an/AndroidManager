package ru.facilicom24.manager.services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

public class MaxLocationTrackingManager {
	final static public int ACCESS_FINE_LOCATION_PERMISSION_REQUEST = 6733;

	final static private int MOCK_DISTANCE = 1500;

	final static private int MIN_DISTANCE = 5;
	final static private int MIN_INTERVAL = 5 * 1000;

	private boolean getLocationGps;

	private Activity activity;
	private LocationManager manager;
	private MXLocationListener gpsListener;
	private MXLocationListener networkListener;

	public MaxLocationTrackingManager(Activity activity) {
		this.activity = activity;
		manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
	}

	public boolean startLocationListeners() {
		getLocationGps = false;

		if (networkListener == null && manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			networkListenerStart();
		}

		if (gpsListener == null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
					gpsListenerStart();
				} else {
					if (activity instanceof IRequestPermissions) {
						((IRequestPermissions) activity).MaxLocationTrackingManagerRequestPermissions();
						return false;
					}
				}
			} else {
				gpsListenerStart();
			}
		}

		return true;
	}

	private void networkListenerStart() {
		try {
			manager.sendExtraCommand(LocationManager.NETWORK_PROVIDER, "delete_aiding_data", null);
			manager.sendExtraCommand(LocationManager.NETWORK_PROVIDER, "force_xtra_injection", null);
			manager.sendExtraCommand(LocationManager.NETWORK_PROVIDER, "force_time_injection", null);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		networkListener = new MXLocationListener();

		try {
			manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_INTERVAL, MIN_DISTANCE, networkListener);
		} catch (SecurityException exception) {
			exception.printStackTrace();
		}
	}

	private void gpsListenerStart() {
		try {
			manager.sendExtraCommand(LocationManager.GPS_PROVIDER, "delete_aiding_data", null);
			manager.sendExtraCommand(LocationManager.GPS_PROVIDER, "force_xtra_injection", null);
			manager.sendExtraCommand(LocationManager.GPS_PROVIDER, "force_time_injection", null);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		gpsListener = new MXLocationListener();

		try {
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_INTERVAL, MIN_DISTANCE, gpsListener);
		} catch (SecurityException exception) {
			exception.printStackTrace();
		}
	}

	public void stopLocationListeners() {
		if (gpsListener != null) {
			try {
				manager.removeUpdates(gpsListener);
			} catch (SecurityException exception) {
				exception.printStackTrace();
			}

			gpsListener = null;
		}

		if (networkListener != null) {
			try {
				manager.removeUpdates(networkListener);
			} catch (SecurityException exception) {
				exception.printStackTrace();
			}

			networkListener = null;
		}
	}

	public boolean locationListenersEnabled() {
		return networkListener != null
				|| gpsListener != null;
	}

	public MaxLocation getLocation() {
		MaxLocation location = null;

		try {
			if (gpsListener == null && networkListener != null) {
				location = getLocation(networkListener, LocationManager.NETWORK_PROVIDER);
			} else if (gpsListener != null && networkListener == null) {
				location = getLocation(gpsListener, LocationManager.GPS_PROVIDER);
			} else if (gpsListener != null) {
				MaxLocation location1 = getLocation(gpsListener, LocationManager.GPS_PROVIDER);
				MaxLocation location2 = getLocation(networkListener, LocationManager.NETWORK_PROVIDER);

				double distance = getDistance(location1, location2);

				if (distance > 0 && distance < MOCK_DISTANCE) {
					if (getLocationGps) {
						location = location1 != null ? location1 : location2;
					} else {
						location = location2 != null ? location2 : location1;
					}
				} else {
					location = location2;
					// new ServantServiceProxy(context).log(Constants.LOG_MESSAGE, "DISTANCE_CHECK", String.valueOf(distance));
				}

				getLocationGps = !getLocationGps;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return location != null ? location : new MaxLocation(new PointF(0, 0), false);
	}

	private double getDistance(MaxLocation p1, MaxLocation p2) {
		double result = 0;

		if (p1 != null && p2 != null) {
			PointF a = p1.getCoordinates();
			PointF b = p2.getCoordinates();

			result = 6371302 * Math.acos(Math.cos(Math.toRadians(a.y)) * Math.cos(Math.toRadians(b.y)) * Math.cos(Math.toRadians(a.x) - Math.toRadians(b.x)) + Math.sin(Math.toRadians(a.y)) * Math.sin(Math.toRadians(b.y)));

			if (Double.isNaN(result)) {
				result = -1;
			}
		}

		return result;
	}

	private MaxLocation getLocation(MXLocationListener mxLocationListener, String provider) {
		MaxLocation result = null;

		Location location = null;
		if (mxLocationListener.locationReady()) {
			location = mxLocationListener.getLocation();
		} else {
			try {
				location = manager.getLastKnownLocation(provider);
			} catch (SecurityException exception) {
				exception.printStackTrace();
			}
		}

		if (location != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
				if (location.isFromMockProvider()) {
					// new ServantServiceProxy(context).log(Constants.LOG_MESSAGE, "MOCK_PROVIDER", String.valueOf(Build.VERSION.SDK_INT));
				}
			}

			result = new MaxLocation(new PointF(
					(float) location.getLatitude(),
					(float) location.getLongitude()),
					false
			);
		}

		return result;
	}

	public interface IRequestPermissions {
		void MaxLocationTrackingManagerRequestPermissions();
	}

	private class MXLocationListener
			implements LocationListener {

		Location location;

		boolean locationReady() {
			return location != null;
		}

		Location getLocation() {
			return location;
		}

		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				this.location = location;
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}
	}
}
