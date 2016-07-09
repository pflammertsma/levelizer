package org.dutchaug.levelizer;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class WhitelistActivity extends AppCompatActivity {

    ListView mListView;
    TextView mListEmpty;
    AppsListAdapter mAdapter;

    private PackageManager mPackageManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whitelist);
        setTitle(R.string.camera_whitelist);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mPackageManager = getPackageManager();

        mListView = (ListView) findViewById(android.R.id.list);
        mListEmpty = (TextView) findViewById(android.R.id.empty);
        mAdapter = new AppsListAdapter(this);

        List<String> whitelistPackageNames = new ArrayList<String>();
        //get defaults
        whitelistPackageNames.addAll(CameraDetectionService.getCameraApps());
        Set<String> packageNamesFromPreferences = Prefs.getOrderedStringSet("whitelist", null);
        if (packageNamesFromPreferences != null) {
            whitelistPackageNames.addAll(packageNamesFromPreferences);
        }
        mAdapter.setData(whitelistPackageNames);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String packageName = adapterView.getAdapter().getItem(i).toString();
                try {
                    //ApplicationInfo packageInfo = mPackageManager.getApplicationInfo(packageName,PackageManager.GET_META_DATA);
                    PackageInfo packageInfo = mPackageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                    //App is installed, launch it.
                    Intent intent = mPackageManager.getLaunchIntentForPackage(packageName);
                    startActivity(intent);
                } catch (PackageManager.NameNotFoundException e) {
                    //App is not installed, open Google Play Store
                    String web = "http://play.google.com/store/apps/details?id=";
                    String app = "market://details?id=";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(web + packageName));
                    startActivity(intent);
                }
            }
        });

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.whitelist_add_btn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddApp();
            }
        });
    }

    public void showAddApp() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter.getCount() == 0) {
            //If this happens, something is wrong, the list should never be empty.
            //Because we have a built-in list of whitelisted app.
            mListEmpty.setVisibility(View.VISIBLE);
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

}
