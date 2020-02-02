package com.meetsid.userApp.Utils.ServerUtils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.MultiPartRequest;
import com.android.volley.toolbox.Volley;

/**
 *
 */
public class VolleySingleton {
    private RequestQueue requestQueue;
    private static VolleySingleton volleySingleton;
    public static Context context;

    public VolleySingleton(Context cntx) {
        context = cntx;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getVolleySingleton(Context appContext) {
        if (volleySingleton == null) {
            return volleySingleton = new VolleySingleton(appContext);
        }
        return volleySingleton;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            return requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(JsonObjectRequest jsonObjectRequest) {
        getRequestQueue().add(jsonObjectRequest);
    }

    public <T> void addToRequestQueue(MultiPartRequest multiPartRequest) {
        getRequestQueue().add(multiPartRequest);
    }
}
