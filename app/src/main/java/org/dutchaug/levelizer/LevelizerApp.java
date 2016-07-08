package org.dutchaug.levelizer;

import android.app.Application;
import android.content.ContextWrapper;

import com.pixplicity.easyprefs.library.Prefs;


public class LevelizerApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //https://github.com/Pixplicity/EasyPreferences
        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }

}
