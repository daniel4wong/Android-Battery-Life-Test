package com.daniel4wong.AndroidBatteryLifeTest.helper;

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

import com.daniel4wong.AndroidBatteryLifeTest.AppContext;

import java.util.ArrayList;
import java.util.List;

public class BleDeviceHelper {
    private static final String TAG = "=BT= " + BleDeviceHelper.class.getName();

    public static final int REQUEST_ENABLE_BT = 1;
    public static final int SCAN_PERIOD = 10000;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> devices;

    private boolean isScanning;
    private Handler handler = new Handler();

    public BleDeviceHelper(Context context) {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            AppContext.getInstance().currentActivity.startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
        devices = new ArrayList<>();
    }

    private ScanCallback startScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            devices.add(result.getDevice());
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
                scanner.stopScan(stopScanCallback);
                Log.i(TAG, String.format("Number of BLE devices: %d", devices.size()));
            }, SCAN_PERIOD);

            isScanning = true;
            devices.clear();
            scanner.startScan(startScanCallback);
            Log.i(TAG, "Scanning BLE devices...");
        } else {
            isScanning = false;
            scanner.stopScan(stopScanCallback);
            Log.i(TAG, String.format("Number of BLE devices: %d", devices.size()));
        }
    }
}
