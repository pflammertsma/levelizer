package org.dutchaug.levelizer.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;

import com.pixplicity.easyprefs.library.Prefs;

import org.dutchaug.levelizer.services.CameraDetectionService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class WhitelistManager {

    public static final String PREFS_WHITELIST = "whitelist";

    private static final String[] CAMERA_APPS_ARRAY = new String[]{
            "com.android.camera",
            "com.google.android.GoogleCamera",
            "com.google.vr.cyclops",
            "com.flavionet.android.camera.pro",
            "net.sourceforge.opencamera",
            "com.motorola.camera",
            "com.lge.camera",
            "com.sec.android.app.camera",
            "com.htc.camera",
            };

    private static Set<String> sWhitelist;

    @NonNull
    public static Set<String> get() {
        if (sWhitelist == null) {
            sWhitelist = Prefs.getOrderedStringSet(PREFS_WHITELIST, null);
            if (sWhitelist == null) {
                sWhitelist = new HashSet<>();
                Collections.addAll(sWhitelist, CAMERA_APPS_ARRAY);
                save();
            }
        }
        return sWhitelist;
    }

    public static void add(Context context, PackageInfo packageInfo) {
        get();
        String packageName = packageInfo.packageName;
        if (!sWhitelist.contains(packageName)) {
            sWhitelist.add(packageName);
        }
        save();
        // Inform CameraDetectionService of changes
        CameraDetectionService.notifyStateChange(context);
    }

    private static void save() {
        Prefs.putOrderedStringSet(PREFS_WHITELIST, sWhitelist);
    }

}
