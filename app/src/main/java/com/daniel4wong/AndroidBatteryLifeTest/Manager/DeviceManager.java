package com.daniel4wong.AndroidBatteryLifeTest.Manager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.text.format.Formatter;
import android.view.WindowManager;

import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.AndroidBatteryLifeTest.AppContext;
import com.daniel4wong.AndroidBatteryLifeTest.R;
import com.daniel4wong.AndroidBatteryLifeTest.Activity.BlackScreenActivity;
import com.daniel4wong.AndroidBatteryLifeTest.Activity.MainActivity;
import com.daniel4wong.AndroidBatteryLifeTest.Core.ISingleton;
import com.daniel4wong.AndroidBatteryLifeTest.Core.Singleton;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
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
        Intent intent = context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                return;
            }
        }, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        return isCharging;
    }
}
