package com.daniel4wong.core.Bluetooth.Hid;

import java.util.Arrays;

/** Helper class to store the mouse state and retrieve the binary report. */
public class MouseReport {

    private final byte[] mouseData = "BXYW".getBytes();

    MouseReport() {
        Arrays.fill(mouseData, (byte) 0);
    }

    byte[] setValue(boolean left, boolean right, boolean middle, int x, int y, int wheel) {
        int buttons = ((left ? 1 : 0) | (right ? 2 : 0) | (middle ? 4 : 0));
        mouseData[0] = (byte) buttons;
        mouseData[1] = (byte) x;
        mouseData[2] = (byte) y;
        mouseData[3] = (byte) wheel;
        return mouseData;
    }

    byte[] getReport() {
        return mouseData;
    }

    /** Interface to send the Mouse data with. */
    public interface MouseDataSender {
        /**
         * Send the Mouse data to the connected HID Host device.
         *
         * @param left Left mouse button press state.
         * @param right Right mouse button press state.
         * @param middle Middle mouse button (a.k.a. wheel) press state.
         * @param dX Mouse movement along X axis since the last event.
         * @param dY Mouse movement along Y axis since the last event.
         * @param dWheel Mouse wheel rotation since the last event.
         */
        void sendMouse(boolean left, boolean right, boolean middle, int dX, int dY, int dWheel);
    }
}
