package com.meetsid.userApp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.meetsid.userApp.BuildConfig;
import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.Common;
import com.meetsid.userApp.Utils.ServerUtils.ConnectServer;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PasscodeActivity extends AppCompatActivity {
    @BindView(R.id.nextBtn)
    Button nextBtn;
    @BindView(R.id.bottomLayer)
    ConstraintLayout bottomLayer;
    String TAG = "PasscodeActivity";
    String pin;
    String method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_passcode);
        Intent intent = getIntent();
        method = intent.getStringExtra("method");
        ButterKnife.bind(this);
        nextBtn.setVisibility(View.INVISIBLE);
        if (BuildConfig.FLAVOR.equalsIgnoreCase("electionApp")) {
            ImageView poweredByIcon = new ImageView(this);
            poweredByIcon.setImageResource(R.drawable.colorpowered);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, 65);
            params.bottomMargin = 20;
            params.bottomToBottom = R.id.bottomLayer;
            params.leftToLeft = R.id.bottomLayer;
            params.rightToRight = R.id.bottomLayer;
            poweredByIcon.setLayoutParams(params);
            bottomLayer.addView(poweredByIcon);
        }
        final PinEntryEditText pinEntry = (PinEntryEditText) findViewById(R.id.txt_pin_entry);
        pinEntry.setHint("\u25CF\u25CF\u25CF\u25CF\u25CF\u25CF");
        if (pinEntry != null) {
            pinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
                @Override
                public void onPinEntered(CharSequence str) {
                    if (str.toString().length() == 6) {
                        pin = str.toString();
                        nextBtn.setVisibility(View.VISIBLE);
                        hideSoftKeyboard(PasscodeActivity.this);
                    }
                }
            });
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    @OnClick(R.id.backBtn)
    public void goBack() {
        this.finish();
        super.onBackPressed();
    }

    @OnClick(R.id.nextBtn)
    public void onNextClick() {
        setupPasscode(pin);
    }

    public void setupPasscode(String pin) {
        if (method.equalsIgnoreCase("register")) {
            SharedPreferences prf = getSharedPreferences(Common.username, Context.MODE_PRIVATE);
            String username = prf.getString(Common.username, null);
            prf = getSharedPreferences(Common.emailToken, Context.MODE_PRIVATE);
            String token = prf.getString(Common.emailToken, null);
            HashMap<String, String> param = new HashMap<>();
            param.put("pincode", Common.getSHA256Hash(pin));
            param.put("username", username);
            param.put("token", token);
            ConnectServer.connect().addPIN(param, this);
        } else {
            HashMap<String, String> param = new HashMap<>();
            param.put("pincode", Common.getSHA256Hash(pin));
            ConnectServer.connect().pinLogin(param, this);
        }
    }
}

