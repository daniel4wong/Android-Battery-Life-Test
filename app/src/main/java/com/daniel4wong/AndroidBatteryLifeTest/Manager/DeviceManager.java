package com.daniel4wong.AndroidBatteryLifeTest.Manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.WindowManager;

import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.core.BaseContext;
import com.daniel4wong.AndroidBatteryLifeTest.Activity.BlackScreenActivity;

import com.daniel4wong.core.ISingleton;
import com.daniel4wong.core.Singleton;

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

    private static final String TAG = DeviceManager.class.getSimpleName();

    private PowerManager.WakeLock wakeLock;
    private Intent intent;

    public void screenOff() {
        screenOff(true);
    }

    public void screenOff(boolean setBrightness) {
        try {
            MainApplication.currentActivity.runOnUiThread(() -> {
                MainApplication.currentActivity.startActivity(intent);
                if (setBrightness)
                    Screen.setBrightness(0f);
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
                Screen.setBrightness(1f);
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
                Screen.setBrightness(-1f);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Screen {
        public static void setBrightness(float brightness) {
            WindowManager.LayoutParams params = BaseContext.getInstance().window.getAttributes();
            params.screenBrightness = brightness;
            BaseContext.getInstance().window.setAttributes(params);
        }
    }

    public static class Network {
        public static String getWiFiIpAddress() {
            Context context = DeviceManager.getInstance().getContext();

            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            return Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        }

        public static boolean isWiFiEnabled(@Nullable boolean shouldEnabled) {
            Context context = DeviceManager.getInstance().getContext();

            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            boolean isWifiEnabled = wifiManager.isWifiEnabled();
            if (!isWifiEnabled && shouldEnabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Intent intent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
                    MainApplication.currentActivity.startActivityForResult(intent, 0);
                } else {
                    wifiManager.setWifiEnabled(true);
                }
            }
            return isWifiEnabled;
        }
    }

    public static class Power {
        public static boolean isCharging() {
            Context context = DeviceManager.getInstance().getContext();

            Intent intent = context.getApplicationContext()
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

        public static boolean isIgnoringBatteryOptimizations() {
            Context context = DeviceManager.getInstance().getContext();

            String packageName = context.getPackageName();
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            Boolean value = powerManager.isIgnoringBatteryOptimizations(packageName);
            return value;
        }

        public static boolean isDozing() {
            Context context = DeviceManager.getInstance().getContext();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String packageName = context.getPackageName();
                PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                return powerManager.isDeviceIdleMode() && !powerManager.isIgnoringBatteryOptimizations(packageName);
            } else {
                return false;
            }
        }

        public static void requestChangeBatteryOptimizations(boolean required) {
            Context context = DeviceManager.getInstance().getContext();
            Activity activity = MainApplication.currentActivity;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent();
                String packageName = context.getPackageName();
                PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                if (required && !powerManager.isIgnoringBatteryOptimizations(packageName)) {
                    intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    activity.startActivity(intent);
                }
                else if (!required && powerManager.isIgnoringBatteryOptimizations(packageName)) {
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    activity.startActivity(intent);
                }
            }
        }

        //https://developer.android.com/training/scheduling/wakelock
        public static void acquireWakeLock() {
            Context context = DeviceManager.getInstance().getContext();
            String packageName = context.getPackageName();

            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            getInstance().wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, packageName);
            getInstance().wakeLock.acquire();
        }

        public static void releaseWakeLock() {
            if (getInstance().wakeLock != null)
                getInstance().wakeLock.release();
        }
    }
}
