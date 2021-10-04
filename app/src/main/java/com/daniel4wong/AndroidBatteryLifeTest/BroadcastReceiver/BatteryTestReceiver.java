package com.daniel4wong.AndroidBatteryLifeTest.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.function.Consumer;

public class BatteryTestReceiver extends BroadcastReceiver {
    public static final String ACTION_STATE_CHANGE = "com.daniel4wong.AndroidBatteryLifeTest.OnStateChanged";
    public static final String ACTION_TEST_CHANGE = "com.daniel4wong.AndroidBatteryLifeTest.OnTestChanged";
    public static final String STATE = "STATE";
    public static final String TYPE = "TYPE";
    public static final String TEST_RESULT = "TEST_RESULT";

    private Consumer<Intent> onReceive;

    public BatteryTestReceiver() { }

    public BatteryTestReceiver(Consumer<Intent> onReceive) {
        this.onReceive = onReceive;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (onReceive != null)
            onReceive.accept(intent);
    }
}
