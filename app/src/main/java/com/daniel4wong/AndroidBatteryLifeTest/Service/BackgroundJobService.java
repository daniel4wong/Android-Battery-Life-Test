package com.daniel4wong.AndroidBatteryLifeTest.Service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.daniel4wong.AndroidBatteryLifeTest.Manager.BatteryTestManager;
import com.daniel4wong.AndroidBatteryLifeTest.Model.Constant.LogType;

public class BackgroundJobService extends JobService {
    private static final String TAG = LogType.JOB + BackgroundJobService.class.getSimpleName();

    public static final int JOB_ID = 0;

    public BackgroundJobService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "onCreate");
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i(TAG, "onStartJob");

        BatteryTestManager.getInstance().runTestOnce();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(TAG, "onStopJob");

        return false;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");

        super.onDestroy();
    }

    public static void scheduleJob(Context context, Long periodInSecond) {
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(context, BackgroundJobService.class))
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .setPersisted(true)
                .setMinimumLatency(0L)
                .setOverrideDeadline(periodInSecond * 1000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        }

        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }
}