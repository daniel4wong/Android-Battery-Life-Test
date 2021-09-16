package com.melsontech.batterytest.manager;

import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

import com.melsontech.batterytest.AppContext;
import com.melsontech.batterytest.BlackScreenActivity;

public class DeviceManager {
    private static final String TAG = DeviceManager.class.getName();

    private Context context;
    private Intent intent;

    private static DeviceManager instance;
    public static DeviceManager getInstance() {
        if (instance == null) {
            instance = new DeviceManager();
        }
        return instance;
    }

    public static DeviceManager init(Context context) {
        DeviceManager deviceManager =  DeviceManager.getInstance();
        deviceManager.context= context;
        deviceManager.intent = new Intent(context, BlackScreenActivity.class);
        return deviceManager;
    }

    public void screenOff() {
        try {
            AppContext.getInstance().currentActivity.runOnUiThread(() -> {
                AppContext.getInstance().currentActivity.startActivity(intent);
                setBrightness(0f);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void screenOn() {
        try {
            AppContext.getInstance().currentActivity.runOnUiThread(() -> {
                if (AppContext.getInstance().currentActivity instanceof BlackScreenActivity)
                    AppContext.getInstance().currentActivity.finish();
                setBrightness(-1f);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBrightness(float brightness) {
        WindowManager.LayoutParams params = AppContext.getInstance().window.getAttributes();
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.screenBrightness = brightness;
        AppContext.getInstance().window.setAttributes(params);
    }
}
