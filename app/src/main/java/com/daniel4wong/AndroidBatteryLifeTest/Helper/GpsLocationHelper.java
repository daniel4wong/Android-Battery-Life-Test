package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.daniel4wong.AndroidBatteryLifeTest.Core.BroadcastReceiver.BatteryTestBroadcastReceiver;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

public class GpsLocationHelper extends AbstractTestHelper {
    private static final String TAG = "=BT= " + GpsLocationHelper.class.getName();
    public static final String TYPE = "GPS";

    private Context context;
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;

    public GpsLocationHelper(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(ProviderType providerType) {
        String provider = LocationManager.GPS_PROVIDER;
        if (providerType == ProviderType.NETWORK)
            provider = LocationManager.NETWORK_PROVIDER;

        Log.i(TAG, "Requesting GPS location...");
        Intent intent = new Intent();
        intent.setAction(BatteryTestBroadcastReceiver.ACTION_TEST_CHANGE);
        intent.putExtra(BatteryTestBroadcastReceiver.STATE, true);
        intent.putExtra(BatteryTestBroadcastReceiver.TYPE, TYPE);
        context.sendBroadcast(intent);

        locationManager.requestLocationUpdates(provider, 0, 0, location -> {
            Log.i(TAG, String.format("Location: %s, %s", location.getLatitude(), location.getLongitude()));
            Intent _intent = new Intent();
            _intent.setAction(BatteryTestBroadcastReceiver.ACTION_TEST_CHANGE);
            _intent.putExtra(BatteryTestBroadcastReceiver.STATE, false);
            _intent.putExtra(BatteryTestBroadcastReceiver.TYPE, TYPE);
            _intent.putExtra(BatteryTestBroadcastReceiver.TEST_RESULT, new Gson().toJson(location));
            context.sendBroadcast(_intent);
        });
    }

    @Override
    public String[] getRequiredPermissions() {
        return new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
    }

    @Override
    public boolean check() {
        PermissionHelper.requirePermission(getRequiredPermissions(), null, null);
        return checkPermissions(context);
    }

    public enum ProviderType {
        NETWORK,
        GPS
    }
}
