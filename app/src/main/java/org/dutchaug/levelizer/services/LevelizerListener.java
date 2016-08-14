package org.dutchaug.levelizer.services;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;

import org.dutchaug.levelizer.BuildConfig;

public abstract class LevelizerListener implements SensorEventListener {

    private static final String TAG = LevelizerListener.class.getSimpleName();

    private static final int GRAVITY_THRESHOLD = 2;

    private Context mContext;
    private int mRotation;

    float mR[] = new float[16];
    float mOrientation[] = new float[3];

    private float mTolerance;

    public LevelizerListener() {
    }

    public void start(@NonNull Context context, int rotation) {
        mContext = context;
        mRotation = rotation;
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            if (rotationSensor != null) {
                sensorManager.registerListener(this, rotationSensor,
                                               SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                // TODO show an error??
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
        if (BuildConfig.DEBUG_LOG) {
            // FIXME display less frequently, say once a second
            Log.d(TAG, String.format("%.4f, %.4f, %.4f", event.values[0], event.values[1], event.values[2]));
        }
        // It is good practice to check that we received the proper sensor event
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // Convert the rotation-vector to a 4x4 matrix.
            SensorManager.getRotationMatrixFromVector(mR,
                                                      event.values);
            switch (mRotation) {
                case Surface.ROTATION_0:
                    SensorManager.remapCoordinateSystem(mR,
                                                        SensorManager.AXIS_X,
                                                        SensorManager.AXIS_Z,
                                                        mR);
                    break;
                case Surface.ROTATION_90:
                    SensorManager.remapCoordinateSystem(mR,
                                                        SensorManager.AXIS_Z,
                                                        SensorManager.AXIS_MINUS_X,
                                                        mR);
                    break;
                case Surface.ROTATION_180:
                    SensorManager.remapCoordinateSystem(mR,
                                                        SensorManager.AXIS_MINUS_X,
                                                        SensorManager.AXIS_MINUS_Z,
                                                        mR);
                    break;
                case Surface.ROTATION_270:
                    SensorManager.remapCoordinateSystem(mR,
                                                        SensorManager.AXIS_MINUS_Z,
                                                        SensorManager.AXIS_X,
                                                        mR);
                    break;
            }
            SensorManager.getOrientation(mR, mOrientation);

            // Optionally convert the result from radians to degrees
            double yaw = Math.toDegrees(mOrientation[0]);
            double pitch = Math.toDegrees(mOrientation[1]);
            double roll = Math.toDegrees(mOrientation[2]);
            if (pitch > 70 || pitch < -70) {
                onOrientationUnreliable();
            } else {
                onOrientation(yaw, pitch, roll);
                double amountOffLevel = roll % 90;
                onOrientation(amountOffLevel);
                if (Math.abs(amountOffLevel) < mTolerance) {
                    onLevel();
                } else {
                    onUnlevel();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void setTolerance(float tolerance) {
        mTolerance = tolerance;
    }

    protected abstract void onOrientation(double yaw, double pitch, double roll);

    protected abstract void onOrientation(double angleOffLevel);

    protected abstract void onOrientationUnreliable();

    protected abstract void onLevel();

    protected abstract void onUnlevel();

}
