package org.dutchaug.levelizer;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private TextView mStatusTextView;
    private Button mEnableButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        mEnableButton = (Button) findViewById(R.id.onboarding_enable_btn);
        mEnableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(i);
            }
        });

        findViewById(R.id.onboarding_whitelist_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), WhitelistActivity.class);
                        startActivity(i);
                    }
                });

        mStatusTextView = (TextView) findViewById(R.id.onboarding_status);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAccessibilityStatus();
    }

    private void checkAccessibilityStatus() {
        AccessibilityManager accessibilityService = (AccessibilityManager)
                getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (accessibilityService != null) {
            List<AccessibilityServiceInfo> infos =
                    accessibilityService.getEnabledAccessibilityServiceList(
                            AccessibilityServiceInfo.FEEDBACK_SPOKEN);

            boolean enabled = false;
            if (infos != null) {
                for (AccessibilityServiceInfo info : infos) {
                    if (info.getId().contains(CameraDetectionService.class.getSimpleName())) {
                        enabled = true;
                        break;
                    }
                }
            }

            if (!enabled) {
                handleAccessibilityDisabled();
            } else {
                handleAccessibilityEnabled();
            }
        } else {
            handleAccessibilityDisabled();
        }
    }

    private void handleAccessibilityEnabled() {
        Log.d(OnboardingActivity.class.getSimpleName(), "AS enabled");
        mStatusTextView.setText(getString(R.string.status) + " " + getString(R.string.enabled));
        mEnableButton.setText(R.string.onboarding_all_done);
        mEnableButton.setEnabled(false);
    }

    private void handleAccessibilityDisabled() {
        Log.d(OnboardingActivity.class.getSimpleName(), "AS disabled");
        mStatusTextView.setText(getString(R.string.status) + " " + getString(R.string.disabled));
        mEnableButton.setText(R.string.enable);
        mEnableButton.setEnabled(true);
    }

}
