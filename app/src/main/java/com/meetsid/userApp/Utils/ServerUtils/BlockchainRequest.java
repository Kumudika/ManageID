package com.meetsid.userApp.Utils.ServerUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.meetsid.userApp.Utils.AppAlertDialog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BlockchainRequest {
    public static String BC_ENDPOINT = "http://tokenchain-testnet-1155326049.us-east-1.elb.amazonaws.com/api/";
    public static final int METHOD = Request.Method.POST;

    public static void jsonRequestHandle(String prefix, Context context, JSONObject param, String title, final ResponseListner listener) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(title);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Log.i("Endpoint: {}", BC_ENDPOINT + prefix);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                METHOD,
                BC_ENDPOINT + prefix,
                param,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        listener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        int statusCode = error.networkResponse.statusCode;
                        switch (statusCode) {
                            case 404:
                            case -1:
                            case 500:
                                AppAlertDialog.serverAlertDialog(context);
                                break;
                        }
                        listener.onError(error);
                    }
                }

        ) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                System.out.println(response);
                Log.i("Network Response:", response.toString());
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                Map<String, String> superHeaders = super.getHeaders();
                headers.put("Content-type", "application/json");
                headers.putAll(superHeaders);
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        VolleySingleton.getVolleySingleton(context).addToRequestQueue(jsonObjectRequest);
    }
}
