package com.daniel4wong.core;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.daniel4wong.core.Helper.LocaleHelper;

public abstract class BaseApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = BaseApplication.class.getSimpleName();

    public static Activity currentActivity = null;
    public static Context context = null;
    private static boolean isInBackground = false;

    protected abstract void onEnterForeground();
    protected abstract void onEnterBackground();
    protected abstract void onSubscribe();

    public void setContext(Context context) {
        BaseApplication.context = context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        onSubscribe();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        currentActivity = activity;
        LocaleHelper.setLocale(currentActivity, BasePreference.Store.getLanguageCode());
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;

        if (isInBackground) {
            Log.d(TAG, "App went to foreground.");
            isInBackground = false;
            onEnterForeground();
        }
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

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            Log.d(TAG, "App went to background.");
            isInBackground = true;
            onEnterBackground();
        }
    }
}
