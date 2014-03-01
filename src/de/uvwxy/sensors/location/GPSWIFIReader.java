package de.uvwxy.sensors.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class GPSWIFIReader extends LocationReader {

	private boolean mUseGPS = true;
	private boolean mUseWifi = true;

	public GPSWIFIReader(Context ctx, long millisDuration, float accuracy, LocationStatusCallback cbStatus, LocationResultCallback cbResult, boolean useGPS,
			boolean useWifi) {
		super(ctx, millisDuration, accuracy, cbStatus, cbResult);

		if (mUseGPS == false && mUseWifi == false) {
			throw new RuntimeException("You must use at least one location source..");
		}

		this.mUseGPS = useGPS;
		this.mUseWifi = useWifi;
	}

	@Override
	protected void registerSensors(LocationListener mLocationListener, LocationManager lm) {
		if (mUseGPS) {
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
		}
		if (mUseWifi) {
			try {
				lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onLocationChanged(Location newLocation) {
		if (mUseGPS && newLocation.getProvider().equals(LocationManager.GPS_PROVIDER)) {
			updateLocation(newLocation);
		}

		if (mUseWifi && newLocation.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
			updateLocation(newLocation);
		}
	}

	@Override
	protected void resetVariables() {
		// nothing to do here
	}

}
