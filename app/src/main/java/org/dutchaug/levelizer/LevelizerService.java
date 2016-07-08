package org.dutchaug.levelizer;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;


public class LevelizerService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        String eventText = null;
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                eventText = "Focused: ";
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                eventText = "Focused: ";
                break;
        }

        eventText = eventText + event.getContentDescription();

        Toast.makeText(getApplicationContext(), eventText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();

        // Set the type of events that this service wants to listen to.  Others
        // won't be passed to this service.
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED |
                AccessibilityEvent.TYPE_VIEW_FOCUSED;

        // If you only want this service to work with specific applications, set their
        // package names here.  Otherwise, when the service is activated, it will listen
        // to events from all applications.
        if (false) {
            info.packageNames = new String[]
                    {"com.example.android.myFirstApp", "com.example.android.mySecondApp"};
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

}
