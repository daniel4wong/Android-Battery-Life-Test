package com.daniel4wong.AndroidBatteryLifeTest.core;

import android.content.Context;

public interface ISingleton {
    Context context = null;
    void setContext(Context context);
    void onLoad();
}
