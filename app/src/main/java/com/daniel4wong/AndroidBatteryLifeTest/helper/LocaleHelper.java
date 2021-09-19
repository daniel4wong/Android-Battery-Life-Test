package com.daniel4wong.AndroidBatteryLifeTest.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.daniel4wong.AndroidBatteryLifeTest.AppContext;
import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;

import java.util.Locale;

public class LocaleHelper {

    public static void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public static void updateLocale() {
        setLocale(MainApplication.currentActivity, AppContext.getInstance().getLanguageCode());
    }

}
