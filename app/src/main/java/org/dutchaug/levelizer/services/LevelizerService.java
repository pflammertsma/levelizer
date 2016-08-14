package org.dutchaug.levelizer.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
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

    private static final String EXTRA_STOP = "stop";

    private VibrationWrapper mVibrationWrapper;

    private WindowManager mWindowManager;

    private ViewGroup mOverlayView;

    private LevelizerListener mLevelizerListener = new LevelizerListener() {

        @Override
        protected void onOrientation(float azimut, float pitch, float roll) {
            // Do nothing
        }

        @Override
        protected void onLevel() {
            mVibrationWrapper.stop();
        }

        @Override
        protected void onUnlevel() {
            mVibrationWrapper.start();
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

        mVibrationWrapper = new VibrationWrapper(this);

        mLevelizerListener.setTolerance(Prefs.getInt(CameraDetectionService.PREF_TOLERANCE, 3) * DEGREES_RATIO);
        mLevelizerListener.start(this);

        mOverlayView = new FrameLayout(this);
        TextView textView = new TextView(this);
        textView.setText("Hello world");
        textView.setTextColor(Color.WHITE);
        mOverlayView.addView(textView);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this))) {
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mOverlayView, params);
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

        mLevelizerListener.stop(this);

        if (mWindowManager != null) {
            mWindowManager.removeView(mOverlayView);
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
