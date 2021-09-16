package com.melsontech.batterytest.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.text.InputType;
import android.widget.EditText;

import com.melsontech.batterytest.R;

import java.util.function.Function;

public class DialogHelper {

    public static AlertDialog NumberInputDialog(Activity activity, Function<Integer, Void> onClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(activity.getString(R.string.header_input_number));
        final EditText editText = new EditText(activity);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setRawInputType(Configuration.KEYBOARD_12KEY);
        builder.setView(editText);
        builder.setPositiveButton(activity.getString(R.string.button_confirm), (dialogInterface, i) -> {
            String text = editText.getText().toString();
            onClick.apply(Integer.parseInt(text));
            return;
        });
        builder.setNegativeButton(activity.getString(R.string.button_cancel), (dialogInterface, i) -> {
            KeyboardHelper.closeKeyboard(activity);
        });
        AlertDialog dialog = builder.show();
        editText.requestFocus();
        KeyboardHelper.showKeyboard(activity);

        return dialog;
    }
}
