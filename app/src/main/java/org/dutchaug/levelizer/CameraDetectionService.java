package org.dutchaug.levelizer;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
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
            };
    private static final List<String> CAMERA_APPS_LIST = Arrays.asList(CAMERA_APPS_ARRAY);

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
            if (CAMERA_APPS_LIST.contains(packageName)) {
                Log.d(TAG, "camera app: " + packageName);
                startLevelizer();
            } else {
                Log.d(TAG, "not a camera app: " + packageName);
                stopLevelizer();
            }
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
                | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;

        // If you only want this service to work with specific applications, set their
        // package names here.  Otherwise, when the service is activated, it will listen
        // to events from all applications.
        info.packageNames = CAMERA_APPS_ARRAY;

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
    }

    private void stopLevelizer() {
    }

}
