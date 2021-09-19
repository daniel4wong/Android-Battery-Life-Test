package com.daniel4wong.AndroidBatteryLifeTest.manager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.view.WindowManager;

import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.AndroidBatteryLifeTest.AppContext;
import com.daniel4wong.AndroidBatteryLifeTest.R;
import com.daniel4wong.AndroidBatteryLifeTest.activity.BlackScreenActivity;
import com.daniel4wong.AndroidBatteryLifeTest.activity.MainActivity;
import com.daniel4wong.AndroidBatteryLifeTest.core.ISingleton;
import com.daniel4wong.AndroidBatteryLifeTest.core.Singleton;

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

    public void showStatusIcon() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getContext().getString(R.string.app_name))
                .setContentText(getContext().getText(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getContext().getText(R.string.app_name)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                //.setContentIntent(contentIntent)
                .setAutoCancel(true)
                //.setWhen(System.currentTimeMillis())
                ;


        Notification notification = builder.build();
        //notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        //NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        //notificationManager.notify(1, notification);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(1, notification);

    }
}
