package com.daniel4wong.AndroidBatteryLifeTest.Job;

import androidx.annotation.NonNull;

import com.daniel4wong.AndroidBatteryLifeTest.Manager.BatteryTestManager;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

//https://github.com/evernote/android-job
public class TestJob extends Job {
    public static final String TAG = "TestJob";
    public static boolean isStarted = false;

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        run();
        return Result.SUCCESS;
    }
    public static int scheduleJob(Long period, boolean isRunFirst) {
        if (isStarted)
             return 0;
        isStarted = true;

        if (isRunFirst)
            run();

        return new JobRequest.Builder(TestJob.TAG)
                .setPeriodic(TimeUnit.SECONDS.toMillis(period), JobRequest.MIN_FLEX)
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }

    public static void cancelJob(int jobId) {
        JobManager.instance().cancel(jobId);
        isStarted = false;
    }

    public static void run() {
        BatteryTestManager.getInstance().runTestOnce();
    }
}
