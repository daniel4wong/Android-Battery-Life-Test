package com.melsontech.batterytest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.Window;

import java.util.Set;
import java.util.function.Function;

public class AppContext {

    public Window window;
    public Activity currentActivity;
    public Context context;
    public PowerManager.WakeLock wakeLock;
    public SharedPreferences preferences;
    public String languageCode = "en";

    private static AppContext instance;
    public static AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    @SuppressLint("InvalidWakeLockTag")
    public static void init(Context context) {
        AppContext instance = getInstance();
        instance.context = context;

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        instance.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MainInstance::WakelockTag");
        instance.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        instance.languageCode = instance.preferences.getString(context.getString(R.string.pref_language_code), "en");
    }

    public void savePreference(Function<SharedPreferences.Editor, Void> func) {
        AppContext instance = getInstance();

        SharedPreferences.Editor editor = preferences.edit();
        func.apply(editor);
        editor.commit();
    }

    public void savePreference(String name, Object value) {
        SharedPreferences.Editor editor = preferences.edit();
        if (value instanceof Boolean) {
            editor.putBoolean(name, (Boolean) value);
        } else if (value instanceof String) {
            editor.putString(name, (String) value);
        } else if (value instanceof Set) {
            editor.putStringSet(name, (Set<String>) value);
        } else if (value instanceof Integer) {
            editor.putInt(name, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(name, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(name, (Long) value);
        }
        editor.commit();
    }
}
