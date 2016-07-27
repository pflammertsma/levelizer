package org.dutchaug.levelizer.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.pixplicity.easyprefs.library.Prefs;

import org.dutchaug.levelizer.services.CameraDetectionService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class WhitelistManager {

    public static final String PREFS_WHITELIST = "whitelist";

    private static final String[] CAMERA_APPS_ARRAY = new String[]{
            // Pre-installed cameras
            "com.android.camera",
            "com.sec.android.app.camera",
            "com.htc.camera",
            "com.motorola.camera",
            "com.motorola.cameraone",
            "com.lenovo.scg",
            "com.lge.camera",
            "com.huawei.camera",
            "com.sonyericsson.android.camera",
            "com.sonyericsson.android.camera3d",
            "com.cyngn.cameranext",
            "org.cyanogenmod.snap",
            // Popular third-party cameras
            "com.google.android.GoogleCamera",
            "com.google.vr.cyclops",
            "com.magix.camera_mx",
            "com.flavionet.android.camera.pro",
            "net.sourceforge.opencamera",
            "com.camtechstudio.lumiocam",
            "com.hecorat.camera",
            "slide.cameraZoom",
            "slide.camZoomFree",
            "com.perracolabs.cp",
            };

    private static Set<String> sWhitelist;

    @NonNull
    public static Set<String> get(Context context) {
        if (sWhitelist == null) {
            sWhitelist = Prefs.getOrderedStringSet(PREFS_WHITELIST, null);
            if (sWhitelist == null) {
                sWhitelist = new HashSet<>();
                Collections.addAll(sWhitelist, CAMERA_APPS_ARRAY);
                save(context);
            }
        }
        return sWhitelist;
    }

    public static void add(Context context, PackageInfo packageInfo) {
        get(context);
        String packageName = packageInfo.packageName;
        if (!sWhitelist.contains(packageName)) {
            sWhitelist.add(packageName);
        }
        Answers.getInstance().logCustom(
                new CustomEvent("whitelist add")
                        .putCustomAttribute("package name", packageName));
        save(context);
    }

    private static void save(Context context) {
        Prefs.putOrderedStringSet(PREFS_WHITELIST, sWhitelist);
        // Inform CameraDetectionService of changes
        CameraDetectionService.notifyStateChange(context);
    }

    public static void remove(Context context, String packageName) {
        get(context);
        sWhitelist.remove(packageName);
        Answers.getInstance().logCustom(
                new CustomEvent("whitelist remove")
                        .putCustomAttribute("package name", packageName));
        save(context);
    }

}
