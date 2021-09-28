package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daniel4wong.AndroidBatteryLifeTest.Core.AppPreferences;
import com.daniel4wong.AndroidBatteryLifeTest.Core.BroadcastReceiver.BatteryTestReceiver;
import com.daniel4wong.AndroidBatteryLifeTest.Manager.DeviceManager;
import com.daniel4wong.AndroidBatteryLifeTest.Model.Constant.LogType;
import com.daniel4wong.AndroidBatteryLifeTest.R;

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
        RequestQueue queue = Volley.newRequestQueue(context);

        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        JSONObject postData = new JSONObject();
        try {
            postData.put("androidId",  androidId);
            postData.put("level", AppPreferences.getInstance().getPreference(R.string.data_web_battery_level, ""));
            postData.put("web",  AppPreferences.getInstance().getPreference(R.string.data_web_request, ""));
            postData.put("gps", AppPreferences.getInstance().getPreference(R.string.data_gps_location, ""));
            postData.put("ble",  AppPreferences.getInstance().getPreference(R.string.data_ble_device_count, ""));
            postData.put("doze", DeviceManager.Power.isDozing());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData, response -> {
            if (consumer != null)
                consumer.accept(response.toString());

            Log.i(TAG, String.format("[Response] %s", response.toString()));
            Intent intent = new Intent();
            intent.setAction(BatteryTestReceiver.ACTION_TEST_CHANGE);
            intent.putExtra(BatteryTestReceiver.STATE, false);
            intent.putExtra(BatteryTestReceiver.TYPE, TYPE);
            try {
                response.put("ts", FormatHelper.dateToString());
                response.put("type", TYPE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent.putExtra(BatteryTestReceiver.TEST_RESULT, response.toString());
            AppPreferences.getInstance().savePreference(R.string.data_web_request, response.toString());
            context.sendBroadcast(intent);
        }, error -> {
            if (consumer != null)
                consumer.accept(null);

            Log.i(TAG, error.toString());
            Intent intent = new Intent();
            intent.setAction(BatteryTestReceiver.ACTION_TEST_CHANGE);
            intent.putExtra(BatteryTestReceiver.STATE, false);
            intent.putExtra(BatteryTestReceiver.TYPE, TYPE);
            context.sendBroadcast(intent);
        });

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

        Log.i(TAG, "[Request] Making a web request...");
        Intent intent = new Intent();
        intent.setAction(BatteryTestReceiver.ACTION_TEST_CHANGE);
        intent.putExtra(BatteryTestReceiver.STATE, true);
        intent.putExtra(BatteryTestReceiver.TYPE, TYPE);
        context.sendBroadcast(intent);

        Log.i(TAG, "Add request to queue...");
        queue.add(request);
    }

    @Override
    public String[] getRequiredPermissions() {
        return new String[0];
    }

    @Override
    public boolean check() {
        return DeviceManager.getInstance().isWiFiEnabled(true);
    }
}
