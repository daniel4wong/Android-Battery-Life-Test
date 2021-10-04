package com.daniel4wong.core.Service;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

public abstract class BaseJobService extends JobService {
    private static final String TAG = BaseJobService.class.getSimpleName();
    public static final int JOB_ID = 1;

    protected abstract Runnable handleMessage();

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        try {
            handleMessage();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    @SuppressLint("MissingPermission")
    public static JobInfo.Builder prepareJob(Context context, Long periodInSecond) {
         JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(context, BaseJobService.class))
                .setPersisted(true)
                .setMinimumLatency(0L)
                .setOverrideDeadline(periodInSecond * 1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        }
        return builder;
    }

    public static int scheduleJob(Context context, JobInfo.Builder builder) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        return jobScheduler.schedule(builder.build());
    }
}
