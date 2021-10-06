package com.daniel4wong.core.Bluetooth.Hid;

import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothHidDeviceAppQosSettings;
import android.bluetooth.BluetoothHidDeviceAppSdpSettings;
import android.os.Build;

import androidx.annotation.RequiresApi;

/** Handy constants for the HID Report Descriptor and SDP configuration. */
@RequiresApi(api = Build.VERSION_CODES.P)
public class Constants {

    static final byte ID_KEYBOARD = 1;
    static final byte ID_MOUSE = 2;
    static final byte ID_BATTERY = 32;

    private static final byte[] HIDD_REPORT_DESC = {
            // Keyboard
            (byte) 0x05, (byte) 0x01, // Usage page (Generic Desktop)
            (byte) 0x09, (byte) 0x06, // Usage (Keyboard)
            (byte) 0xA1, (byte) 0x01, // Collection (Application)
            (byte) 0x85, ID_KEYBOARD, //    Report ID
            (byte) 0x05, (byte) 0x07, //       Usage page (Key Codes)
            (byte) 0x19, (byte) 0xE0, //       Usage minimum (224)
            (byte) 0x29, (byte) 0xE7, //       Usage maximum (231)
            (byte) 0x15, (byte) 0x00, //       Logical minimum (0)
            (byte) 0x25, (byte) 0x01, //       Logical maximum (1)
            (byte) 0x75, (byte) 0x01, //       Report size (1)
            (byte) 0x95, (byte) 0x08, //       Report count (8)
            (byte) 0x81, (byte) 0x02, //       Input (Data, Variable, Absolute) ; Modifier byte
            (byte) 0x75, (byte) 0x08, //       Report size (8)
            (byte) 0x95, (byte) 0x01, //       Report count (1)
            (byte) 0x81, (byte) 0x01, //       Input (Constant)                 ; Reserved byte
            (byte) 0x75, (byte) 0x08, //       Report size (8)
            (byte) 0x95, (byte) 0x06, //       Report count (6)
            (byte) 0x15, (byte) 0x00, //       Logical Minimum (0)
            (byte) 0x25, (byte) 0x65, //       Logical Maximum (101)
            (byte) 0x05, (byte) 0x07, //       Usage page (Key Codes)
            (byte) 0x19, (byte) 0x00, //       Usage Minimum (0)
            (byte) 0x29, (byte) 0x65, //       Usage Maximum (101)
            (byte) 0x81, (byte) 0x00, //       Input (Data, Array)              ; Key array (6 keys)
            (byte) 0xC0,              // End Collection

            // Mouse
            (byte) 0x05, (byte) 0x01, // Usage Page (Generic Desktop)
            (byte) 0x09, (byte) 0x02, // Usage (Mouse)
            (byte) 0xA1, (byte) 0x01, // Collection (Application)
            (byte) 0x85, ID_MOUSE,    //    Report ID
            (byte) 0x09, (byte) 0x01, //    Usage (Pointer)
            (byte) 0xA1, (byte) 0x00, //    Collection (Physical)
            (byte) 0x05, (byte) 0x09, //       Usage Page (Buttons)
            (byte) 0x19, (byte) 0x01, //       Usage minimum (1)
            (byte) 0x29, (byte) 0x03, //       Usage maximum (3)
            (byte) 0x15, (byte) 0x00, //       Logical minimum (0)
            (byte) 0x25, (byte) 0x01, //       Logical maximum (1)
            (byte) 0x75, (byte) 0x01, //       Report size (1)
            (byte) 0x95, (byte) 0x03, //       Report count (3)
            (byte) 0x81, (byte) 0x02, //       Input (Data, Variable, Absolute)
            (byte) 0x75, (byte) 0x05, //       Report size (5)
            (byte) 0x95, (byte) 0x01, //       Report count (1)
            (byte) 0x81, (byte) 0x01, //       Input (constant)                 ; 5 bit padding
            (byte) 0x05, (byte) 0x01, //       Usage page (Generic Desktop)
            (byte) 0x09, (byte) 0x30, //       Usage (X)
            (byte) 0x09, (byte) 0x31, //       Usage (Y)
            (byte) 0x09, (byte) 0x38, //       Usage (Wheel)
            (byte) 0x15, (byte) 0x81, //       Logical minimum (-127)
            (byte) 0x25, (byte) 0x7F, //       Logical maximum (127)
            (byte) 0x75, (byte) 0x08, //       Report size (8)
            (byte) 0x95, (byte) 0x03, //       Report count (3)
            (byte) 0x81, (byte) 0x06, //       Input (Data, Variable, Relative)
            (byte) 0xC0,              //    End Collection
            (byte) 0xC0,              // End Collection

            // Battery
            (byte) 0x05, (byte) 0x0C, // Usage page (Consumer)
            (byte) 0x09, (byte) 0x01, // Usage (Consumer Control)
            (byte) 0xA1, (byte) 0x01, // Collection (Application)
            (byte) 0x85, ID_BATTERY,  //    Report ID
            (byte) 0x05, (byte) 0x01, //    Usage page (Generic Desktop)
            (byte) 0x09, (byte) 0x06, //    Usage (Keyboard)
            (byte) 0xA1, (byte) 0x02, //    Collection (Logical)
            (byte) 0x05, (byte) 0x06, //       Usage page (Generic Device Controls)
            (byte) 0x09, (byte) 0x20, //       Usage (Battery Strength)
            (byte) 0x15, (byte) 0x00, //       Logical minimum (0)
            (byte) 0x26, (byte) 0xff, (byte) 0x00, // Logical maximum (255)
            (byte) 0x75, (byte) 0x08, //       Report size (8)
            (byte) 0x95, (byte) 0x01, //       Report count (3)
            (byte) 0x81, (byte) 0x02, //       Input (Data, Variable, Absolute)
            (byte) 0xC0,              //    End Collection
            (byte) 0xC0,              // End Collection
    };

    private static final String SDP_NAME = "HID Input";
    private static final String SDP_DESCRIPTION = "Android HID Device";
    private static final String SDP_PROVIDER = "Google";
    private static final int QOS_TOKEN_RATE = 800; // 9 bytes * 1000000 us / 11250 us
    private static final int QOS_TOKEN_BUCKET_SIZE = 9;
    private static final int QOS_PEAK_BANDWIDTH = 0;
    private static final int QOS_LATENCY = 11250;

    static final BluetoothHidDeviceAppSdpSettings SDP_RECORD = new BluetoothHidDeviceAppSdpSettings(
                    Constants.SDP_NAME,
                    Constants.SDP_DESCRIPTION,
                    Constants.SDP_PROVIDER,
                    BluetoothHidDevice.SUBCLASS1_COMBO,
                    Constants.HIDD_REPORT_DESC);

    static final BluetoothHidDeviceAppQosSettings QOS_OUT = new BluetoothHidDeviceAppQosSettings(
                    BluetoothHidDeviceAppQosSettings.SERVICE_BEST_EFFORT,
                    Constants.QOS_TOKEN_RATE,
                    Constants.QOS_TOKEN_BUCKET_SIZE,
                    Constants.QOS_PEAK_BANDWIDTH,
                    Constants.QOS_LATENCY,
                    BluetoothHidDeviceAppQosSettings.MAX);
}
