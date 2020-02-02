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
import com.android.volley.request.MultiPartRequest;
import com.android.volley.toolbox.HttpHeaderParser;
import com.meetsid.userApp.Utils.AppAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestAPI {
    public static String ENDPOINT = "http://ab6040ee8989c11e98cff0a75697d87c-1513868072.us-east-1.elb.amazonaws.com/v1.0/";
    public static String LOCAL_ENDPOINT = "http://192.168.1.7:8100/v1.0/";
    public static final int METHOD = Request.Method.POST;
    public static final String SIGNIN_PREFIX = "register/new/";
    public static final String LOGIN_PREFIX = "login/face_login/";
    public static String token = "";

    public static void multiPartRequestHandler(String prefix, Context context, HashMap filePara, HashMap param, String title, final ResponseListner listner) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(title);
        progressDialog.setCancelable(false);
        progressDialog.show();
        MultiPartRequest multiPartRequest = new MultiPartRequest<JSONObject>(METHOD, ENDPOINT + prefix,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        listner.onResponse(response);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        if (error.networkResponse.statusCode == -1)
                            AppAlertDialog.errorMessageDialog(context, "Request time out", MessageType.ERROR);
                        else
                            listner.onError(error);
                    }
                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    parsed = new String(response.data);
                }
                try {
                    return Response.success(new JSONObject(parsed), HttpHeaderParser.parseCacheHeaders(response));
                } catch (JSONException e) {
                    return Response.error(new VolleyError(e.getMessage()));
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                Map<String, String> superHeaders = super.getHeaders();
                String k = "Bearer " + token;
                headers.put("Authorization", k);
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.putAll(superHeaders);
                return headers;
            }
        };
        multiPartRequest.setOnProgressListener(new Response.ProgressListener() {
            @Override
            public void onProgress(long transferredBytes, long totalSize) {
                int progress = (int) (transferredBytes * 100 / totalSize);
                progressDialog.setProgress(progress);
            }
        });
        if (filePara != null) {
            for (Map.Entry<String, String> entry : ((Map<String, String>) filePara).entrySet()) {
                multiPartRequest.addFile(entry.getKey(), entry.getValue());
            }
        }
        if (param != null) {
            Map<String, String> map = param;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                multiPartRequest.addStringParam(entry.getKey(), entry.getValue());
            }
        }
        multiPartRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        VolleySingleton.getVolleySingleton(context).addToRequestQueue(multiPartRequest);
    }

    public static void voiceFileUploadHandle(String prefix, Context context, ArrayList<String> files, String phrase, final ResponseListner listner) {
        ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        MultiPartRequest multiPartRequest = new MultiPartRequest<JSONObject>(METHOD, ENDPOINT + prefix,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        listner.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        int statusCode = error.networkResponse.statusCode;
                        switch (statusCode) {
                            case 404:
                            case -1:
                            case 500:
                                AppAlertDialog.serverAlertDialog(context);
                                break;
                        }

                        listner.onError(error);
                    }
                }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    parsed = new String(response.data);
                }
                try {
                    return Response.success(new JSONObject(parsed), HttpHeaderParser.parseCacheHeaders(response));
                } catch (JSONException e) {
                    return Response.error(new VolleyError(e.getMessage()));
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                Map<String, String> superHeaders = super.getHeaders();
                String k = "Bearer " + token;
                headers.put("Authorization", k);
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.putAll(superHeaders);
                return headers;
            }
        };
        int i = 1;
        for (String file : files) {
            String tag = "voice" + i;
            multiPartRequest.addFile(tag, file);
            i++;
        }
        multiPartRequest.addStringParam("phrase", phrase);
        multiPartRequest.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        VolleySingleton.getVolleySingleton(context).addToRequestQueue(multiPartRequest);
    }

    public static void jsonRequestHandle(String prefix, Context context, HashMap param, final ResponseListner listener) {
        ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                METHOD,
                ENDPOINT + prefix,
                new JSONObject(param),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        listener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
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
                if (prefix == SIGNIN_PREFIX || prefix == LOGIN_PREFIX) {
                    Log.i("token:", "no token");
                } else {
                    String k = "Bearer " + token;
                    headers.put("Authorization", k);
                }
                headers.put("Content-Type", "application/json; charset=utf-8");
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

    public static void jsonRequestAPI(String prefix, final Context context, HashMap param, String title, final ResponseListner listener) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(title);
        progressDialog.setCancelable(false);
        progressDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                METHOD,
                ENDPOINT + prefix,
                new JSONObject(param),
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
                        if (error.networkResponse.statusCode == -1)
                            AppAlertDialog.errorMessageDialog(context, "Request time out", MessageType.ERROR);
                        else
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
                if (token.equalsIgnoreCase(null) || token.equals("")) {
                    Log.i("token:", "no token");
                } else {
                    String k = "Bearer " + token;
                    headers.put("Authorization", k);
                }
                headers.put("Content-Type", "application/json; charset=utf-8");
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
