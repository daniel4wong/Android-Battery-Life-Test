package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.daniel4wong.AndroidBatteryLifeTest.Control.InputButton;
import com.daniel4wong.AndroidBatteryLifeTest.Core.AppPreferences;

import java.util.Arrays;
import java.util.Optional;

public class InputButtonHelper {

    public static void prepareInputs(View[] views) {
        for (View view : views) {
            if (view instanceof InputButton) {
                InputButton button = (InputButton) view;
                button.setGetResult(s -> {
                    AppPreferences.getInstance().savePreference(button.getTag().toString(), s);
                    Optional<View> textViewAny = Arrays.stream(views).filter(i -> i.getTag().equals(button.getTag()) && i instanceof TextView).findAny();
                    if (textViewAny.isPresent()) {
                        TextView textView = (TextView) textViewAny.get();
                        textView.setText(s);
                    }
                });
            } else if (view instanceof Switch) {
                Switch aSwitch = (Switch) view;
                aSwitch.setChecked(AppPreferences.getInstance().getPreference(aSwitch, false));
                aSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
                    AppPreferences.getInstance().savePreference(compoundButton, compoundButton.isChecked());
                });
            } else if (view instanceof Button) {
                //
            } else if (view instanceof TextView) {
                TextView textView = (TextView) view;
                String data = AppPreferences.getInstance().getPreference(textView.getTag().toString(), "");
                if (!data.isEmpty())
                    textView.setText(data);
            }
        }
    }
}
