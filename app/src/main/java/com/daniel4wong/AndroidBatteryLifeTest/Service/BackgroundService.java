package com.daniel4wong.AndroidBatteryLifeTest.Service;

import android.app.Notification;

import com.daniel4wong.AndroidBatteryLifeTest.Helper.AppNotificationHelper;
import com.daniel4wong.core.Helper.NotificationHelper;
import com.daniel4wong.AndroidBatteryLifeTest.Manager.BatteryTestManager;
import com.daniel4wong.AndroidBatteryLifeTest.R;
import com.daniel4wong.core.Service.BaseService;


public class BackgroundService extends BaseService {
    @Override
    protected Runnable handleMessage() {
        return () -> BatteryTestManager.getInstance().runTestOnce();
    }

    @Override
    protected Notification getNotification() {
        return AppNotificationHelper.getTestNotification(getString(R.string.msg_testing), null);
    }
}
