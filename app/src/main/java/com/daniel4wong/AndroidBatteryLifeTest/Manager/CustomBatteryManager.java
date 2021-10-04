package com.daniel4wong.AndroidBatteryLifeTest.Manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import com.daniel4wong.AndroidBatteryLifeTest.AppPreference;
import com.daniel4wong.AndroidBatteryLifeTest.Helper.NotificationHelper;
import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.AndroidBatteryLifeTest.Database.AppDatabase;
import com.daniel4wong.AndroidBatteryLifeTest.Model.BatteryHistory;
import com.daniel4wong.AndroidBatteryLifeTest.R;
import com.daniel4wong.core.ISingleton;
import com.daniel4wong.core.Singleton;

import java.util.Date;

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
    private boolean showNotification = false;

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Date now = new Date();
            Date fromDate = new Date(now.getYear(), now.getMonth(), now.getDate(), now.getHours(), now.getMinutes());
            Date toDate = new Date(fromDate.getTime() + 60000L);
            BatteryHistory model = new BatteryHistory();
            model.logTs = fromDate;
            model.btryLvl = (long) intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            model.btryScl = (long) intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            model.markInsert();

            if (model.btryLvl < 0)
                return;

            Log.i(TAG, String.format("Log battery level: %d", model.btryLvl));
            AppPreference.getInstance().savePreference(R.string.data_web_battery_level, model.btryLvl.toString());

            AppDatabase.getInstance().batteryHistoryDao().getList(fromDate, toDate)
                    .subscribeOn(Schedulers.computation())
                    .subscribe(models -> {
                        try {
                            if (models.size() == 0) {
                                AppDatabase.getInstance().batteryHistoryDao().insertRecord(model);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, throwable -> { throwable.printStackTrace(); });

            if (showNotification) {
                String name = DeviceManager.getInstance().isCharging() ? context.getString(R.string.msg_charging) : context.getString(R.string.msg_not_charging);
                String description = String.format("%d", model.btryLvl) + "%";
                NotificationHelper.showBatteryNotification(name , description);
            }
        }
    };

    public void start() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        getContext().registerReceiver(this.batteryReceiver, filter);
    }

    public void stop() {
        getContext().unregisterReceiver(this.batteryReceiver);
    }
}
