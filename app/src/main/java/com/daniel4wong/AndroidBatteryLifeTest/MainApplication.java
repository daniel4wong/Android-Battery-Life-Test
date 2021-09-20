package com.daniel4wong.AndroidBatteryLifeTest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.daniel4wong.AndroidBatteryLifeTest.Database.AppDatabase;
import com.daniel4wong.AndroidBatteryLifeTest.Helper.LocaleHelper;
import com.daniel4wong.AndroidBatteryLifeTest.Manager.CustomBatteryManager;

public class MainApplication extends BaseApplication {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        MainApplication.context = getApplicationContext();
        this.registerActivityLifecycleCallbacks(this);

        AppDatabase.init(getApplicationContext());
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
        MainApplication.context = base;
        AppContext.getInstance().setContext(base);
        LocaleHelper.setLocale(base, AppContext.getInstance().getLanguageCode());
        super.attachBaseContext(base);
    }

}
