package de.uvwxy.sensors.filters;

public class LowPassFilter extends SensorFilter {

	final float alpha;

	public LowPassFilter() {
		this.alpha = 0.8f;
	}

	public LowPassFilter(float alpha) {
		this.alpha = alpha;
	}

	@Override
	public float[] filterImplementation(float[] newValues, float[] inPlaceResult) {

		inPlaceResult[0] = alpha * inPlaceResult[0] + (1 - alpha) * newValues[0];
		inPlaceResult[1] = alpha * inPlaceResult[1] + (1 - alpha) * newValues[1];
		inPlaceResult[2] = alpha * inPlaceResult[2] + (1 - alpha) * newValues[2];

		return inPlaceResult;
	}

}
