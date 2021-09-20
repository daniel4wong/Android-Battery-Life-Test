package com.daniel4wong.AndroidBatteryLifeTest;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.daniel4wong.AndroidBatteryLifeTest.Helper.LocaleHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BaseApplication extends Application implements Application.ActivityLifecycleCallbacks {
    public static Activity currentActivity;

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        currentActivity = activity;
        LocaleHelper.updateLocale();
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        currentActivity = activity;
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
