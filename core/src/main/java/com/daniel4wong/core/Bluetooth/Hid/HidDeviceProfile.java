package com.daniel4wong.core.Bluetooth.Hid;

import static androidx.core.util.Preconditions.checkNotNull;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

/** Wrapper for BluetoothHidDevice profile that manages paired HID Host devices. */
@SuppressLint("RestrictedApi")
@RequiresApi(api = Build.VERSION_CODES.P)
public class HidDeviceProfile {

    private static final String TAG = HidDeviceProfile.class.getSimpleName();

    private static final ParcelUuid HOGP_UUID = ParcelUuid.fromString("00001812-0000-1000-8000-00805f9b34fb");
    private static final ParcelUuid HID_UUID = ParcelUuid.fromString("00001124-0000-1000-8000-00805f9b34fb");

    /** Used to call back when a profile proxy connection state has changed. */
    public interface ServiceStateListener {
        /**
         * Callback to receive the new profile proxy object.
         *
         * @param proxy Profile proxy object or {@code null} if the service was disconnected.
         */
        @MainThread
        void onServiceStateChanged(BluetoothProfile proxy);
    }

    private BluetoothAdapter bluetoothAdapter;
    @Nullable private ServiceStateListener serviceStateListener;
    @Nullable private BluetoothHidDevice service;

    HidDeviceProfile() {
        this.bluetoothAdapter = checkNotNull(BluetoothAdapter.getDefaultAdapter());
    }

    /**
     * Check if a device supports HID Host profile.
     *
     * @param device Device to check.
     * @return {@code true} if the HID Host profile is supported, {@code false} otherwise.
     */
    public boolean isProfileSupported(BluetoothDevice device) {
        // If a device reports itself as a HID Device, then it isn't a HID Host.
        ParcelUuid[] uuidArray = device.getUuids();
        if (uuidArray != null) {
            for (ParcelUuid uuid : uuidArray) {
                if (HID_UUID.equals(uuid) || HOGP_UUID.equals(uuid)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Initiate the connection to the profile proxy service.
     *
     * @param context Context that is required to establish the service connection.
     * @param listener Callback that will receive the profile proxy object.
     */
    @MainThread
    void registerServiceListener(Context context, ServiceStateListener listener) {
        context = checkNotNull(context).getApplicationContext();
        serviceStateListener = checkNotNull(listener);
        bluetoothAdapter.getProfileProxy(context, new ServiceListener(), BluetoothProfile.HID_DEVICE);
    }

    /** Close the profile service connection. */
    @MainThread
    void unregisterServiceListener() {
        if (service != null) {
            try {
                bluetoothAdapter.closeProfileProxy(BluetoothProfile.HID_DEVICE, service);
            } catch (Throwable t) {
                Log.w(TAG, "Error cleaning up proxy", t);
            }
            service = null;
        }
        serviceStateListener = null;
    }

    /**
     * Examine the device for current connection status.
     *
     * @param device Remote Bluetooth device to examine.
     * @return A Bluetooth profile connection state.
     */
    public int getConnectionState(BluetoothDevice device) {
        if (service == null) {
            return BluetoothProfile.STATE_DISCONNECTED;
        }
        return service.getConnectionState(checkNotNull(device));
    }

    /**
     * Initiate the connection to the remote HID Host device.
     *
     * @param device Device to connect to.
     */
    @MainThread
    void connect(BluetoothDevice device) {
        if (service != null && isProfileSupported(device)) {
            service.connect(device);
        }
    }

    /**
     * Close the connection with the remote HID Host device.
     *
     * @param device Device to disconnect from.
     */
    @MainThread
    void disconnect(BluetoothDevice device) {
        if (service != null && isProfileSupported(device)) {
            service.disconnect(device);
        }
    }

    /**
     * Get all devices that are in the "Connected" state.
     *
     * @return Connected devices list.
     */
    @MainThread
    List<BluetoothDevice> getConnectedDevices() {
        if (service == null) {
            return new ArrayList<>();
        }
        return service.getConnectedDevices();
    }

    /**
     * Get all devices that match one of the specified connection states.
     *
     * @param states List of states we are interested in.
     * @return List of devices that match one of the states.
     */
    @MainThread
    List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] states) {
        if (service == null) {
            return new ArrayList<>();
        }
        return service.getDevicesMatchingConnectionStates(states);
    }

    private final class ServiceListener implements BluetoothProfile.ServiceListener {
        @Override
        @MainThread
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            service = (BluetoothHidDevice) proxy;
            if (serviceStateListener != null) {
                serviceStateListener.onServiceStateChanged(service);
            } else {
                bluetoothAdapter.closeProfileProxy(BluetoothProfile.HID_DEVICE, proxy);
            }
        }

        @Override
        @MainThread
        public void onServiceDisconnected(int profile) {
            service = null;
            if (serviceStateListener != null) {
                serviceStateListener.onServiceStateChanged(null);
            }
        }
    }
}
