package de.uvwxy.sensors.filters;

import android.location.Location;

public abstract class LocationFilter {

	/**
	 * No inplace calculation, as developers tend to make errors with call by
	 * reference. This avoids copying everything manually...
	 * 
	 * @param newValues
	 * @return the filtered location
	 */
	public Location filter(Location newLocation) {
		if (newLocation == null)
			return null;

		return filterImplementation(newLocation);

	}

	protected abstract Location filterImplementation(Location newLocation);
}
