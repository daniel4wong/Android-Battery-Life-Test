package com.daniel4wong.AndroidBatteryLifeTest.Manager;

import android.content.Intent;
import android.content.IntentFilter;

import com.daniel4wong.AndroidBatteryLifeTest.BroadcastReceiver.BatteryReceiver;
import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.core.ISingleton;
import com.daniel4wong.core.Singleton;

public class CustomBatteryManager extends Singleton implements ISingleton {
    public static CustomBatteryManager getInstance() {
        ISingleton _instance = getInstance(CustomBatteryManager.class);
        if (_instance == null)
            CustomBatteryManager.init(MainApplication.context, new CustomBatteryManager());
        return (CustomBatteryManager) getInstance(CustomBatteryManager.class);
    }
    @Override
    public void onLoad() {
        batteryReceiver = new BatteryReceiver(null);
    }
    /// end Singleton

    private static final String TAG = CustomBatteryManager.class.getSimpleName();
    private BatteryReceiver batteryReceiver;

    public void start() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        getContext().registerReceiver(batteryReceiver, filter);
    }

    public void stop() {
        getContext().unregisterReceiver(batteryReceiver);
    }
}
