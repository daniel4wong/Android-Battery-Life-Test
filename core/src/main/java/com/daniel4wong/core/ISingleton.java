package com.daniel4wong.core;

import android.content.Context;

public interface ISingleton {
    Context context = null;
    void setContext(Context context);
    void onLoad();
}
