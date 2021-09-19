package com.daniel4wong.AndroidBatteryLifeTest.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import androidx.core.app.ActivityCompat;

public class GpsLocationHelper extends AbstractTestHelper {
    private static final String TAG = "=BT= " + GpsLocationHelper.class.getName();

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
        locationManager.requestLocationUpdates(provider, 0, 0, location -> {
            Log.i(TAG, String.format("Location: %s, %s", location.getLatitude(), location.getLongitude()));
            return;
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
