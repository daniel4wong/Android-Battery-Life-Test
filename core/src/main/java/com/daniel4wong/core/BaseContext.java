package com.daniel4wong.core;

import android.view.Window;

public class BaseContext extends Singleton implements ISingleton {
    public static BaseContext getInstance() {
        ISingleton _instance = getInstance(BaseContext.class);
        if (_instance == null)
            BaseContext.init(BaseApplication.context, new BaseContext());
        return (BaseContext) getInstance(BaseContext.class);
    }
    @Override
    public void onLoad() {
    }
    /// end Singleton

    //https://nklvsbit7y0xh01yywqtjj.hooks.webhookrelay.com
    //https://en345750ztapgxo.m.pipedream.net
    public static String webRequestUrl = "https://en345750ztapgxo.m.pipedream.net";
    public static Integer bleScanTimeout = 60;

    public Window window;

    public static Object getSystemService(String name) {
        return getInstance().getContext().getSystemService(name);
    }
}
