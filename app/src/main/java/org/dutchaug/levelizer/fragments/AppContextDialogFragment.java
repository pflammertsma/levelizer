package org.dutchaug.levelizer.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.dutchaug.levelizer.R;
import org.dutchaug.levelizer.util.WhitelistManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppContextDialogFragment extends DialogFragment {

    public static final String TAG = AppContextDialogFragment.class.getSimpleName();

    private static final String KEY_PACKAGE_NAME = "package_name";

    @BindView(android.R.id.list)
    protected ListView mListView;

    @BindView(android.R.id.empty)
    protected TextView mListEmpty;

    @BindView(R.id.iv_icon)
    protected ImageView ivIcon;

    @BindView(R.id.tv_name)
    protected TextView tvName;

    @BindView(R.id.tv_package_name)
    protected TextView tvPackageName;

    private PackageManager mPackageManager;

    public static AppContextDialogFragment create(String packageName) {
        AppContextDialogFragment fragment = new AppContextDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_PACKAGE_NAME, packageName);
        fragment.setArguments(args);
        return fragment;
    }

    public AppContextDialogFragment() {
        //no argument constructor
    }

    @Override
    public int getTheme() {
        return R.style.DialogStyle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPackageManager = getContext().getPackageManager();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_app_context, container, true);
        ButterKnife.bind(this, view);

        final String packageName = getArguments().getString(KEY_PACKAGE_NAME);

        final PackageInfo packageInfo = getPackageInfo(packageName);
        final Intent launchIntent;
        if (packageInfo != null) {
            launchIntent = mPackageManager.getLaunchIntentForPackage(packageInfo.packageName);
        } else {
            launchIntent = null;
        }

        ApplicationInfo appInfo = packageInfo == null ? null : packageInfo.applicationInfo;
        try {
            if (appInfo == null) {
                appInfo = mPackageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            }
        } catch (PackageManager.NameNotFoundException e) {
            // Ignore
        }
        if (appInfo != null) {
            String applicationLabel = mPackageManager.getApplicationLabel(appInfo).toString();
            tvName.setText(applicationLabel);
        } else {
            tvName.setText(R.string.not_installed);
        }
        tvPackageName.setText(packageName);

        try {
            Drawable applicationIcon = packageInfo == null
                    ? null : packageInfo.applicationInfo.loadIcon(mPackageManager);
            if (applicationIcon == null) {
                mPackageManager.getApplicationIcon(packageName);
            }
            ivIcon.setImageDrawable(applicationIcon);
        } catch (PackageManager.NameNotFoundException e) {
            //viewHolder.imageView.setImageDrawable(getDrawable(android.R.drawable.sym_def_app_icon));
            ivIcon.setImageDrawable(mPackageManager.getDefaultActivityIcon());
        }

        BaseAdapter adapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_list_item_1, new String[]{
                packageInfo == null ? getString(R.string.install) : getString(R.string.launch),
                getString(R.string.remove)
        }) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    if (packageInfo != null && launchIntent == null) {
                        return false;
                    }
                }
                return super.isEnabled(position);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position == 0) {
                    if (packageInfo != null && launchIntent == null) {
                        view.setAlpha(0.5f);
                    } else {
                        view.setAlpha(1f);
                    }

                }
                return view;
            }
        };
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // App is installed; launch it
                        if (packageInfo != null) {
                            if (launchIntent != null) {
                                startActivity(launchIntent);
                            }
                        } else {
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
                        dismiss();
                        break;
                    case 1:
                        WhitelistManager.remove(getContext(), packageName);
                        dismiss();
                        break;
                }
            }
        });

        return view;
    }

    private PackageInfo getPackageInfo(String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = mPackageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            // App is not installed
            // TODO
            packageInfo = null;
        }
        return packageInfo;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

}
