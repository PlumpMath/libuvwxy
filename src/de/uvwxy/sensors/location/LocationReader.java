package de.uvwxy.sensors.location;

import java.util.ArrayList;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import de.uvwxy.sensors.filters.LocationFilter;

public abstract class LocationReader {
	public final static long SECOND = 1000;
	public final static long MINUTE = 60 * SECOND;
	public final static long HOUR = 60 * MINUTE;

	/**
	 * Called when location requirement is met
	 * 
	 * @author paul
	 * 
	 */
	public interface LocationResultCallback {
		void result(Location l);
	}

	/**
	 * Called with every location update until requirement is met
	 * 
	 * @author paul
	 * 
	 */
	public interface LocationStatusCallback {
		void status(Location l);
	}

	private long millisStartup = -1;
	private long mRequiredMillisDuration = -1;
	private float mRequiredAccuracy = -1f;
	protected static LocationManager lm;
	private LocationResultCallback mCallbackResult;
	private LocationStatusCallback mCallbackStatus;
	@SuppressWarnings("unused")
	private Context mCtx;

	protected LocationReader mThis = null;

	protected Location lastLocation;
	protected ArrayList<Location> locationBuffer = new ArrayList<Location>();
	protected ArrayList<LocationFilter> locationFilters = new ArrayList<LocationFilter>();

	protected LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLocationChanged(Location location) {
			mThis.onLocationChanged(location);
		}
	};

	public LocationReader(Context ctx, long millisDuration, float accuracy, LocationStatusCallback cbStatus,
			LocationResultCallback cbResult) {
		if (ctx == null) {
			throw new RuntimeException("Context can not be null");
		}
		if (cbResult == null) {
			throw new RuntimeException("LocationResultCallback can not be null");
		}

		if (accuracy <= 0) {
			accuracy = -1;
		}

		if (millisDuration <= 0) {
			millisDuration = -1;
		}

		this.mCtx = ctx;
		this.mRequiredMillisDuration = millisDuration;
		this.mRequiredAccuracy = accuracy;
		this.mCallbackStatus = cbStatus;
		this.mCallbackResult = cbResult;
		this.mThis = this;
		lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
	}

	protected void resetMemory() {
		lastLocation = null;
		locationBuffer.clear();
		resetVariables();
		System.gc();
	}

	public void startReading() {
		resetMemory();
		registerSensors(mLocationListener, lm);
		millisStartup = System.currentTimeMillis();
	}

	protected void updateLocation(Location l) {
		lastLocation = l;

		for (LocationFilter f : locationFilters) {
			// apply possible filters
			lastLocation = f.filter(lastLocation); // call non inplace variant
		}

		if (mRequiredMillisDuration == -1) {
			// instant reporting, this never stops. only reports locations with sufficient accuracy
			if ((lastLocation != null && lastLocation.getAccuracy() <= mRequiredAccuracy) || mRequiredAccuracy == -1) {
				mCallbackResult.result(lastLocation);
			}
		} else if (mRequiredMillisDuration <= (System.currentTimeMillis() - millisStartup)) {
			// time has run out
			mCallbackResult.result(lastLocation);
			// and stop
			stopReading();
		} else if (lastLocation != null && lastLocation.getAccuracy() <= mRequiredAccuracy) {
			// accuracy reached
			mCallbackResult.result(lastLocation);
			// and stop
			stopReading();
		} else {
			// continue looping until time/accuracy runs out, this is a status update (for ui stuff):f
			if (lastLocation != null) {
				mCallbackStatus.status(lastLocation);
			}
		}

		locationBuffer.add(lastLocation);
	}

	public void stopReading() {
		lm.removeUpdates(mLocationListener);
	}

	/**
	 * Check if the given provider is enabled. Example:
	 * LocationReader.isEnabled(getContext(), LocationManager.GPS_PROVIDER)
	 * 
	 * @param ctx
	 *            the Application Context
	 * @param provider
	 *            the location provider
	 * @return true if the provider is enabled
	 */
	public static boolean isEnabled(Context ctx, String provider) {
		LocationManager manager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

		return manager.isProviderEnabled(provider);
	}

	protected abstract void registerSensors(LocationListener mLocationListener2, LocationManager lm2);

	protected abstract void onLocationChanged(Location newLocation);

	protected abstract void resetVariables();

}
