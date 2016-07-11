package org.dutchaug.levelizer.util;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public abstract class PackageUtils {

    private static final String TAG = PackageUtils.class.getSimpleName();

    @NonNull
    public static List<PackageInfo> getPackagesHoldingPermissions(PackageManager packageManager, String[] permissions) {
        //Get a list of installed apps, that have camera permission
        List<PackageInfo> packagesHoldingPermissions;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            packagesHoldingPermissions = packageManager.getPackagesHoldingPermissions(permissions, 0);
        } else {
            packagesHoldingPermissions = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        }
        List<PackageInfo> apps = new ArrayList<>();
        for (PackageInfo pkg : packagesHoldingPermissions) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                int permissionsMissing = permissions.length;
                for (String permissionA : pkg.requestedPermissions) {
                    for (String permissionB : permissions) {
                        if (permissionA.equals(permissionB)) {
                            permissionsMissing--;
                        }
                    }
                }
                if (permissionsMissing > 0) {
                    continue;
                }
            }
            apps.add(pkg);
        }
        PackageUtils.sort(packageManager, apps);
        return apps;
    }

    public static List<PackageInfo> getPackageInfos(PackageManager packageManager, List<String> packageNames) {
        List<PackageInfo> apps = new ArrayList<>();
        for (String packageName : packageNames) {
            PackageInfo pi;
            try {
                pi = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                pi = new PackageInfo();
                pi.packageName = packageName;
            }
            apps.add(pi);
        }
        PackageUtils.sort(packageManager, apps);
        return apps;
    }

    private static void sort(final PackageManager packageManager, List<PackageInfo> packages) {
        Collections.sort(packages, new Comparator<PackageInfo>() {
                    @Override
                    public int compare(PackageInfo left, PackageInfo right) {
                        ApplicationInfo leftApp = left.applicationInfo;
                        if (leftApp == null) {
                            try {
                                leftApp = packageManager.getApplicationInfo(left.packageName, PackageManager.GET_META_DATA);
                            } catch (PackageManager.NameNotFoundException e) {
                            }
                        }
                        ApplicationInfo rightApp = right.applicationInfo;
                        if (rightApp == null) {
                            try {
                                rightApp = packageManager.getApplicationInfo(right.packageName, PackageManager.GET_META_DATA);
                            } catch (PackageManager.NameNotFoundException e) {
                            }
                        }
                        int comparisonOffset = 0;
                        String leftLabel, rightLabel;
                        if (leftApp == null) {
                            leftLabel = left.packageName;
                        } else {
                            leftLabel = leftApp.loadLabel(packageManager).toString().toLowerCase(Locale.getDefault());
                        }
                        if (leftLabel.equals(left.packageName)) {
                            // Move package names (apps without label) to bottom of list
                            comparisonOffset += 100;
                        }
                        if (rightApp == null) {
                            rightLabel = right.packageName;
                        } else {
                            rightLabel = rightApp.loadLabel(packageManager).toString().toLowerCase(Locale.getDefault());
                        }
                        if (rightLabel.equals(right.packageName)) {
                            // Move package names (apps without label) to bottom of list
                            comparisonOffset -= 100;
                        }
                        return leftLabel.compareTo(rightLabel) + comparisonOffset;
                    }
                }
        );
    }

}
