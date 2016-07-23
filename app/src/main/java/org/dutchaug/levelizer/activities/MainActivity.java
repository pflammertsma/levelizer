package org.dutchaug.levelizer.activities;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;

import org.dutchaug.levelizer.R;
import org.dutchaug.levelizer.fragments.InstructionsFragment;
import org.dutchaug.levelizer.services.CameraDetectionService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements InstructionsFragment.Callback {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String KEY_SHOW_SUCCESS = "show_success";
    private static final String KEY_SHOW_ERROR = "show_error";

    public static final String EXTRA_SHOW_SUCCESS = "show_success";

    @BindView(R.id.cv_service)
    protected CardView mCvService;

    @BindView(R.id.tv_service)
    protected TextView mTvService;

    @BindView(R.id.bt_service)
    protected Button mBtService;

    @BindView(R.id.cv_overlay)
    protected CardView mCvOverlay;

    @BindView(R.id.tv_overlay)
    protected TextView mTvOverlay;

    @BindView(R.id.bt_overlay)
    protected Button mBtOverlay;

    @BindView(R.id.sw_overlay_arrows)
    protected SwitchCompat mSwOverlayArrows;

    @BindView(R.id.sw_overlay_toggle)
    protected SwitchCompat mSwOverlayToggle;

    @BindView(R.id.tv_toggle)
    protected TextView mTvToggle;

    @BindView(R.id.bt_toggle)
    protected Button mBtToggle;

    private boolean mShowSuccess = false;
    private boolean mShowError = true;

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            updateStatus();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            mShowSuccess = intent.getBooleanExtra(EXTRA_SHOW_SUCCESS, mShowSuccess);
        }
        if (savedInstanceState != null) {
            mShowSuccess = savedInstanceState.getBoolean(KEY_SHOW_SUCCESS, true);
            mShowError = savedInstanceState.getBoolean(KEY_SHOW_ERROR, true);
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateStatus();
        mHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_SHOW_SUCCESS, mShowSuccess);
        outState.putBoolean(KEY_SHOW_ERROR, mShowError);
        super.onSaveInstanceState(outState);
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

    private void updateStatus() {
        checkAccessibilityStatus();
        checkOverlayStatus();
    }

    private void checkAccessibilityStatus() {
        AccessibilityManager accessibilityService = (AccessibilityManager)
                getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (accessibilityService != null) {
            List<AccessibilityServiceInfo> services =
                    accessibilityService.getEnabledAccessibilityServiceList(
                            AccessibilityServiceInfo.FEEDBACK_HAPTIC);

            boolean enabled = false;
            if (services != null) {
                for (AccessibilityServiceInfo service : services) {
                    if (service.getId().contains(CameraDetectionService.class.getSimpleName())) {
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
        if (serviceEnabled) {
            Animation animation = mCvService.getAnimation();
            if (animation != null) {
                animation.cancel();
            }
            if (mShowSuccess) {
                mShowSuccess = false;
                new AlertDialog.Builder(this, R.style.DialogStyle)
                        .setView(R.layout.fragment_success)
                        .setPositiveButton(R.string.instructions_great, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
            mTvService.setVisibility(View.GONE);
            mBtService.setVisibility(View.GONE);
            if (Prefs.getBoolean(CameraDetectionService.PREF_ENABLED, true)) {
                mTvToggle.setText(R.string.status_enabled);
                mBtToggle.setText(R.string.disable);
            } else {
                mTvToggle.setText(R.string.status_disabled);
                mBtToggle.setText(R.string.enable);
            }
            mTvToggle.setVisibility(View.VISIBLE);
            mBtToggle.setVisibility(View.VISIBLE);
        } else {
            if (mShowError) {
                mShowError = false;
                // Show animation
                Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                mCvService.startAnimation(shake);
            }
            mTvService.setVisibility(View.VISIBLE);
            mBtService.setVisibility(View.VISIBLE);
            mTvToggle.setVisibility(View.GONE);
            mBtToggle.setVisibility(View.GONE);
        }
    }

    private void checkOverlayStatus() {
        onOverlayStatus(Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)));
    }

    private void onOverlayStatus(boolean canDrawOverlays) {
        if (canDrawOverlays) {
            mTvOverlay.setVisibility(View.GONE);
            mBtOverlay.setVisibility(View.GONE);
            mSwOverlayArrows.setVisibility(View.VISIBLE);
            mSwOverlayToggle.setVisibility(View.VISIBLE);
        } else {
            mTvOverlay.setVisibility(View.VISIBLE);
            mBtOverlay.setVisibility(View.VISIBLE);
            mSwOverlayArrows.setVisibility(View.GONE);
            mSwOverlayToggle.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.bt_service)
    protected void onClickService() {
        FragmentManager fm = getSupportFragmentManager();
        InstructionsFragment dialog = InstructionsFragment.create();
        dialog.show(fm, InstructionsFragment.TAG);
    }

    @OnClick(R.id.bt_overlay)
    protected void onClickOverlay() {
        Intent settingsIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                           Uri.parse("package:" + getPackageName()));
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(settingsIntent);
    }

    @OnClick(R.id.bt_toggle)
    protected void onClickToggle() {
        boolean enabled = !Prefs.getBoolean(CameraDetectionService.PREF_ENABLED, true);
        Prefs.putBoolean(CameraDetectionService.PREF_ENABLED, enabled);
        CameraDetectionService.notifyStateChange(this);
        updateStatus();
    }

    @OnClick(R.id.bt_whitelist)
    protected void onClickWhitelist() {
        startActivity(new Intent(getApplicationContext(), WhitelistActivity.class));
    }

    @Override
    public void onEnterAccessibility() {
        mShowError = true;
        mShowSuccess = true;
        Prefs.putBoolean(CameraDetectionService.PREF_FIRST_RESPONSE, true);
        Prefs.putBoolean(CameraDetectionService.PREF_ENABLED, true);
    }

}
