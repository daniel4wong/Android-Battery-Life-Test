package com.daniel4wong.AndroidBatteryLifeTest.Core.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.daniel4wong.AndroidBatteryLifeTest.Core.AppPreferences;
import com.daniel4wong.AndroidBatteryLifeTest.R;
import com.daniel4wong.AndroidBatteryLifeTest.RecurrenceJobService;

public class RecurrenceJobServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Integer period = 3600 / AppPreferences.getInstance().getPreference(R.string.pref_test_frequency, 1);
        RecurrenceJobService.scheduleJob(context, period);
    }
}
