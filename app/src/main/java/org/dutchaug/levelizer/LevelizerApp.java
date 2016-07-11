package org.dutchaug.levelizer;

import android.app.Application;
import android.content.ContextWrapper;

import com.pixplicity.easyprefs.library.Prefs;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;


public class LevelizerApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Fabric
        Fabric.with(this, new Crashlytics());

        // Initialize EasyPreferences
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }

}
