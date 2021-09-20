package com.daniel4wong.AndroidBatteryLifeTest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.daniel4wong.AndroidBatteryLifeTest.Helper.LocaleHelper;

public class BackgroundService extends Service {
    private static final String TAG = BackgroundService.class.getName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Service is created.");
        LocaleHelper.setLocale(getApplicationContext(), AppContext.getInstance().getLanguageCode());
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service is stopped.");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG, "Service is by user.");
    }
}
