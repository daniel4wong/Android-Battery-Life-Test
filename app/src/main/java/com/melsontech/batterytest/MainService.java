package com.melsontech.batterytest;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.melsontech.batterytest.receiver.WeakLockReceiver;

public class MainService extends IntentService {
    public MainService() {
        super("MainService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Bundle extras = intent.getExtras();
        doWork(extras);
        WeakLockReceiver.completeWakefulIntent(intent);
    }

    protected void doWork(Bundle extras) {
    }
}
