package de.uvwxy.sensors.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class MockLocationProviderFromAsset implements Runnable {
	private LinkedList<String> lines = new LinkedList<String>();
	private LocationManager mocLocationManager = null;
	private String mocProvider = LocationManager.GPS_PROVIDER;
	private long posTimeoutMS = 1111;
	private double posAltitude = 0;

	public MockLocationProviderFromAsset(Context context, String assetFileName, long posTimeoutMS, double posAltitude) throws IOException {
		this.posTimeoutMS = posTimeoutMS;
		this.posAltitude = posAltitude;
		mocLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		mocLocationManager.addTestProvider(mocProvider, false, false, false, false, true, false, false, 0, 5);

		mocLocationManager.setTestProviderEnabled(mocProvider, true);

		InputStream is = ((Context) context).getAssets().open(assetFileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = reader.readLine();
		while (line != null) {
			lines.add(line);
			line = reader.readLine();
		}
	}

	@Override
	public void run() {
		long sleepTime = 1000;
		@SuppressWarnings("unused") // TODO: remove?
		int posNum = 0;
		for (String str : lines) {
			posNum++;

			String[] values = str.split(",");

			// Log.i("JNR_LOCMOV", "read: " + values.length);

			if (values[0] != null && !values[0].equals("")) {
				sleepTime = Long.parseLong(values[0]);
			} else {
				sleepTime = posTimeoutMS;
			}

			double latitude = Double.parseDouble(values[1]);
			double longitude = Double.parseDouble(values[2]);
			double altitude = posAltitude;

			if (values.length > 3 && values[3] != null) {
				altitude = Double.parseDouble(values[3]);
			}

			// Log.i("JNR_LOCMOV", "read (" + posNum + "): lat=" + latitude
			// + ", lon=" + longitude + ", alti=" + altitude);

			Location location = new Location(mocProvider);
			location.setLatitude(latitude);
			location.setLongitude(longitude);
			location.setAltitude(altitude);
			location.setAccuracy(1.0f);
			Log.i("JNR_LOCMOV", location.toString());

			// new timestamp per location, otherwise ignored
			location.setTime(System.currentTimeMillis());
			if (!kill) {
				mocLocationManager.setTestProviderLocation(mocProvider, location);
			} else {
				return;
			}
			try {

				Thread.sleep(sleepTime);

			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
			mocLocationManager.removeTestProvider(mocProvider);
	}

	boolean kill = false;

	public void remove() {
		kill = true;
		mocLocationManager.removeTestProvider(mocProvider);
	}
}
