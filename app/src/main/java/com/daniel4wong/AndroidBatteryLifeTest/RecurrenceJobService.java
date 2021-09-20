package com.daniel4wong.AndroidBatteryLifeTest;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.daniel4wong.AndroidBatteryLifeTest.Helper.NotificationHelper;

public class RecurrenceJobService extends JobService {
    public static final int JOB_ID = 0;
    public static final String CHANNEL_ID = "AndroidBatteryLifeTest::RecurrenceJobService";

    public RecurrenceJobService() {
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        NotificationHelper.createNotification(getApplicationContext(), CHANNEL_ID);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        return false;
    }

    public static void scheduleJob(Context context, Integer recurrenceInSeconds) {
        ComponentName serviceComponent = new ComponentName(context, RecurrenceJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceComponent);
        builder.setMinimumLatency(recurrenceInSeconds * 1000);
        builder.setOverrideDeadline(recurrenceInSeconds * 1000);

        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }
}