package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.daniel4wong.AndroidBatteryLifeTest.Core.BroadcastReceiver.BatteryTestReceiver;
import com.daniel4wong.AndroidBatteryLifeTest.Model.Constant.LogType;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GpsLocationHelper extends AbstractTestHelper {
    private static final String TAG = LogType.TEST + GpsLocationHelper.class.getSimpleName();
    public static final String TYPE = "GPS";

    private Context context;
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Log.i(TAG, String.format("[Response] Location: %s, %s", location.getLatitude(), location.getLongitude()));
            Intent _intent = new Intent();
            _intent.setAction(BatteryTestReceiver.ACTION_TEST_CHANGE);
            _intent.putExtra(BatteryTestReceiver.STATE, false);
            _intent.putExtra(BatteryTestReceiver.TYPE, TYPE);
            _intent.putExtra(BatteryTestReceiver.TEST_RESULT, new Gson().toJson(location));
            context.sendBroadcast(_intent);
            locationManager.removeUpdates(locationListener);
        }
    };

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

        Log.i(TAG, "[Request] Requesting GPS location...");
        Intent intent = new Intent();
        intent.setAction(BatteryTestReceiver.ACTION_TEST_CHANGE);
        intent.putExtra(BatteryTestReceiver.STATE, true);
        intent.putExtra(BatteryTestReceiver.TYPE, TYPE);
        context.sendBroadcast(intent);

        locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
    }

    @Override
    public String[] getRequiredPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };
        List<String> list = new ArrayList(Arrays.asList(permissions));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            list.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        return list.toArray(new String[0]);
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
