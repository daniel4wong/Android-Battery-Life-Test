package com.melsontech.batterytest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.melsontech.batterytest.helper.LocaleHelper;
import com.melsontech.batterytest.manager.CustomBatteryManager;

import java.util.Locale;

public class Global extends Application implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onCreate() {
        super.onCreate();

        this.registerActivityLifecycleCallbacks(this);

        AppContext.init(getApplicationContext());
        AppDatabase.init(getApplicationContext());
        CustomBatteryManager.getInstance().start(getApplicationContext());

        startService(new Intent(this, BackgroundService.class));
    }

    @Override
    public void onTerminate() {
        CustomBatteryManager.getInstance().stop();

        super.onTerminate();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        AppContext.getInstance().currentActivity = activity;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        AppContext.getInstance().currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        AppContext.getInstance().currentActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        AppContext.getInstance().currentActivity = activity;
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
