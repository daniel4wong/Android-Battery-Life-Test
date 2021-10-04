package com.daniel4wong.AndroidBatteryLifeTest.Service;

import android.content.Context;

import com.daniel4wong.AndroidBatteryLifeTest.Manager.BatteryTestManager;
import com.daniel4wong.core.Service.BaseJobService;

public class BackgroundJobService extends BaseJobService {
    @Override
    protected Runnable handleMessage() {
        BatteryTestManager.getInstance().runTestOnce();
        return null;
    }

    public static void scheduleJob(Context context, Long periodInSecond) {
        scheduleJob(context, prepareJob(context, periodInSecond));
    }
}