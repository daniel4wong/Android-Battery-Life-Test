package com.daniel4wong.AndroidBatteryLifeTest.Manager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.AndroidBatteryLifeTest.AppContext;
import com.daniel4wong.AndroidBatteryLifeTest.R;
import com.daniel4wong.AndroidBatteryLifeTest.Core.*;
import com.daniel4wong.AndroidBatteryLifeTest.Core.BroadcastReceiver.BatteryTestReceiver;
import com.daniel4wong.AndroidBatteryLifeTest.Helper.*;

public class BatteryTestManager extends Singleton implements ISingleton {
    public static BatteryTestManager getInstance() {
        ISingleton _instance = getInstance(BatteryTestManager.class);
        if (_instance == null)
            BatteryTestManager.init(MainApplication.context, new BatteryTestManager());
        return (BatteryTestManager) getInstance(BatteryTestManager.class);
    }
    @Override
    public void onLoad() {
        handler = new Handler();
        deviceManager = DeviceManager.getInstance();
        webRequestHelper = new WebRequestHelper(getContext());
        gpsLocationHelper = new GpsLocationHelper(getContext());
        bleDeviceHelper = new BleDeviceHelper(getContext());
    }
    /// end Singleton

    private static final String TAG = BatteryTestManager.class.getName();

    private Handler handler;
    private Runnable runnable;
    private DeviceManager deviceManager;
    private boolean isRunning = false;
    private boolean isReset = false;

    private WebRequestHelper webRequestHelper;
    private GpsLocationHelper gpsLocationHelper;
    private BleDeviceHelper bleDeviceHelper;

    public void runTestOnce(Integer screenTime) {
        this.isReset = false;

        if (!canRunTest())
            return;

        Intent intent = new Intent();
        intent.setAction(BatteryTestReceiver.ACTION_STATE_CHANGE);
        intent.putExtra(BatteryTestReceiver.STATE, true);
        getContext().sendBroadcast(intent);

        this.deviceManager.screenOn();

        //web
        if (Check.isRunWebRequest()) {
            webRequestHelper.httpGet("https://worldtimeapi.org/api/timezone/Asia/Hong_Kong", null);
        }
        //gps
        if (Check.isRunGpsRequest()) {
            gpsLocationHelper.getCurrentLocation(GpsLocationHelper.ProviderType.NETWORK);
        }
        //ble
        if (Check.isRunBleRequest()) {
            bleDeviceHelper.scan();
        }
        //screen
        if (Check.isControlScreenOnOff()) {
            handler.postDelayed(() -> {
                if (!this.isReset)
                    this.deviceManager.screenOff();
            }, screenTime * 1000);
        }
    }

    public void start(Integer testFrequency) {
        Integer screenTime = AppPreferences.getInstance().getPreference(R.string.pref_screen_seconds, 0);
        Integer testInterval = 3600 / testFrequency;

        if (!isRunning) {
            runnable = () -> {
                if (!isRunning) {
                    reset();
                    return;
                }
                Log.d(TAG, "Service is still running.");
                runTestOnce(screenTime);
                handler.postDelayed(runnable, testInterval * 1000);
            };
            handler.postDelayed(runnable,  0);
            isRunning = true;
            Toast.makeText(getContext(), R.string.msg_test_start, Toast.LENGTH_LONG).show();
            AppPreferences.getInstance().savePreference(R.string.flag_state_test_started, true);
        }
    }

    public void stop() {
        reset();
        isRunning = false;
        Toast.makeText(getContext(), R.string.msg_test_stop, Toast.LENGTH_LONG).show();
        AppPreferences.getInstance().savePreference(R.string.flag_state_test_started, false);
    }

    public void reset() {
        Intent intent = new Intent();
        intent.setAction(BatteryTestReceiver.ACTION_STATE_CHANGE);
        intent.putExtra(BatteryTestReceiver.STATE, false);
        getContext().sendBroadcast(intent);

        this.deviceManager.screenReset();
        this.isReset = true;
    }

    public static boolean canRunTest() {
        boolean canRun = true;
        if (AppPreferences.getInstance().isMakeWebRequest() && !BatteryTestManager.getInstance().webRequestHelper.check()) {
            return false;
        }
        if (AppPreferences.getInstance().isMakeGpsRequest() && !BatteryTestManager.getInstance().gpsLocationHelper.check()) {
            return false;
        }
        if (AppPreferences.getInstance().isMakeBleRequest() && !BatteryTestManager.getInstance().bleDeviceHelper.check()) {
            return false;
        }

        return Check.isReadyWebRequest();
    }

    public void setQuickTestProfile() {
        AppPreferences.getInstance().savePreference(R.string.pref_test_frequency, 60);
        AppPreferences.getInstance().savePreference(R.string.pref_screen_seconds, 5);
        AppPreferences.getInstance().savePreference(R.string.pref_screen_always_on, false);
        AppPreferences.getInstance().savePreference(R.string.pref_web_request, true);
        AppPreferences.getInstance().savePreference(R.string.pref_gps_request, true);
        AppPreferences.getInstance().savePreference(R.string.pref_ble_request, true);
    }
    public void setDefaultTestProfile() {
        AppPreferences.getInstance().savePreference(R.string.pref_test_frequency, 1);
        AppPreferences.getInstance().savePreference(R.string.pref_screen_seconds, 60);
        AppPreferences.getInstance().savePreference(R.string.pref_screen_always_on, false);
        AppPreferences.getInstance().savePreference(R.string.pref_web_request, true);
        AppPreferences.getInstance().savePreference(R.string.pref_gps_request, true);
        AppPreferences.getInstance().savePreference(R.string.pref_ble_request, true);
    }

    public static class Check {
        public static boolean isRunWebRequest() {
            return AppPreferences.getInstance().getPreference(R.string.pref_web_request, false);
        }
        public static boolean isRunGpsRequest() {
            return AppPreferences.getInstance().getPreference(R.string.pref_gps_request, false);
        }
        public static boolean isRunBleRequest() {
            return AppPreferences.getInstance().getPreference(R.string.pref_ble_request, false);
        }
        public static boolean isControlScreenOnOff() {
            return !AppPreferences.getInstance().getPreference(R.string.pref_cpu_always_on, false);
        }
        public static boolean isReadyWebRequest() {
            try {
                ConnectivityManager connectivityManager = (ConnectivityManager) AppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                boolean isConnected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
                return isConnected;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        public static boolean isReadySimCard() {
            TelephonyManager telephonyManager = (TelephonyManager) AppContext.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
        }
    }
}
