package com.daniel4wong.core.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HidActivity extends BaseActivity {

    private static final String TAG = HidActivity.class.getSimpleName();

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private VirtualKeyboard virtualKeyboard;
    private VirtualTouchpad virtualTouchpad;
    private ImageButton keyboardButton;
    private ImageButton mouseButton;
    private ProgressDialog progressDialog;

    private HidDataSender hidDataSender;
    private HidKeyboardHelper hidKeyboardHelper;

    private ArrayList<Map<String, String>> names = new ArrayList<>();
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();
    private SimpleAdapter adapter;

    private final String deviceName = "deviceName";
    private final String macAddress = "macAddress";

    private final HidDataSender.ProfileListener profileListener = new HidDataSender.ProfileListener() {
            @Override
            @MainThread
            public void onConnectionStateChanged(BluetoothDevice device, int state) {
                Log.d(TAG, "onConnectionStateChanged");
                switch (state) {
                    case BluetoothProfile.STATE_CONNECTED:
                        KeyboardHelper.showKeyboard(BaseApplication.currentActivity);
                        Toast.makeText(getApplicationContext(), String.format("%s connected", device.getName()), Toast.LENGTH_LONG).show();
                        progressDialog.cancel();
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        KeyboardHelper.closeKeyboard(BaseApplication.currentActivity);
                        Toast.makeText(getApplicationContext(), String.format("%s disconnected", device.getName()), Toast.LENGTH_LONG).show();
                        progressDialog.cancel();
                        break;
                    case BluetoothProfile.STATE_CONNECTING:
                        progressDialog.setMessage("Connecting...");
                        progressDialog.show();
                        break;
                    case BluetoothProfile.STATE_DISCONNECTING:
                        progressDialog.setMessage("Disconnecting...");
                        progressDialog.show();
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            hidDataSender = HidDataSender.getInstance();
            hidDataSender.register(this, profileListener);
            hidKeyboardHelper = new HidKeyboardHelper(hidDataSender);
        }

        adapter = new SimpleAdapter(this, names,
                android.R.layout.simple_list_item_2,
                new String[] { deviceName, macAddress },
                new int[] {android.R.id.text1, android.R.id.text2 });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            BluetoothDevice device = devices.get(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                hidDataSender.toggleConnect(device);
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
        virtualKeyboard.setVisibility(View.GONE);
        keyboardButton.setOnClickListener(view -> {
            boolean visible = virtualKeyboard.getVisibility() == View.VISIBLE;
            virtualKeyboard.setVisibility(visible ? View.GONE : View.VISIBLE);

            if (!isKeyboardActive)
                KeyboardHelper.showKeyboard(activity);
            else
                KeyboardHelper.closeKeyboard(activity);
        });

        virtualTouchpad.setVisibility(View.GONE);
        mouseButton.setOnClickListener(view -> {
            boolean visible = virtualTouchpad.getVisibility() == View.VISIBLE;
            virtualTouchpad.setVisibility(visible ? View.GONE : View.VISIBLE);
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

        KeyboardHelper.closeKeyboard(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            hidDataSender.unregister(this, profileListener);
        }
    }

    private void refreshDeviceList() {
        names.clear();
        devices.clear();

        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        for (BluetoothDevice bluetoothDevice : pairedDevices) {
            Map<String, String> listItem = new HashMap<>();
            listItem.put(deviceName, bluetoothDevice.getName());
            listItem.put(macAddress, bluetoothDevice.getAddress());
            names.add(listItem);
            devices.add(bluetoothDevice);
        }

        Collections.sort(names, Comparator.comparing(t -> t.get(deviceName)));
        Collections.sort(devices, Comparator.comparing(BluetoothDevice::getName));

        adapter.notifyDataSetChanged();
    }
}
