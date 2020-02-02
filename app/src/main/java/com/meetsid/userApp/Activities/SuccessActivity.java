package com.meetsid.userApp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.meetsid.userApp.BuildConfig;
import com.meetsid.userApp.FaceDetectionUtil.ScannerActivity;
import com.meetsid.userApp.MainActivity;
import com.meetsid.userApp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SuccessActivity extends AppCompatActivity {
    String activity;
    Intent nxtIntent;
    String msg;
    @BindView(R.id.bottomLayer)
    LinearLayout bottomLayer;
    @BindView(R.id.successText)
    TextView successText;
    @BindView(R.id.nxtBtn)
    Button nextBtn;
    @BindView(R.id.extraText)
    TextView extraText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        Intent intent = getIntent();
        msg = intent.getStringExtra("msg");
        activity = intent.getStringExtra("activity");
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        successText.setText(msg);
        if (BuildConfig.FLAVOR.equalsIgnoreCase("electionApp")) {
            ImageView poweredByIcon = new ImageView(this);
            poweredByIcon.setImageResource(R.drawable.whitepowered);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 65);
            params.bottomMargin = 20;
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            poweredByIcon.setLayoutParams(params);
            bottomLayer.addView(poweredByIcon);
        }
        if (activity == "PaymentGateway") {
            extraText.setVisibility(View.VISIBLE);
            nextBtn.setText("Login");
        } else {
            extraText.setVisibility(View.INVISIBLE);
            nextBtn.setText("Next");
        }
    }

    @Override
    public void onBackPressed() {
        //
    }

    @OnClick(R.id.nxtBtn)
    public void onNextClick() {
        switch (activity) {
            case "PaymentGateway":
                nxtIntent = new Intent(this, MainActivity.class);
                break;
            case "FaceDetection":
                nxtIntent = new Intent(this, VoiceRecognition.class);
                break;
            case "PasscodeActivity":
                nxtIntent = new Intent(this, DocumentSelection.class);
                break;
            case "MobileVerification":
                nxtIntent = new Intent(this, ScannerActivity.class);
                nxtIntent.putExtra("method", "register");
                break;
        }
        startActivity(nxtIntent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (intent == null) {
            intent = new Intent();
        }
        super.startActivityForResult(intent, requestCode);
    }
}
