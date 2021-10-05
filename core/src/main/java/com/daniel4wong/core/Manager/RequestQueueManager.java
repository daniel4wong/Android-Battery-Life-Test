package com.daniel4wong.core.Manager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daniel4wong.core.BaseApplication;
import com.daniel4wong.core.ISingleton;
import com.daniel4wong.core.Singleton;

import org.json.JSONObject;

import java.util.function.Consumer;

public class RequestQueueManager extends Singleton implements ISingleton {
    public static RequestQueueManager getInstance() {
        ISingleton _instance = getInstance(RequestQueueManager.class);
        if (_instance == null)
            RequestQueueManager.init(BaseApplication.context, new RequestQueueManager());
        return (RequestQueueManager) getInstance(RequestQueueManager.class);
    }
    @Override
    public void onLoad() {
        requestQueue = Volley.newRequestQueue(context);

        retryPolicy = new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }
    /// end SingletonΩ

    private static final String TAG = RequestQueueManager.class.getSimpleName();

    private RequestQueue requestQueue;
    private RetryPolicy retryPolicy;

    public void add(int method, String url, JSONObject data,
                    Consumer<JSONObject> onSuccess, Consumer<Exception> onFail) {

        JsonObjectRequest request = new JsonObjectRequest(method, url, data,
                response -> {
                    if (onSuccess != null)
                        onSuccess.accept(response);
                },
                error -> {
                    if (onFail != null)
                        onFail.accept(error);
                });

        request.setRetryPolicy(retryPolicy);

        requestQueue.add(request);
    }

    public void get(String url, Consumer<JSONObject> onSuccess, Consumer<Exception> onFail) {
        add(Request.Method.GET, url, null, onSuccess, onFail);
    }

    public void post(String url, JSONObject data, Consumer<JSONObject> onSuccess, Consumer<Exception> onFail) {
        add(Request.Method.POST, url, data, onSuccess, onFail);
    }
}
