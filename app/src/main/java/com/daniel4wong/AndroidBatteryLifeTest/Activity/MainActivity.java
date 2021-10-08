package com.daniel4wong.AndroidBatteryLifeTest.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.daniel4wong.AndroidBatteryLifeTest.AppPreference;
import com.daniel4wong.AndroidBatteryLifeTest.BroadcastReceiver.BatteryReceiver;
import com.daniel4wong.core.Activity.BaseActivity;
import com.daniel4wong.core.Activity.HidActivity;
import com.daniel4wong.core.Helper.LayoutHelper;
import com.daniel4wong.core.Helper.PermissionHelper;
import com.daniel4wong.AndroidBatteryLifeTest.Manager.BatteryTestManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.core.BaseContext;
import com.daniel4wong.AndroidBatteryLifeTest.R;
import com.daniel4wong.AndroidBatteryLifeTest.databinding.ActivityMainBinding;
import com.daniel4wong.AndroidBatteryLifeTest.Database.AppDatabase;
import com.daniel4wong.core.Helper.LocaleHelper;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private Menu optionMenu;
    private MenuItem menuStart;
    private MenuItem menuStop;

    BatteryReceiver receiver = new BatteryReceiver(intent -> {
        boolean isStart = intent.getBooleanExtra(BatteryReceiver.STATE, false);
        if (optionMenu != null) {
            for(int i = 0; i < optionMenu.size(); i++) {
                MenuItem item = optionMenu.getItem(i);
                if (item.getItemId() == R.id.menu_start || item.getItemId() == R.id.menu_stop)
                    continue;
                item.setEnabled(!isStart);
            }
        }
        LayoutHelper.setTouchablesEnable(binding.navView, !isStart);
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        BaseContext.getInstance().window = getWindow();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_config,
                R.id.navigation_dashboard,
                R.id.navigation_notifications
        ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BatteryReceiver.ACTION_STATE_CHANGE);
        registerReceiver(receiver, filter);

        if (Long.valueOf(AppPreference.getInstance().getPreference(R.string.pref_test_period_seconds, "0")) <= 0)
            AppPreference.getInstance().savePreference(R.string.flag_state_test_started, false);

        if (AppPreference.isTestStarted())
            BatteryTestManager.Job.startJob();

        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);

        startActivity(new Intent(this, HidActivity.class));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        optionMenu = menu;
        for (int i = 0; i < optionMenu.size(); i++) {
            switch (optionMenu.getItem(i).getItemId()) {
                case R.id.menu_start:
                    menuStart = optionMenu.getItem(i);
                    menuStart.setVisible(!AppPreference.isTestStarted());
                    break;
                case R.id.menu_stop:
                    menuStop = optionMenu.getItem(i);
                    menuStop.setVisible(AppPreference.isTestStarted());
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_language: {
                BottomSheetDialog dialog = new BottomSheetDialog(this);
                dialog.setContentView(R.layout.layout_bottom_sheet_language);
                Button buttonLanguageChinese = dialog.findViewById(R.id.button_language_chinese);
                buttonLanguageChinese.setOnClickListener(view -> {
                    dialog.cancel();

                    if (AppPreference.Store.setLanguageCode(com.daniel4wong.core.R.string.language_chinese)) {
                        LocaleHelper.setLocale(MainApplication.currentActivity, AppPreference.Store.getLanguageCode());
                        MainApplication.restart(this);
                    }

                });
                Button buttonLanguageEnglish = dialog.findViewById(R.id.button_language_english);
                buttonLanguageEnglish.setOnClickListener(view -> {
                    dialog.cancel();

                    if (AppPreference.Store.setLanguageCode(com.daniel4wong.core.R.string.language_english)) {
                        LocaleHelper.setLocale(MainApplication.currentActivity, AppPreference.Store.getLanguageCode());
                        MainApplication.restart(this);
                    }
                });
                dialog.show();
                break;
            }
            case R.id.menu_download: {
                String[] permissions = {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
                PermissionHelper.requirePermission(permissions, () -> {
                    AppDatabase.getInstance().copyDatabase(MainApplication.currentActivity, getString(R.string.msg_database_downloaded_success));
                }, () -> {
                    Toast.makeText(this, R.string.msg_permissions_not_grant, Toast.LENGTH_LONG).show();
                });
                break;
            }
            case R.id.menu_start: {
                if (BatteryTestManager.Job.startJob()) {
                    menuStart.setVisible(false);
                    menuStop.setVisible(true);
                }
                break;
            }
            case R.id.menu_stop: {
                if (BatteryTestManager.Job.stopJob()) {
                    menuStart.setVisible(true);
                    menuStop.setVisible(false);
                }
                break;
            }
            default: {
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}