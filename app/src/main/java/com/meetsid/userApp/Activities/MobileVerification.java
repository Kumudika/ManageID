package com.meetsid.userApp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.textfield.TextInputLayout;
import com.meetsid.userApp.BuildConfig;
import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.ServerUtils.ConnectServer;
import com.meetsid.userApp.Utils.Validation.TextValidator;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MobileVerification extends AppCompatActivity {
    @BindView(R.id.backBtn)
    ImageButton backBtn;
    @BindView(R.id.pinCode)
    EditText pinCode;
    @BindView(R.id.pinInput)
    TextInputLayout pinInput;
    @BindView(R.id.btnVerify)
    Button btnVerify;
    @BindView(R.id.btnResend)
    TextView btnResend;
    @BindView(R.id.bottomLayer)
    ConstraintLayout bottomLayer;
    String username = null;
    String token = null;
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_mobile_verification);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        token = intent.getStringExtra("token");
        btnResend.postDelayed(new Runnable() {
            public void run() {
                btnResend.setVisibility(View.VISIBLE);
            }
        }, 10000);
        init();
    }

    private void init() {
        btnVerify.setVisibility(View.INVISIBLE);
        pinCode.setHint("\u25CF\u25CF\u25CF\u25CF\u25CF");
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
        pinCode.addTextChangedListener(new TextValidator(pinCode) {
            @Override
            public void validate(EditText textView, String text) {
                if (text.isEmpty()) {
                    pinCode.setHint("\u25CF\u25CF\u25CF\u25CF\u25CF");
                }
                if (pinCode.getText().toString().length() < 5) {
                    pinInput.setError("");
                    btnVerify.setVisibility(View.INVISIBLE);
                } else {
                    pinInput.setError(null);
                    btnVerify.setVisibility(View.VISIBLE);
                    hideSoftKeyboard(MobileVerification.this);
                    pinCode.clearFocus();
                }
            }
        });
//        pinCode.addValidator(new METValidator("") {
//            @Override
//            public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
//                if (isEmpty) {
//                    pinCode.setHint("\u25CF\u25CF\u25CF\u25CF\u25CF");
//                    return true;
//                }
//                if (pinCode.getText().toString().length() < 5) {
//                    btnVerify.setVisibility(View.INVISIBLE);
//                    return false;
//                } else {
//                    btnVerify.setVisibility(View.VISIBLE);
//                    hideSoftKeyboard(MobileVerification.this);
//                    return true;
//                }
//            }
//        });
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

    @OnClick(R.id.btnVerify)
    public void onVerifyClick() {
        if (pinCode.getText().length() < 5) {
            pinCode.setError("Invalid Mobile Number!");
        } else if (pinCode.getText().length() == 5) {
            code = pinCode.getText().toString();
            verifyMobile(code);
        }
    }

    @OnClick(R.id.btnResend)
    public void resendVerificationCode() {
        HashMap<String, String> param = new HashMap<>();
        param.put("username", username);
        ConnectServer.connect().resendMobileCode(param, this);
    }

    private void verifyMobile(String code) {
        HashMap<String, String> param = new HashMap<>();
        param.put("username", username);
        param.put("token", token);
        param.put("mobile_code", "11111");
        param.put("idType", "email");
        ConnectServer.connect().verfifyMobile(param, this);
    }
}
