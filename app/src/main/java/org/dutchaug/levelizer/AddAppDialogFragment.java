package org.dutchaug.levelizer;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.List;

/**
 * Created by FrankkieNL on 7/9/2016.
 */
public class AddAppDialogFragment extends DialogFragment {

    PackageManager mPackageManager;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mPackageManager = getActivity().getPackageManager();
        return super.onCreateDialog(savedInstanceState);
    }

    public void init(){
        //Get a list of installed apps, that have camera permission
        List<PackageInfo> packagesHoldingPermissions = mPackageManager.getPackagesHoldingPermissions(new String[]{Manifest.permission.CAMERA}, 0);

    }
}
