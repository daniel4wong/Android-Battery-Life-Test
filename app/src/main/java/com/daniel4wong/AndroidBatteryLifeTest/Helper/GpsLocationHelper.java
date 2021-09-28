package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.daniel4wong.AndroidBatteryLifeTest.Core.AppPreferences;
import com.daniel4wong.AndroidBatteryLifeTest.Core.BroadcastReceiver.BatteryTestReceiver;
import com.daniel4wong.AndroidBatteryLifeTest.Model.Constant.LogType;
import com.daniel4wong.AndroidBatteryLifeTest.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GpsLocationHelper extends AbstractTestHelper {
    private static final String TAG = LogType.TEST + GpsLocationHelper.class.getSimpleName();
    public static final String TYPE = "GPS";

    private Context context;
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String lastProvider;

    private LocationListener locationListener = location -> readLocation(location, lastProvider);

    public GpsLocationHelper(Context context) {
        this.context = context;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {
        Log.i(TAG, "[Request] Requesting GPS location...");
        Intent intent = new Intent();
        intent.setAction(BatteryTestReceiver.ACTION_TEST_CHANGE);
        intent.putExtra(BatteryTestReceiver.STATE, true);
        intent.putExtra(BatteryTestReceiver.TYPE, TYPE);
        context.sendBroadcast(intent);

        AsyncHelper.run(() -> {
            try {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> readLocation(location, "fuse"));
                //lastProvider = getBestProvider();
                //locationManager.requestLocationUpdates(lastProvider, 0, 0, locationListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private String getBestProvider() {
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(false);
        criteria.setSpeedRequired(false);
        criteria.setBearingRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        String bestProvider = locationManager.getBestProvider(criteria, true);
        return bestProvider;
    }

    private void readLocation(Location location, String by) {
        String message = String.format("Location: %s, %s", location.getLatitude(), location.getLongitude());
        Log.i(TAG, String.format("[Response] %s", message));

        JSONObject data = new JSONObject();
        try {
            data.put("ts", FormatHelper.dateToString());
            data.put("type", TYPE);
            data.put("latitude", location.getLatitude());
            data.put("longitude", location.getLongitude());
            data.put("by", by);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent _intent = new Intent();
        _intent.setAction(BatteryTestReceiver.ACTION_TEST_CHANGE);
        _intent.putExtra(BatteryTestReceiver.STATE, false);
        _intent.putExtra(BatteryTestReceiver.TYPE, TYPE);
        _intent.putExtra(BatteryTestReceiver.TEST_RESULT, data.toString());
        context.sendBroadcast(_intent);
        locationManager.removeUpdates(locationListener);

        AppPreferences.getInstance().savePreference(R.string.data_gps_location, data.toString());
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
}
