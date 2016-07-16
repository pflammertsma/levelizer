package org.dutchaug.levelizer.activities;

import android.Manifest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.dutchaug.levelizer.adapters.AppsListAdapter;
import org.dutchaug.levelizer.R;
import org.dutchaug.levelizer.util.PackageUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppListActivity extends AppCompatActivity {

    @BindView(android.R.id.list)
    protected ListView mListView;

    @BindView(android.R.id.empty)
    protected TextView mListEmpty;

    private AppsListAdapter mAdapter;

    private PackageManager mPackageManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        ButterKnife.bind(this);

        setTitle(R.string.app_list);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mPackageManager = getPackageManager();

        mAdapter = new AppsListAdapter(this);

        List<PackageInfo> apps = PackageUtils.getPackagesHoldingPermissions(mPackageManager, new String[]{Manifest.permission.CAMERA});
        mAdapter.setData(apps);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String packageName = adapterView.getAdapter().getItem(i).toString();
                // TODO add package name to prefs
                finish();
            }
        });
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