package org.dutchaug.levelizer.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.pixplicity.easyprefs.library.Prefs;

import org.dutchaug.levelizer.util.WhitelistManager;

import java.util.Set;


public class CameraDetectionService extends AccessibilityService {

    public static final String ACTION_STATE_CHANGE = "state_change";
    public static final String PREF_ENABLED = "enabled";

    private static final String TAG = CameraDetectionService.class.getSimpleName();

    private static final String PACKAGE_SYSTEMUI = "com.android.systemui";

    private String mLastPackageName;

    private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent.getAction());
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                if (mLastPackageName != null && !mLastPackageName.equals(PACKAGE_SYSTEMUI)) {
                    // Unregister the last app
                    mLastPackageName = null;
                }
                stopLevelizer();
            }
        }
    };

    private BroadcastReceiver mStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent.getAction());
            if (intent.getAction().equals(ACTION_STATE_CHANGE)) {
                // Enable or disable this accessibility service
                setServiceInfo();
            }
        }
    };


    public static void notifyStateChange(Context context) {
        context.sendBroadcast(new Intent(CameraDetectionService.ACTION_STATE_CHANGE));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(mScreenOffReceiver, filter);
        }
        {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_STATE_CHANGE);
            registerReceiver(mStateReceiver, filter);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent: " + event);

        final int eventType = event.getEventType();
        String packageName = null;
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                CharSequence pn = event.getPackageName();
                if (pn != null) {
                    packageName = pn.toString();
                }
                break;
        }

        if (packageName != null) {
            if (packageName.equals(PACKAGE_SYSTEMUI)) {
                // Ignore when the user navigates to the SystemUI
                pauseLeveler();
                return;
            } else if (mLastPackageName != null && mLastPackageName.equals(packageName)) {
                // Ignore when the same app returns; e.g. when the user opens the notification tray
                resumeLeveler();
                return;
            } else if (WhitelistManager.get(this).contains(packageName)) {
                Log.d(TAG, "camera app: " + packageName);
                startLevelizer();
            } else {
                Log.d(TAG, "not a camera app: " + packageName);
                stopLevelizer();
            }
            mLastPackageName = packageName;
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mStateReceiver);
        unregisterReceiver(mScreenOffReceiver);
        super.onDestroy();
    }

    @Override
    public void onServiceConnected() {
        setServiceInfo();
    }

    private void setServiceInfo() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();

        // Set the type of feedback your service will provide.
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_HAPTIC;

        if (Prefs.getBoolean(CameraDetectionService.PREF_ENABLED, true)) {
            // Set the type of events that this service wants to listen to.  Others
            // won't be passed to this service.
            info.eventTypes = AccessibilityEvent.TYPE_WINDOWS_CHANGED
                    | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

            // Default services are invoked only if no package-specific ones are present
            // for the type of AccessibilityEvent generated.  This service *is*
            // application-specific, so the flag isn't necessary.  If this was a
            // general-purpose service, it would be worth considering setting the
            // DEFAULT flag.

            // info.flags = AccessibilityServiceInfo.DEFAULT;

            info.notificationTimeout = 100;
        }

        setServiceInfo(info);
    }

    private void startLevelizer() {
        Intent serviceIntent = new Intent(this, LevelizerService.class);
        startService(serviceIntent);
    }

    private void stopLevelizer() {
        Intent serviceIntent = new Intent(this, LevelizerService.class);
        stopService(serviceIntent);
    }

    private void pauseLeveler() {
        // TODO instruct LevelizerService to pause vibration
    }

    private void resumeLeveler() {
        // TODO instruct LevelizerService to resume vibration
    }

}
