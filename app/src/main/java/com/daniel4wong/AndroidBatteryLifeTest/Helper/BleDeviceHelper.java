package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.daniel4wong.AndroidBatteryLifeTest.Core.AppPreferences;
import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.AndroidBatteryLifeTest.Core.BroadcastReceiver.BatteryTestReceiver;
import com.daniel4wong.AndroidBatteryLifeTest.Model.Constant.LogType;
import com.daniel4wong.AndroidBatteryLifeTest.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//https://proandroiddev.com/background-ble-scan-in-doze-mode-on-android-devices-3c2ce1764570
public class BleDeviceHelper extends AbstractTestHelper {
    private static final String TAG = LogType.TEST + BleDeviceHelper.class.getSimpleName();
    public static final String TYPE = "BLE";

    public static final int REQUEST_ENABLE_BT = 1;
    public static final int SCAN_PERIOD = 10000;
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

    private ScanCallback startScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            synchronized (devices) {
                devices.add(result.getDevice());
            }
        }
    };
    private ScanCallback stopScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
        }
    };

    public void scan() {
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null && !bluetoothAdapter.isEnabled())
            return;

        BluetoothLeScanner scanner = bluetoothAdapter.getBluetoothLeScanner();

        if (!isScanning) {
            handler.postDelayed(() -> {
                isScanning = false;

                Intent intent = new Intent();
                intent.setAction(BatteryTestReceiver.ACTION_TEST_CHANGE);
                intent.putExtra(BatteryTestReceiver.STATE, false);
                intent.putExtra(BatteryTestReceiver.TYPE, TYPE);
                synchronized (devices) {
                    String message = String.format("Number of BLE devices: %d", devices.size());
                    Log.i(TAG, String.format("[Response] %s", message));

                    JSONObject data = new JSONObject();
                    try {
                        data.put("type",  TYPE);
                        data.put("count",  devices.size());
                        data.put("ts", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    intent.putExtra(BatteryTestReceiver.TEST_RESULT, data.toString());

                    AppPreferences.getInstance().savePreference(R.string.data_ble_device_count, data.toString());
                }
                context.sendBroadcast(intent);

                scanner.stopScan(stopScanCallback);

            }, SCAN_PERIOD);

            isScanning = true;
            synchronized (devices) {
                devices.clear();
            }

            Log.i(TAG, "[Request] Scanning BLE devices...");
            Intent intent = new Intent();
            intent.setAction(BatteryTestReceiver.ACTION_TEST_CHANGE);
            intent.putExtra(BatteryTestReceiver.TYPE, TYPE);
            intent.putExtra(BatteryTestReceiver.STATE, true);
            context.sendBroadcast(intent);

            scanner.startScan(startScanCallback);
        } else {
            isScanning = false;
            scanner.stopScan(stopScanCallback);
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
        PermissionHelper.requirePermission(getRequiredPermissions(), null, null);
        return checkPermissions(context);
    }
}
