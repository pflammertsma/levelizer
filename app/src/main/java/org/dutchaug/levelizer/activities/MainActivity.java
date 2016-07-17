package org.dutchaug.levelizer.activities;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;

import org.dutchaug.levelizer.R;
import org.dutchaug.levelizer.services.CameraDetectionService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.tv_service)
    TextView mTvService;

    @BindView(R.id.bt_service)
    Button mBtService;

    @BindView(R.id.tv_toggle)
    TextView mTvToggle;

    @BindView(R.id.bt_toggle)
    Button mBtToggle;

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
        setContentView(R.layout.activity_main);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.onboarding, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkAccessibilityStatus() {
        AccessibilityManager accessibilityService = (AccessibilityManager)
                getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (accessibilityService != null) {
            List<AccessibilityServiceInfo> infos =
                    accessibilityService.getEnabledAccessibilityServiceList(
                            AccessibilityServiceInfo.FEEDBACK_HAPTIC);

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

    private void onAccessibilityStatus(boolean serviceEnabled) {
        Log.d(TAG, "accessibility service " + (serviceEnabled ? "enabled" : "disabled"));
        mBtService.setText(serviceEnabled ? R.string.onboarding_all_done : R.string.enable);
        mBtService.setEnabled(!serviceEnabled);
        if (serviceEnabled) {
            mTvService.setVisibility(View.GONE);
            mBtService.setVisibility(View.GONE);
            if (Prefs.getBoolean(CameraDetectionService.PREF_ENABLED, true)) {
                mTvToggle.setText("Levelizer is currently enabled.");
                mBtToggle.setText("Disable");
            } else {
                mTvToggle.setText("Levelizer is currently disabled.");
                mBtToggle.setText("Enable");
            }
            mTvToggle.setVisibility(View.VISIBLE);
            mBtToggle.setVisibility(View.VISIBLE);
        } else {
            mTvService.setVisibility(View.VISIBLE);
            mBtService.setVisibility(View.VISIBLE);
            mTvToggle.setVisibility(View.GONE);
            mBtToggle.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.bt_service)
    protected void onClickService() {
        Intent i = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @OnClick(R.id.bt_toggle)
    protected void onClickToggle() {
        boolean enabled = !Prefs.getBoolean(CameraDetectionService.PREF_ENABLED, true);
        Prefs.putBoolean(CameraDetectionService.PREF_ENABLED, enabled);
        CameraDetectionService.notifyStateChange(this);
        checkAccessibilityStatus();
    }

    @OnClick(R.id.bt_whitelist)
    protected void onClickWhitelist() {
        startActivity(new Intent(getApplicationContext(), WhitelistActivity.class));
    }

}
