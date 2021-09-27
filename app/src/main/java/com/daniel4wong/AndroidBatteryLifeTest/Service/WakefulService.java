package com.daniel4wong.AndroidBatteryLifeTest.Service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.daniel4wong.AndroidBatteryLifeTest.Core.BroadcastReceiver.BackgroundWakefulReceiver;

public class WakefulService extends IntentService {
    public WakefulService() {
        super(WakefulService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Bundle extras = intent.getExtras();
        BackgroundWakefulReceiver.completeWakefulIntent(intent);
    }
}
