package com.daniel4wong.AndroidBatteryLifeTest.Service;

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

import com.daniel4wong.AndroidBatteryLifeTest.Helper.NotificationHelper;
import com.daniel4wong.AndroidBatteryLifeTest.Manager.BatteryTestManager;
import com.daniel4wong.AndroidBatteryLifeTest.Model.Constant.LogType;
import com.daniel4wong.AndroidBatteryLifeTest.R;

//https://developer.android.com/guide/components/services#java
public class BackgroundService extends Service {
    private static final String TAG = LogType.JOB + BackgroundService.class.getSimpleName();

    public static final int JOB_ID = 1;
    public static final String CHANNEL_ID = "ABLT/" + BackgroundService.class.getSimpleName();

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private HandlerThread thread;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            BatteryTestManager.getInstance().runTestOnce();
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "onCreate");

        thread = new HandlerThread("ServiceStartArguments", THREAD_PRIORITY_BACKGROUND);
        thread.start();
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        //https://stackoverflow.com/questions/44425584/context-startforegroundservice-did-not-then-call-service-startforeground
        Notification notification = NotificationHelper.getTestNotification(getString(R.string.msg_testing), null);
        startForeground(JOB_ID, notification);

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
    }
}
