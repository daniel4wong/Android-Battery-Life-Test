package com.daniel4wong.AndroidBatteryLifeTest.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.AndroidBatteryLifeTest.core.*;
import com.daniel4wong.AndroidBatteryLifeTest.db.AppDatabase;
import com.daniel4wong.AndroidBatteryLifeTest.model.BatteryHistory;

import java.util.Calendar;

public class CustomBatteryManager extends Singleton implements ISingleton {
    public static CustomBatteryManager getInstance() {
        ISingleton _instance = getInstance(CustomBatteryManager.class);
        if (_instance == null)
            CustomBatteryManager.init(MainApplication.context, new CustomBatteryManager());
        return (CustomBatteryManager) getInstance(CustomBatteryManager.class);
    }
    @Override
    public void onLoad() { }
    /// end Singleton

    private static final String TAG = BatteryTestManager.class.getName();

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BatteryHistory history = new BatteryHistory(
                    Calendar.getInstance().getTime(),
                    Long.valueOf(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)),
                    Long.valueOf(intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1))
            );

            Log.i(TAG, String.format("Log battery level: %d", history.level));
            AppDatabase.getInstance().batteryHistoryDao().insert(history);
        }
    };

    public void start() {
        getContext().registerReceiver(this.batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void stop() {
        getContext().unregisterReceiver(this.batteryReceiver);
    }
}
