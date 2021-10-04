package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import android.content.Context;

import com.daniel4wong.core.Helper.PermissionHelper;

public abstract class AbstractTestHelper {

    public abstract String[] getRequiredPermissions();

    public boolean checkPermissions(Context context) {
        return PermissionHelper.checkPermissions(context, getRequiredPermissions(), true);
    }

    public abstract boolean check();
}
