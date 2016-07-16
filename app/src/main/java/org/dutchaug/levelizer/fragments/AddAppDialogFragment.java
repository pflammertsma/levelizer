package org.dutchaug.levelizer.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.dutchaug.levelizer.adapters.AppsListAdapter;
import org.dutchaug.levelizer.util.PackageUtils;

import java.util.List;

public class AddAppDialogFragment extends DialogFragment {

    public static final String TAG = AddAppDialogFragment.class.getSimpleName();

    private PackageManager mPackageManager;
    private AppsListAdapter mAdapter;

    public AddAppDialogFragment(){
        //no argument constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return createView();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mPackageManager =  getActivity().getPackageManager();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(createView());
        return builder.create();
    }

    public View createView(){
        List<PackageInfo> apps = PackageUtils.getPackagesHoldingPermissions(mPackageManager, new String[]{Manifest.permission.CAMERA});

        ListView listView = new ListView(getActivity());
        mAdapter = new AppsListAdapter(getActivity());
        mAdapter.setData(apps);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String packageName = adapterView.getAdapter().getItem(i).toString();
                // TODO add package name to prefs
                Toast.makeText(getContext(), "TODO: add " + packageName, Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        });

        return listView;
    }

}
