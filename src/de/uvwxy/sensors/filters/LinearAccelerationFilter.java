package de.uvwxy.sensors.filters;

public class LinearAccelerationFilter extends SensorFilter{
	float[] lowPassValues;
	private LowPassFilter f;
	
	public LinearAccelerationFilter() {
		f = new LowPassFilter(0.8f);
	}

	public LinearAccelerationFilter(float alpha) {
		f = new LowPassFilter(alpha);
	}

	@Override
	public float[] filterImplementation(float[] newValues, float[] inPlaceResult) {
		
		lowPassValues = f.filter(newValues, lowPassValues);
		
		inPlaceResult[0] = newValues[0] - lowPassValues[0];
		inPlaceResult[1] = newValues[1] - lowPassValues[1];
		inPlaceResult[2] = newValues[2] - lowPassValues[2];

		return inPlaceResult;
	}
}
