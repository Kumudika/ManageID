package com.meetsid.userApp.Utils.ServerUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.error.VolleyError;
import com.google.gson.Gson;
import com.meetsid.userApp.Activities.DeclinedPage;
import com.meetsid.userApp.Activities.DocumentSelection;
import com.meetsid.userApp.Activities.EmailVerification;
import com.meetsid.userApp.Activities.InstituteVerification;
import com.meetsid.userApp.Activities.MobileVerification;
import com.meetsid.userApp.Activities.NavActivity;
import com.meetsid.userApp.Activities.PasscodeActivity;
import com.meetsid.userApp.Activities.PaymentGateway;
import com.meetsid.userApp.Activities.SignUp;
import com.meetsid.userApp.Activities.SuccessActivity;
import com.meetsid.userApp.Activities.VoiceLogin;
import com.meetsid.userApp.BuildConfig;
import com.meetsid.userApp.MainActivity;
import com.meetsid.userApp.Models.Token;
import com.meetsid.userApp.Utils.AppAlertDialog;
import com.meetsid.userApp.Utils.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Stack;

public abstract class MeetSIDRequestAPI {
    private boolean success = false;

    public abstract void docRetrieve(HashMap para, Context context, String type);

    public abstract void addDocument(HashMap stringPara, Context context);

    public abstract void addFace(HashMap stringPara, Context context);

    public abstract void addVoice(HashMap stringPara, HashMap filePara, Context context);

    public abstract void addPayment(HashMap hashMap, Context context);

    public abstract void verfifyMobile(HashMap hashMap, Context context);

    public abstract void updateProfile(HashMap hashMap, Context context);

    public void create(HashMap hashMap, Context context) {
        RequestAPI.ENDPOINT = "http://ab6040ee8989c11e98cff0a75697d87c-1513868072.us-east-1.elb.amazonaws.com/v1.0/";
        RequestAPI.jsonRequestAPI("register/new/", context, hashMap, "Creating user..", new ResponseListner() {
            @Override
            public void onError(VolleyError msg) {
                Log.e("Sign up error: ", msg.toString());
                int code = msg.networkResponse.statusCode;
                if (code == 400) {
                    ErrorResponseObject object = new ErrorResponseObject();
                    object.type = 2;
                    object.msgType = "error";
                    object.ActivityToOpen = MainActivity.class;
                    super.onBasicErrorHandler(msg, context, object);
                } else {
                    super.onCommonErrorHandle(msg, context);
                }
            }

            @Override
            public void onResponse(Object response) {
                try {
                    if (((JSONObject) response).get("user_status").toString().equals("EMAIL_PENDING")) {
                        String email = ((JSONObject) response).get("username").toString();
                        SharedPreferences preferences = context.getSharedPreferences(Common.username, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Common.username, email);
                        editor.apply();
                        Intent intent = new Intent(context, EmailVerification.class);
                        intent.putExtra("email", email);
                        context.startActivity(intent);
                    } else {
                        SharedPreferences prf = context.getSharedPreferences(Common.tokenData, Context.MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = prf.getString(Common.tokenData, "");
                        Token tokenData = gson.fromJson(json, Token.class);
                        if (tokenData == null) {
                            AppAlertDialog.openActivityErrorDialog(context, "Please recover your account.",
                                    MessageType.ERROR, MainActivity.class);
                        } else {
                            if (((JSONObject) response).get("user_status").toString().equals("DOCUMENT_PENDING")) {
                                Log.i("user_status: ", ((JSONObject) response).get("user_status").toString());
                                AppAlertDialog.openActivityErrorDialog(context,
                                        "You have already registered. Please complete the registration.", MessageType.INFO, DocumentSelection.class);
                            } else if (((JSONObject) response).get("user_status").toString().equals("FUND_PENDING")) {
                                Log.i("user_status: ", ((JSONObject) response).get("user_status").toString());
                                AppAlertDialog.openActivityErrorDialog(context,
                                        "You have already registered. Please complete the registration.", MessageType.INFO, PaymentGateway.class);
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    AppAlertDialog.errorMessageDialog(context, "Unexpected error.Contact support.", MessageType.ERROR);
                }
            }
        });
    }

    public void verifyMail(HashMap hashMap, Context context) {
        RequestAPI.ENDPOINT = "http://ab6040ee8989c11e98cff0a75697d87c-1513868072.us-east-1.elb.amazonaws.com/v1.0/";
        RequestAPI.jsonRequestAPI("register/approve_email/", context, hashMap, "Verifying mail..", new ResponseListner() {
            @Override
            public void onError(VolleyError msg) {
                Log.e("Email verify error: ", msg.toString());
                String message = null;
                int code = msg.networkResponse.statusCode;
                if (code == 401) {
                    String responseBody = null;
                    JSONObject data = null;
                    String errorCode = null;
                    try {
                        responseBody = new String(msg.networkResponse.data, "utf-8");
                        data = new JSONObject(responseBody);
                        message = data.getString("description");
                        errorCode = data.getString("code");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (message != null && errorCode.equals("USER_EXIST"))
                        AppAlertDialog.openActivityErrorDialog(context, message, MessageType.ERROR, MainActivity.class);
                    else if (message != null && errorCode.equals("INVALID_EMAIL_CODE"))
                        AppAlertDialog.errorMessageDialog(context, message, MessageType.ERROR);
                    else
                        AppAlertDialog.errorMessageDialog(context, "There is an error.please try again.", MessageType.ERROR);
                } else
                    super.onCommonErrorHandle(msg, context);
            }

            @Override
            public void onResponse(Object response) {
                try {
                    String token = ((JSONObject) response).get("token").toString();
                    SharedPreferences preferences = context.getSharedPreferences(Common.emailToken, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(Common.emailToken, token);
                    editor.apply();
                    Intent intent = new Intent(context, MobileVerification.class);
                    intent.putExtra("username", ((JSONObject) response).get("username").toString());
                    intent.putExtra("token", ((JSONObject) response).get("token").toString());
                    context.startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    AppAlertDialog.errorMessageDialog(context, "Unexpected error.Contact support.", MessageType.ERROR);
                }
            }
        });
    }

    public void resendMailCode(HashMap hashMap, Context context) {
        RequestAPI.ENDPOINT = "http://ab6040ee8989c11e98cff0a75697d87c-1513868072.us-east-1.elb.amazonaws.com/v1.0/";
        RequestAPI.jsonRequestAPI("register/resend_email/", context, hashMap, "Resending mail..", new ResponseListner() {
            @Override
            public void onError(VolleyError msg) {
                Log.e("Email resend error: ", msg.toString());
                int code = msg.networkResponse.statusCode;
                if (code == 400) {
                    ErrorResponseObject object = new ErrorResponseObject();
                    object.type = 2;
                    object.msgType = "error";
                    super.onBasicErrorHandler(msg, context, object);
                } else {
                    super.onCommonErrorHandle(msg, context);
                }

            }

            @Override
            public void onResponse(Object response) {
            }
        });
    }

    public void resendMobileCode(HashMap hashMap, Context context) {
        RequestAPI.ENDPOINT = "http://ab6040ee8989c11e98cff0a75697d87c-1513868072.us-east-1.elb.amazonaws.com/v1.0/";
        RequestAPI.jsonRequestAPI("register/resend_mobile/", context, hashMap, "Resending mail..", new ResponseListner() {
            @Override
            public void onError(VolleyError msg) {
                Log.e("Mobile resend error: ", msg.toString());
                int code = msg.networkResponse.statusCode;
                if (code == 400) {
                    ErrorResponseObject object = new ErrorResponseObject();
                    object.type = 2;
                    object.msgType = "error";
                    super.onBasicErrorHandler(msg, context, object);
                } else {
                    super.onCommonErrorHandle(msg, context);
                }
            }

            @Override
            public void onResponse(Object response) {
            }
        });
    }

    public void addPIN(HashMap hashMap, Context context) {
        RequestAPI.ENDPOINT = "http://ab6040ee8989c11e98cff0a75697d87c-1513868072.us-east-1.elb.amazonaws.com/v1.0/";
        RequestAPI.jsonRequestAPI("register/setup_pincode/", context, hashMap, "Setting passcode..", new ResponseListner() {
            @Override
            public void onError(VolleyError msg) {
                Log.e("pincode setup error: ", msg.toString());
                String message = null;
                int code = msg.networkResponse.statusCode;
                if (code == 400) {
                    String responseBody = null;
                    JSONObject data = null;
                    String errorCode = null;
                    try {
                        responseBody = new String(msg.networkResponse.data, "utf-8");
                        data = new JSONObject(responseBody);
                        message = data.getString("description");
                        errorCode = data.getString("code");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        AppAlertDialog.errorMessageDialog(context, "Unexpected error.Contact support.", MessageType.ERROR);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AppAlertDialog.errorMessageDialog(context, "Unexpected error.Contact support.", MessageType.ERROR);
                    }
                    if (message != null && errorCode.equals("INVALID_USER"))
                        AppAlertDialog.openActivityErrorDialog(context, message, MessageType.ERROR, SignUp.class);
                    else if (message != null && errorCode.equals("INVALID_REQUEST"))
                        AppAlertDialog.errorMessageDialog(context, message, MessageType.ERROR);
                    else
                        AppAlertDialog.errorMessageDialog(context, "There is an error.please try again.", MessageType.ERROR);
                } else
                    super.onCommonErrorHandle(msg, context);
            }

            @Override
            public void onResponse(Object response) {
                try {
                    Intent intent = new Intent(context, DocumentSelection.class);
                    context.startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void faceLogin(HashMap hashMap, Context context, HashMap filePara) {
        RequestAPI.ENDPOINT = "http://ab6040ee8989c11e98cff0a75697d87c-1513868072.us-east-1.elb.amazonaws.com/v1.0/";
        RequestAPI.multiPartRequestHandler("login/face_login/", context, filePara, hashMap, "Processing face..", new ResponseListner() {
            @Override
            public void onError(VolleyError msg) {
                Log.e("face login error: ", msg.toString());
                String message = null;
                int code = msg.networkResponse.statusCode;
                if (code == 401) {
                    String responseBody = null;
                    JSONObject data = null;
                    String errorCode = null;
                    try {
                        responseBody = new String(msg.networkResponse.data, "utf-8");
                        data = new JSONObject(responseBody);
                        message = data.getString("description");
                        errorCode = data.getString("code");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        AppAlertDialog.errorMessageDialog(context, "Unexpected error.Contact support.", MessageType.ERROR);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AppAlertDialog.errorMessageDialog(context, "Unexpected error.Contact support.", MessageType.ERROR);
                    }
                    if (message != null && (errorCode.equals("INVALID_TOKEN") || errorCode.equals("EXPIRED_TOKEN")))
                        AppAlertDialog.openActivityErrorDialog(context, message, MessageType.ERROR, MainActivity.class);
                    else if (message != null && (errorCode.equals("LIVENESS_FAIL") || errorCode.equals("INVALID_USER") || errorCode.equals("FACE_NOT_FOUND")))
                        AppAlertDialog.errorMessageDialog(context, message, MessageType.ERROR);
                    else if (message != null && errorCode.equals("FACE_AUTH_FAIL"))
                        AppAlertDialog.errorMessageDialog(context, message, MessageType.ERROR);
                    else
                        AppAlertDialog.errorMessageDialog(context, "There is an error.please try again.", MessageType.ERROR);
                } else
                    super.onCommonErrorHandle(msg, context);

            }

            @Override
            public void onResponse(Object response) {
                try {
                    RequestAPI.token = ((JSONObject) response).get("token").toString();
                    String code = ((JSONObject) response).has("status") ?
                            ((JSONObject) ((JSONObject) response).get("status")).get("code").toString() : "0";
                    if (code.equalsIgnoreCase("0")) {
                        JSONObject json = new JSONObject(((JSONObject) response).get("user").toString());
                        Gson gson = new Gson();
                        SharedPreferences preferences = context.getSharedPreferences(Common.tokenData, Context.MODE_PRIVATE);
                        String data = preferences.getString(Common.tokenData, "");
                        Token token = gson.fromJson(data, Token.class);
                        SharedPreferences.Editor editor = preferences.edit();
                        if (json.has("verificationMedal")) {
                            token.setVerificationMedal(json.getInt("verificationMedal"));
                        } else {
                            token.setVerificationMedal(0);
                        }
                        String tokenData = gson.toJson(token);
                        editor.putString(Common.tokenData, tokenData);
                        editor.apply();
                        preferences = context.getSharedPreferences(Common.userPref, Context.MODE_PRIVATE);
                        editor = preferences.edit();
                        editor.putString("username", ((JSONObject) ((JSONObject) response).get("user")).get("username").toString());
                        editor.commit();
                        if (json.has("user_status")) {
                            if (json.get("user_status").toString().equalsIgnoreCase("DOCUMENT_PENDING")) {
                                Intent intent = new Intent(context, DocumentSelection.class);
                                context.startActivity(intent);
                            } else if (json.get("user_status").toString().equalsIgnoreCase("FUND_PENDING")) {
                                if (BuildConfig.FLAVOR.equalsIgnoreCase("electionApp")) {
                                    Intent intent = new Intent(context, NavActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);
                                } else {
                                    Intent intent = null;
                                    if (Common.amount > 0) {
                                        intent = new Intent(context, PaymentGateway.class);

                                    } else {
                                        intent = new Intent(context, NavActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    }
                                    context.startActivity(intent);
                                }
                            } else {
                                Intent intent = new Intent(context, NavActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intent);
                            }
                        }
                    } else if (code.equalsIgnoreCase("PARTIAL_SUCCESS")) {
                        String phrase = ((JSONObject) response).get("voice_phrase").toString();
                        Common.voice_phrase_login = phrase;
                        AppAlertDialog.openActivityErrorDialog(context, "Additional verification required.",
                                MessageType.INFO, VoiceLogin.class);
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setTitle("Error!");
                        alertDialog.setMessage("Face Login Failed!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(context, MainActivity.class);
                                        context.startActivity(intent);
                                    }
                                });
                        alertDialog.show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    AppAlertDialog.errorMessageDialog(context, "Unexpected error.Contact support.", MessageType.ERROR);
                }
            }
        });
    }

    public void voiceLogin(HashMap hashMap, Context context, HashMap filePara) {
        RequestAPI.ENDPOINT = "http://ab6040ee8989c11e98cff0a75697d87c-1513868072.us-east-1.elb.amazonaws.com/v1.0/";
        RequestAPI.multiPartRequestHandler("login/voice_login/", context, filePara, hashMap, "Process voice..", new ResponseListner() {
            @Override
            public void onError(VolleyError msg) {
                Log.e("voice login error: ", msg.toString());
                String message = null;
                int code = msg.networkResponse.statusCode;
                if (code == 401) {
                    String responseBody = null;
                    JSONObject data = null;
                    String errorCode = null;
                    try {
                        responseBody = new String(msg.networkResponse.data, "utf-8");
                        data = new JSONObject(responseBody);
                        message = data.getString("description");
                        errorCode = data.getString("code");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        AppAlertDialog.errorMessageDialog(context, "Unexpected error.Contact support.", MessageType.ERROR);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AppAlertDialog.errorMessageDialog(context, "Unexpected error.Contact support.", MessageType.ERROR);
                    }
                    if (message != null && errorCode.equals("INVALID_USER"))
                        AppAlertDialog.openActivityErrorDialog(context, message, MessageType.ERROR, MainActivity.class);
                    else if (message != null && errorCode.equals("VOICE_AUTH_FAIL"))
                        AppAlertDialog.errorMessageDialog(context, message, MessageType.ERROR);
                    else
                        AppAlertDialog.errorMessageDialog(context, "There is an error.please try again.", MessageType.ERROR);
                } else
                    super.onCommonErrorHandle(msg, context);
            }

            @Override
            public void onResponse(Object response) {
                try {
                    RequestAPI.token = ((JSONObject) response).get("token").toString();
                    String code = ((JSONObject) response).has("status") ?
                            ((JSONObject) ((JSONObject) response).get("status")).get("code").toString() : "0";
                    if (code.equalsIgnoreCase("0")) {
                        String user = ((JSONObject) response).getJSONObject("user").toString();
                        SharedPreferences preferences = context.getSharedPreferences(Common.sharedPref, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("user", user);
                        editor.commit();
                        preferences = context.getSharedPreferences(Common.userPref, Context.MODE_PRIVATE);
                        editor = preferences.edit();
                        editor.putString("username", ((JSONObject) ((JSONObject) response).get("user")).get("username").toString());
                        editor.commit();
                        Intent intent = new Intent(context, NavActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    } else if (code.equalsIgnoreCase("PARTIAL_SUCCESS")) {
                        AppAlertDialog.errorMessageDialog(context, "Additional verification required.",
                                MessageType.INFO);
                        Intent intent = new Intent(context, PasscodeActivity.class);
                        intent.putExtra("method", "login");
                        context.startActivity(intent);
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setTitle("Error!");
                        alertDialog.setMessage("Voice Login Failed!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(context, MainActivity.class);
                                        context.startActivity(intent);
                                    }
                                });
                        alertDialog.show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    AppAlertDialog.errorMessageDialog(context, "Unexpected error.Contact support.", MessageType.ERROR);
                }
            }
        });
    }

    public void pinLogin(HashMap hashMap, Context context) {
        RequestAPI.ENDPOINT = "http://ab6040ee8989c11e98cff0a75697d87c-1513868072.us-east-1.elb.amazonaws.com/v1.0/";
        RequestAPI.jsonRequestAPI("login/login_pin/", context, hashMap, "Process pin..", new ResponseListner() {
            @Override
            public void onError(VolleyError msg) {
                Log.e("pincode login error: ", msg.toString());
                int code = msg.networkResponse.statusCode;
                if (code == 400) {
                    ErrorResponseObject object = new ErrorResponseObject();
                    object.type = 2;
                    object.msgType = "error";
                    super.onBasicErrorHandler(msg, context, object);
                } else {
                    super.onCommonErrorHandle(msg, context);
                }
            }

            @Override
            public void onResponse(Object response) {
                try {
                    RequestAPI.token = ((JSONObject) response).get("token").toString();
                    if (((JSONObject) response).has("user")) {
                        String user = ((JSONObject) response).getJSONObject("user").toString();
                        SharedPreferences preferences = context.getSharedPreferences(Common.sharedPref, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("user", user);
                        editor.commit();
                        preferences = context.getSharedPreferences(Common.userPref, Context.MODE_PRIVATE);
                        editor = preferences.edit();
                        editor.putString("username", ((JSONObject) ((JSONObject) response).get("user")).get("username").toString());
                        editor.commit();
                        Intent intent = new Intent(context, NavActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setTitle("Error!");
                        alertDialog.setMessage("Pin Login Failed!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(context, MainActivity.class);
                                        context.startActivity(intent);
                                    }
                                });
                        alertDialog.show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void getUserInfo(Context context) {
        RequestAPI.ENDPOINT = "http://ab6040ee8989c11e98cff0a75697d87c-1513868072.us-east-1.elb.amazonaws.com/v1.0/";
        RequestAPI.jsonRequestAPI("user/user_info/", context, null, "Extract user info..", new ResponseListner() {
            @Override
            public void onError(VolleyError msg) {
                Log.e("User info error: ", msg.toString());
                String message = null;
                if (msg.networkResponse.data != null) {
                    try {
                        String responseBody = new String(msg.networkResponse.data, "utf-8");
                        JSONObject data = new JSONObject(responseBody);
                        message = data.getString("reason");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AppAlertDialog.errorMessageDialog(context, "Unexpected error.Contact support.", MessageType.ERROR);
                    } catch (UnsupportedEncodingException errorr) {
                        errorr.printStackTrace();
                        AppAlertDialog.errorMessageDialog(context, "Unexpected error.Contact support.", MessageType.ERROR);
                    }
                }
                if (msg.networkResponse.statusCode == 400) {
                    if (message != null)
                        AppAlertDialog.openActivityErrorDialog(context, message, MessageType.ERROR, MainActivity.class);
                    else
                        AppAlertDialog.openActivityErrorDialog(context, "There is a server error please try again.",
                                MessageType.ERROR, MainActivity.class);
                } else
                    AppAlertDialog.openActivityErrorDialog(context, "There is a server error please try again.",
                            MessageType.ERROR, MainActivity.class);
            }

            @Override
            public void onResponse(Object response) {
                try {
                    RequestAPI.token = ((JSONObject) response).get("token").toString();
                    if (((JSONObject) response).has("user")) {
                        String user = ((JSONObject) response).getJSONObject("user").toString();
                        SharedPreferences preferences = context.getSharedPreferences(Common.sharedPref, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("user", user);
                        editor.commit();
                        preferences = context.getSharedPreferences(Common.userPref, Context.MODE_PRIVATE);
                        editor = preferences.edit();
                        editor.putString("username", ((JSONObject) ((JSONObject) response).get("user")).get("username").toString());
                        editor.commit();
                        Intent intent = new Intent(context, NavActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setTitle("Error!");
                        alertDialog.setMessage("Pin Login Failed!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(context, MainActivity.class);
                                        context.startActivity(intent);
                                    }
                                });
                        alertDialog.show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    AppAlertDialog.errorMessageDialog(context, "Unexpected error.Contact support.", MessageType.ERROR);
                }
            }
        });
    }

    public void getPaymentInfo(HashMap hashMap, Context context) {
        RequestAPI.ENDPOINT = "http://ab6040ee8989c11e98cff0a75697d87c-1513868072.us-east-1.elb.amazonaws.com/v1.0/";
        RequestAPI.jsonRequestAPI("payment/fee_amount_calc/", context, hashMap, "Processing..", new ResponseListner() {
            @Override
            public void onError(VolleyError msg) {
                Log.e("fee error: ", msg.toString());
            }

            @Override
            public void onResponse(Object response) {
                if (((JSONObject) response).has("amount")) {
                    try {
                        int amount = ((JSONObject) response).getInt("amount");
                        Common.amount = amount;
                        Intent intent = null;
                        if (Common.amount > 0) {
                            intent = new Intent(context, PaymentGateway.class);

                        } else {
                            intent = new Intent(context, SuccessActivity.class);
                            intent.putExtra("msg", "Success!");
                            intent.putExtra("activity", "PaymentGateway");
                        }
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //TODO
            }
        });
    }

    public void getFeeAmount(HashMap hashMap, Context context) {
        RequestAPI.ENDPOINT = "http://ab6040ee8989c11e98cff0a75697d87c-1513868072.us-east-1.elb.amazonaws.com/v1.0/";
        RequestAPI.jsonRequestAPI("payment/fee_amount_calc/", context, hashMap, "Retrieving the amount..", new ResponseListner() {
            @Override
            public void onError(VolleyError msg) {
                Log.e("fee error: ", msg.toString());
                AppAlertDialog.openActivityErrorDialog(context, "Please press ok to retry", MessageType.ERROR, PaymentGateway.class);
            }

            @Override
            public void onResponse(Object response) {
                if (((JSONObject) response).has("amount")) {

                }
            }
        });
    }

    public void faceVerification(HashMap hashMap, Context context, HashMap filePara) {
        RequestAPI.ENDPOINT = "http://ab6040ee8989c11e98cff0a75697d87c-1513868072.us-east-1.elb.amazonaws.com/v1.0/";
        RequestAPI.multiPartRequestHandler("user/face_verification/", context, filePara, hashMap, "Processing face..", new ResponseListner() {
            @Override
            public void onError(VolleyError msg) {
                Log.e("face login error: ", msg.toString());
                String message = null;
                int code = msg.networkResponse.statusCode;
                String responseBody = null;
                JSONObject data = null;
                String errorCode = null;
                if (code == 401 || code == 400) {
                    try {
                        responseBody = new String(msg.networkResponse.data, "utf-8");
                        data = new JSONObject(responseBody);
                        message = data.getString("description");
                        errorCode = data.getString("code");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        AppAlertDialog.errorMessageDialog(context, "Unexpected error.Contact support.", MessageType.ERROR);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AppAlertDialog.errorMessageDialog(context, "Unexpected error.Contact support.", MessageType.ERROR);
                    }
                    if (message != null && errorCode.equals("INVALID_USER"))
                        AppAlertDialog.errorMessageDialog(context, message, MessageType.ERROR);
                    else if (message != null && errorCode.equals("LIVENESS_FAIL"))
                        AppAlertDialog.errorMessageDialog(context, message, MessageType.ERROR);
                    else if (message != null && errorCode.equals("FACE_AUTH_FAIL"))
                        AppAlertDialog.errorMessageDialog(context, message, MessageType.ERROR);
                    else
                        AppAlertDialog.errorMessageDialog(context, "There is an error.please try again.", MessageType.ERROR);
                } else
                    super.onCommonErrorHandle(msg, context);

            }

            @Override
            public void onResponse(Object response) {
                try {
                    RequestAPI.token = ((JSONObject) response).get("token").toString();
                    String code = ((JSONObject) response).has("status") ?
                            ((JSONObject) ((JSONObject) response).get("status")).get("code").toString() : "0";
                    if (code.equalsIgnoreCase("0")) {
                        if(Common.isDeclined) {
                            Intent intent = new Intent(context, DeclinedPage.class);
                            context.startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, NavActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        }
                    } else if (code.equalsIgnoreCase("PARTIAL_SUCCESS")) {
                        String phrase = ((JSONObject) response).get("voice_phrase").toString();
                        Common.voice_phrase_login = phrase;
                        AppAlertDialog.openActivityErrorDialog(context, "Additional verification required.",
                                MessageType.INFO, VoiceLogin.class);
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setTitle("Error!");
                        alertDialog.setMessage("Face Login Failed!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(context, MainActivity.class);
                                        context.startActivity(intent);
                                    }
                                });
                        alertDialog.show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    AppAlertDialog.errorMessageDialog(context, "Unexpected error.Contact support.", MessageType.ERROR);
                }
            }
        });
    }

    public void getQRCode(HashMap hashMap, Context context) {
        RequestAPI.ENDPOINT = "http://ab6040ee8989c11e98cff0a75697d87c-1513868072.us-east-1.elb.amazonaws.com/v1.0/";
        RequestAPI.jsonRequestAPI("user/generate_token/", context, hashMap, "Creating token..", new ResponseListner() {
            @Override
            public void onError(VolleyError msg) {
                Log.e("QR code error: ", msg.toString());
                int code = msg.networkResponse.statusCode;
                if (code == 400) {
                    ErrorResponseObject object = new ErrorResponseObject();
                    object.type = 2;
                    object.msgType = "error";
                    object.ActivityToOpen = MainActivity.class;
                    super.onBasicErrorHandler(msg, context, object);
                } else {
                    super.onCommonErrorHandle(msg, context);
                }
            }

            @Override
            public void onResponse(Object response) {
                try {
                    try {
                        if (!((JSONObject) response).get("token").toString().equalsIgnoreCase(null)) {
                            Long expireTime = Long.parseLong(((JSONObject) response).get("expire_time").toString());
                            Long current = System.currentTimeMillis();
                            Long difference = expireTime - current;
                            Common.time = difference;
                            Common.qrToken = ((JSONObject) response).get("token").toString();
                            Intent intent = new Intent(context, InstituteVerification.class);
                            context.startActivity(intent);
                        } else {

                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    AppAlertDialog.errorMessageDialog(context, "Unexpected error.Contact support.", MessageType.ERROR);
                }
            }
        });
    }
}