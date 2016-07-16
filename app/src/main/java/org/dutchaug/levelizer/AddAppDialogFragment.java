package org.dutchaug.levelizer;

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

import org.dutchaug.levelizer.util.PackageUtils;

import java.util.List;

public class AddAppDialogFragment extends DialogFragment {

    PackageManager mPackageManager;
    AppsListAdapter mAdapter;

    public AddAppDialogFragment(){
        //no argument constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initUI();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mPackageManager =  getActivity().getPackageManager();
        return makeDialog();
    }

    public View initUI(){
        List<PackageInfo> apps = PackageUtils.getPackagesHoldingPermissions(mPackageManager, new String[]{Manifest.permission.CAMERA});

        ListView listView = new ListView(getActivity());
        mAdapter = new AppsListAdapter(getActivity());
        mAdapter.setData(apps);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        return listView;
    }

    public Dialog makeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(initUI());
        return builder.create();
    }

}
