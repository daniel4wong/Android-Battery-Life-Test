package com.melsontech.batterytest.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

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
import com.melsontech.batterytest.App;
import com.melsontech.batterytest.AppContext;
import com.melsontech.batterytest.R;
import com.melsontech.batterytest.databinding.ActivityMainBinding;
import com.melsontech.batterytest.helper.LocaleHelper;

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
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_config,
                R.id.navigation_dashboard,
                R.id.navigation_notifications
        ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1);
        }

        isShowChart = AppContext.getInstance().preferences.getBoolean(getString(R.string.pref_show_chart), false);
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
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}