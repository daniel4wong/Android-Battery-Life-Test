package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import android.content.Context;

public abstract class AbstractTestHelper {

    public abstract String[] getRequiredPermissions();

    public boolean checkPermissions(Context context) {
        return PermissionHelper.checkPermissions(context, getRequiredPermissions(), true);
    }

    public abstract boolean check();
}
