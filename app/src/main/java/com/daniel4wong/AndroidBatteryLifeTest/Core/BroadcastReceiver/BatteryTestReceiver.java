package com.daniel4wong.AndroidBatteryLifeTest.Core.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.function.Function;

public class BatteryTestReceiver extends BroadcastReceiver {
    public static final String ACTION_STATE_CHANGE = "com.daniel4wong.AndroidBatteryLifeTest.OnStateChanged";
    public static final String ACTION_TEST_CHANGE = "com.daniel4wong.AndroidBatteryLifeTest.OnTestChanged";
    public static final String STATE = "STATE";
    public static final String TYPE = "TYPE";
    public static final String TEST_RESULT = "TEST_RESULT";

    private Function<Intent, Void> onReceive;

    public BatteryTestReceiver() { }

    public BatteryTestReceiver(Function<Intent, Void> onReceive) {
        this.onReceive = onReceive;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (onReceive != null)
            onReceive.apply(intent);
    }
}
