package com.daniel4wong.core.Helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import com.daniel4wong.core.BaseApplication;

import java.util.function.Function;

public class NotificationHelper {
    protected static final int NotificationId = 0;

    public static void notify(Notification notification) {
        NotificationManager notificationManager = (NotificationManager) BaseApplication.context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationId, notification);
    }

    public static Notification createNotification(Context context, String channelId, String name, String description) {
        return createNotification(context, channelId, name, description, null);
    }

    public static Notification createNotification(Context context, String channelId, String name, String description,
                                                  Function<NotificationCompat.Builder, NotificationCompat.Builder> onBuild) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            if (channel == null) {
                String _name = name;
                String _description = description;
                channel = new NotificationChannel(
                        channelId,
                        _name,
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(_description);
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent intent = new Intent(context, BaseApplication.currentActivity.getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(name)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setContentIntent(pendingIntent);

        if (onBuild != null)
            builder = onBuild.apply(builder);

        Notification notification = builder.build();
        return notification;
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
