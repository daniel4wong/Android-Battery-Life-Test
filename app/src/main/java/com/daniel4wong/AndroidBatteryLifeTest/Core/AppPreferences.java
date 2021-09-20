package com.daniel4wong.AndroidBatteryLifeTest.Core;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Button;

import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.AndroidBatteryLifeTest.R;

import java.util.Set;

public class AppPreferences extends Singleton {
    public static AppPreferences getInstance() {
        ISingleton _instance = getInstance(AppPreferences.class);
        if (_instance == null)
            AppPreferences.init(MainApplication.context, new AppPreferences());
        return (AppPreferences) getInstance(AppPreferences.class);
    }
    @Override
    public void onLoad() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }
    /// end Singleton

    private SharedPreferences preferences;
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
    public void savePreference(int resId, Object value) {
        savePreference(getContext().getString(resId), value);
    }
    public void savePreference(Button button, Object value) {
        savePreference(button.getTag().toString(), value);
    }
    public <T> T getPreference(String name, T value) {
        if (value instanceof Boolean) {
            return (T) (Boolean) preferences.getBoolean(name, (Boolean) value);
        } else if (value instanceof String) {
            return (T) (String) preferences.getString(name, (String) value);
        } else if (value instanceof Set) {
            return (T) (Set<String>) preferences.getStringSet(name, (Set<String>) value);
        } else if (value instanceof Integer) {
            return (T) (Integer) preferences.getInt(name, (Integer) value);
        } else if (value instanceof Float) {
            return (T) (Float) preferences.getFloat(name, (Float) value);
        } else if (value instanceof Long) {
            return (T) (Long) preferences.getLong(name, (Long) value);
        }
        return null;
    }
    public <T> T getPreference(int resId, T value) {
        return getPreference(getContext().getString(resId), value);
    }
    public <T> T getPreference(Button button, T value) {
        return getPreference(button.getTag().toString(), value);
    }

    public boolean isKeepScreenOn() {
        return getPreference(R.string.pref_screen_always_on, false);
    }
    public boolean isMakeWebRequest() {
        return getPreference(R.string.pref_web_request, false);
    }
    public boolean isMakeGpsRequest() {
        return getPreference(R.string.pref_gps_request, false);
    }
    public boolean isMakeBleRequest() {
        return getPreference(R.string.pref_ble_request, false);
    }
}
