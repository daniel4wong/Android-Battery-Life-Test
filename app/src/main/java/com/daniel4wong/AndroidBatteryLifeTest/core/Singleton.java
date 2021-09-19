package com.daniel4wong.AndroidBatteryLifeTest.core;

import android.content.Context;

import java.util.HashMap;

public abstract class Singleton implements ISingleton {

    protected static HashMap<Class, ISingleton> instances = new HashMap<>();
    public static void init(Context context, ISingleton newObject) {
        if (instances.containsKey(newObject.getClass()))
            return;

        instances.put(newObject.getClass(), newObject);
        newObject.setContext(context);
        newObject.onLoad();
    }

    protected static ISingleton getInstance(Class _class) {
        return instances.get(_class);
    }

    private Context context;
    public void setContext(Context context) {
        this.context = context;
    }
    public Context getContext() {
        return context;
    }

    public abstract void onLoad();
}