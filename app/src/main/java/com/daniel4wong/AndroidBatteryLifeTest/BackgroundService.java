package com.daniel4wong.AndroidBatteryLifeTest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.daniel4wong.AndroidBatteryLifeTest.helper.BatteryTestHelper;

public class BackgroundService extends Service {
    private static final String TAG = BackgroundService.class.getName();

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    private static BackgroundService instance = null;

    private boolean isRunning = false;
    private BatteryTestHelper batteryTestHelper;

    public static BackgroundService getInstance() {
        return instance;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Service is created.");

        BackgroundService.instance = this;

        batteryTestHelper = new BatteryTestHelper(context);
        handler = new Handler();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service is stopped.");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG, "Service is by user.");
    }

    public void start(Integer testFrequency) {
        Integer screenTime = AppContext.getInstance().preferences.getInt(getString(R.string.pref_screen_seconds), 0);
        Integer testInterval = 3600 / testFrequency;

        if (!isRunning) {
            runnable = () -> {
                if (!isRunning) {
                    batteryTestHelper.reset();
                    return;
                }

                Toast.makeText(context, getString(R.string.msg_test_start), Toast.LENGTH_LONG).show();
                Log.d(TAG, "Service is still running.");
                batteryTestHelper.runTestOnce(screenTime);
                handler.postDelayed(runnable, testInterval * 1000);
            };
            handler.postDelayed(runnable,  0);
            isRunning = true;
        }
    }

    public void stop() {
        batteryTestHelper.reset();
        isRunning = false;
        runnable = () -> { };
        Toast.makeText(context, getString(R.string.msg_test_stop), Toast.LENGTH_LONG).show();
    }

}
