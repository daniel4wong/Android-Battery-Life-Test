package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import android.os.AsyncTask;

import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;

import java.util.function.Predicate;

public class AsyncHelper {
    public static void run(final Runnable executeFunc) {
        AsyncTask.execute(() -> executeFunc.run());
    }

    public static void run(final Predicate<Void> executeFunc) {
        AsyncTask.execute(() -> executeFunc.test(null));
    }

    public static void runOnUiThread(final Runnable executeFunc) {
        new Thread(() -> {
            if (MainApplication.currentActivity != null) {
                MainApplication.currentActivity.runOnUiThread(() -> executeFunc.run());
            }
        });
    }
}
