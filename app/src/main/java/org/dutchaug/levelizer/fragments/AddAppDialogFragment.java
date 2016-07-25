package org.dutchaug.levelizer.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.dutchaug.levelizer.R;
import org.dutchaug.levelizer.adapters.AppsListAdapter;
import org.dutchaug.levelizer.util.PackageUtils;
import org.dutchaug.levelizer.util.WhitelistManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddAppDialogFragment extends DialogFragment {

    public static final String TAG = AddAppDialogFragment.class.getSimpleName();

    @BindView(android.R.id.list)
    protected ListView mListView;

    @BindView(android.R.id.empty)
    protected TextView mListEmpty;


    public static AddAppDialogFragment create() {
        return new AddAppDialogFragment();
    }

    public AddAppDialogFragment() {
        //no argument constructor
    }

    @Override
    public int getTheme() {
        return R.style.DialogStyle;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_app_list, container, true);
        ButterKnife.bind(this, view);

        List<PackageInfo> apps = PackageUtils.getPackagesHoldingPermissions(
                getContext(), new String[]{Manifest.permission.CAMERA});

        AppsListAdapter adapter = new AppsListAdapter(getActivity());
        if (apps == null) {
            mListEmpty.setText(R.string.app_list_denied);
        } else {
            mListEmpty.setText(R.string.app_list_empty);
            adapter.setData(apps);
        }
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PackageInfo packageInfo = (PackageInfo) adapterView.getAdapter().getItem(i);
                WhitelistManager.add(getContext(), packageInfo);
                getDialog().dismiss();
            }
        });
        mListEmpty.setVisibility(adapter.getCount() == 0 ? View.VISIBLE : View.GONE);

        return view;
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
