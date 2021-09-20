package com.daniel4wong.AndroidBatteryLifeTest.Helper;

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

import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.AndroidBatteryLifeTest.Core.BroadcastReceiver.BatteryTestBroadcastReceiver;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class BleDeviceHelper extends AbstractTestHelper {
    private static final String TAG = "=BT= " + BleDeviceHelper.class.getName();
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

                Log.i(TAG, String.format("[Response] Number of BLE devices: %d", devices.size()));
                Intent intent = new Intent();
                intent.setAction(BatteryTestBroadcastReceiver.ACTION_TEST_CHANGE);
                intent.putExtra(BatteryTestBroadcastReceiver.STATE, false);
                intent.putExtra(BatteryTestBroadcastReceiver.TYPE, TYPE);
                intent.putExtra(BatteryTestBroadcastReceiver.TEST_RESULT, new Gson().toJson(devices));
                context.sendBroadcast(intent);

                scanner.stopScan(stopScanCallback);

            }, SCAN_PERIOD);

            isScanning = true;
            devices.clear();

            Log.i(TAG, "[Request] Scanning BLE devices...");
            Intent intent = new Intent();
            intent.setAction(BatteryTestBroadcastReceiver.ACTION_TEST_CHANGE);
            intent.putExtra(BatteryTestBroadcastReceiver.TYPE, TYPE);
            intent.putExtra(BatteryTestBroadcastReceiver.STATE, true);
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
        return new String[0];
    }

    @Override
    public boolean check() {
        return true;
    }
}
