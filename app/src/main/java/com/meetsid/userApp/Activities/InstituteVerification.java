package com.meetsid.userApp.Activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.error.VolleyError;
import com.google.zxing.WriterException;
import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.AppAlertDialog;
import com.meetsid.userApp.Utils.Common;
import com.meetsid.userApp.Utils.ServerUtils.ConnectServer;
import com.meetsid.userApp.Utils.ServerUtils.MessageType;
import com.meetsid.userApp.Utils.ServerUtils.RequestAPI;
import com.meetsid.userApp.Utils.ServerUtils.ResponseListner;

import org.json.JSONObject;

import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InstituteVerification extends AppCompatActivity {
    @BindView(R.id.imageQR)
    ImageView imageQR;
    @BindView(R.id.token)
    TextView tokenText;
    @BindView(R.id.countDownTimerText)
    TextView countDownTimerText;
    //    @BindView(R.id.getQR)
//    TextView btnGetQR;
    @BindView(R.id.backBtn)
    ImageButton backBtn;
    CountDownTimer countDownTimer;
    private long leftTime;
    private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_institute_verification);
        ButterKnife.bind(this);
        if (Common.qrToken != null) {
            leftTime = Common.time;
            generateQRandTimer(Common.qrToken);
        }

    }

    @OnClick(R.id.backBtn)
    public void goBack() {
        Common.qrToken = null;
        this.finish();
        super.onBackPressed();
    }

    public void getQR() {
        HashMap<String, String> param = new HashMap<>();
        param.put("instid", "00001");
        final Context context = this;
        RequestAPI.jsonRequestHandle("user/generate_token/", context, param, new ResponseListner() {
            @Override
            public void onError(VolleyError msg) {
                Log.e("Add Mobile: ", msg.toString());
                if (msg.networkResponse.statusCode == 400) {
                    AppAlertDialog.errorMessageDialog(context, "Mobile verification code is incorrect.", MessageType.ERROR);
                }
            }

            @Override
            public void onResponse(Object response) {
                Log.i("Sign up response: ", response.toString());
                try {
                    if (!((JSONObject) response).get("token").toString().equalsIgnoreCase(null)) {
                        Long expireTime = Long.parseLong(((JSONObject) response).get("expire_time").toString());
                        Long current = System.currentTimeMillis();
                        Long difference = expireTime - current;
                        leftTime = difference;
                        String token = ((JSONObject) response).get("token").toString();
                        generateQRandTimer(token);
                    } else {

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
    }

    public void generateQRandTimer(String token) {
        Context context = this;
        tokenText.setText(token);
        updateTimer();
        countDownTimer = new CountDownTimer(leftTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                leftTime = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
//                getUserInfo();
            }
        }.start();
        updateTimer();
        QRGEncoder qrgEncoder = new QRGEncoder(token, null, QRGContents.Type.TEXT, 500);
        try {
            // Getting QR-Code as Bitmap
            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            // Setting Bitmap to ImageView
            imageQR.setImageBitmap(bitmap);
            imageQR.setVisibility(View.VISIBLE);
            countDownTimerText.setVisibility(View.VISIBLE);
        } catch (WriterException e) {
            Log.v("QR generation", e.toString());
            e.printStackTrace();
        }
    }

    public void updateTimer() {
        int minutes = (int) leftTime / 60000;
        int seconds = (int) leftTime % 60000 / 1000;

        String timeLeftText;
        timeLeftText = "" + minutes;
        timeLeftText += ":";
        if (seconds < 10)
            timeLeftText += "0";
        timeLeftText += seconds;
        countDownTimerText.setText(timeLeftText);
    }

    public void getUserInfo() {
        final Context context = this;
        ConnectServer.connect().getUserInfo(this);
    }
}
