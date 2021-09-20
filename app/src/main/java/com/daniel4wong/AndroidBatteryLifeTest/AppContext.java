package com.daniel4wong.AndroidBatteryLifeTest;

import android.view.Window;

import com.daniel4wong.AndroidBatteryLifeTest.Core.*;

public class AppContext extends Singleton implements ISingleton {
    public static AppContext getInstance() {
        ISingleton _instance = getInstance(AppContext.class);
        if (_instance == null)
            AppContext.init(MainApplication.context, new AppContext());
        return (AppContext) getInstance(AppContext.class);
    }
    @Override
    public void onLoad() {
    }
    /// end Singleton

    public Window window;

    public String getLanguageCode() {
        return AppPreferences.getInstance().getPreference(R.string.pref_language_code, "en");
    }

    public static Object getSystemService(String name) {
        return getInstance().getContext().getSystemService(name);
    }
}
