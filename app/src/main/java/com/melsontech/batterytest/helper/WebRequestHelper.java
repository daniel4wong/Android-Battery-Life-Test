package com.melsontech.batterytest.helper;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class WebRequestHelper {
    private static final String TAG = "=BT= " + WebRequestHelper.class.getName();

    private Context context;

    public WebRequestHelper(Context context) {
        this.context = context;
    }

    public void httpGet(String url) {
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Log.i(TAG, response);
        }, error -> {
            Log.i(TAG, error.toString());
        });
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        Log.i(TAG, "Making a web request...");
        queue.add(request);
    }
}
