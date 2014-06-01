package de.uvwxy.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class TemperatureReader extends SensorReader{
    
    public TemperatureReader(Context ctx, long millisDuration, SensorResultCallback cb) {
        super(ctx, millisDuration, cb);
    }

    @Override
    protected void registerSensors(SensorEventListener mSensorCallback, SensorManager sm) {
        sm.registerListener(mSensorCallback, sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
        case Sensor.TYPE_AMBIENT_TEMPERATURE:
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
}
