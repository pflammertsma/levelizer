package org.dutchaug.levelizer;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.util.Arrays;
import java.util.List;


public class CameraDetectionService extends AccessibilityService {

    private static final String TAG = CameraDetectionService.class.getSimpleName();
    private static final String[] CAMERA_APPS_ARRAY = new String[]{
            "com.motorola.camera",
            "com.google.vr.cyclops",
            "com.flavionet.android.camera.pro",
            "com.lge.camera",
            "net.sourceforge.opencamera",
            "com.google.android.GoogleCamera"
            };
    private static final List<String> CAMERA_APPS_LIST = Arrays.asList(CAMERA_APPS_ARRAY);

    /**
     * It'd be nice if this worked, but we can't detect when the camera app is closed.
     */
    private static final boolean FILTER_WITH_CAMERA_WHITELIST = false;
    private String mLastPackageName;


    public static List<String> getCameraApps() {
        return CAMERA_APPS_LIST;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        String packageName = null;
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                packageName = event.getPackageName().toString();
                break;
        }

        Log.d(TAG, "onAccessibilityEvent: " + event);

        if (packageName != null) {
            if (packageName.equals("com.android.systemui")) {
                // Ignore when the user navigates to the SystemUI
                pauseLeveler();
                return;
            } else if (mLastPackageName != null && mLastPackageName.equals(packageName)) {
                // Ignore when the same app returns; e.g. when the user opens the notification tray
                resumeLeveler();
                return;
            } else if (CAMERA_APPS_LIST.contains(packageName)) {
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
    public void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();

        // Set the type of events that this service wants to listen to.  Others
        // won't be passed to this service.
        info.eventTypes = AccessibilityEvent.TYPE_WINDOWS_CHANGED
                | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

        if (FILTER_WITH_CAMERA_WHITELIST) {
            // If you only want this service to work with specific applications, set their
            // package names here.  Otherwise, when the service is activated, it will listen
            // to events from all applications.
            info.packageNames = CAMERA_APPS_ARRAY;
        }

        // Set the type of feedback your service will provide.
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;

        // Default services are invoked only if no package-specific ones are present
        // for the type of AccessibilityEvent generated.  This service *is*
        // application-specific, so the flag isn't necessary.  If this was a
        // general-purpose service, it would be worth considering setting the
        // DEFAULT flag.

        // info.flags = AccessibilityServiceInfo.DEFAULT;

        info.notificationTimeout = 100;

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