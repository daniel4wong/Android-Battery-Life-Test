package com.daniel4wong.AndroidBatteryLifeTest.Core.BroadcastReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.daniel4wong.AndroidBatteryLifeTest.BackgroundService;
import com.daniel4wong.AndroidBatteryLifeTest.Model.Constant.LogType;
import com.daniel4wong.AndroidBatteryLifeTest.BackgroundJobService;

import java.util.UUID;

//https://erev0s.com/blog/run-android-service-background-reliably-every-n-seconds/
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = LogType.JOB + AlarmReceiver.class.getSimpleName();

    private static final String JOB_ID = "JOB_ID";
    private static final String PERIOD_IN_SECOND = "PERIOD_IN_SECOND";
    private static final int REQUEST_CODE = 0;
    private static String latestJobId = "";

    private Context context;
    private PendingIntent pendingIntent;
    private boolean isCreated;
    private String jobId;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");

        String jobId = intent.getStringExtra(JOB_ID);
        Long periodInSecond = intent.getLongExtra(PERIOD_IN_SECOND, 0);

        if (!latestJobId.equals(jobId)) {
            Log.i(TAG, "onReceive: exit job as stopped (jobId not matched");
            return;
        }

        run(context, periodInSecond, jobId);
    }

    public void createAlert(Context context, Long periodInSecond, boolean isRunFirst) {
        if (this.isCreated || periodInSecond <= 0)
            return;

        this.context = context;
        this.jobId = UUID.randomUUID().toString();
        this.latestJobId = this.jobId;
        this.isCreated = true;
        if (isRunFirst) {
            run(context, periodInSecond, this.jobId);
        } else {
            this.setAlarm(context, periodInSecond, this.jobId);
        }

        Log.i(TAG, String.format("createAlert: %s", this.jobId));
    }

    private void setAlarm(Context context, Long periodInSecond, String jobId) {
        if (periodInSecond <= 0)
            return;

        Log.i(TAG, String.format("setAlarm: %s [%d]", jobId, periodInSecond));

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(JOB_ID, jobId);
        intent.putExtra(PERIOD_IN_SECOND,periodInSecond);
        pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        Long next = (System.currentTimeMillis() / 1000L + periodInSecond) * 1000L;
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next, pendingIntent);
    }

    public void stopAlarm() {
        Log.i(TAG, String.format("stopAlarm: %s", this.jobId));
        this.isCreated = false;
        this.jobId = "";
        if (pendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            assert alarmManager != null;
            alarmManager.cancel(pendingIntent);
        }
    }

    public void run(Context context, Long periodInSecond, String jobId) {
        Class _class = BackgroundJobService.class; //BackgroundService.class
        Intent intent = new Intent(context, _class);

        Log.i(TAG, String.format("run %s", _class.getSimpleName()));

        if (_class == BackgroundJobService.class) {
            BackgroundJobService.scheduleJob(context, periodInSecond);
        } else if (_class == BackgroundService.class) {
            //https://developer.android.com/training/location/background
            //https://developer.android.com/about/versions/oreo/background-location-limits
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(intent);
            else
                context.startService(intent);
        }

        setAlarm(context, periodInSecond, jobId);
    }
}
