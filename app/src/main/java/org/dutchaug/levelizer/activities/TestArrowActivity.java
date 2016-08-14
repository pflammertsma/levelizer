package org.dutchaug.levelizer.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.dutchaug.levelizer.R;
import org.dutchaug.levelizer.services.LevelizerListener;
import org.dutchaug.levelizer.views.LevelView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestArrowActivity extends AppCompatActivity {

    @BindView(R.id.tv_values)
    protected TextView mTvValues;

    @BindView(R.id.lv_indicator)
    protected LevelView mLvIndicator;

    private LevelizerListener mLevelizerListener = new LevelizerListener() {

        @Override
        protected void onOrientation(double yaw, double pitch, double roll) {
            mTvValues.setText(String.format(Locale.getDefault(), "%f \t%f \t%f", yaw, pitch, roll));
        }

        @Override
        protected void onOrientation(double angleOffLevel) {
            mLvIndicator.setActive(true);
            mLvIndicator.setOrientation(-angleOffLevel);
        }

        @Override
        protected void onOrientationUnreliable() {
            mLvIndicator.setActive(false);
        }

        @Override
        protected void onLevel() {
            // Do nothing
        }

        @Override
        protected void onUnlevel() {
            // Do nothing
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_arrow);

        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLevelizerListener.start(this, getWindowManager().getDefaultDisplay().getRotation());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLevelizerListener.stop(this);
    }

}
