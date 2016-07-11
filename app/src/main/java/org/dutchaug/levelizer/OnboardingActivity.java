package org.dutchaug.levelizer;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OnboardingActivity extends AppCompatActivity {

    private static final String TAG = OnboardingActivity.class.getSimpleName();

    @BindView(R.id.onboarding_enable_btn)
    Button mEnableButton;

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            checkAccessibilityStatus();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAccessibilityStatus();
        mHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    protected void onStop() {
        mHandler.removeCallbacks(mRunnable);
        super.onStop();
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

            onAccessibilityStatus(enabled);
        } else {
            onAccessibilityStatus(false);
        }
    }

    private void onAccessibilityStatus(boolean enabled) {
        Log.d(TAG, "accessibility service " + (enabled ? "enabled" : "disabled"));
        mEnableButton.setText(enabled ? R.string.onboarding_all_done : R.string.enable);
        mEnableButton.setEnabled(!enabled);
    }

    @OnClick(R.id.onboarding_enable_btn)
    protected void onClickEnable() {
        Intent i = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @OnClick(R.id.onboarding_whitelist_btn)
    protected void onClickWhitelist() {
        startActivity(new Intent(getApplicationContext(), WhitelistActivity.class));
    }

}
