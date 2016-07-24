package org.dutchaug.levelizer.util;

import android.content.Context;
import android.os.Vibrator;

import com.pixplicity.easyprefs.library.Prefs;

import org.dutchaug.levelizer.services.CameraDetectionService;

public class VibrationWrapper {

    private final Context mContext;
    private Vibrator mVibrator;

    private boolean mIsVibrating;
    private int mVibrationDuration;
    private int mVibrationPause;

    public VibrationWrapper(Context context) {
        mContext = context;
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        setVibrationStrength(Prefs.getInt(CameraDetectionService.PREF_VIBRATION, 1));
    }

    public void setVibrationStrength(int value) {
        switch (value) {
            default:
            case 0:
                mVibrationDuration = 0;
                mVibrationPause = 10000;
                break;
            case 1:
                mVibrationDuration = 20;
                mVibrationPause = 300;
                break;
            case 2:
                mVibrationDuration = 40;
                mVibrationPause = 400;
                break;
            case 3:
                mVibrationDuration = 80;
                mVibrationPause = 500;
                break;
        }
    }

    public void sample() {
        start(false);
    }

    public void start() {
        start(true);
    }

    private void start(boolean repeat) {
        if (mVibrationDuration == 0) {
            return;
        }
        /* TODO we need to change the vibration frequency dynamically, but this isn't so easy :)
        if (mIsVibrating) {
            mVibrator.cancel();
        }
        int mVibrationPause = (int) Math.max(20, (FREQUENCY_HIGH * Math.min(1f, amountOffLevel / 3f)));
        */
        if (!mIsVibrating) {
            mIsVibrating = repeat;
            mVibrator.vibrate(new long[]{0,
                                         mVibrationDuration, mVibrationPause,
                                         mVibrationDuration, mVibrationPause},
                              repeat ? 0 : -1);
        }
    }

    public void stop() {
        mIsVibrating = false;
        mVibrator.cancel();
    }

}
