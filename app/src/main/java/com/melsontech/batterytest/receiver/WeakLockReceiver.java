package com.melsontech.batterytest.receiver;

import android.content.Context;
import android.content.Intent;

import androidx.legacy.content.WakefulBroadcastReceiver;

import com.melsontech.batterytest.MainService;

// https://developer.android.com/training/scheduling/wakelock#wakeful

public class WeakLockReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, MainService.class);
        startWakefulService(context, service);
    }

}
