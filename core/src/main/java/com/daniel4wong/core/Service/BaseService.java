package com.daniel4wong.core.Service;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//https://developer.android.com/guide/components/services#java
public abstract class BaseService extends Service {
    private static final String TAG = BaseService.class.getSimpleName();
    public static final int JOB_ID = 0;

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private HandlerThread thread;

    protected abstract Runnable handleMessage();
    protected abstract Notification getNotification();

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            BaseService.this.handleMessage();
            stopSelf(msg.arg1);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        thread = new HandlerThread("ServiceStartArguments", THREAD_PRIORITY_BACKGROUND);
        thread.start();
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //https://stackoverflow.com/questions/44425584/context-startforegroundservice-did-not-then-call-service-startforeground
        Log.i(TAG, String.format("startForeground notification: %s", JOB_ID));

        Notification notification = getNotification();
        startForeground(JOB_ID, notification);

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        return START_STICKY;
    }
}
