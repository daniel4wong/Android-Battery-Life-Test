package com.daniel4wong.AndroidBatteryLifeTest.activity;

import com.daniel4wong.AndroidBatteryLifeTest.helper.PermissionHelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class BaseActivity extends AppCompatActivity {
    private Runnable allowRunnable;
    private Runnable denyRunnable;

    public void requestPermissions(String[] permissions, Runnable allowRunnable, Runnable denyRunnable) {
        this.allowRunnable = allowRunnable;
        this.denyRunnable = denyRunnable;

        if (!PermissionHelper.checkPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 0);
        } else {
            if (allowRunnable != null)
                allowRunnable.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (!PermissionHelper.checkPermissions(this, permissions)) {
            if (denyRunnable != null)
                denyRunnable.run();
        } else {
            if (allowRunnable != null)
                allowRunnable.run();
        }
    }
}
