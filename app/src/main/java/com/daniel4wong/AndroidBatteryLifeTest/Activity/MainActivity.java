package com.daniel4wong.AndroidBatteryLifeTest.Activity;

import android.Manifest;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.daniel4wong.AndroidBatteryLifeTest.Core.AppPreferences;
import com.daniel4wong.AndroidBatteryLifeTest.Core.BroadcastReceiver.BatteryTestReceiver;
import com.daniel4wong.AndroidBatteryLifeTest.Helper.LayoutHelper;
import com.daniel4wong.AndroidBatteryLifeTest.Helper.PermissionHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.daniel4wong.AndroidBatteryLifeTest.MainApplication;
import com.daniel4wong.AndroidBatteryLifeTest.AppContext;
import com.daniel4wong.AndroidBatteryLifeTest.R;
import com.daniel4wong.AndroidBatteryLifeTest.databinding.ActivityMainBinding;
import com.daniel4wong.AndroidBatteryLifeTest.Database.AppDatabase;
import com.daniel4wong.AndroidBatteryLifeTest.Helper.LocaleHelper;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private Menu optionMenu;

    BatteryTestReceiver receiver = new BatteryTestReceiver(intent -> {
        boolean isStart = intent.getBooleanExtra(BatteryTestReceiver.STATE, false);
        for(int i = 0; i < optionMenu.size(); i++) {
            optionMenu.getItem(i).setEnabled(!isStart);
        }
        LayoutHelper.setTouchablesEnable(binding.navView, !isStart);
        return null;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        AppContext.getInstance().window = getWindow();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

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

        IntentFilter filter = new IntentFilter();
        filter.addAction(BatteryTestReceiver.ACTION_STATE_CHANGE);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
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

                    if (AppContext.getInstance().getLanguageCode().equals(getString(R.string.lang_chinese)))
                        return;

                    AppPreferences.getInstance().savePreference(buttonLanguageChinese, getString(R.string.lang_chinese));
                    LocaleHelper.updateLocale();
                    MainApplication.restart(this);
                });
                Button buttonLanguageEnglish = dialog.findViewById(R.id.button_language_english);
                buttonLanguageEnglish.setOnClickListener(view -> {
                    dialog.cancel();

                    if (AppContext.getInstance().getLanguageCode().equals(getString(R.string.lang_english)))
                        return;

                    AppPreferences.getInstance().savePreference(buttonLanguageEnglish, getString(R.string.lang_english));
                    LocaleHelper.updateLocale();
                    MainApplication.restart(this);
                });
                dialog.show();
                break;
            }
            case R.id.menu_download: {
                String[] permissions = {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
                PermissionHelper.requirePermission(permissions, () -> {
                    AppDatabase.getInstance().copyDatabase();
                }, () -> {
                    Toast.makeText(this, R.string.msg_permissions_not_grant, Toast.LENGTH_LONG).show();
                });

                break;
            }
            default: {
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}