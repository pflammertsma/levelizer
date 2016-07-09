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

import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by FrankkieNL on 7/9/2016.
 */
class AppsListAdapter extends BaseAdapter {

    List<String> mPackageNames = new ArrayList<String>();
    private PackageManager mPackageManager;
    private Context context;

    public AppsListAdapter(Context context) {
        this.context = context;
        mPackageManager = context.getPackageManager();

    }

    public void setData(List<String> packageNames){
        this.mPackageNames = packageNames;
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
        if (view == null) {
            //does this leak the activity on config change??
            LayoutInflater layoutInflater = LayoutInflater.from(context);
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
