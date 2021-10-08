package com.daniel4wong.core.Ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import com.daniel4wong.core.Bluetooth.Input.TouchpadGestureDetector;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class VirtualTouchpad extends View {

    private static final int DATA_RATE_LOW_US = 20000;
    private static final int DATA_RATE_HIGH_US = 11250;

    private ScheduledThreadPoolExecutor executor;
    private ScheduledFuture<?> scheduledFuture;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MouseButton.LEFT, MouseButton.RIGHT, MouseButton.MIDDLE})
    public @interface MouseButton {
        int LEFT = 0;
        int RIGHT = 1;
        int MIDDLE = 2;
    }

    private static final class ButtonEvent {
        final @MouseButton int button;
        final boolean state;

        ButtonEvent(@MouseButton int b, boolean s) {
            button = b;
            state = s;
        }
    }
    private final List<ButtonEvent> pendingEvents = new ArrayList<>();

    private MouseData lastMouseData;
    private float dX;
    private float dY;
    private float dWheel;
    private boolean leftButton;
    private boolean rightButton;

    private TouchpadGestureDetector gestureDetector;
    private TouchpadGestureDetector.GestureListener gestureListener;

    public VirtualTouchpad(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        executor = new ScheduledThreadPoolExecutor(1);
        scheduledFuture = executor.scheduleAtFixedRate(this::sendData, 0, DATA_RATE_LOW_US, TimeUnit.MICROSECONDS);

        gestureListener = new TouchpadGestureListener();
        gestureDetector = new TouchpadGestureDetector(getContext(), gestureListener);

        setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                // Prevent Swipe-To-Dismiss
                MotionEvent cancel = MotionEvent.obtain(motionEvent);
                cancel.setAction(MotionEvent.ACTION_CANCEL);
                cancel.recycle();
            }
            gestureDetector.onTouchEvent(motionEvent);
            return true;
        });
    }

    private class TouchpadGestureListener implements TouchpadGestureDetector.GestureListener {
        @Override
        public void onLeftDown() {
            sendButtonEvent(MouseButton.LEFT, true);
        }

        @Override
        public void onRightDown() {
            sendButtonEvent(MouseButton.RIGHT, true);
        }

        @Override
        public void onLeftUp() {
            sendButtonEvent(MouseButton.LEFT, false);
        }

        @Override
        public void onRightUp() {
            sendButtonEvent(MouseButton.RIGHT, false);
        }

        @Override
        public void onMove(float x, float y) {
            synchronized (pendingEvents) {
                dX += x;
                dY += y;
            }
        }

        @Override
        public void onScroll(float wheel) {
            synchronized (pendingEvents) {
                dWheel += wheel;
            }
        }

        private void sendButtonEvent(@MouseButton int button, boolean state) {
            synchronized (pendingEvents) {
                // Looks like one event is not enough
                ButtonEvent event = new ButtonEvent(button, state);
                pendingEvents.add(event);
                pendingEvents.add(event);
            }
        }
    }

    private void sendData() {
        int x, y, wheel;
        synchronized (pendingEvents) {
            x = (int) dX;
            y = (int) dY;
            wheel = (int) dWheel;
            dX -= x;
            dY -= y;
            dWheel -= wheel;

            if (!pendingEvents.isEmpty()) {
                ButtonEvent event = pendingEvents.remove(0);
                if (event.button == MouseButton.LEFT) {
                    leftButton = event.state;
                } else {
                    rightButton = event.state;
                }
            }
        }

        MouseData mouseData = new MouseData(leftButton, rightButton, false, x, y, wheel);

        if (eventListener != null && !mouseData.equals(lastMouseData))
            eventListener.onUpdate(mouseData);

        lastMouseData = mouseData;
    }

    public class MouseData {
        public boolean left;
        public boolean right;
        public boolean middle;
        public int dX;
        public int dY;
        public int dWheel;

        public MouseData(boolean left, boolean right, boolean middle, int dX, int dY, int dWheel) {
            this.left = left;
            this.right = right;
            this.middle = middle;
            this.dX = dX;
            this.dY = dY;
            this.dWheel = dWheel;
        }

        public boolean equals(MouseData mouseData) {
            if (mouseData == null)
                return false;

            return left == mouseData.left
                    && right == mouseData.right
                    && middle == mouseData.middle
                    && dX == mouseData.dX
                    && dY == mouseData.dY
                    && dWheel == mouseData.dWheel;
        }
    }

    public interface VirtualTouchpadListener {
        void onUpdate(MouseData mouseData);
    }
    VirtualTouchpad.VirtualTouchpadListener eventListener;
    public void setVirtualTouchpadListener(VirtualTouchpad.VirtualTouchpadListener eventListener) {
        this.eventListener = eventListener;
    }
}