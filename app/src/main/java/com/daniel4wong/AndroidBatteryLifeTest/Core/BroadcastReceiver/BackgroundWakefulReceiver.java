package com.daniel4wong.AndroidBatteryLifeTest.Core.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;

import androidx.legacy.content.WakefulBroadcastReceiver;

import com.daniel4wong.AndroidBatteryLifeTest.Service.WakefulService;

public class BackgroundWakefulReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, WakefulService.class);
        startWakefulService(context, intent1);
    }
}
