package org.dutchaug.levelizer;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AddAppDialogFragment extends DialogFragment {

    PackageManager mPackageManager;
    AppsListAdapter mAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mPackageManager = getActivity().getPackageManager();
        return makeDialog();
    }

    public Dialog makeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //Get a list of installed apps, that have camera permission
        List<PackageInfo> packagesHoldingPermissions = mPackageManager.getPackagesHoldingPermissions(new String[]{Manifest.permission.CAMERA}, 0);
        List<String> packageNames = new ArrayList<>();
        for (PackageInfo pkg : packagesHoldingPermissions) {
            packageNames.add(pkg.packageName);
        }

        ListView listView = new ListView(getActivity());
        builder.setView(listView);
        mAdapter = new AppsListAdapter(getActivity());
        mAdapter.setData(packageNames);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        return builder.create();
    }
}
