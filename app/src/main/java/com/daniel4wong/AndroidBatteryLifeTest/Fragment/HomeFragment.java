package com.daniel4wong.AndroidBatteryLifeTest.Fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.daniel4wong.AndroidBatteryLifeTest.Activity.BlackScreenActivity;
import com.daniel4wong.AndroidBatteryLifeTest.Database.AppDatabase;
import com.daniel4wong.AndroidBatteryLifeTest.Model.TestHistory;
import com.daniel4wong.AndroidBatteryLifeTest.R;
import com.daniel4wong.AndroidBatteryLifeTest.Core.AppPreferences;
import com.daniel4wong.AndroidBatteryLifeTest.Core.BroadcastReceiver.BatteryTestReceiver;
import com.daniel4wong.AndroidBatteryLifeTest.Helper.*;
import com.daniel4wong.AndroidBatteryLifeTest.databinding.FragmentHomeBinding;
import com.daniel4wong.AndroidBatteryLifeTest.Manager.BatteryTestManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {
    private static String TAG = HomeFragment.class.getName();

    private FragmentHomeBinding binding;

    private Handler planTestHandler;

    private LinearLayout layoutTestConfig;
    private Button buttonPlanTest;
    private Button buttonStartTest;
    private Button buttonStopTest;
    private Button buttonScreenTime;
    private TextView textViewScreenTime;
    private Button buttonTestFrequency;
    private TextView textViewTestFrequency;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Boolean state = intent.getBooleanExtra(BatteryTestReceiver.STATE, false);
            String type = intent.getStringExtra(BatteryTestReceiver.TYPE);
            String result = intent.getStringExtra(BatteryTestReceiver.TEST_RESULT);
            String date = new SimpleDateFormat("hh:mm:ss").format(new Date());

            if (!state) {
                TestHistory model = new TestHistory();
                model.logTs = Calendar.getInstance().getTime();
                model.type = type;
                model.dataText = result;
                model.markInsert();
                AppDatabase.getInstance().testHistoryDao().insertRecord(model);
            }

            switch (type) {
                case WebRequestHelper.TYPE: {
                    if (!state) {
                        binding.textViewResultWeb.setText(date);
                    }
                    break;
                }
                case GpsLocationHelper.TYPE: {
                    if (!state)
                        binding.textViewResultGps.setText(date);
                    break;
                }
                case BleDeviceHelper.TYPE: {
                    if (!state)
                        binding.textViewResultBle.setText(date);
                    break;
                }
            }
        }
    };

    @SuppressLint("InvalidWakeLockTag")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        layoutTestConfig = binding.layoutTestConfig;

        View[] views = {
                binding.switchScreenAlwaysOn,
                binding.switchCpuAlwaysOn,
                binding.switchWebRequest,
                binding.switchGpsRequest,
                binding.switchBleRequest,
                binding.textViewDate,
                binding.textViewTime,
                binding.textViewScreenTime,
                binding.textViewTestFrequency,
                binding.buttonPickDate,
                binding.buttonPickTime,
                binding.buttonScreenTime,
                binding.buttonTestFrequency
        };
        InputButtonHelper.prepareInputs(views);

        // test buttons
        buttonPlanTest = binding.buttonPlanTest;
        buttonPlanTest.setOnClickListener(view -> planTest());
        buttonStartTest = binding.buttonStartTest;
        buttonStartTest.setOnClickListener(view -> startTest());
        buttonStopTest = binding.buttonStopTest;
        buttonStopTest.setOnClickListener(view -> stopTest());
        binding.buttonBlack.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), BlackScreenActivity.class));
        });
        binding.buttonOnce.setOnClickListener(view -> {
            Integer _screenTime = AppPreferences.getInstance().getPreference(R.string.pref_screen_seconds, 0);
            BatteryTestManager.getInstance().runTestOnce(_screenTime);
        });


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

        IntentFilter filter = new IntentFilter();
        filter.addAction(BatteryTestReceiver.ACTION_TEST_CHANGE);
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();

        getActivity().unregisterReceiver(broadcastReceiver);
    }

    private Runnable planTestRunnable = () -> startTest();

    public void planTest() {
        if (!checkConfig())
            return;
        if (!BatteryTestManager.canRunTest())
            return;

        String textDate = AppPreferences.getInstance().getPreference(R.string.pref_test_bgn_date, "");
        String textTime = AppPreferences.getInstance().getPreference(R.string.pref_test_bgn_time, "");

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
            Toast.makeText(getActivity(), R.string.msg_past_time_warning, Toast.LENGTH_LONG).show();
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
            Toast.makeText(getActivity(), R.string.msg_frequency_warning, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}