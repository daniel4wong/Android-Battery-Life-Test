package com.daniel4wong.AndroidBatteryLifeTest.helper;

import android.content.Context;
import android.content.pm.PackageManager;

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
}
