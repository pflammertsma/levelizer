package org.dutchaug.levelizer;

import android.content.Context;
import android.content.pm.ApplicationInfo;
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


public class AppsListAdapter extends BaseAdapter {

    private final LayoutInflater mLayoutInflater;
    private List<String> mPackageNames = new ArrayList<String>();
    private PackageManager mPackageManager;

    public AppsListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mPackageManager = context.getPackageManager();
    }

    public void setData(List<String> packageNames){
        mPackageNames = packageNames;
    }

    @Override
    public int getCount() {
        return mPackageNames.size();
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
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.listitem, viewGroup, false);
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
            ApplicationInfo packageInfo = mPackageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            String applicationLabel = mPackageManager.getApplicationLabel(packageInfo).toString();
            viewHolder.firstLine.setText(applicationLabel);
        } catch (PackageManager.NameNotFoundException e) {
            viewHolder.firstLine.setText(R.string.not_installed);
            //e.printStackTrace();
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
