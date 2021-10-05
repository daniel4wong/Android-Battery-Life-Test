package com.daniel4wong.AndroidBatteryLifeTest.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import com.daniel4wong.AndroidBatteryLifeTest.AppPreference;
import com.daniel4wong.AndroidBatteryLifeTest.Database.AppDatabase;
import com.daniel4wong.AndroidBatteryLifeTest.Helper.AppNotificationHelper;
import com.daniel4wong.AndroidBatteryLifeTest.Manager.DeviceManager;
import com.daniel4wong.AndroidBatteryLifeTest.Model.BatteryHistory;
import com.daniel4wong.AndroidBatteryLifeTest.R;

import java.util.Date;
import java.util.function.Consumer;

import io.reactivex.rxjava3.schedulers.Schedulers;

public class BatteryReceiver extends BroadcastReceiver {
    private static final String TAG = BatteryReceiver.class.getSimpleName();

    public static final String ACTION_STATE_CHANGE = "com.daniel4wong.AndroidBatteryLifeTest.OnStateChanged";
    public static final String ACTION_TEST_CHANGE = "com.daniel4wong.AndroidBatteryLifeTest.OnTestChanged";
    public static final String STATE = "STATE";
    public static final String TYPE = "TYPE";
    public static final String TEST_RESULT = "TEST_RESULT";
    public static boolean showNotification = false;

    private Consumer<Intent> onReceive;

    public BatteryReceiver(Consumer<Intent> onReceive) {
        this.onReceive = onReceive;
    }

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
            String name = DeviceManager.Power.isCharging() ? context.getString(R.string.msg_charging) : context.getString(R.string.msg_not_charging);
            String description = String.format("%d", model.btryLvl) + "%";
            AppNotificationHelper.showBatteryNotification(name , description);
        }

        if (onReceive != null)
            onReceive.accept(intent);
    }
}
