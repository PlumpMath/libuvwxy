package de.uvwxy.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class CompassReader extends SensorReader {

	float[] accelerationValues;
	float[] magneticValues;
	float[] rotationMatrix;

	public CompassReader(Context ctx, long millisDuration, SensorResultCallback cb) {
		super(ctx, millisDuration, cb);
	}

	/*
	 * Abstract Implementation
	 */

	/**
	 * Initialize the sensor(s) for measurement
	 */
	protected void registerSensors(SensorEventListener mSensorCallback, SensorManager sm) {
		sm.registerListener(mSensorCallback, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
		sm.registerListener(mSensorCallback, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
	}

	/**
	 * Resets the variables containing the sensor readings.
	 */
	protected void resetVariables() {
		accelerationValues = new float[3];
		magneticValues = new float[3];
		rotationMatrix = new float[16];
	}

	protected void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:

			accelerationValues = event.values.clone();
			generateRotationMatrix(accelerationValues, magneticValues, rotationMatrix);
			if (rotationMatrix != null) {
				determineOrientation(rotationMatrix, sensorValues);
				updateSensorValues(sensorValues);
			}

			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			magneticValues = event.values.clone();
			break;
		default:
		}
	}

	/*
	 * Static functions that might be needed elsewhere
	 */

	/**
	 * Calculates the rotation matrix from the given acceleration values and
	 * magnetic values. If the paramter rotationMarix is not null and has a
	 * length of 16 the function calculates inplace, otherwise a new array is
	 * created and returned.
	 * 
	 * @param accelerationValues
	 * @param magneticValues
	 * @param inPlaceRotationMatrix
	 * @return
	 */
	public static float[] generateRotationMatrix(float[] accelerationValues, float[] magneticValues, float[] inPlaceRotationMatrix) {
		boolean inPlace = true;
		if (inPlaceRotationMatrix == null || inPlaceRotationMatrix.length != 16) {
			inPlace = false;
		}

		if (accelerationValues != null && magneticValues != null) {
			if (inPlaceRotationMatrix == null || inPlaceRotationMatrix.length != 16) {
				inPlaceRotationMatrix = new float[16];
			}

			boolean rotationMatrixGenerated;
			rotationMatrixGenerated = SensorManager.getRotationMatrix(inPlaceRotationMatrix, null, accelerationValues, magneticValues);
			if (!rotationMatrixGenerated) {
				Log.w("DAISY", "Error");
				inPlaceRotationMatrix = null;
			}
		}
		if (inPlace) {
			return null;
		} else {
			return inPlaceRotationMatrix;
		}

	}

	/**
	 * Calculates the orientation from the given rotation matrix. If the
	 * parameter inPlace is an array of length 3 the function calculates
	 * inplace, other wise a new array is returned with the orientation values.
	 * 
	 * @param rotationMatrix
	 * @param inPlaceOrientation
	 * @return
	 */
	public static float[] determineOrientation(float[] rotationMatrix, float[] inPlaceOrientation) {
		float[] orientationValues = new float[3];
		SensorManager.getOrientation(rotationMatrix, orientationValues);
		float azimuth = (float) Math.toDegrees(orientationValues[0]);
		float pitch = (float) Math.toDegrees(orientationValues[1]);
		float roll = (float) Math.toDegrees(orientationValues[2]);

		if (inPlaceOrientation != null && inPlaceOrientation.length == 3) {
			inPlaceOrientation[0] = azimuth;
			inPlaceOrientation[1] = pitch;
			inPlaceOrientation[2] = roll;
			return null;
		} else {

			return new float[] { azimuth, pitch, roll };
		}
	}

}
