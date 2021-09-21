package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import com.daniel4wong.AndroidBatteryLifeTest.Activity.MainActivity;
import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.AndroidBatteryLifeTest.R;

public class NotificationHelper {
    private static final int NotificationId = 0;
    private static final String BatteryStatusChannelId = "NotificationHelper::BatteryStatusChannelId";

    public static void createNotification(Context context, String channelId,
                                          String name, String description) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            if (channel == null) {
                String _name = context.getString(R.string.app_name);
                String _description = context.getString(R.string.msg_background_task_is_running);
                channel = new NotificationChannel(channelId, _name, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(_description);
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_battery_white_24dp)
                .setContentTitle(name)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setContentIntent(pendingIntent)
                ;
        Notification notification = builder.build();
        PermissionHelper.requirePermission(new String[]{ Manifest.permission.USE_FULL_SCREEN_INTENT }, () -> {
            notificationManager.notify(NotificationId, notification);
        }, null);
    }

    public static void showBatteryNotification(String name, String description) {
        createNotification(MainApplication.context, BatteryStatusChannelId, name, description);
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
