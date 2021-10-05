package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.daniel4wong.AndroidBatteryLifeTest.AppPreference;
import com.daniel4wong.AndroidBatteryLifeTest.BroadcastReceiver.BatteryReceiver;
import com.daniel4wong.AndroidBatteryLifeTest.Manager.DeviceManager;
import com.daniel4wong.AndroidBatteryLifeTest.Model.Constant.LogType;
import com.daniel4wong.AndroidBatteryLifeTest.R;
import com.daniel4wong.core.Helper.FormatHelper;
import com.daniel4wong.core.Manager.RequestQueueManager;

import java.util.function.Consumer;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class WebRequestHelper extends AbstractTestHelper {
    private static final String TAG = LogType.TEST + WebRequestHelper.class.getSimpleName();
    public static final String TYPE = "WEB";

    private Context context;

    public WebRequestHelper(Context context) {
        this.context = context;
    }

    public void httpGet(String url, @Nullable Consumer<String> consumer) {
        Log.i(TAG, "[Request] Making a web request...");
        context.sendBroadcast(new Intent(BatteryReceiver.ACTION_TEST_CHANGE)
                .putExtra(BatteryReceiver.STATE, true)
                .putExtra(BatteryReceiver.TYPE, TYPE)
        );

        JSONObject postData = new JSONObject();
        try {
            postData.put("androidId", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            postData.put("level", AppPreference.getInstance().getPreference(R.string.data_web_battery_level, ""));
            postData.put("web",  AppPreference.getInstance().getPreference(R.string.data_web_request, ""));
            postData.put("gps", AppPreference.getInstance().getPreference(R.string.data_gps_location, ""));
            postData.put("ble",  AppPreference.getInstance().getPreference(R.string.data_ble_device_count, ""));
            postData.put("doze", DeviceManager.Power.isDozing());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueueManager.getInstance().post(url, postData, response -> {
            if (consumer != null)
                consumer.accept(response.toString());

            Log.i(TAG, String.format("[Response] %s", response.toString()));
            try {
                response.put("ts", FormatHelper.dateToString());
                response.put("type", TYPE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            AppPreference.getInstance().savePreference(R.string.data_web_request, response.toString());
            context.sendBroadcast(new Intent(BatteryReceiver.ACTION_STATE_CHANGE)
                    .putExtra(BatteryReceiver.STATE, false)
                    .putExtra(BatteryReceiver.TYPE, TYPE)
                    .putExtra(BatteryReceiver.TEST_RESULT, response.toString())
            );
        }, error -> {
            if (consumer != null)
                consumer.accept(null);

            Log.i(TAG, String.format("[Error] %s", error.toString()));
            AppPreference.getInstance().savePreference(R.string.data_web_request, error.toString());
            context.sendBroadcast(new Intent(BatteryReceiver.ACTION_TEST_CHANGE)
                    .putExtra(BatteryReceiver.STATE, false)
                    .putExtra(BatteryReceiver.TYPE, TYPE)
                    .putExtra(BatteryReceiver.TEST_RESULT, error.toString())
            );
        });


    }

    @Override
    public String[] getRequiredPermissions() {
        return new String[0];
    }

    @Override
    public boolean check() {
        return DeviceManager.Network.isWiFiEnabled(true);
    }
}
