package org.dutchaug.levelizer.util;

import android.content.pm.PackageInfo;

import com.pixplicity.easyprefs.library.Prefs;

import java.util.HashSet;
import java.util.Set;

public class WhitelistManager {

    public static final String PREFS_WHITELIST = "whitelist";

    public static Set<String> get() {
        return Prefs.getOrderedStringSet(PREFS_WHITELIST, null);
    }

    public static void add(PackageInfo packageInfo) {
        Set<String> list = get();
        String packageName = packageInfo.packageName;
        if (list == null) {
            list = new HashSet<>();
        }
        if (!list.contains(packageName)) {
            list.add(packageName);
        }
        Prefs.putStringSet(PREFS_WHITELIST, list);
    }

}
