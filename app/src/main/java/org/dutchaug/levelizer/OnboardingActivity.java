package org.dutchaug.levelizer;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OnboardingActivity extends AppCompatActivity {

    TextView mStatusTextView;
    Button mWhitelistButton;
    Button mEnableButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        mEnableButton = (Button) findViewById(R.id.onboarding_enable_btn);
        mEnableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                //startActivityForResult(i, 1337);
                startActivity(i);
            }
        });

        mWhitelistButton = (Button) findViewById(R.id.onboarding_whitelist_btn);
        mWhitelistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), WhitelistActivity.class);
                startActivity(i);
            }
        });

        mStatusTextView = (TextView) findViewById(R.id.onboarding_status);

    }

    @Override
    protected void onResume() {
        super.onResume();

        checkEnabled();
    }

    public void checkEnabled() {
        boolean isEnabled = true;

        if (isEnabled) {
            mStatusTextView.setText(getString(R.string.status) + " " + getString(R.string.enabled));
            mEnableButton.setText(R.string.onboarding_all_done);
            mEnableButton.setEnabled(false);
        } else {
            mStatusTextView.setText(getString(R.string.status) + " " + getString(R.string.disabled));
            mEnableButton.setText(R.string.enable);
            mEnableButton.setEnabled(true);
        }
    }
}
