package com.daniel4wong.AndroidBatteryLifeTest.Manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.AndroidBatteryLifeTest.Core.*;
import com.daniel4wong.AndroidBatteryLifeTest.Database.AppDatabase;
import com.daniel4wong.AndroidBatteryLifeTest.Model.BatteryHistory;

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
            BatteryHistory model = new BatteryHistory();
            model.logTs = Calendar.getInstance().getTime();
            model.btryLvl = Long.valueOf(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
            model.btryScl = Long.valueOf(intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1));
            model.markInsert();

            Log.i(TAG, String.format("Log battery level: %d", model.btryLvl));
            AppDatabase.getInstance().batteryHistoryDao().insertRecord(model);
        }
    };

    public void start() {
        getContext().registerReceiver(this.batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void stop() {
        getContext().unregisterReceiver(this.batteryReceiver);
    }
}
