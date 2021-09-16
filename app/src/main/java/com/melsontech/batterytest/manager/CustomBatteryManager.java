package com.melsontech.batterytest.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.melsontech.batterytest.db.AppDatabase;
import com.melsontech.batterytest.model.BatteryHistory;

import java.util.Calendar;

public class CustomBatteryManager {

    private Context context;

    private static CustomBatteryManager instance;
    public static CustomBatteryManager getInstance() {
        if (instance == null) {
            instance = new CustomBatteryManager();
        }
        return instance;
    }

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BatteryHistory history = new BatteryHistory(
                    Calendar.getInstance().getTime(),
                    Long.valueOf(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)),
                    Long.valueOf(intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1))
            );

            AppDatabase.getInstance().batteryHistoryDao().insert(history);
        }
    };

    public void start(Context context) {
        this.context = context;
        context.registerReceiver(this.batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void stop() {
        context.unregisterReceiver(this.batteryReceiver);
        this.context = null;
    }
}
