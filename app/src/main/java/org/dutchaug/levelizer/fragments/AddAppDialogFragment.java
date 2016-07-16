package org.dutchaug.levelizer.fragments;

import android.Manifest;
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
import android.widget.Toast;

import org.dutchaug.levelizer.R;
import org.dutchaug.levelizer.adapters.AppsListAdapter;
import org.dutchaug.levelizer.util.PackageUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddAppDialogFragment extends DialogFragment {

    public static final String TAG = AddAppDialogFragment.class.getSimpleName();

    @BindView(android.R.id.list)
    protected ListView mListView;

    @BindView(android.R.id.empty)
    protected TextView mListEmpty;

    public AddAppDialogFragment(){
        //no argument constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_app_list, null, false);
        ButterKnife.bind(this, view);

        List<PackageInfo> apps = PackageUtils.getPackagesHoldingPermissions(
                getActivity().getPackageManager(), new String[]{Manifest.permission.CAMERA});

        AppsListAdapter adapter = new AppsListAdapter(getActivity());
        adapter.setData(apps);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String packageName = adapterView.getAdapter().getItem(i).toString();
                // TODO add package name to prefs
                Toast.makeText(getContext(), "TODO: add " + packageName, Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        });

        return view;
    }

}
