package com.meetsid.userApp.Utils.ServerUtils;

import android.content.Context;

import com.android.volley.error.VolleyError;
import com.meetsid.userApp.Utils.AppAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public abstract class ResponseListner {
    public abstract void onError(VolleyError msg);

    public abstract void onResponse(Object response);

    public void onBasicErrorHandler(VolleyError error, Context context, ErrorResponseObject responseObject) {
        int code = error.networkResponse.statusCode;
        if (code == 400 || code == 401) {
            String message = null;
            String responseBody = null;
            JSONObject data = null;
            try {
                responseBody = new String(error.networkResponse.data, "utf-8");
                data = new JSONObject(responseBody);
                message = data.getString("description");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                AppAlertDialog.errorMessageDialog(context, "Unexpected error.Contact support.", MessageType.ERROR);
            } catch (JSONException e) {
                e.printStackTrace();
                AppAlertDialog.errorMessageDialog(context, "Unexpected error.Contact support.", MessageType.ERROR);
            }
            if (message != null && responseObject.type == 1)
                AppAlertDialog.openActivityErrorDialog(context, message, MessageType.ERROR, responseObject.ActivityToOpen);
            else if (message != null && responseObject.type == 2)
                AppAlertDialog.errorMessageDialog(context, message, MessageType.ERROR);
            else
                AppAlertDialog.errorMessageDialog(context, "There is an error.please try again.", MessageType.ERROR);

        }
    }

    public void onCommonErrorHandle(VolleyError error, Context context) {
        int code = error.networkResponse.statusCode;
        if (code >= 500) {
            AppAlertDialog.errorMessageDialog(context, "There is a server error please try again.", MessageType.ERROR);
        } else if (code < 400 && code >= 300) {
            AppAlertDialog.errorMessageDialog(context, "There is a connection problem please try again.", MessageType.ERROR);
        }
    }
}
