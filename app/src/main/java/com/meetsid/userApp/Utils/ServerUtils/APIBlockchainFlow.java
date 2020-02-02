package com.meetsid.userApp.Utils.ServerUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.error.VolleyError;
import com.google.gson.Gson;
import com.meetsid.userApp.Activities.DocumentRetrieval;
import com.meetsid.userApp.Activities.NavActivity;
import com.meetsid.userApp.Activities.PasscodeSetupActivity;
import com.meetsid.userApp.Activities.SuccessActivity;
import com.meetsid.userApp.Activities.VoiceRecognition;
import com.meetsid.userApp.BuildConfig;
import com.meetsid.userApp.FaceDetectionUtil.ScannerActivity;
import com.meetsid.userApp.MainActivity;
import com.meetsid.userApp.Models.Token;
import com.meetsid.userApp.Utils.AppAlertDialog;
import com.meetsid.userApp.Utils.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class APIBlockchainFlow extends MeetSIDRequestAPI {
    String programToken = "a722242d9ed47cb9deda6730b6162b5c00e71d45a0a502406bc3905731696125";

    @Override
    public void docRetrieve(HashMap para, Context context, String type) {

        BlockchainRequest.jsonRequestHandle((programToken + "/getDocument"), context, new JSONObject(para), "Retrieving document..", new ResponseListner() {
            @Override
            public void onError(VolleyError msg) {
                Log.e("doc retrieval error: ", msg.toString());
                String message = null;
                try {
                    message = new String(msg.networkResponse.data, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (message != null)
                    AppAlertDialog.errorMessageDialog(context, message, MessageType.ERROR);
                else
                    AppAlertDialog.errorMessageDialog(context, "Error occured. Please try again.", MessageType.ERROR);
            }

            @Override
            public void onResponse(Object response) {
                try {
                    JSONObject jsonObject = new JSONObject(((JSONObject) response).get("data").toString());
                    Common.jsonObject = new JSONObject(jsonObject.get("data").toString());
                    Intent intent = new Intent(context, DocumentRetrieval.class);
                    intent.putExtra("type", type);
                    context.startActivity(intent);
//                    if (type.equals("passport"))
//                        intent = new Intent(context, PassportRetrieval.class);
//
//                    else if (type.equals("nic"))
//                        intent = new Intent(context, NICRetrieval.class);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void addDocument(HashMap stringPara, Context context) {
        String type = stringPara.get("type").toString();
        String x = new JSONObject(stringPara).toString();
        SharedPreferences prf = context.getSharedPreferences(Common.tokenData, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prf.getString(Common.tokenData, "");
        Token tokenData = gson.fromJson(json, Token.class);
        if (tokenData == null) {
            AppAlertDialog.openActivityErrorDialog(context, "Please recover your account.",
                    MessageType.ERROR, MainActivity.class);
        } else {
            JSONObject body = new JSONObject();
            try {
                body.put("walletid", tokenData.getTokenId());
                body.put("secretshare", tokenData.getTokenShare());
                body.put("tokenshare", programToken);
                body.put("payload", x);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//        body.put("payload", new JSONObject(hashMap));
            BlockchainRequest.jsonRequestHandle((programToken + "/uploadDocument/"), context, body, "Uploading document..", new ResponseListner() {
                @Override
                public void onError(VolleyError msg) {
                    Log.e("document upload error: ", msg.toString());
                    String message = null;
                    try {
                        message = new String(msg.networkResponse.data, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if (message != null)
                        AppAlertDialog.errorMessageDialog(context, message, MessageType.ERROR);
                    else
                        AppAlertDialog.errorMessageDialog(context, "Error occured. Please try again.", MessageType.ERROR);
                }

                @Override
                public void onResponse(Object response) {
                    try {
                        JSONObject json = new JSONObject(((JSONObject) response).get("data").toString());
                        Gson gson = new Gson();
                        SharedPreferences preferences = context.getSharedPreferences(Common.tokenData, Context.MODE_PRIVATE);
                        String data = preferences.getString(Common.tokenData, "");
                        Token token = gson.fromJson(data, Token.class);
                        if (type.equalsIgnoreCase("passport")) {
                            token.setPassportTokenId(json.get("tokenId").toString());
                            token.setPassportTokenShare(json.get("tokenShare").toString());
                        } else if (type.equalsIgnoreCase("nic")) {
                            token.setNicTokenId(json.get("tokenId").toString());
                            token.setNicTokenShare(json.get("tokenShare").toString());
                        } else if (type.equalsIgnoreCase("license")) {
                            token.setDLTokenId(json.get("tokenId").toString());
                            token.setDLTokenShare(json.get("tokenShare").toString());
                        } else if (type.equalsIgnoreCase("newNic")) {
                            token.setNewNicTokenId(json.get("tokenId").toString());
                            token.setNewNicTokenShare(json.get("tokenShare").toString());
                        }
                        SharedPreferences.Editor editor = preferences.edit();
                        String tokenData = gson.toJson(token);
                        editor.putString(Common.tokenData, tokenData);
                        editor.commit();
                        if (Common.isRegCompleted) {
                            Intent intent = new Intent(context, NavActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        } else {
                            if (token.getPaymentTokenId() == null) {
                                Intent intent;
                                if (BuildConfig.FLAVOR.equalsIgnoreCase("electionApp")) {
                                    intent = new Intent(context, SuccessActivity.class);
                                    intent.putExtra("msg", "Success!");
                                    intent.putExtra("activity", "PaymentGateway");
                                    context.startActivity(intent);
                                } else {
                                    SharedPreferences prf = context.getSharedPreferences(Common.username, Context.MODE_PRIVATE);
                                    String username = prf.getString(Common.username, null);
                                    HashMap<String, String> param = new HashMap<>();
                                    param.put("username", username);
                                    if (BuildConfig.FLAVOR.equalsIgnoreCase("electionApp")) {
                                        param.put("services", "ELECTION");
                                    } else {
                                        param.put("services", "ALL");
                                    }
                                    ConnectServer.connect().getPaymentInfo(param, context);

                                }
                            } else {
                                Intent intent = new Intent(context, NavActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intent);
                            }
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void addFace(HashMap stringPara, Context context) {
        String x = new JSONObject(stringPara).toString();
        SharedPreferences prf = context.getSharedPreferences(Common.tokenData, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prf.getString(Common.tokenData, "");
        Token tokenData = gson.fromJson(json, Token.class);
        JSONObject body = new JSONObject();
        try {
            body.put("walletid", tokenData.getTokenId());
            body.put("secretshare", tokenData.getTokenShare());
            body.put("tokenshare", programToken);
            body.put("payload", x);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        body.put("payload", new JSONObject(hashMap));
        BlockchainRequest.jsonRequestHandle((programToken + "/uploadResource/"), context, body, "Face uploading..", new ResponseListner() {
            @Override
            public void onError(VolleyError msg) {
                Log.e("face upload error: ", msg.toString());
                String message = null;
                try {
                    message = new String(msg.networkResponse.data, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (message != null)
                    AppAlertDialog.errorMessageDialog(context, message, MessageType.ERROR);
                else
                    AppAlertDialog.errorMessageDialog(context, "Error occured. Please try again.", MessageType.ERROR);

                Intent nxtIntent = new Intent(context, ScannerActivity.class);
                nxtIntent.putExtra("method", "register");
                context.startActivity(nxtIntent);
            }

            @Override
            public void onResponse(Object response) {
                try {
                    JSONObject json = new JSONObject(((JSONObject) response).get("data").toString());
                    Gson gson = new Gson();
                    SharedPreferences preferences = context.getSharedPreferences(Common.tokenData, Context.MODE_PRIVATE);
                    String data = preferences.getString(Common.tokenData, "");
                    Token token = gson.fromJson(data, Token.class);
                    token.setFaceTokenId(json.get("tokenId").toString());
                    token.setFaceTokenShare(json.get("tokenShare").toString());
                    SharedPreferences.Editor editor = preferences.edit();
                    String tokenData = gson.toJson(token);
                    editor.putString(Common.tokenData, tokenData);
                    editor.commit();
                    Intent intent = new Intent(context, VoiceRecognition.class);
                    context.startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void addVoice(HashMap stringPara, HashMap filePara, Context context) {
        String x = new JSONObject(stringPara).toString();
        SharedPreferences prf = context.getSharedPreferences(Common.tokenData, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prf.getString(Common.tokenData, "");
        Token tokenData = gson.fromJson(json, Token.class);
        if (tokenData == null) {
            AppAlertDialog.openActivityErrorDialog(context, "Please recover your account.",
                    MessageType.ERROR, MainActivity.class);
        }
        JSONObject body = new JSONObject();
        try {
            body.put("walletid", tokenData.getTokenId());
            body.put("secretshare", tokenData.getTokenShare());
            body.put("tokenshare", programToken);
            body.put("payload", x);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        body.put("payload", new JSONObject(hashMap));
        BlockchainRequest.jsonRequestHandle((programToken + "/uploadResource/"), context, body, "Voice uploading", new ResponseListner() {
            @Override
            public void onError(VolleyError msg) {
                String message = null;
                try {
                    message = new String(msg.networkResponse.data, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (message != null)
                    AppAlertDialog.openActivityErrorDialog(context, message, MessageType.ERROR, VoiceRecognition.class);
                else
                    AppAlertDialog.openActivityErrorDialog(context, "Error occured. Please try again.",
                            MessageType.ERROR, VoiceRecognition.class);
            }

            @Override
            public void onResponse(Object response) {
                try {
                    JSONObject json = new JSONObject(((JSONObject) response).get("data").toString());
                    Gson gson = new Gson();
                    SharedPreferences preferences = context.getSharedPreferences(Common.tokenData, Context.MODE_PRIVATE);
                    String data = preferences.getString(Common.tokenData, "");
                    Token token = gson.fromJson(data, Token.class);
                    token.setVoiceTokenId(json.get("tokenId").toString());
                    token.setVoiceTokenShare(json.get("tokenShare").toString());
                    SharedPreferences.Editor editor = preferences.edit();
                    String tokenData = gson.toJson(token);
                    editor.putString(Common.tokenData, tokenData);
                    editor.commit();
                    Intent intent = new Intent(context, PasscodeSetupActivity.class);
                    context.startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void addPayment(HashMap hashMap, Context context) {
        String x = new JSONObject(hashMap).toString();
        SharedPreferences prf = context.getSharedPreferences(Common.tokenData, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prf.getString(Common.tokenData, "");
        Token tokenData = gson.fromJson(json, Token.class);
        if (tokenData == null) {
            AppAlertDialog.openActivityErrorDialog(context, "Please recover your account.",
                    MessageType.ERROR, MainActivity.class);
        } else {
            JSONObject body = new JSONObject();
            try {
                body.put("walletid", tokenData.getTokenId());
                body.put("secretshare", tokenData.getTokenShare());
                body.put("tokenshare", programToken);
                body.put("payload", x);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//        body.put("payload", new JSONObject(hashMap));
            BlockchainRequest.jsonRequestHandle((programToken + "/addCard/"), context, body, "Completing payment..", new ResponseListner() {
                @Override
                public void onError(VolleyError msg) {
                    Log.e("payment error: ", msg.toString());
                    String message = null;
                    try {
                        message = new String(msg.networkResponse.data, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if (message != null)
                        AppAlertDialog.errorMessageDialog(context, message, MessageType.ERROR);
                    else
                        AppAlertDialog.errorMessageDialog(context, "Error occured. Please try again.",
                                MessageType.ERROR);
                }

                @Override
                public void onResponse(Object response) {
                    try {
                        JSONObject json = new JSONObject(((JSONObject) response).get("data").toString());
                        Gson gson = new Gson();
                        SharedPreferences preferences = context.getSharedPreferences(Common.tokenData, Context.MODE_PRIVATE);
                        String data = preferences.getString(Common.tokenData, "");
                        Token token = gson.fromJson(data, Token.class);
                        token.setPaymentTokenId(json.get("tokenId").toString());
                        token.setPaymentTokenShare(json.get("tokenShare").toString());
                        SharedPreferences.Editor editor = preferences.edit();
                        String tokenData = gson.toJson(token);
                        editor.putString(Common.tokenData, tokenData);
                        editor.commit();
                        Intent intent = new Intent(context, SuccessActivity.class);
                        intent.putExtra("msg", "Success!");
                        intent.putExtra("activity", "PaymentGateway");
                        context.startActivity(intent);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void verfifyMobile(HashMap hashMap, Context context) {
        String x = new JSONObject(hashMap).toString();
        JSONObject payload = new JSONObject();
        try {
            payload.put("payload", x);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        body.put("payload", new JSONObject(hashMap));
        BlockchainRequest.jsonRequestHandle((programToken + "/createUWallet/"), context, payload, "Creating wallet..", new ResponseListner() {
            @Override
            public void onError(VolleyError msg) {
                Log.e("create account error: ", msg.toString());
                String message = null;
                try {
                    message = new String(msg.networkResponse.data, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (message != null)
                    AppAlertDialog.errorMessageDialog(context, message, MessageType.ERROR);
                else
                    AppAlertDialog.errorMessageDialog(context, "Error occured. Please try again.", MessageType.ERROR);
            }

            @Override
            public void onResponse(Object response) {
                try {
                    JSONObject json = new JSONObject(((JSONObject) response).get("data").toString());
                    Token token = new Token();
                    Token.Type type = token.new Type();
                    token.setTokenId(json.get("tokenId").toString());
                    token.setTokenShare(json.get("tokenShare").toString());
                    JSONObject typeData = (JSONObject) json.get("type");
                    type.setTokenType(typeData.getString("tokenType"));
                    type.setExecutable(typeData.getBoolean("executable"));
                    type.setLinkable(typeData.getBoolean("linkable"));
                    type.setTransferable(typeData.getBoolean("transferable"));
                    token.setType(type);
                    SharedPreferences preferences = context.getSharedPreferences(Common.tokenData, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    Gson gson = new Gson();
                    String tokenData = gson.toJson(token);
                    editor.putString(Common.tokenData, tokenData);
                    editor.commit();
                    Intent intent = new Intent(context, SuccessActivity.class);
                    intent.putExtra("activity", "MobileVerification");
                    intent.putExtra("msg", "Wallet Created");
                    context.startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void updateProfile(HashMap hashMap, Context context) {

    }
}
