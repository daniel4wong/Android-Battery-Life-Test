package com.melsontech.batterytest.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.telephony.TelephonyManager;

import com.melsontech.batterytest.AppContext;
import com.melsontech.batterytest.R;
import com.melsontech.batterytest.manager.DeviceManager;

public class BatteryTestHelper {

    private Context context;
    private Handler handler;
    private DeviceManager deviceManager;

    public BatteryTestHelper(Context context) {
        this.context = context;
        this.handler = new Handler();
        this.deviceManager = DeviceManager.init(context);
    }

    public void runTestOnce(Integer screenTime) {
        if (!canRunTest())
            return;

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
                this.deviceManager.screenOff();
            }, screenTime * 1000);
        }
    }

    public static boolean canRunTest() { return Check.isReadyWebRequest();
    }

    public static class Check {
        public static boolean isRunWebRequest() {
            return AppContext.getInstance().preferences.getBoolean(
                    AppContext.getInstance().context.getString(R.string.tag_switch_web_request), false);
        }
        public static boolean isRunGpsRequest() {
            return AppContext.getInstance().preferences.getBoolean(
                    AppContext.getInstance().context.getString(R.string.tag_switch_gps_request), false);
        }
        public static boolean isRunBleRequest() {
            return AppContext.getInstance().preferences.getBoolean(
                    AppContext.getInstance().context.getString(R.string.tag_switch_ble_request), false);
        }
        public static boolean isControlScreenOnOff() {
            return !AppContext.getInstance().preferences.getBoolean(
                    AppContext.getInstance().context.getString(R.string.tag_switch_cpu_always_on), false);
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
