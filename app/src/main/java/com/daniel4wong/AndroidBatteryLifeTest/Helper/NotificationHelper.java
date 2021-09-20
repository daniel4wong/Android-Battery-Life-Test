package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import com.daniel4wong.AndroidBatteryLifeTest.Activity.MainActivity;
import com.daniel4wong.AndroidBatteryLifeTest.R;

public class NotificationHelper {
    private static final int NotificationId = 0;

    public static void createNotification(Context context, String channelId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            if (channel == null) {
                String name = context.getString(R.string.app_name);
                String description = context.getString(R.string.msg_background_task_is_runnung);
                channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_battery_white_24dp)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.msg_background_task_is_runnung))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        notificationManager.notify(NotificationId, builder.build());
    }

    public static void clearNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationId);
    }

    public static void showNotificationSettings(Context context, String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId);
            context.startActivity(intent);
        }
    }
}
