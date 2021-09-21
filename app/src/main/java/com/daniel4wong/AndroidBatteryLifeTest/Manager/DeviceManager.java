package com.daniel4wong.AndroidBatteryLifeTest.Manager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.text.format.Formatter;
import android.view.WindowManager;

import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.AndroidBatteryLifeTest.AppContext;
import com.daniel4wong.AndroidBatteryLifeTest.Activity.BlackScreenActivity;
import com.daniel4wong.AndroidBatteryLifeTest.Core.ISingleton;
import com.daniel4wong.AndroidBatteryLifeTest.Core.Singleton;

import androidx.annotation.Nullable;

public class DeviceManager extends Singleton implements ISingleton {
    public static DeviceManager getInstance() {
        ISingleton _instance = getInstance(DeviceManager.class);
        if (_instance == null)
            DeviceManager.init(MainApplication.context, new DeviceManager());
        return (DeviceManager) getInstance(DeviceManager.class);
    }
    @Override
    public void onLoad() {
        intent = new Intent(getContext(), BlackScreenActivity.class);
    }
    /// end Singleton

    private static final String TAG = DeviceManager.class.getName();

    private Intent intent;

    public void screenOff() {
        try {
            MainApplication.currentActivity.runOnUiThread(() -> {
                MainApplication.currentActivity.startActivity(intent);
                setBrightness(0f);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void screenOn() {
        try {
            MainApplication.currentActivity.runOnUiThread(() -> {
                if (MainApplication.currentActivity instanceof BlackScreenActivity)
                    MainApplication.currentActivity.finish();
                setBrightness(1f);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void screenReset() {
        try {
            MainApplication.currentActivity.runOnUiThread(() -> {
                if (MainApplication.currentActivity instanceof BlackScreenActivity)
                    MainApplication.currentActivity.finish();
                setBrightness(-1f);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBrightness(float brightness) {
        WindowManager.LayoutParams params = AppContext.getInstance().window.getAttributes();
        params.screenBrightness = brightness;
        AppContext.getInstance().window.setAttributes(params);
    }

    public boolean isWiFiEnabled(@Nullable boolean shouldEnabled) {
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        boolean isWifiEnabled = wifiManager.isWifiEnabled();
        if (!isWifiEnabled && shouldEnabled) {
            wifiManager.setWifiEnabled(true);
        }
        return isWifiEnabled;
    }

    public String getWiFiIpAddress() {
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        return Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
    }

    public boolean isCharging() {
        Intent intent = getContext().getApplicationContext()
                .registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (intent != null) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                case BatteryManager.BATTERY_STATUS_FULL:
                    return true;
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                default:
                    return false;
            }
        }
        return false;
    }
}
