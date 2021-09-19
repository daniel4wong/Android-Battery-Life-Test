package com.daniel4wong.AndroidBatteryLifeTest.ui.home;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.daniel4wong.AndroidBatteryLifeTest.R;
import com.daniel4wong.AndroidBatteryLifeTest.core.AppPreferences;
import com.daniel4wong.AndroidBatteryLifeTest.helper.*;
import com.daniel4wong.AndroidBatteryLifeTest.databinding.FragmentHomeBinding;
import com.daniel4wong.AndroidBatteryLifeTest.manager.BatteryTestManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {
    private static String TAG = HomeFragment.class.getName();

    private FragmentHomeBinding binding;

    private Handler planTestHandler;

    private LinearLayout layoutTestConfig;
    private Switch switchScreenAlwaysOn;
    private Switch switchCpuAlwaysOn;
    private Switch switchWebRequest;
    private Switch switchGpsRequest;
    private Switch switchBleRequest;
    private Button buttonPickDate;
    private Button buttonPickTime;
    private TextView textViewDate;
    private TextView textViewTime;
    private Button buttonPlanTest;
    private Button buttonStartTest;
    private Button buttonStopTest;
    private Button buttonScreenTime;
    private TextView textViewScreenTime;
    private Button buttonTestFrequency;
    private TextView textViewTestFrequency;

    @SuppressLint("InvalidWakeLockTag")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        layoutTestConfig = binding.layoutTestConfig;

        switchScreenAlwaysOn = binding.switchScreenAlwaysOn;
        switchCpuAlwaysOn = binding.switchCpuAlwaysOn;
        switchWebRequest = binding.switchWebRequest;
        switchGpsRequest = binding.switchGpsRequest;
        switchBleRequest = binding.switchBleRequest;

        Switch[] switches = {
                switchScreenAlwaysOn,
                switchCpuAlwaysOn,
                switchWebRequest,
                switchGpsRequest,
                switchBleRequest
        };
        for (Switch aSwitch : switches) {
            aSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
                AppPreferences.getInstance().savePreference(compoundButton, compoundButton.isChecked());
            });
        }

        buttonPickDate = binding.buttonPickDate;
        buttonPickTime = binding.buttonPickTime;
        buttonScreenTime = binding.buttonScreenTime;
        buttonTestFrequency = binding.buttonTestFrequency;

        textViewDate = binding.textViewDate;
        textViewTime = binding.textViewTime;
        textViewScreenTime = binding.textViewScreenTime;
        textViewTestFrequency = binding.textViewTestFrequency;

        String textDate = AppPreferences.getInstance().getPreference(buttonPickDate, "");
        if (!textDate.isEmpty())
            textViewDate.setText(textDate);
        buttonPickDate.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), (datePicker, y, m, d) -> {
                calendar.set(y, m, d);
                final String date = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
                AppPreferences.getInstance().savePreference(buttonPickDate, date);
                textViewDate.setText(date);
            }, year, month, day);
            dialog.show();
        });

        String textTime = AppPreferences.getInstance().getPreference(buttonPickTime, "");
        if (!textTime.isEmpty())
            textViewTime.setText(textTime);
        buttonPickTime.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 1);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = 0;

            TimePickerDialog dialog = new TimePickerDialog(getActivity(), (timePicker, h, m) -> {
                Date date = new Date();
                date.setHours(h);
                date.setMinutes(m);
                calendar.setTime(date);
                final String time = new SimpleDateFormat("HH:mm").format(calendar.getTime());
                AppPreferences.getInstance().savePreference(buttonPickTime, time);
                textViewTime.setText(time);
            }, hour, minute, true);
            dialog.show();
        });

        Integer frequency = AppPreferences.getInstance().getPreference(buttonTestFrequency, 0);
        textViewTestFrequency.setText(frequency.toString());
        buttonTestFrequency.setOnClickListener(view -> DialogHelper.NumberInputDialog(getActivity(), integer -> {
            if (integer == null)
                return null;
            AppPreferences.getInstance().savePreference(buttonTestFrequency, integer);
            textViewTestFrequency.setText(integer.toString());
            return null;
        }));

        Integer screenTime =  AppPreferences.getInstance().getPreference(buttonScreenTime, 0);
        textViewScreenTime.setText(screenTime.toString());
        buttonScreenTime.setOnClickListener(view -> DialogHelper.NumberInputDialog(getActivity(), integer -> {
            if (integer == null)
                return null;
            AppPreferences.getInstance().savePreference(buttonScreenTime, integer);
            textViewScreenTime.setText(integer.toString());
            return null;
        }));

        // test buttons
        buttonPlanTest = binding.buttonPlanTest;
        buttonPlanTest.setOnClickListener(view -> planTest());
        buttonStartTest = binding.buttonStartTest;
        buttonStartTest.setOnClickListener(view -> startTest());
        buttonStopTest = binding.buttonStopTest;
        buttonStopTest.setOnClickListener(view -> stopTest());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        Switch[] switches = {
                switchScreenAlwaysOn,
                switchCpuAlwaysOn,
                switchWebRequest,
                switchGpsRequest,
                switchBleRequest
        };
        for (Switch aSwitch : switches) {
            aSwitch.setChecked(AppPreferences.getInstance().getPreference(aSwitch, false));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private Runnable planTestRunnable = () -> startTest();

    public void planTest() {
        if (!checkConfig())
            return;
        if (!BatteryTestManager.canRunTest())
            return;

        String textDate = AppPreferences.getInstance().getPreference(buttonPickDate, "");
        String textTime = AppPreferences.getInstance().getPreference(buttonPickTime, "");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar planTime = Calendar.getInstance();
        try {
            planTime.setTime(format.parse(String.format("%s %s", textDate, textTime)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar now = Calendar.getInstance();

        long millis = planTime.getTimeInMillis() - now.getTimeInMillis();
        if (millis <= 0) {
            Toast.makeText(getActivity(), getString(R.string.msg_past_time_warning), Toast.LENGTH_LONG).show();
            return;
        }

        LayoutHelper.setTouchablesEnable(layoutTestConfig, false);
        buttonStopTest.setEnabled(true);

        planTestHandler = new Handler();
        planTestHandler.postDelayed(planTestRunnable, millis);
    }

    public void startTest() {
        if (!checkConfig())
            return;
        if (!BatteryTestManager.canRunTest())
            return;

        LayoutHelper.setTouchablesEnable(layoutTestConfig, false);
        buttonStopTest.setEnabled(true);

        Integer testFrequency = AppPreferences.getInstance().getPreference(R.string.pref_test_frequency, 0);
        BatteryTestManager.getInstance().start(testFrequency);
    }

    public void stopTest() {
        if (planTestHandler != null) {
            planTestHandler.removeCallbacks(planTestRunnable);
            planTestHandler = null;
        }
        BatteryTestManager.getInstance().stop();
        LayoutHelper.setTouchablesEnable(layoutTestConfig, true);
    }

    public boolean checkConfig() {
        Integer testFrequency = AppPreferences.getInstance().getPreference(R.string.pref_test_frequency, 0);
        if (testFrequency == null || testFrequency < 1) {
            Toast.makeText(getActivity(), getString(R.string.msg_frequency_warning), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}