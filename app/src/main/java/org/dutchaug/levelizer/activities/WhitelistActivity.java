package org.dutchaug.levelizer.activities;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;

import org.dutchaug.levelizer.fragments.AddAppDialogFragment;
import org.dutchaug.levelizer.adapters.AppsListAdapter;
import org.dutchaug.levelizer.services.CameraDetectionService;
import org.dutchaug.levelizer.R;
import org.dutchaug.levelizer.util.PackageUtils;
import org.dutchaug.levelizer.util.WhitelistManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WhitelistActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    @BindView(android.R.id.list)
    protected ListView mListView;

    @BindView(android.R.id.empty)
    protected TextView mListEmpty;

    @BindView(R.id.fab)
    protected FloatingActionButton mFab;

    private AppsListAdapter mAdapter;
    private PackageManager mPackageManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whitelist);
        ButterKnife.bind(this);

        setTitle(R.string.camera_whitelist);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mPackageManager = getPackageManager();

        mAdapter = new AppsListAdapter(this);

        List<String> whitelistPackageNames = new ArrayList<>();
        // Fill the preset ones
        whitelistPackageNames.addAll(CameraDetectionService.getCameraApps());
        // Also add the user ones
        Set<String> userPackageNames = WhitelistManager.get();
        if (userPackageNames != null) {
            whitelistPackageNames.addAll(userPackageNames);
        }
        List<PackageInfo> apps = PackageUtils.getPackageInfos(mPackageManager, whitelistPackageNames);
        mAdapter.setData(apps);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PackageInfo packageInfo = (PackageInfo) adapterView.getAdapter().getItem(i);
                if (packageInfo == null) {
                    return;
                }
                String packageName = packageInfo.packageName;
                try {
                    PackageInfo internalPackageInfo = mPackageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                    // App is installed; launch it
                    Intent intent = mPackageManager.getLaunchIntentForPackage(internalPackageInfo.packageName);
                    startActivity(intent);
                } catch (PackageManager.NameNotFoundException e) {
                    // App is not installed; open Google Play Store
                    try {
                        String app = "market://details?id=";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(app + packageName));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e2) {
                        // Google Play not installed; open browser
                        String web = "http://play.google.com/store/apps/details?id=";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(web + packageName));
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @OnClick(R.id.fab)
    public void onClickFab() {
        FragmentManager fm = getSupportFragmentManager();
        AddAppDialogFragment dialog = new AddAppDialogFragment();
        dialog.show(fm, AddAppDialogFragment.TAG);
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

    @Override
    public void onDismiss(DialogInterface dialogInterface) {

    }

}
