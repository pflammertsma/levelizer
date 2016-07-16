package org.dutchaug.levelizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        setTitle(R.string.about_title);

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
