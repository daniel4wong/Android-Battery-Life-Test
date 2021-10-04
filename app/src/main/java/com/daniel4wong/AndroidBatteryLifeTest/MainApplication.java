package com.daniel4wong.AndroidBatteryLifeTest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.daniel4wong.AndroidBatteryLifeTest.Database.AppDatabase;
import com.daniel4wong.core.BaseContext;
import com.daniel4wong.core.BaseApplication;
import com.daniel4wong.core.Helper.LocaleHelper;
import com.daniel4wong.AndroidBatteryLifeTest.Helper.NotificationHelper;
import com.daniel4wong.AndroidBatteryLifeTest.Manager.CustomBatteryManager;

public class MainApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        MainApplication.context = getApplicationContext();
        this.registerActivityLifecycleCallbacks(this);

        AppDatabase.init(getApplicationContext(), getString(R.string.msg_database_downloaded_success));
        CustomBatteryManager.getInstance().start();
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
        setContext(base);

        BaseContext.getInstance().setContext(base);
        LocaleHelper.setLocale(base, AppPreference.Store.getLanguageCode());
        super.attachBaseContext(base);
    }

    @Override
    protected void onEnterForeground() {
        NotificationHelper.clearNotification(this);
    }

    @Override
    protected void onEnterBackground() {
    }
}
