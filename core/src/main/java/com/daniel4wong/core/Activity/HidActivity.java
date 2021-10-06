package com.daniel4wong.core.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.MainThread;

import com.daniel4wong.core.Bluetooth.Helper.HidKeyboardHelper;
import com.daniel4wong.core.Bluetooth.Hid.HidDataSender;
import com.daniel4wong.core.Helper.KeyboardHelper;
import com.daniel4wong.core.R;
import com.daniel4wong.core.Ui.VirtualKeyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HidActivity extends BaseActivity {

    private static final String TAG = HidActivity.class.getSimpleName();

    private ListView listView;
    private VirtualKeyboard virtualKeyboard;

    private HidDataSender hidDataSender;
    private HidKeyboardHelper hidKeyboardHelper;

    private List<String> names = new ArrayList<>();
    private List<BluetoothDevice> devices = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private final HidDataSender.ProfileListener profileListener =
            new HidDataSender.ProfileListener() {
                @Override
                @MainThread
                public void onConnectionStateChanged(BluetoothDevice device, int state) {
                    Log.d(TAG, "onConnectionStateChanged");
                    switch (state) {
                        case BluetoothProfile.STATE_CONNECTED:
                            Toast.makeText(getApplicationContext(), String.format("%s connected", device.getName()), Toast.LENGTH_LONG).show();
                            break;
                        case BluetoothProfile.STATE_DISCONNECTED:
                            Toast.makeText(getApplicationContext(), String.format("%s disconnected", device.getName()), Toast.LENGTH_LONG).show();
                            break;
                        default:
                            break;
                    }
                }

                @Override
                @MainThread
                public void onAppStatusChanged(boolean registered) {
                    Log.d(TAG, "onAppStatusChanged");
                }

                @Override
                @MainThread
                public void onServiceStateChanged(BluetoothProfile proxy) {
                    Log.d(TAG, "onServiceStateChanged");
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        BluetoothHidDevice hidDevice = (BluetoothHidDevice) proxy;
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hid);

        listView = findViewById(R.id.listViewDevice);
        virtualKeyboard = findViewById(R.id.virtualKeyboard);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            hidDataSender = HidDataSender.getInstance();
            hidDataSender.register(this, profileListener);
            hidKeyboardHelper = new HidKeyboardHelper(hidDataSender);
        }

        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        for (BluetoothDevice bluetoothDevice : pairedDevices) {
            names.add(bluetoothDevice.getName());
            devices.add(bluetoothDevice);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            BluetoothDevice device = devices.get(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                hidDataSender.requestConnect(device);
            }
        });

        virtualKeyboard.setVirtualKeyboardListener(key -> hidKeyboardHelper.send(key));
    }

    @Override
    protected void onResume() {
        super.onResume();

        KeyboardHelper.showKeyboard(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            hidDataSender.unregister(this, profileListener);
        }
    }
}
