package com.melsontech.batterytest;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.melsontech.batterytest.db.AppDatabase;
import com.melsontech.batterytest.helper.LocaleHelper;
import com.melsontech.batterytest.manager.CustomBatteryManager;

public class App extends Application implements Application.ActivityLifecycleCallbacks {

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

    public static void restart(Context context){
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

    @Override
    protected void attachBaseContext(Context base) {
        LocaleHelper.setLocale(base, AppContext.getInstance().languageCode);

        super.attachBaseContext(base);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        LocaleHelper.setLocale(activity, AppContext.getInstance().languageCode);

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
