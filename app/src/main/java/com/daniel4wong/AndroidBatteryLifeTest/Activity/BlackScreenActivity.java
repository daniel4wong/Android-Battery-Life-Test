package com.daniel4wong.AndroidBatteryLifeTest.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.daniel4wong.AndroidBatteryLifeTest.AppPreference;
import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.AndroidBatteryLifeTest.Manager.BatteryTestManager;
import com.daniel4wong.AndroidBatteryLifeTest.databinding.ActivityBlackScreenBinding;
import com.daniel4wong.core.Activity.BaseActivity;

public class BlackScreenActivity extends BaseActivity {

    private ActivityBlackScreenBinding binding;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Integer level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            MainApplication.currentActivity.runOnUiThread(() -> {
                binding.textViewPower.setText(String.format("%d", level) + "%");
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBlackScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().setNavigationBarColor(Color.BLACK);
        getSupportActionBar().hide();

        registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }
    @Override
    public void onBackPressed() {
        if (!AppPreference.isTestStarted()) {
            super.onBackPressed();
            BatteryTestManager.getInstance().reset();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}