package com.daniel4wong.core.Activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.daniel4wong.core.Helper.PermissionHelper;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    private Runnable allowRunnable;
    private Runnable denyRunnable;

    public static boolean isKeyboardActive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int MIN_KEYBOARD_HEIGHT_PX = 150;
        final View decorView = getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private final Rect windowVisibleDisplayFrame = new Rect();
            private int lastVisibleDecorViewHeight;

            @Override
            public void onGlobalLayout() {
                decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
                final int visibleDecorViewHeight = windowVisibleDisplayFrame.height();

                if (lastVisibleDecorViewHeight != 0) {
                    if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
                        Log.d(TAG, "Keyboard show.");
                        isKeyboardActive = true;
                    } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                        Log.d(TAG, "Keyboard hide.");
                        isKeyboardActive = false;
                    }
                }
                lastVisibleDecorViewHeight = visibleDecorViewHeight;
            }
        });
    }

    public void requestPermissions(String[] permissions, Runnable allowRunnable, Runnable denyRunnable) {
        this.allowRunnable = allowRunnable;
        this.denyRunnable = denyRunnable;

        if (!PermissionHelper.checkPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 0);
        } else {
            if (allowRunnable != null)
                allowRunnable.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (!PermissionHelper.checkPermissions(this, permissions)) {
            if (denyRunnable != null)
                denyRunnable.run();
        } else {
            if (allowRunnable != null)
                allowRunnable.run();
        }
    }
}
