package de.uvwxy.sensors.filters;

public abstract class SensorFilter {
	float[] nonInplaceValues;

	public float[] filter(float[] newValues, float[] inPlaceResult) {
		if (newValues == null)
			return null;

		// inplace array must be same length, if wrong: inplace does not work.
		if (inPlaceResult == null || inPlaceResult.length != newValues.length) {
			inPlaceResult = nonInplaceValues;
		}

		// if inPlaceValues was wrong, and nonInplaceValues was wrong initialize nonInplaceValues
		if (inPlaceResult == null || inPlaceResult.length != newValues.length) {
			nonInplaceValues = new float[newValues.length];
			inPlaceResult = nonInplaceValues;
		}
		
		return filterImplementation(newValues, inPlaceResult);

	}

	protected abstract float[] filterImplementation(float[] values, float[] inPlaceResult);
}
