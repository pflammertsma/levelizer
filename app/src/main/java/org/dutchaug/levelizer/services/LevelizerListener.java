package org.dutchaug.levelizer.services;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.Log;

import org.dutchaug.levelizer.BuildConfig;

public abstract class LevelizerListener implements SensorEventListener {

    private static final String TAG = LevelizerListener.class.getSimpleName();

    private static final int GRAVITY_THRESHOLD = 2;

    private float mTolerance;

    private float[] mGravity, mGeomagnetic;

    public LevelizerListener() {
    }

    public void start(@NonNull Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (rotationSensor != null) {
                sensorManager.registerListener(this, rotationSensor,
                                               SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                // TODO show an error??
            }
            Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            if (magneticSensor != null) {
                sensorManager.registerListener(this, magneticSensor,
                                               SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    public void stop(@NonNull Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public void onSensorChanged(SensorEvent event) {
        float amountOffLevel = 0f;
        if (BuildConfig.DEBUG_LOG) {
            // FIXME display less frequently, say once a second
            Log.d(TAG, String.format("%.4f, %.4f, %.4f", event.values[0], event.values[1], event.values[2]));
        }
        // check which axis we need to investigate (one has ~9.81 and has ~0)
        if (Math.abs(event.values[1]) > GRAVITY_THRESHOLD) {
            // This suggests axis with index 1 is pointing down
            amountOffLevel = Math.abs(event.values[0]);
        } else if (Math.abs(event.values[0]) > GRAVITY_THRESHOLD) {
            // This suggests axis with index 0 is pointing down
            amountOffLevel = Math.abs(event.values[1]);
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        float azimut = 0, pitch = 0, roll = 0;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                pitch = orientation[1];
                roll = orientation[2];
            }
        }
        boolean leveled = amountOffLevel < mTolerance;
        onOrientation(azimut, pitch, roll);
        if (leveled) {
            onLevel();
        } else {
            onUnlevel();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void setTolerance(float tolerance) {
        mTolerance = tolerance;
    }

    protected abstract void onOrientation(float azimut, float pitch, float roll);

    protected abstract void onLevel();

    protected abstract void onUnlevel();

}
