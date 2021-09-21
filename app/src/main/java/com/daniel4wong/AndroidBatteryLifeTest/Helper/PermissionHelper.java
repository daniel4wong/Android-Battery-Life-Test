package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import android.content.Context;
import android.content.pm.PackageManager;

import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.AndroidBatteryLifeTest.Activity.BaseActivity;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class PermissionHelper {

    public static boolean checkPermissions(Context context, String[] permissions) {
        ArrayList<String> noPermissions = new ArrayList<>();

        for (String permission: permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                noPermissions.add(permission);
        }

        return noPermissions.size() == 0;
    }

    public static void requirePermission(String[] permissions, Runnable allowRunnable, Runnable denyRunnable) {
        if (!checkPermissions(MainApplication.currentActivity, permissions)) {
            ((BaseActivity)MainApplication.currentActivity).requestPermissions(permissions, allowRunnable, denyRunnable);
        } else {
            if (allowRunnable != null)
                allowRunnable.run();
        }
    }
}