package org.dutchaug.levelizer.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.pixplicity.easyprefs.library.Prefs;

import org.dutchaug.levelizer.BuildConfig;
import org.dutchaug.levelizer.R;
import org.dutchaug.levelizer.util.VibrationWrapper;

public class LevelizerService extends Service {

    private static final String TAG = LevelizerService.class.getSimpleName();

    private static final int REQUEST_CODE = 1000;
    private static final int NOTIFICATION_ID = 1;

    private static final float DEGREES_RATIO = 0.2f;
    private static final int GRAVITY_THRESHOLD = 2;

    private static final String EXTRA_STOP = "stop";

    private float mTolerance;

    private VibrationWrapper mVibrationWrapper;

    private SensorEventListener mSensorEventListener = new SensorEventListener() {

        @SuppressWarnings("SimplifiableIfStatement")
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float amountOffLevel = 0f;
            if (BuildConfig.DEBUG_LOG) {
                // FIXME display less frequently, say once a second
                Log.d(TAG, String.format("%.4f, %.4f, %.4f", sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]));
            }
            // check which axis we need to investigate (one has ~9.81 and has ~0)
            if (Math.abs(sensorEvent.values[1]) > GRAVITY_THRESHOLD) {
                // This suggests axis with index 1 is pointing down
                amountOffLevel = Math.abs(sensorEvent.values[0]);
            } else if (Math.abs(sensorEvent.values[0]) > GRAVITY_THRESHOLD) {
                // This suggests axis with index 0 is pointing down
                amountOffLevel = Math.abs(sensorEvent.values[1]);
            }
            boolean leveled = amountOffLevel < mTolerance;
            if (!leveled) {
                mVibrationWrapper.start();
            } else {
                mVibrationWrapper.stop();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getBooleanExtra(EXTRA_STOP, false)) {
            stopSelf();
            return START_NOT_STICKY;
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG && BuildConfig.DEBUG_SHOW_TOASTS) {
            Toast.makeText(this, "Levelizer started", Toast.LENGTH_SHORT).show();
        }

        // Display an ongoing notification
        Intent intent = new Intent(this, LevelizerService.class);
        intent.putExtra(EXTRA_STOP, true);
        PendingIntent pi = PendingIntent.getService(this, REQUEST_CODE,
                                                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentIntent(pi)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setSmallIcon(R.drawable.ic_notification)
                .setOngoing(true)
                .build();
        // Display the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, notification);

        // Start this service in the foreground on the notification
        startForeground(NOTIFICATION_ID, notification);

        mTolerance = Prefs.getInt(CameraDetectionService.PREF_TOLERANCE, 3) * DEGREES_RATIO;

        mVibrationWrapper = new VibrationWrapper(this);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (rotationSensor != null) {
                sensorManager.registerListener(mSensorEventListener, rotationSensor,
                                               SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                // TODO show an error??
            }
        }
    }

    @Override
    public void onDestroy() {
        if (BuildConfig.DEBUG && BuildConfig.DEBUG_SHOW_TOASTS) {
            Toast.makeText(this, "Levelizer stopped", Toast.LENGTH_SHORT).show();
        }

        // Cancel the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(NOTIFICATION_ID);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensorManager.unregisterListener(mSensorEventListener);
        }

        mVibrationWrapper.stop();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
