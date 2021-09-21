package com.daniel4wong.AndroidBatteryLifeTest.Core.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CustomBatteryReceiver extends BroadcastReceiver {
    public static final String ACTION_SHOW_NOTIFICATION = "com.daniel4wong.AndroidBatteryLifeTest.OnShowNotification";
    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
