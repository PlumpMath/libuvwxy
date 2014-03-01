package de.uvwxy.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class BarometerReader extends SensorReader {

	public BarometerReader(Context ctx, long millisDuration, SensorResultCallback cb) {
		super(ctx, millisDuration, cb);
	}

	@Override
	protected void registerSensors(SensorEventListener mSensorCallback, SensorManager sm) {
		sm.registerListener(mSensorCallback, sm.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_PRESSURE:
			sensorValues = event.values.clone();
			updateSensorValues(sensorValues);
			break;
		default:
			// not my sensor
		}

	}

	@Override
	protected void resetVariables() {
		// nothing to do here
	}

	public static float getHeight(float pressure) {

		return getHeight(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);
	}

	public static float getHeight(float pressureAtNN, float pressure) {
		return SensorManager.getAltitude(pressureAtNN, pressure);
	}

	public static float getHeightFromDiff(float value0, float value1) {
		return getHeight(value0) - getHeight(value1);
	}
}
