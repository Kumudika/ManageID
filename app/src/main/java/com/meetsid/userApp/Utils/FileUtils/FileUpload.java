package com.meetsid.userApp.Utils.FileUtils;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.RequiresPermission;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.MultiPartRequest;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class FileUpload {

    public static void uploadFileWithProgress(final String fileName, String serverUrl, Context context, final FileUploadCallback fileUploadCallback) {

        MultiPartRequest smr = new MultiPartRequest<JSONObject>(Request.Method.POST, serverUrl,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        fileUploadCallback.onComplete(fileName, response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                fileUploadCallback.onError(fileName, error.getMessage());
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
            public void onProgress(long transferredBytes, long totalSize) {
                super.onProgress(transferredBytes, totalSize);
                fileUploadCallback.onProgress(fileName, transferredBytes * 100 / totalSize);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        smr.addFile("image", fileName);

        smr.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(smr);
    }

    @RequiresPermission(WRITE_EXTERNAL_STORAGE)
    public static File saveImage(byte[] content, String extension) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "FACE_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName + "." + extension);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(image);
            out.write(content);
            out.close();
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return image;
    }
}
