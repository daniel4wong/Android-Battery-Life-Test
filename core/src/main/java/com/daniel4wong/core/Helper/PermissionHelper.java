package com.daniel4wong.core.Helper;

import android.content.Context;
import android.content.pm.PackageManager;

import com.daniel4wong.core.Activity.BaseActivity;
import com.daniel4wong.core.BaseApplication;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class PermissionHelper {

    public static boolean checkPermissions(Context context, String[] permissions) {
        return checkPermissions(context, permissions, false);
    }

    public static boolean checkPermissions(Context context, String[] permissions, boolean autoRequire) {
        if (context == null || permissions == null)
            return false;

        if (permissions.length == 0)
            return true;

        ArrayList<String> noPermissions = new ArrayList<>();
        for (String permission: permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                noPermissions.add(permission);
        }
        if (autoRequire && noPermissions.size() > 0) {
            requirePermission(noPermissions.toArray(new String[0]), null, null);
        }
        return noPermissions.size() == 0;
    }

    public static void requirePermission(String[] permissions, Runnable allowRunnable, Runnable denyRunnable) {
        if (!checkPermissions(BaseApplication.currentActivity, permissions, false)) {
            ((BaseActivity)BaseApplication.currentActivity).requestPermissions(permissions, allowRunnable, denyRunnable);
        } else {
            if (allowRunnable != null)
                allowRunnable.run();
        }
    }
}
