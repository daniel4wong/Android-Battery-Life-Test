package com.daniel4wong.core.Activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daniel4wong.core.BaseApplication;
import com.daniel4wong.core.Bluetooth.BluetoothUtils;
import com.daniel4wong.core.Bluetooth.Input.HidKeyboardHelper;
import com.daniel4wong.core.Bluetooth.Hid.HidDataSender;
import com.daniel4wong.core.Helper.KeyboardHelper;
import com.daniel4wong.core.R;
import com.daniel4wong.core.Ui.VirtualKeyboard;
import com.daniel4wong.core.Ui.VirtualTouchpad;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HidActivity extends BaseActivity {

    private static final String TAG = HidActivity.class.getSimpleName();

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private VirtualKeyboard virtualKeyboard;
    private VirtualTouchpad virtualTouchpad;
    private ImageButton keyboardButton;
    private ImageButton mouseButton;

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
                            KeyboardHelper.showKeyboard(BaseApplication.currentActivity);
                            Toast.makeText(getApplicationContext(), String.format("%s connected", device.getName()), Toast.LENGTH_LONG).show();
                            break;
                        case BluetoothProfile.STATE_DISCONNECTED:
                            KeyboardHelper.closeKeyboard(BaseApplication.currentActivity);
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
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hid);

        swipeRefreshLayout = findViewById(R.id.layoutSwipeRefresh);
        listView = findViewById(R.id.listViewDevice);
        virtualKeyboard = findViewById(R.id.virtualKeyboard);
        virtualTouchpad = findViewById(R.id.virtualTouchpad);
        keyboardButton = findViewById(R.id.imageButtonKeyboard);
        mouseButton = findViewById(R.id.imageButtonMouse);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            hidDataSender = HidDataSender.getInstance();
            hidDataSender.register(this, profileListener);
            hidKeyboardHelper = new HidKeyboardHelper(hidDataSender);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            BluetoothDevice device = devices.get(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (!BluetoothUtils.isConnected(device))
                    hidDataSender.requestConnect(device);
                else
                    hidDataSender.requestDisconnect(device);
            }
        });

        virtualKeyboard.setVirtualKeyboardListener(key -> hidKeyboardHelper.send(key));
        virtualTouchpad.setVirtualTouchpadListener(mouseData -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                hidDataSender.sendMouse(mouseData.left, mouseData.right, mouseData.middle, mouseData.dX, mouseData.dY, mouseData.dWheel);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshDeviceList();
            swipeRefreshLayout.setRefreshing(false);
        });

        Activity activity = this;
        keyboardButton.setOnClickListener(view -> {
            if (!isKeyboardActive)
                KeyboardHelper.showKeyboard(activity);
            else
                KeyboardHelper.closeKeyboard(activity);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        BluetoothUtils.setScanMode(BluetoothAdapter.getDefaultAdapter(), BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, 120);
        refreshDeviceList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            hidDataSender.unregister(this, profileListener);
        }
    }

    private void refreshDeviceList() {
        names.clear();
        devices.clear();

        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        for (BluetoothDevice bluetoothDevice : pairedDevices) {
            names.add(bluetoothDevice.getName());
            devices.add(bluetoothDevice);
        }

        adapter.notifyDataSetChanged();
    }
}
