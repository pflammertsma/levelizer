package org.dutchaug.levelizer.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;

import org.dutchaug.levelizer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.tv_version)
    protected TextView mTvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        Answers.getInstance().logContentView(new ContentViewEvent().putContentName("about"));

        setTitle(R.string.about_title);

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            mTvVersion.setText(getString(R.string.version, packageInfo.versionName, packageInfo.versionCode));
        } catch (PackageManager.NameNotFoundException e) {
            mTvVersion.setText("—");
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.vg_about_paul)
    protected void onClickAboutPaul() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/pflammertsma"));
        startActivity(intent);
    }

    @OnClick(R.id.vg_about_martin)
    protected void onClickAboutMartin() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Goddchen"));
        startActivity(intent);
    }

    @OnClick(R.id.vg_about_frank)
    protected void onClickAboutFrank() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/frankkienl"));
        startActivity(intent);
    }

    @OnClick(R.id.vg_about_butter_knife)
    protected void onClickAboutButterKnife() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/JakeWharton/butterknife"));
        startActivity(intent);
    }

    @OnClick(R.id.vg_about_easyprefs)
    protected void onClickAboutEasyPrefs() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Pixplicity/EasyPreferences"));
        startActivity(intent);
    }

}
