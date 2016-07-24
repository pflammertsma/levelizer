package org.dutchaug.levelizer.activities;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import org.dutchaug.levelizer.R;
import org.dutchaug.levelizer.adapters.AppsListAdapter;
import org.dutchaug.levelizer.fragments.AddAppDialogFragment;
import org.dutchaug.levelizer.fragments.AppContextDialogFragment;
import org.dutchaug.levelizer.util.PackageUtils;
import org.dutchaug.levelizer.util.WhitelistManager;

import java.util.ArrayList;
import java.util.List;

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

        Answers.getInstance().logContentView(new ContentViewEvent().putContentName("whitelist"));

        setTitle(R.string.camera_whitelist);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mPackageManager = getPackageManager();

        mAdapter = new AppsListAdapter(this);
        mListView.setAdapter(mAdapter);
        refreshAppList();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PackageInfo packageInfo = (PackageInfo) adapterView.getAdapter().getItem(i);
                if (packageInfo == null) {
                    return;
                }
                FragmentManager fm = getSupportFragmentManager();
                AppContextDialogFragment dialog = AppContextDialogFragment.create(packageInfo.packageName);
                dialog.show(fm, AppContextDialogFragment.TAG);
            }
        });
    }

    private void refreshAppList() {
        List<String> whitelistPackageNames = new ArrayList<>();
        whitelistPackageNames.addAll(WhitelistManager.get(this));
        List<PackageInfo> apps = PackageUtils.getPackageInfos(mPackageManager, whitelistPackageNames);
        mAdapter.setData(apps);
    }

    @OnClick(R.id.fab)
    public void onClickFab() {
        FragmentManager fm = getSupportFragmentManager();
        AddAppDialogFragment dialog = AddAppDialogFragment.create();
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
        refreshAppList();
    }

}
