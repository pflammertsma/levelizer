package org.dutchaug.levelizer.adapters;

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

import org.dutchaug.levelizer.R;

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
        notifyDataSetChanged();
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
            view = mLayoutInflater.inflate(R.layout.list_app, viewGroup, false);
            ListItemViewHolder vh = new ListItemViewHolder(view);
            view.setTag(vh);
        }

        ListItemViewHolder viewHolder = (ListItemViewHolder) view.getTag();

        PackageInfo packageInfo = (PackageInfo) getItem(i);
        String packageName = packageInfo.packageName;
        viewHolder.tvPackageName.setText(packageName);

        try {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            if (appInfo == null) {
                mPackageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            }
            String applicationLabel = mPackageManager.getApplicationLabel(appInfo).toString();
            viewHolder.tvName.setText(applicationLabel);
            viewHolder.tvName.setAlpha(1f);
            viewHolder.tvPackageName.setAlpha(1f);
        } catch (PackageManager.NameNotFoundException e) {
            viewHolder.tvName.setText(R.string.not_installed);
            viewHolder.tvName.setAlpha(0.5f);
            viewHolder.tvPackageName.setAlpha(0.5f);
        }

        try {
            Drawable applicationIcon = packageInfo.applicationInfo == null
                    ? null : packageInfo.applicationInfo.loadIcon(mPackageManager);
            if (applicationIcon == null) {
                mPackageManager.getApplicationIcon(packageName);
            }
            viewHolder.ivIcon.setImageDrawable(applicationIcon);
        } catch (PackageManager.NameNotFoundException e) {
            //viewHolder.imageView.setImageDrawable(getDrawable(android.R.drawable.sym_def_app_icon));
            viewHolder.ivIcon.setImageDrawable(mPackageManager.getDefaultActivityIcon());
        }

        return view;
    }

    class ListItemViewHolder {

        @BindView(R.id.iv_icon)
        protected ImageView ivIcon;

        @BindView(R.id.tv_name)
        protected TextView tvName;

        @BindView(R.id.tv_package_name)
        protected TextView tvPackageName;

        ListItemViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
