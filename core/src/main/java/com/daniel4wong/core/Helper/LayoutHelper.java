package com.daniel4wong.core.Helper;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class LayoutHelper {

    public static void setTouchablesEnable(View layout, boolean enabled) {
        if (!(layout instanceof ViewGroup))
            return;

        ViewGroup viewGroup = (ViewGroup) layout;

        ArrayList<View> views = flatChildren(viewGroup);
        for(View view : views) {
            if (view instanceof Button) {
                view.setEnabled(enabled);
                view.setAlpha(enabled ? 1.0f : 0.8f);
            }
        }
    }

    public static ArrayList<View> flatChildren(ViewGroup layout) {
        ArrayList<View> views = new ArrayList<>();
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            views.add(view);
            if (view instanceof ViewGroup) {
                ArrayList<View> _views = flatChildren((ViewGroup) view);
                views.addAll(_views);
            }
        }
        return views;
    }

    public static ArrayList<View> findChildren(ViewGroup layout, Class _class) {
        ArrayList<View> views = flatChildren(layout);
        return views.stream().filter(i -> i.getClass().equals(_class)).collect(Collectors.toCollection(ArrayList::new));
    }

    public static ArrayList<View> findChildren(Activity activity, Class _class) {
        ViewGroup viewGroup = getActivityView(activity);
        ArrayList<View> views = flatChildren(viewGroup);
        return views.stream().filter(i -> {
            return _class.isInstance(i);
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    public static ViewGroup getActivityView(Activity activity) {
        return activity.findViewById(android.R.id.content);
    }
}
