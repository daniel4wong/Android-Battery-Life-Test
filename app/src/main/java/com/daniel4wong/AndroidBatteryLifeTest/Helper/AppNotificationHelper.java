package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.daniel4wong.AndroidBatteryLifeTest.R;
import com.daniel4wong.core.BaseApplication;
import com.daniel4wong.core.Helper.NotificationHelper;

public class AppNotificationHelper extends NotificationHelper {

    public static Notification showDefaultNotification(String name, String description) {
        String channelId = AppNotificationHelper.class.getSimpleName() + "::DefaultNotification";

        Notification notification = createNotification(BaseApplication.context, channelId, name, description,
                builder -> {
                    return builder
                            .setSmallIcon(R.drawable.ic_battery_white_24dp)
                            .setAutoCancel(true);
                });
        notify(notification);

        return notification;
    }

    public static Notification showBatteryNotification(String name, String description) {
        String channelId = AppNotificationHelper.class.getSimpleName() + "::BatteryNotification";

        Notification notification = createNotification(BaseApplication.context, channelId, name, description,
                builder -> builder.setSmallIcon(R.drawable.ic_battery_white_24dp));
        notify(notification);

        return notification;
    }

    public static Notification getTestNotification(String name, String description) {
        String channelId = AppNotificationHelper.class.getSimpleName() + "::TestNotification";

        Notification notification = createNotification(BaseApplication.context, channelId, name, description,
                builder -> builder.setSmallIcon(R.drawable.ic_battery_white_24dp));

        return notification;
    }
}
