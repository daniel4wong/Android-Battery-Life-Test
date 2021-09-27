package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daniel4wong.AndroidBatteryLifeTest.Core.BroadcastReceiver.BatteryTestReceiver;
import com.daniel4wong.AndroidBatteryLifeTest.Manager.DeviceManager;
import com.daniel4wong.AndroidBatteryLifeTest.Model.Constant.LogType;

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
                response.put("type", TYPE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent.putExtra(BatteryTestReceiver.TEST_RESULT, response.toString());
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
