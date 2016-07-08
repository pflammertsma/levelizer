package org.dutchaug.levelizer;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by FrankkieNL on 7/8/2016.
 */
public class WhitelistActivity extends AppCompatActivity {

    ListView mListView;
    TextView mListEmpty;
    AppsListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
    }

    public void initUI() {
        setContentView(R.layout.activity_whitelist);
        mListView = (ListView) findViewById(android.R.id.list);
        mListEmpty = (TextView) findViewById(android.R.id.empty);
        mAdapter = new AppsListAdapter();
        mListView.setAdapter(mAdapter);
        if (mAdapter.getCount() == 0){
            mListEmpty.setVisibility(View.VISIBLE);
        }
    }

    public class AppsListAdapter extends BaseAdapter {

        List<String> mPackageNames = new ArrayList<String>();
        PackageManager mPackageManager = WhitelistActivity.this.getPackageManager();

        public AppsListAdapter() {
            Set defaultPackageNames = new LinkedHashSet();
            defaultPackageNames.addAll(Arrays.asList(CameraDetectionService.CAMERA_APPS_ARRAY));
            mPackageNames.addAll(Prefs.getOrderedStringSet("whitelist", defaultPackageNames));
        }

        @Override
        public int getCount() {
            int count = mPackageNames.size();
            return count;
        }

        @Override
        public Object getItem(int i) {
            return mPackageNames.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null){
                //does this leak the activity on config change??
                LayoutInflater layoutInflater = LayoutInflater.from(WhitelistActivity.this);
                view = layoutInflater.inflate(R.layout.listitem, viewGroup, false);
                ListItemViewHolder vh = new ListItemViewHolder();
                vh.imageView = (ImageView) view.findViewById(R.id.listiem_icon);
                vh.firstLine = (TextView) view.findViewById(R.id.listitem_firstline);
                vh.secondLine = (TextView) view.findViewById(R.id.listitem_secondline);
                view.setTag(vh);
            }
            ListItemViewHolder viewHolder = (ListItemViewHolder) view.getTag();
            String packageName = getItem(i).toString();
            viewHolder.secondLine.setText(packageName);

            try {
                ApplicationInfo packageInfo = mPackageManager.getApplicationInfo(packageName,PackageManager.GET_META_DATA);
                String applicationLabel = mPackageManager.getApplicationLabel(packageInfo).toString();
                viewHolder.firstLine.setText(applicationLabel);
            } catch (PackageManager.NameNotFoundException e) {
                viewHolder.firstLine.setText(packageName);
                e.printStackTrace();
            }

            try {
                Drawable applicationIcon = mPackageManager.getApplicationIcon(packageName);
                viewHolder.imageView.setImageDrawable(applicationIcon);
            } catch (PackageManager.NameNotFoundException e) {
                //viewHolder.imageView.setImageDrawable(getDrawable(android.R.drawable.sym_def_app_icon));
                viewHolder.imageView.setImageDrawable(mPackageManager.getDefaultActivityIcon());
            }

            return view;
        }

        public class ListItemViewHolder {
            ImageView imageView;
            TextView firstLine;
            TextView secondLine;
        }

    }
}
