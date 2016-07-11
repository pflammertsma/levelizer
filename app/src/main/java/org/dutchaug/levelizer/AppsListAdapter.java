package org.dutchaug.levelizer;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AppsListAdapter extends BaseAdapter {

    private final LayoutInflater mLayoutInflater;
    private List<PackageInfo> mPackages = new ArrayList<>();
    private PackageManager mPackageManager;

    public AppsListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mPackageManager = context.getPackageManager();
    }

    public void setData(List<PackageInfo> packages) {
        mPackages = packages;
    }

    @Override
    public int getCount() {
        return mPackages.size();
    }

    @Override
    public Object getItem(int i) {
        return mPackages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.listitem, viewGroup, false);
            ListItemViewHolder vh = new ListItemViewHolder(view);
            view.setTag(vh);
        }

        ListItemViewHolder viewHolder = (ListItemViewHolder) view.getTag();

        PackageInfo pkg = (PackageInfo) getItem(i);
        String packageName = pkg.packageName;
        viewHolder.secondLine.setText(packageName);

        try {
            ApplicationInfo appInfo = pkg.applicationInfo;
            if (appInfo == null) {
                mPackageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            }
            String applicationLabel = mPackageManager.getApplicationLabel(appInfo).toString();
            viewHolder.firstLine.setText(applicationLabel);
        } catch (PackageManager.NameNotFoundException e) {
            viewHolder.firstLine.setText(R.string.not_installed);
            //e.printStackTrace();
        }

        try {
            Drawable applicationIcon = pkg.applicationInfo == null
                    ? null : pkg.applicationInfo.loadIcon(mPackageManager);
            if (applicationIcon == null) {
                mPackageManager.getApplicationIcon(packageName);
            }
            viewHolder.imageView.setImageDrawable(applicationIcon);
        } catch (PackageManager.NameNotFoundException e) {
            //viewHolder.imageView.setImageDrawable(getDrawable(android.R.drawable.sym_def_app_icon));
            viewHolder.imageView.setImageDrawable(mPackageManager.getDefaultActivityIcon());
        }

        return view;
    }

    class ListItemViewHolder {

        @BindView(R.id.listiem_icon)
        ImageView imageView;

        @BindView(R.id.listitem_firstline)
        TextView firstLine;

        @BindView(R.id.listitem_secondline)
        TextView secondLine;

        ListItemViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
