package com.daniel4wong.core.Ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.daniel4wong.core.BasePreference;
import com.daniel4wong.core.Helper.DialogHelper;
import com.daniel4wong.core.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressLint("AppCompatCustomView")
public class InputButton extends android.widget.Button {
    private String format;
    private boolean isDatePicker;
    private boolean isTimePicker;
    private boolean isNumberInput;
    private Consumer<String> getResult;
    private Runnable getCancel;
    private Animation animation;

    public InputButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.InputButton, 0, 0);
        if (array.getBoolean(R.styleable.InputButton_is_date_picker, false)) {
            format = "yyyy-MM-dd";
            isDatePicker = true;
        } else if (array.getBoolean(R.styleable.InputButton_is_time_picker, false)) {
            format = "HH:mm";
            isTimePicker = true;
        } else if (array.getBoolean(R.styleable.InputButton_is_number_input, false)) {
            isNumberInput = true;
        }
        array.recycle();

        setOnClickListener(view -> {
            this.startAnimation(animation);

            if (isDatePicker) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getContext(), (datePicker, y, m, d) -> {
                    calendar.set(y, m, d);
                    final String result = new SimpleDateFormat(format).format(calendar.getTime());
                    if (getResult != null)
                        getResult.accept(result);
                }, year, month, day);
                dialog.setOnCancelListener(dialogInterface -> {
                    if (getCancel != null)
                        getCancel.run();
                });
                dialog.show();
            } else if (isTimePicker) {
                final Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR, 1);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = 0;

                TimePickerDialog dialog = new TimePickerDialog(getContext(), (timePicker, h, m) -> {
                    Date date = new Date();
                    date.setHours(h);
                    date.setMinutes(m);
                    calendar.setTime(date);
                    final String result = new SimpleDateFormat(format).format(calendar.getTime());
                    if (getResult != null)
                        getResult.accept(result);
                }, hour, minute, true);
                dialog.setOnCancelListener(dialogInterface -> {
                    if (getCancel != null)
                        getCancel.run();
                });
                dialog.show();
            } else if (isNumberInput) {
                DialogHelper.NumberInputDialog(getContext(), integer -> {
                    if (getResult != null)
                        getResult.accept(String.valueOf(integer));
                }, () -> {
                    if (getCancel != null)
                        getCancel.run();
                });
            }
        });

        animation = new AlphaAnimation(1f, 0.5f);
        animation.setDuration(200);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(5);
        animation.setRepeatMode(Animation.REVERSE);
    }

    public String getFormat() {
        return format;
    }
    public boolean isDatePicker() {
        return isDatePicker;
    }
    public boolean isTimePicker() {
        return isTimePicker;
    }
    public boolean isNumberInput() {
        return isNumberInput;
    }

    public void setGetResult(Consumer<String> getResult) {
        this.getResult = getResult;
    }

    public static void prepareInputs(View[] views) {
        for (View view : views) {
            if (view instanceof InputButton) {
                InputButton button = (InputButton) view;
                button.setGetResult(s -> {
                    BasePreference.getInstance().savePreference(button.getTag().toString(), s);
                    Optional<View> textViewAny = Arrays.stream(views).filter(i -> i.getTag().equals(button.getTag()) && i instanceof TextView).findAny();
                    if (textViewAny.isPresent()) {
                        TextView textView = (TextView) textViewAny.get();
                        textView.setText(s);
                    }
                });
            } else if (view instanceof Switch) {
                Switch aSwitch = (Switch) view;
                aSwitch.setChecked(BasePreference.getInstance().getPreference(aSwitch, false));
                aSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
                    BasePreference.getInstance().savePreference(compoundButton, compoundButton.isChecked());
                });
            } else if (view instanceof Button) {
                //
            } else if (view instanceof TextView) {
                TextView textView = (TextView) view;
                String data = BasePreference.getInstance().getPreference(textView.getTag().toString(), "");
                if (!data.isEmpty())
                    textView.setText(data);
            } else if (view instanceof RadioGroup) {
                RadioGroup radioGroup = (RadioGroup) view;
                String data = BasePreference.getInstance().getPreference(radioGroup.getTag().toString(), "");
                RadioButton radioButton = radioGroup.findViewWithTag(data);
                if (radioButton != null)
                    radioButton.setChecked(true);
                radioGroup.setOnCheckedChangeListener((_radioGroup, i) -> {
                    RadioButton button = _radioGroup.findViewById(i);
                    BasePreference.getInstance().savePreference(_radioGroup.getTag().toString(), button.getTag().toString());
                });
            }
        }
    }
}
