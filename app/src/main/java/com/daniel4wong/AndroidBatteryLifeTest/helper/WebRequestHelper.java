package com.daniel4wong.AndroidBatteryLifeTest.helper;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daniel4wong.AndroidBatteryLifeTest.manager.DeviceManager;

import java.util.function.Consumer;

import androidx.annotation.Nullable;

public class WebRequestHelper extends AbstractTestHelper {
    private static final String TAG = "=BT= " + WebRequestHelper.class.getName();

    private Context context;

    public WebRequestHelper(Context context) {
        this.context = context;
    }

    public void httpGet(String url, @Nullable Consumer<String> consumer) {
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            if (consumer != null)
                consumer.accept(response);
            Log.i(TAG, response);
        }, error -> {
            if (consumer != null)
                consumer.accept(null);
            Log.i(TAG, error.toString());
        });
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        Log.i(TAG, "Making a web request...");
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
