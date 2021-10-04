package com.daniel4wong.core.Helper;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.function.Predicate;

public class AsyncHelper {
    public static void run(final Runnable executeFunc) {
        AsyncTask.execute(() -> executeFunc.run());
    }

    public static void run(final Predicate<Void> executeFunc) {
        AsyncTask.execute(() -> executeFunc.test(null));
    }

    public static void runOnUiThread(Activity activity, final Runnable executeFunc) {
        new Thread(() -> {
            if (activity != null) {
                activity.runOnUiThread(() -> executeFunc.run());
            }
        });
    }
}
