package com.daniel4wong.core.Helper;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class LayoutHelper {

    public static void setTouchablesEnable(View layout, boolean enabled) {
        if (!(layout instanceof ViewGroup))
            return;

        ViewGroup viewGroup = (ViewGroup) layout;

        ArrayList<View> views = flatChilds(viewGroup);
        for(View view : views) {
            if (view instanceof Button) {
                view.setEnabled(enabled);
                view.setAlpha(enabled ? 1.0f : 0.8f);
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
