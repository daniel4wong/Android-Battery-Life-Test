package com.daniel4wong.AndroidBatteryLifeTest.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.daniel4wong.AndroidBatteryLifeTest.R;

import java.util.function.Function;

public class DialogHelper {

    public static AlertDialog NumberInputDialog(Activity activity, Function<Integer, Void> onClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(activity.getString(R.string.header_input_number));
        final EditText editText = new EditText(activity);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setRawInputType(Configuration.KEYBOARD_12KEY);
        editText.setMaxLines(1);
        editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2) });
        builder.setView(editText);
        builder.setPositiveButton(activity.getString(R.string.button_confirm), (dialogInterface, i) -> {
            String text = editText.getText().toString();
            onClick.apply(Integer.parseInt(text));
            KeyboardHelper.closeKeyboard(activity);
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
