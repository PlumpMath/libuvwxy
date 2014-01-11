package de.uvwxy.sensors;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import de.uvwxy.sensors.filters.SensorFilter;

public abstract class SensorReader {
	/*
	 * Public access below
	 */

	public interface SensorResultCallback {
		void result(float[] f);
	}

	private long millisStartup = -1;
	private long mRequiredMillisDuration = -1;
	protected static SensorManager sm;
	private SensorResultCallback mCallbackResult;
	@SuppressWarnings("unused")
	private Context mCtx;

	protected SensorReader mThis = null;

	protected float[] sensorValues;
	protected ArrayList<float[]> sensorBuffer = new ArrayList<float[]>();
	protected ArrayList<SensorFilter> sensorFilters = new ArrayList<SensorFilter>();

	private boolean dropOtherValues = false;
	
	protected SensorEventListener mSensorCallback = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO: use this?
		}

		@Override
		public void onSensorChanged(SensorEvent event) {

			mThis.onSensorChanged(event);
		}
	};

	/**
	 * Create a ReadCompass object that read for millisDuration millis from the
	 * compass. The result will be returned via the SensorResultCallback.
	 * 
	 * @param ctx
	 * @param millisDuration
	 *            if -1 instant reporting of values
	 * @param cb
	 */
	public SensorReader(Context ctx, long millisDuration, SensorResultCallback cb) {
		if (cb == null) {
			throw new RuntimeException("SensorResultCallback can not be null");
		}

		if (ctx == null) {
			throw new RuntimeException("Context can not be null");
		}

		this.mThis = this;
		this.mCtx = ctx;
		this.mCallbackResult = cb;
		this.mRequiredMillisDuration = millisDuration;

		resetMemory();
		sm = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
	}

	/**
	 * Resets the sensor data buffer and last sensor values
	 */
	protected void resetMemory() {
		sensorValues = new float[3];
		sensorBuffer.clear();
		resetVariables();
		System.gc();
	}

	public void startReading() {
		resetMemory();
		dropOtherValues = false;
		registerSensors(mSensorCallback, sm);
		millisStartup = System.currentTimeMillis();
	}

	protected void updateSensorValues(float[] sensorValues) {
		if (dropOtherValues){
			// catch sensor readings coming from a very fast sensor
			return;
		}
		
		for (SensorFilter f : sensorFilters) {
			// apply possible filters
			sensorValues = f.filter(sensorValues, null); // call non inplace variant
		}

		if (mRequiredMillisDuration == -2) {
			// report last values once
			mCallbackResult.result(sensorValues);
			// and stop
			stopReading();
		} else if (mRequiredMillisDuration == -1) {
			// instant reporting
			mCallbackResult.result(sensorValues);
		} else if (mRequiredMillisDuration <= (System.currentTimeMillis() - millisStartup)) {
			// report last values after millisDuration
			mCallbackResult.result(sensorValues);
			// and stop
			stopReading();
		} else {
			// continue logging wait until running longer than millisDuration
			// i.e. do nothing
		}

		sensorBuffer.add(sensorValues);
	}

	/*
	 * Public access below
	 */

	public List<Sensor> getAvailableSensors() {
		return sm.getSensorList(Sensor.TYPE_ALL);
	}

	public void addFilter(SensorFilter f) {
		if (f == null) {
			throw new RuntimeException("SensorFilter cannot be null");
		}

		sensorFilters.add(f);
	}

	public void clearFilters() {
		sensorFilters.clear();
	}

	public void stopReading() {
		dropOtherValues = true;
		sm.unregisterListener(mSensorCallback);
	}

	/*
	 * Abstract methods
	 */

	protected abstract void registerSensors(SensorEventListener mSensorCallback, SensorManager sm);

	protected abstract void onSensorChanged(SensorEvent event);

	protected abstract void resetVariables();

}
