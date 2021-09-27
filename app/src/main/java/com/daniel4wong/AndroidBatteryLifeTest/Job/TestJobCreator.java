package com.daniel4wong.AndroidBatteryLifeTest.Job;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class TestJobCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case TestJob.TAG:
                return new TestJob();
            default:
                return null;
        }
    }
}
