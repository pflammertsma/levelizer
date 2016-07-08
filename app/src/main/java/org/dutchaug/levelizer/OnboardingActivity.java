package org.dutchaug.levelizer;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Locale;

public class OnboardingActivity extends AppCompatActivity {

    private String mSensorName;

    private TextView mDebugInfoTextView;

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @SuppressWarnings("SimplifiableIfStatement")
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (mDebugInfoTextView != null) {
                boolean leveled;
                // check which axis we need to investigate (one has ~9.81 and has ~0)
                if (sensorEvent.values[1] > 2) {
                    leveled = Math.abs(sensorEvent.values[0]) < 0.5;
                } else if (sensorEvent.values[0] > 2) {
                    leveled = Math.abs(sensorEvent.values[1]) < 0.5;
                } else {
                    leveled = false;
                }
                mDebugInfoTextView.setText(String.format(Locale.US,
                        "%s\n\n[0]: %.6f\n[1]: %.6f\n[2]: %.6f\n\nLeveled: %b",
                        mSensorName == null ? "Unknown sensor" : mSensorName,
                        sensorEvent.values[0],
                        sensorEvent.values[1],
                        sensorEvent.values[2],
                        leveled));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        mDebugInfoTextView = (TextView) findViewById(R.id.sensor_info);
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (rotationSensor != null) {
                mSensorName = String.format("%s (%s)",
                        rotationSensor.getName(),
                        rotationSensor.getVendor());
                sensorManager.registerListener(mSensorEventListener, rotationSensor,
                        SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
        startService(new Intent(this, LevelizerService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensorManager.unregisterListener(mSensorEventListener);
        }
        stopService(new Intent(this, LevelizerService.class));
    }
}
