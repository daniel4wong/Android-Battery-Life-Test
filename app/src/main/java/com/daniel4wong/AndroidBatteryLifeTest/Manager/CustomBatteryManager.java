package com.daniel4wong.AndroidBatteryLifeTest.Manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import com.daniel4wong.AndroidBatteryLifeTest.Core.BroadcastReceiver.CustomBatteryReceiver;
import com.daniel4wong.AndroidBatteryLifeTest.Helper.NotificationHelper;
import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.AndroidBatteryLifeTest.Core.*;
import com.daniel4wong.AndroidBatteryLifeTest.Database.AppDatabase;
import com.daniel4wong.AndroidBatteryLifeTest.Model.BatteryHistory;
import com.daniel4wong.AndroidBatteryLifeTest.R;

import java.util.Calendar;
import java.util.Date;

import io.reactivex.rxjava3.internal.util.BlockingIgnoringReceiver;
import io.reactivex.rxjava3.schedulers.Schedulers;

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

    private static final String TAG = CustomBatteryManager.class.getSimpleName();

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Date fromDate = Calendar.getInstance().getTime();
            fromDate = new Date(fromDate.getYear(), fromDate.getMonth(), fromDate.getDay(),
                    fromDate.getHours(), fromDate.getMinutes());
            Date toDate = new Date(fromDate.getTime() + 60000L);

            BatteryHistory model = new BatteryHistory();
            model.logTs = fromDate;
            model.btryLvl = (long) intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            model.btryScl = (long) intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            model.markInsert();

            if (model.btryLvl < 0)
                return;

            Log.i(TAG, String.format("Log battery level: %d", model.btryLvl));

            AppDatabase.getInstance().batteryHistoryDao().getList(fromDate, toDate)
                    .subscribeOn(Schedulers.computation())
                    .doOnError(new BlockingIgnoringReceiver())
                    .subscribe(models -> {
                        try {
                            if (models.size() == 0) {
                                AppDatabase.getInstance().batteryHistoryDao().insertRecord(model);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

            boolean isCharging = DeviceManager.getInstance().isCharging();

            NotificationHelper.showBatteryNotification(
                    isCharging ? context.getString(R.string.msg_charging) : context.getString(R.string.msg_not_charging),
                    String.format("%d", model.btryLvl) + "%");
        }
    };

    public void start() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(CustomBatteryReceiver.ACTION_SHOW_NOTIFICATION);
        getContext().registerReceiver(this.batteryReceiver, filter);
    }

    public void stop() {
        getContext().unregisterReceiver(this.batteryReceiver);
    }
}
