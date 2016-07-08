package org.dutchaug.levelizer;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        Button enableButton = (Button) findViewById(R.id.onboarding_enable_btn);
        enableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                //startActivityForResult(i, 1337);
                startActivity(i);
            }
        });

        Button whitelisting = (Button) findViewById(R.id.onboarding_whitelist_btn);

    }
}
