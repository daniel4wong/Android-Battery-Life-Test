package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import com.daniel4wong.core.BaseContext;
import com.daniel4wong.AndroidBatteryLifeTest.AppPreference;
import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.AndroidBatteryLifeTest.BroadcastReceiver.BatteryReceiver;
import com.daniel4wong.AndroidBatteryLifeTest.Model.Constant.LogType;
import com.daniel4wong.AndroidBatteryLifeTest.R;
import com.daniel4wong.core.Helper.FormatHelper;
import com.daniel4wong.core.Helper.PermissionHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//https://proandroiddev.com/background-ble-scan-in-doze-mode-on-android-devices-3c2ce1764570
public class BleDeviceHelper extends AbstractTestHelper {
    private static final String TAG = LogType.TEST + BleDeviceHelper.class.getSimpleName();
    public static final String TYPE = "BLE";

    public static final int REQUEST_ENABLE_BT = 1;
    public static final int SCAN_PERIOD = BaseContext.bleScanTimeout * 1000;
    private Context context;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> devices;

    private boolean isScanning;
    private Handler handler = new Handler();

    public BleDeviceHelper(Context context) {
        this.context = context;
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            MainApplication.currentActivity.startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
        devices = new ArrayList<>();
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            synchronized (devices) {
                BluetoothDevice device = result.getDevice();
                if (device.getName() == null || devices.stream().anyMatch(i -> i.getAddress().equals(device.getAddress())))
                    return;
                devices.add(device);
                Log.d(TAG, String.format("Find device: %s, %s", device.getName(), device.getAddress()));
            }
        }
    };

    public void scan() {
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null)
            return;

        if (!bluetoothAdapter.isEnabled())
            bluetoothAdapter.enable();

        BluetoothLeScanner scanner = bluetoothAdapter.getBluetoothLeScanner();

        if (!isScanning) {
            handler.postDelayed(() -> {
                isScanning = false;

                Intent intent = new Intent();
                intent.setAction(BatteryReceiver.ACTION_TEST_CHANGE);
                intent.putExtra(BatteryReceiver.STATE, false);
                intent.putExtra(BatteryReceiver.TYPE, TYPE);
                synchronized (devices) {
                    String message = String.format("Number of BLE devices: %d", devices.size());
                    Log.i(TAG, String.format("[Response] %s", message));

                    JSONObject data = new JSONObject();
                    try {
                        data.put("type",  TYPE);
                        data.put("count",  devices.size());
                        data.put("ts", FormatHelper.dateToString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    intent.putExtra(BatteryReceiver.TEST_RESULT, data.toString());

                    AppPreference.getInstance().savePreference(R.string.data_ble_device_count, data.toString());
                }
                context.sendBroadcast(intent);

                isScanning = false;
                Log.i(TAG, "stopScan");
                scanner.stopScan(scanCallback);
                scanner.flushPendingScanResults(scanCallback);
            }, SCAN_PERIOD);

            synchronized (devices) {
                devices.clear();
            }

            Log.i(TAG, "[Request] Scanning BLE devices...");
            Intent intent = new Intent();
            intent.setAction(BatteryReceiver.ACTION_TEST_CHANGE);
            intent.putExtra(BatteryReceiver.TYPE, TYPE);
            intent.putExtra(BatteryReceiver.STATE, true);
            context.sendBroadcast(intent);

            ScanSettings scanSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    .build();
            List<ScanFilter> scanFilters = new ArrayList<>();
            scanFilters.add(new ScanFilter.Builder()
                    //.setServiceUuid(ParcelUuid.fromString("5C3A1523-897E-45E1-B016-007107C96DF6"))
                    .build());

            isScanning = true;
            Log.i(TAG, "startScan");
            scanner.startScan(scanFilters, scanSettings, scanCallback);
        } else {
            isScanning = false;
            Log.i(TAG, "stopScan");
            scanner.stopScan(scanCallback);
            scanner.flushPendingScanResults(scanCallback);
            Log.i(TAG, String.format("[Response] Number of BLE devices: %d", devices.size()));
        }
    }

    @Override
    public String[] getRequiredPermissions() {
        //https://punchthrough.com/android-ble-guide/
        return new String[] {
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };
    }

    @Override
    public boolean check() {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH))
            return false;

        PermissionHelper.requirePermission(getRequiredPermissions(), null, null);
        return checkPermissions(context);
    }
}
