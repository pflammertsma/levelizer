package org.dutchaug.levelizer;

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
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

public class LevelizerService extends Service {

    private static final int REQUEST_CODE = 1000;
    public static final int NOTIFICATION_ID = 1;
    private static final String EXTRA_STOP = "stop";

    private boolean mIsVibrating = false;

    private Vibrator mVibrator;

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @SuppressWarnings("SimplifiableIfStatement")
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            boolean leveled;
            // check which axis we need to investigate (one has ~9.81 and has ~0)
            if (sensorEvent.values[1] > 2) {
                leveled = Math.abs(sensorEvent.values[0]) < 0.7;
            } else if (sensorEvent.values[0] > 2) {
                leveled = Math.abs(sensorEvent.values[1]) < 0.7;
            } else {
                leveled = false;
            }
            Log.d(LevelizerService.class.getSimpleName(), String.format("Leveled: %b", leveled));
            if (!leveled) {
                if (!mIsVibrating) {
                    mIsVibrating = true;
                    mVibrator.vibrate(new long[]{0, 200, 200}, 0);
                }
            } else {
                mIsVibrating = false;
                mVibrator.cancel();
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

        Toast.makeText(this, "Levelizer started", Toast.LENGTH_SHORT).show();
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
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

        // Cancel the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(NOTIFICATION_ID);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensorManager.unregisterListener(mSensorEventListener);
        }

        if (mVibrator != null) {
            mVibrator.cancel();
        }

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
