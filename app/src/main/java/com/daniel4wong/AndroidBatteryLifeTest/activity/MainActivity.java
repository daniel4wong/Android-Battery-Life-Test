package com.daniel4wong.AndroidBatteryLifeTest.activity;

import android.Manifest;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.daniel4wong.AndroidBatteryLifeTest.core.broadcastReceiver.BatteryTestBroadcastReceiver;
import com.daniel4wong.AndroidBatteryLifeTest.helper.LayoutHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.daniel4wong.AndroidBatteryLifeTest.App;
import com.daniel4wong.AndroidBatteryLifeTest.AppContext;
import com.daniel4wong.AndroidBatteryLifeTest.R;
import com.daniel4wong.AndroidBatteryLifeTest.databinding.ActivityMainBinding;
import com.daniel4wong.AndroidBatteryLifeTest.db.AppDatabase;
import com.daniel4wong.AndroidBatteryLifeTest.helper.LocaleHelper;
import com.daniel4wong.AndroidBatteryLifeTest.helper.PermissionHelper;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Menu optionMenu;
    private boolean isShowChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppContext.getInstance().window = getWindow();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_config,
                R.id.navigation_dashboard,
                R.id.navigation_notifications
        ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        isShowChart = AppContext.getInstance().preferences.getBoolean(getString(R.string.pref_show_chart), false);

        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };

        if (!PermissionHelper.checkPermissions(this, permissions))
            ActivityCompat.requestPermissions(this, permissions, 1);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BatteryTestBroadcastReceiver.ACTION);

        registerReceiver(receiver, filter);
    }

    BatteryTestBroadcastReceiver receiver = new BatteryTestBroadcastReceiver(intent -> {
        boolean isStart = intent.getBooleanExtra(BatteryTestBroadcastReceiver.STATE, false);
        for(int i = 0; i < optionMenu.size(); i++) {
            optionMenu.getItem(i).setEnabled(!isStart);
        }
        LayoutHelper.setTouchablesEnable(binding.navView, !isStart);
        return null;
    });

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        optionMenu = menu;
        optionMenu.findItem(R.id.menu_chart_on).setVisible(!isShowChart);
        optionMenu.findItem(R.id.menu_chart_off).setVisible(isShowChart);
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

                    if (AppContext.getInstance().languageCode.equals(getString(R.string.lang_chinese)))
                        return;

                    AppContext.getInstance().languageCode = getString(R.string.lang_chinese);
                    AppContext.getInstance().savePreference(buttonLanguageChinese.getTag().toString(), AppContext.getInstance().languageCode);
                    LocaleHelper.setLocale(AppContext.getInstance().currentActivity, AppContext.getInstance().languageCode);
                    App.restart(this);
                });
                Button buttonLanguageEnglish = dialog.findViewById(R.id.button_language_english);
                buttonLanguageEnglish.setOnClickListener(view -> {
                    dialog.cancel();

                    if (AppContext.getInstance().languageCode.equals(getString(R.string.lang_english)))
                        return;

                    AppContext.getInstance().languageCode = getString(R.string.lang_english);
                    AppContext.getInstance().savePreference(buttonLanguageEnglish.getTag().toString(), AppContext.getInstance().languageCode);
                    LocaleHelper.setLocale(AppContext.getInstance().currentActivity, AppContext.getInstance().languageCode);
                    App.restart(this);
                });
                dialog.show();
                break;
            }
            case R.id.menu_chart_on: {
                item.setVisible(false);
                optionMenu.findItem(R.id.menu_chart_off).setVisible(true);
                AppContext.getInstance().savePreference(getString(R.string.pref_show_chart), true);
                recreate();
                break;
            }
            case R.id.menu_chart_off: {
                item.setVisible(false);
                optionMenu.findItem(R.id.menu_chart_on).setVisible(true);
                AppContext.getInstance().savePreference(getString(R.string.pref_show_chart), false);
                recreate();
                break;
            }
            case R.id.menu_download: {
                AppDatabase.getInstance().copyDatabase();
                break;
            }
            default: {
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}