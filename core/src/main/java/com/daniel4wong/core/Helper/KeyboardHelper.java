package com.daniel4wong.core.Helper;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyboardHelper {

    private static final int INTERVAL_MS = 100;
    private static Handler handler = new Handler(Looper.getMainLooper());

    public static void showKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        handler.postDelayed(() -> {
            view.requestFocus();
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }, INTERVAL_MS);
    }

    public static void showKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        try {
            View view = null;

            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                view = LayoutHelper.findChildren(activity, EditText.class).get(0);
            }

            if (view != null) {
                showKeyboard(view);
            } else {
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        try {
            View view = null;

            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                view = activity.getCurrentFocus();
            }

            if (view != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } else {
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
