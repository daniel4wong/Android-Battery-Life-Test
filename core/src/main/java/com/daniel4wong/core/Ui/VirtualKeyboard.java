package com.daniel4wong.core.Ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.daniel4wong.core.Bluetooth.Helper.HidKeyboardHelper;

@SuppressLint("AppCompatCustomView")
public class VirtualKeyboard extends EditText {

    private boolean isHidden = true;

    public VirtualKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);

        setBackgroundColor(Color.YELLOW);
        if (isHidden) {
            setWidth(1);
            setHeight(1);
            setAlpha(0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            this.addOnUnhandledKeyEventListener((view, keyEvent) -> {
                if (keyEvent.getAction() == KeyEvent.ACTION_UP && keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    eventListener.onKeyPress(HidKeyboardHelper.Key.BACKSPACE);
                    return true;
                }
                return false;
            });
        }

        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // This method is called to notify you that, within s,
                // the count characters beginning at start are about to be replaced by new text with length after.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // This method is called to notify you that, within s,
                // the count characters beginning at start have just replaced old text that had length before.
                if (count > 0) {
                    if (eventListener != null) {
                        char key = charSequence.charAt(start);
                        eventListener.onKeyPress(key);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // This method is called to notify you that, somewhere within s, the text has been changed.
            }
        });
    }

    public interface VirtualKeyboardListener {
        void onKeyPress(Object key);
    }

    VirtualKeyboardListener eventListener;

    public void setVirtualKeyboardListener(VirtualKeyboardListener eventListener) {
        this.eventListener = eventListener;
    }
}
