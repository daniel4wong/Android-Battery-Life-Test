package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.daniel4wong.AndroidBatteryLifeTest.R;

import java.util.function.Consumer;
import java.util.function.Function;

public class DialogHelper {

    public static AlertDialog NumberInputDialog(Context context, Consumer<Integer> onInput, Runnable onCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity) context);

        builder.setTitle(context.getString(R.string.header_input_number));
        final EditText editText = new EditText(context);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setRawInputType(Configuration.KEYBOARD_12KEY);
        editText.setMaxLines(1);
        editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(8) });
        builder.setView(editText);
        builder.setPositiveButton(context.getString(R.string.button_confirm), (dialogInterface, i) -> {
            String text = editText.getText().toString();
            try {
                if (onInput != null)
                    onInput.accept(Integer.parseInt(text));
            } catch (Exception e) {
                e.printStackTrace();
            }
            KeyboardHelper.closeKeyboard(context);
        });
        builder.setNegativeButton(context.getString(R.string.button_cancel), (dialogInterface, i) -> {
            if (onCancel != null)
                onCancel.run();
            KeyboardHelper.closeKeyboard(context);
        });
        AlertDialog dialog = builder.show();
        editText.requestFocus();
        KeyboardHelper.showKeyboard(context);

        return dialog;
    }
}
