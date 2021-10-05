package com.daniel4wong.AndroidBatteryLifeTest.Manager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.telephony.TelephonyManager;

import com.daniel4wong.AndroidBatteryLifeTest.AppPreference;
import com.daniel4wong.AndroidBatteryLifeTest.BroadcastReceiver.AlarmReceiver;
import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.core.BaseContext;
import com.daniel4wong.AndroidBatteryLifeTest.Model.Constant.LogType;
import com.daniel4wong.AndroidBatteryLifeTest.R;
import com.daniel4wong.AndroidBatteryLifeTest.BroadcastReceiver.BatteryReceiver;
import com.daniel4wong.AndroidBatteryLifeTest.Helper.*;
import com.daniel4wong.core.ISingleton;
import com.daniel4wong.core.Singleton;

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
        alarmReceiver = new AlarmReceiver();
        deviceManager = DeviceManager.getInstance();
        webRequestHelper = new WebRequestHelper(getContext());
        gpsLocationHelper = new GpsLocationHelper(getContext());
        bleDeviceHelper = new BleDeviceHelper(getContext());
    }
    /// end Singleton

    private static final String TAG = LogType.BAT + BatteryTestManager.class.getSimpleName();

    private Handler handler;
    private Runnable runnable;
    private DeviceManager deviceManager;
    private boolean isRunning = false;
    private boolean isReset = false;

    private WebRequestHelper webRequestHelper;
    private GpsLocationHelper gpsLocationHelper;
    private BleDeviceHelper bleDeviceHelper;

    private AlarmReceiver alarmReceiver;
    private int jobId;

    public void runTestOnce() {
        runTestOnce(0);
    }

    public void runTestOnce(Integer screenTime) {
        this.isReset = false;

        if (!canRunTest())
            return;

        //screen
        if (!AppPreference.isKeepScreenOn()) {
            this.deviceManager.screenOn();
            handler.postDelayed(() -> {
                if (!this.isReset)
                    this.deviceManager.screenOff();
            }, screenTime * 1000);
        }

        //web
        if (AppPreference.isMakeWebRequest()) {
            webRequestHelper.httpGet(BaseContext.webRequestUrl, null);
        }
        //gps
        if (AppPreference.isMakeGpsRequest()) {
            gpsLocationHelper.getCurrentLocation();
        }
        //ble
        if (AppPreference.isMakeBleRequest()) {
            bleDeviceHelper.scan();
        }
    }

    public void reset() {
        this.deviceManager.screenReset();
        this.isReset = true;
    }

    public static boolean canRunTest() {
        if (!DeviceManager.Power.isIgnoringBatteryOptimizations()) {
            DeviceManager.Power.requestChangeBatteryOptimizations(true);
            return false;
        }
        if (AppPreference.isMakeWebRequest() && !BatteryTestManager.getInstance().webRequestHelper.check()) {
            return false;
        }
        if (AppPreference.isMakeGpsRequest() && !BatteryTestManager.getInstance().gpsLocationHelper.check()) {
            return false;
        }
        if (AppPreference.isMakeBleRequest() && !BatteryTestManager.getInstance().bleDeviceHelper.check()) {
            return false;
        }
        return Check.isReadyWebRequest();
    }

    public void setDefaultTestProfile() {
        AppPreference.getInstance().savePreference(R.string.pref_test_period_seconds, "600");
        AppPreference.getInstance().savePreference(R.string.pref_screen_seconds, "0");
        AppPreference.getInstance().savePreference(R.string.pref_screen_always_on, true);
        AppPreference.getInstance().savePreference(R.string.pref_web_request, true);
        AppPreference.getInstance().savePreference(R.string.pref_gps_request, true);
        AppPreference.getInstance().savePreference(R.string.pref_ble_request, true);
    }

    public static class Check {
        public static boolean isReadyWebRequest() {
            try {
                ConnectivityManager connectivityManager = (ConnectivityManager) BaseContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                boolean isConnected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
                return isConnected;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        public static boolean isReadySimCard() {
            TelephonyManager telephonyManager = (TelephonyManager) BaseContext.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
        }
    }

    public static class Job {
        public static boolean startJob() {
            if (!BatteryTestManager.canRunTest())
                return false;

            AppPreference.getInstance().savePreference(R.string.flag_state_test_started, true);
            BatteryTestManager manager = BatteryTestManager.getInstance();
            DeviceManager.Power.acquireWakeLock();

            Long period = Long.valueOf(AppPreference.getInstance().getPreference(R.string.pref_test_period_seconds, "0"));
            manager.alarmReceiver.createAlert(manager.getContext(), period, true);

            Intent intent = new Intent();
            intent.setAction(BatteryReceiver.ACTION_STATE_CHANGE);
            intent.putExtra(BatteryReceiver.STATE, true);
            manager.getContext().sendBroadcast(intent);

            return true;
        }

        public static boolean stopJob() {
            AppPreference.getInstance().savePreference(R.string.flag_state_test_started, false);
            BatteryTestManager manager = BatteryTestManager.getInstance();
            DeviceManager.Power.releaseWakeLock();

            manager.alarmReceiver.stopAlarm();

            Intent intent = new Intent();
            intent.setAction(BatteryReceiver.ACTION_STATE_CHANGE);
            intent.putExtra(BatteryReceiver.STATE, false);
            manager.getContext().sendBroadcast(intent);

            return true;
        }
    }
}
