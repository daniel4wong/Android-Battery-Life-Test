package com.daniel4wong.AndroidBatteryLifeTest.helper;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.telephony.TelephonyManager;

import com.daniel4wong.AndroidBatteryLifeTest.AppContext;
import com.daniel4wong.AndroidBatteryLifeTest.R;
import com.daniel4wong.AndroidBatteryLifeTest.core.broadcastReceiver.BatteryTestBroadcastReceiver;
import com.daniel4wong.AndroidBatteryLifeTest.manager.DeviceManager;

public class BatteryTestHelper {

    private Context context;
    private Handler handler;
    private DeviceManager deviceManager;
    private boolean isReset = false;

    public BatteryTestHelper(Context context) {
        this.context = context;
        this.handler = new Handler();
        this.deviceManager = DeviceManager.init(context);
    }

    public void runTestOnce(Integer screenTime) {
        this.isReset = false;

        if (!canRunTest())
            return;

        Intent intent = new Intent();
        intent.setAction(BatteryTestBroadcastReceiver.ACTION);
        intent.putExtra("STATE_RUN", true);
        context.sendBroadcast(intent);

        this.deviceManager.screenOn();

        //web
        if (Check.isRunWebRequest()) {
            WebRequestHelper webRequestHelper = new WebRequestHelper(context);
            webRequestHelper.httpGet("https://worldtimeapi.org/api/timezone/Asia/Hong_Kong");
        }
        //gps
        if (Check.isRunGpsRequest()) {
            GpsLocationHelper gpsLocationHelper = new GpsLocationHelper(context);
            gpsLocationHelper.getCurrentLocation();
        }
        //ble
        if (Check.isRunBleRequest()) {
            BleDeviceHelper bleDeviceHelper = new BleDeviceHelper(context);
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

    public void reset() {
        Intent intent = new Intent();
        intent.setAction(BatteryTestBroadcastReceiver.ACTION);
        intent.putExtra(BatteryTestBroadcastReceiver.STATE, false);
        context.sendBroadcast(intent);

        this.deviceManager.screenOn();
        this.isReset = true;
    }

    public static boolean canRunTest() { return Check.isReadyWebRequest();
    }

    public static class Check {
        public static boolean isRunWebRequest() {
            return AppContext.getInstance().preferences.getBoolean(
                    AppContext.getInstance().context.getString(R.string.pref_web_request), false);
        }
        public static boolean isRunGpsRequest() {
            return AppContext.getInstance().preferences.getBoolean(
                    AppContext.getInstance().context.getString(R.string.pref_gps_request), false);
        }
        public static boolean isRunBleRequest() {
            return AppContext.getInstance().preferences.getBoolean(
                    AppContext.getInstance().context.getString(R.string.pref_ble_request), false);
        }
        public static boolean isControlScreenOnOff() {
            return !AppContext.getInstance().preferences.getBoolean(
                    AppContext.getInstance().context.getString(R.string.pref_cpu_always_on), false);
        }
        public static boolean isReadyWebRequest() {
            try {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) AppContext.getInstance().context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                boolean isConnected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
                return isConnected;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        public static boolean isReadySimCard() {
            TelephonyManager telephonyManager =
                    (TelephonyManager) AppContext.getInstance().context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
        }
    }
}
