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

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        BatteryTestManager.getInstance().runTestOnce();
        return Result.SUCCESS;
    }
    public static int scheduleJob(Long period) {
        return new JobRequest.Builder(TestJob.TAG)
                .setPeriodic(TimeUnit.SECONDS.toMillis(period))
                .build()
                .schedule();
    }

    public static void cancelJob(int jobId) {
        JobManager.instance().cancel(jobId);
    }
}
