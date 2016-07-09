package org.dutchaug.levelizer;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends AppCompatActivity {

    ListView mListView;
    TextView mListEmpty;
    AppsListAdapter mAdapter;

    private PackageManager mPackageManager;
    private LayoutInflater mLayoutInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        setTitle(R.string.app_list);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mLayoutInflater = LayoutInflater.from(this);
        mPackageManager = getPackageManager();

        mListView = (ListView) findViewById(android.R.id.list);
        mListEmpty = (TextView) findViewById(android.R.id.empty);
        mAdapter = new AppsListAdapter(this);

        List<String> packageNames = new ArrayList<>();
        List<ApplicationInfo> packages = mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo pkg : packages) {
            packageNames.add(pkg.packageName);
        }
        mAdapter.setData(packageNames);

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