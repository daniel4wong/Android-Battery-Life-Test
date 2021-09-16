package com.melsontech.batterytest.ui.home;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.melsontech.batterytest.AppContext;
import com.melsontech.batterytest.AppDatabase;
import com.melsontech.batterytest.BackgroundService;
import com.melsontech.batterytest.R;
import com.melsontech.batterytest.helper.DialogHelper;
import com.melsontech.batterytest.helper.LayoutHelper;
import com.melsontech.batterytest.helper.LineChartHelper;
import com.melsontech.batterytest.databinding.FragmentHomeBinding;
import com.melsontech.batterytest.helper.WebRequestHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;

import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment {
    private static String TAG = HomeFragment.class.getName();

    private boolean drawGraph = false;
    private FragmentHomeBinding binding;

    private Timer chartUpdateTimer;

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

    private LineChartHelper lineChartHelper;

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
                AppContext.getInstance().savePreference(
                        compoundButton.getTag().toString(),
                        compoundButton.isChecked());
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

        String textDate = AppContext.getInstance().preferences.getString(buttonPickDate.getTag().toString(), null);
        if (textDate != null) textViewDate.setText(textDate);
        buttonPickDate.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), (datePicker, y, m, d) -> {
                calendar.set(y, m, d);
                final String date = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
                AppContext.getInstance().savePreference(buttonPickDate.getTag().toString(), date);
                textViewDate.setText(date);
            }, year, month, day);
            dialog.show();
        });

        String textTime = AppContext.getInstance().preferences.getString(buttonPickTime.getTag().toString(), null);
        if (textTime != null) textViewTime.setText(textTime);
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
                AppContext.getInstance().savePreference(buttonPickTime.getTag().toString(), time);
                textViewTime.setText(time);
            }, hour, minute, true);
            dialog.show();
        });

        Integer frequency =  AppContext.getInstance().preferences.getInt(buttonTestFrequency.getTag().toString(), 0);
        textViewTestFrequency.setText(frequency.toString());
        buttonTestFrequency.setOnClickListener(view -> DialogHelper.NumberInputDialog(getActivity(), integer -> {
            if (integer == null)
                return null;
            AppContext.getInstance().savePreference(buttonTestFrequency.getTag().toString(), integer);
            textViewTestFrequency.setText(integer.toString());
            return null;
        }));

        Integer screenTime =  AppContext.getInstance().preferences.getInt(buttonScreenTime.getTag().toString(), 0);
        textViewScreenTime.setText(screenTime.toString());
        buttonScreenTime.setOnClickListener(view -> DialogHelper.NumberInputDialog(getActivity(), integer -> {
            if (integer == null)
                return null;
            AppContext.getInstance().savePreference(buttonScreenTime.getTag().toString(), integer);
            textViewScreenTime.setText(integer.toString());
            return null;
        }));

        buttonPlanTest = binding.buttonPlanTest;
        buttonPlanTest.setOnClickListener(view -> planTest());
        buttonStartTest = binding.buttonStartTest;
        buttonStartTest.setOnClickListener(view -> startTest());
        buttonStopTest = binding.buttonStopTest;
        buttonStopTest.setOnClickListener(view -> stopTest());

        if (drawGraph) {
            this.lineChartHelper = new LineChartHelper();
            this.lineChartHelper.initChart(getActivity(), binding.lineChart);
        } else {
            binding.lineChart.setVisibility(View.INVISIBLE);
        }

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
            aSwitch.setChecked(AppContext.getInstance().preferences.getBoolean(aSwitch.getTag().toString(), false));
        }

        if (drawGraph) {
            startChartUpdateTimer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (chartUpdateTimer != null) {
            chartUpdateTimer.cancel();
            chartUpdateTimer = null;
        }
    }

    public void startChartUpdateTimer() {
        chartUpdateTimer = new Timer();
        chartUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Date begin = new Date();
                begin.setMinutes(0);
                begin.setSeconds(0);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(begin);
                Date fromDate = calendar.getTime();
                calendar.add(Calendar.HOUR, 1);
                Date toDate = calendar.getTime();
                Log.d(TAG, String.format("Query data: from %tT to %tT", fromDate, toDate));

                AppDatabase.getInstance().batteryHistoryDao().getList(fromDate, toDate)
                        .subscribeOn(Schedulers.computation())
                        .subscribe(models -> {
                            List<Pair<Integer, Integer>> data = new ArrayList<>();
                            models.forEach(i -> {
                                if (data.stream().anyMatch(d -> d.first == i.logTs.getMinutes()))
                                    return;
                                data.add(new Pair<>(i.logTs.getMinutes(), i.level.intValue()));
                            });
                            lineChartHelper.setData(data);
                        }, e -> System.out.println("RoomWithRx: " + e.getMessage()));
            }
        }, 1000, 5000);
    }

    private Runnable planTestRunnable = () -> startTest();

    public void planTest() {
        if (!checkConfig())
            return;

        String textDate = AppContext.getInstance().preferences.getString(buttonPickDate.getTag().toString(), null);
        String textTime = AppContext.getInstance().preferences.getString(buttonPickTime.getTag().toString(), null);

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

        Integer testFrequency = AppContext.getInstance().preferences.getInt(getString(R.string.tag_input_test_frequency), 0);

        LayoutHelper.setTouchablesEnable(layoutTestConfig, false);
        buttonStopTest.setEnabled(true);
        BackgroundService.getInstance().start(testFrequency);
    }

    public void stopTest() {
        if (planTestHandler != null) {
            planTestHandler.removeCallbacks(planTestRunnable);
            planTestHandler = null;
        }
        BackgroundService.getInstance().stop();
        LayoutHelper.setTouchablesEnable(layoutTestConfig, true);
    }

    public boolean checkConfig() {
        Integer testFrequency = AppContext.getInstance().preferences.getInt(getString(R.string.tag_input_test_frequency), 0);
        if (testFrequency == null || testFrequency < 1) {
            Toast.makeText(getActivity(), getString(R.string.msg_frequency_warning), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}