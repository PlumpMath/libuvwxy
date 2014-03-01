package de.uvwxy.sensors.filters;

import java.util.ArrayList;

import android.location.Location;

public class BadAverageLocationFilter extends LocationFilter {

	ArrayList<Location> oldLocations = new ArrayList<Location>();

	@Override
	protected Location filterImplementation(Location newLocation) {
		oldLocations.add(newLocation);

		double lat = 0.0;
		double lon = 0.0;
		double accuracy = 0.0;
		double bearing = 0.0;
		double altitude = 0.0;

		for (Location l : oldLocations) {
			lat += l.getLatitude();
			lon += l.getLongitude();
			accuracy += l.getAccuracy();
			bearing += l.getBearing();
			altitude += l.getAltitude();
		}

		int num = oldLocations.size();
		Location ret = new Location(newLocation.getProvider());

		ret.setLatitude(lat /= num);
		ret.setLongitude(lon /= num);
		ret.setAccuracy((float) (accuracy /= num));
		ret.setBearing((float) (bearing /= num));
		ret.setAltitude(altitude /= num);

		return ret;
	}
}
