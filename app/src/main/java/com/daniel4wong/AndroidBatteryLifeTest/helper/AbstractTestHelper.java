package com.daniel4wong.AndroidBatteryLifeTest.helper;

import android.content.Context;

public abstract class AbstractTestHelper {

    public abstract String[] getRequiredPermissions();

    public boolean checkPermissions(Context context) {
        return PermissionHelper.checkPermissions(context, getRequiredPermissions());
    }

    public abstract boolean check();
}
