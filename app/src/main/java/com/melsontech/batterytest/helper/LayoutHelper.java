package com.melsontech.batterytest.helper;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LayoutHelper {

    public static void setTouchablesEnable(ViewGroup layout, boolean enabled) {
        ArrayList<View> views = flatChilds(layout);
        for(View view : views) {
            if (view instanceof Button) {
                ((Button) view).setEnabled(enabled);
            }
        }
    }

    public static ArrayList<View> flatChilds(ViewGroup layout) {
        ArrayList<View> views = new ArrayList<>();
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            views.add(view);
            if (view instanceof ViewGroup) {
                ArrayList<View> _views = flatChilds((ViewGroup) view);
                views.addAll(_views);
            }
        }
        return views;
    }
}
