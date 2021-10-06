package com.daniel4wong.core.Helper;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyboardHelper {

    public static void showKeyboard(Context context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                View view = LayoutHelper.findChildren(activity, EditText.class).get(0);
                view.requestFocus();
                inputMethodManager.showSoftInputFromInputMethod(view.getWindowToken(), InputMethodManager.SHOW_FORCED);
            } else {
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeKeyboard(Context context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                View view = activity.getCurrentFocus();
                if (view != null) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            } else {
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
