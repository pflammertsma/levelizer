package org.dutchaug.levelizer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class LevelizerService extends Service {

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @SuppressWarnings("SimplifiableIfStatement")
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            boolean leveled;
            // check which axis we need to investigate (one has ~9.81 and has ~0)
            if (sensorEvent.values[1] > 2) {
                leveled = Math.abs(sensorEvent.values[0]) < 0.5;
            } else if (sensorEvent.values[0] > 2) {
                leveled = Math.abs(sensorEvent.values[1]) < 0.5;
            } else {
                leveled = false;
            }
            Log.d(LevelizerService.class.getSimpleName(), String.format("Leveled: %b", leveled));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Levelizer started", Toast.LENGTH_SHORT).show();
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (rotationSensor != null) {
                sensorManager.registerListener(mSensorEventListener, rotationSensor,
                        SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Levelizer stopped", Toast.LENGTH_SHORT).show();
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensorManager.unregisterListener(mSensorEventListener);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
