package com.daniel4wong.core.Bluetooth.Hid;

import java.util.Arrays;

public class KeyboardReport {

    private final byte[] keyboardData = "M0ABCDEF".getBytes();

    KeyboardReport() {
        Arrays.fill(keyboardData, (byte) 0);
    }

    byte[] setValue(int modifier, int key1, int key2, int key3, int key4, int key5, int key6) {
        keyboardData[0] = (byte) modifier;
        keyboardData[1] = 0;
        keyboardData[2] = (byte) key1;
        keyboardData[3] = (byte) key2;
        keyboardData[4] = (byte) key3;
        keyboardData[5] = (byte) key4;
        keyboardData[6] = (byte) key5;
        keyboardData[7] = (byte) key6;
        return keyboardData;
    }

    byte[] getReport() {
        return keyboardData;
    }

    /** Interface to send the Keyboard data with. */
    public interface KeyboardDataSender {
        /**
         * Send Keyboard data to the connected HID Host device. Up to six buttons pressed
         * simultaneously are supported (not including modifier keys).
         *
         * @param modifier Modifier keys bit mask (Ctrl/Shift/Alt/GUI).
         * @param key1 Scan code of the 1st button that is currently pressed (or 0 if none).
         * @param key2 Scan code of the 2nd button that is currently pressed (or 0 if none).
         * @param key3 Scan code of the 3rd button that is currently pressed (or 0 if none).
         * @param key4 Scan code of the 4th button that is currently pressed (or 0 if none).
         * @param key5 Scan code of the 5th button that is currently pressed (or 0 if none).
         * @param key6 Scan code of the 6th button that is currently pressed (or 0 if none).
         */
        void sendKeyboard(int modifier, int key1, int key2, int key3, int key4, int key5, int key6);
    }
}
