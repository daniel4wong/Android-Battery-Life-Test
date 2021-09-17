package com.daniel4wong.AndroidBatteryLifeTest.core.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.function.Function;

public class BatteryTestBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION = "com.daniel4wong.OnTestStateChanged";
    public static final String STATE = "STATE_RUN";

    private Function<Intent, Void> onReceive;

    public BatteryTestBroadcastReceiver() { }

    public BatteryTestBroadcastReceiver(Function<Intent, Void> onReceive) {
        this.onReceive = onReceive;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (onReceive != null)
            onReceive.apply(intent);
    }
}
