package org.dutchaug.levelizer.activities;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.dutchaug.levelizer.R;
import org.dutchaug.levelizer.fragments.InstructionsFragment;
import org.dutchaug.levelizer.services.CameraDetectionService;
import org.dutchaug.levelizer.util.VibrationWrapper;

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

    @BindView(R.id.sw_toggle)
    protected SwitchCompat mSwToggle;

    @BindView(R.id.vg_vibration)
    protected ViewGroup mVgVibration;

    @BindView(R.id.tv_vibration)
    protected TextView mTvVibration;

    @BindView(R.id.dsb_vibration)
    protected DiscreteSeekBar mDsbVibration;

    @BindView(R.id.vg_tolerance)
    protected ViewGroup mVgTolerance;

    @BindView(R.id.tv_tolerance)
    protected TextView mTvTolerance;

    @BindView(R.id.dsb_tolerance)
    protected DiscreteSeekBar mDsbTolerance;

    private boolean mShowSuccess = false;
    private boolean mShowError = true;

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            checkAccessibilityStatus();
        }
    };

    private VibrationWrapper mVibrationWrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            mShowSuccess = intent.getBooleanExtra(EXTRA_SHOW_SUCCESS, mShowSuccess);
        }
        if (savedInstanceState != null) {
            mShowSuccess = savedInstanceState.getBoolean(KEY_SHOW_SUCCESS, true);
            mShowError = savedInstanceState.getBoolean(KEY_SHOW_ERROR, true);
        }

        // For sampling vibrations
        mVibrationWrapper = new VibrationWrapper(this);

        int vibrationStrength = Prefs.getInt(CameraDetectionService.PREF_VIBRATION, 1);
        mDsbVibration.setProgress(vibrationStrength);
        mDsbVibration.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {

            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                onChangeVibration(value, true);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
            }

        });
        onChangeVibration(vibrationStrength, false);

        int tolerance = Prefs.getInt(CameraDetectionService.PREF_TOLERANCE, 3);
        mDsbTolerance.setProgress(tolerance);
        mDsbTolerance.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {

            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                onChangeTolerance(value, true);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
            }

        });
        onChangeTolerance(tolerance, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAccessibilityStatus();
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
        mVibrationWrapper.stop();
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
        mBtService.setText(serviceEnabled ? R.string.onboarding_all_done : R.string.enable_service);
        mBtService.setEnabled(!serviceEnabled);
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
            boolean enabled = Prefs.getBoolean(CameraDetectionService.PREF_ENABLED, true);
            float alpha = enabled ? 1f : 0.4f;
            mSwToggle.setChecked(enabled);
            mTvVibration.setAlpha(alpha);
            mDsbVibration.setEnabled(enabled);
            mDsbVibration.setAlpha(alpha);
            mTvTolerance.setAlpha(alpha);
            mDsbTolerance.setEnabled(enabled);
            mDsbTolerance.setAlpha(alpha);
            mSwToggle.setVisibility(View.VISIBLE);
            mVgVibration.setVisibility(View.VISIBLE);
            mVgTolerance.setVisibility(View.VISIBLE);
        } else {
            if (mShowError) {
                mShowError = false;
                // Show animation
                Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                mCvService.startAnimation(shake);
            }
            mTvService.setVisibility(View.VISIBLE);
            mBtService.setVisibility(View.VISIBLE);
            mSwToggle.setVisibility(View.GONE);
            mVgVibration.setVisibility(View.GONE);
            mVgTolerance.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.bt_service)
    protected void onClickService() {
        FragmentManager fm = getSupportFragmentManager();
        InstructionsFragment dialog = InstructionsFragment.create();
        dialog.show(fm, InstructionsFragment.TAG);
    }

    @OnClick(R.id.sw_toggle)
    protected void onClickToggle() {
        boolean enabled = !Prefs.getBoolean(CameraDetectionService.PREF_ENABLED, true);
        Prefs.putBoolean(CameraDetectionService.PREF_ENABLED, enabled);
        CameraDetectionService.notifyStateChange(this);
        checkAccessibilityStatus();
    }

    private void onChangeVibration(int value, boolean fromUi) {
        int vibrationStrength;
        switch (value) {
            default:
            case 0:
                vibrationStrength = R.string.vibration_0;
                value = 0;
                break;
            case 1:
                vibrationStrength = R.string.vibration_1;
                break;
            case 2:
                vibrationStrength = R.string.vibration_2;
                break;
            case 3:
                vibrationStrength = R.string.vibration_3;
                break;
        }
        mTvVibration.setText(getString(R.string.vibration_value, getString(vibrationStrength)));
        int oldValue = Prefs.getInt(CameraDetectionService.PREF_VIBRATION, 1);
        if (oldValue != value) {
            Prefs.putInt(CameraDetectionService.PREF_VIBRATION, value);
            CameraDetectionService.notifyStateChange(MainActivity.this);
        }
        mVibrationWrapper.setVibrationStrength(value);
        if (fromUi) {
            mVibrationWrapper.sample();
        }
    }

    private void onChangeTolerance(int value, boolean fromUi) {
        if (value < 1) {
            value = 1;
        }
        if (value > 10) {
            value = 10;
        }
        mTvTolerance.setText(getString(R.string.tolerance_value, value));
        int oldValue = Prefs.getInt(CameraDetectionService.PREF_TOLERANCE, 3);
        if (oldValue != value) {
            Prefs.putInt(CameraDetectionService.PREF_TOLERANCE, value);
            CameraDetectionService.notifyStateChange(MainActivity.this);
        }
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
