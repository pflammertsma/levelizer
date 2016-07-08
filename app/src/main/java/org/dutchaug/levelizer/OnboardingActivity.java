package org.dutchaug.levelizer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class OnboardingActivity extends AppCompatActivity {

    private String mSensorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        /*final TextView debugInfoTextView = (TextView) findViewById(R.id.sensor_info);
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            if (rotationSensor != null) {
                mSensorName = String.format("%s (%s)",
                        rotationSensor.getName(),
                        rotationSensor.getVendor());
                sensorManager.registerListener(new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent sensorEvent) {
                        debugInfoTextView.setText(String.format("%s\n\n",
                                mSensorName == null ? "Unknown sensor" : mSensorName));
                        sensorEvent.
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int i) {

                    }
                }, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }*/
    }
}
