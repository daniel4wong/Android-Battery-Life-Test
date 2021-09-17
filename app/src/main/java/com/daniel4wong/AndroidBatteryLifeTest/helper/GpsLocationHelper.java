package com.daniel4wong.AndroidBatteryLifeTest.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class GpsLocationHelper {
    private static final String TAG = "=BT= " + GpsLocationHelper.class.getName();

    private Context context;
    private LocationManager locationManager;

    public GpsLocationHelper(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.i(TAG, "Requesting GPS location...");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, location -> {
            Log.i(TAG, String.format("Location: %s, %s", location.getLatitude(), location.getLongitude()));
            return;
        });
    }
}
