package com.daniel4wong.AndroidBatteryLifeTest.Service;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.daniel4wong.AndroidBatteryLifeTest.Helper.AppNotificationHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class AppFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = AppFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            Log.d(TAG, "Message data payload: " + data);
        }

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            String title = notification.getTitle();
            String body = notification.getBody();
            Log.d(TAG, String.format("Message notification body: %s %s", title, body));

            synchronized (notification) {
                notification.notify();
            }
        }
    }

    @Override
    public void handleIntent(@NonNull Intent intent) {
        super.handleIntent(intent);
    }
}
