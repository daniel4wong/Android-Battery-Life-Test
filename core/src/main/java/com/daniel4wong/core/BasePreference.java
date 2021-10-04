package com.daniel4wong.core;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Button;

import java.util.Set;

public class BasePreference extends Singleton {
    public static BasePreference getInstance() {
        ISingleton _instance = getInstance(BasePreference.class);
        if (_instance == null)
            BasePreference.init(BaseApplication.context, new BasePreference());
        return (BasePreference) getInstance(BasePreference.class);
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
        return value;
    }
    public <T> T getPreference(int resId, T value) {
        return getPreference(getContext().getString(resId), value);
    }
    public <T> T getPreference(Button button, T value) {
        return getPreference(button.getTag().toString(), value);
    }

    public static class Store {
        public static String getLanguageCode() {
            return BasePreference.getInstance().getPreference(R.string.preference_language_code, "en");
        }
        public static boolean setLanguageCode(int resId) {
            if (getLanguageCode().equals(getInstance().getContext().getString(resId)))
                return false;

            BasePreference.getInstance().savePreference(R.string.preference_language_code,
                    getInstance().getContext().getString(resId));
            return true;
        }
    }
}