package com.daniel4wong.AndroidBatteryLifeTest;

import com.daniel4wong.AndroidBatteryLifeTest.R;
import com.daniel4wong.core.BasePreference;

public class AppPreference extends BasePreference {
    public static boolean isTestStarted() {
        return getInstance().getPreference(R.string.flag_state_test_started, false);
    }
    public static boolean isKeepScreenOn() {
        return getInstance().getPreference(R.string.pref_screen_always_on, false);
    }
    public static boolean isMakeWebRequest() {
        return getInstance().getPreference(R.string.pref_web_request, false);
    }
    public static boolean isMakeGpsRequest() {
        return getInstance().getPreference(R.string.pref_gps_request, false);
    }
    public static boolean isMakeBleRequest() {
        return getInstance().getPreference(R.string.pref_ble_request, false);
    }
}
