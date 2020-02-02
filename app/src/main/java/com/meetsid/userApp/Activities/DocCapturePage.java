package com.meetsid.userApp.Activities;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.meetsid.userApp.R;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DocCapturePage extends AppCompatActivity {
    @BindView(R.id.nicInfo)
    TextView nicInfo;
    @BindView(R.id.nicFront)
    ImageView nicFront;
    @BindView(R.id.btnCapture)
    Button btnCapture;
    @BindView(R.id.btnPanel)
    LinearLayout btnPanel;
    @BindView(R.id.topic)
    TextView topic;
    @BindView(R.id.nicText)
    TextView nicText;
    String type;
    Map<String, String> nicData = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_nic_front_page);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        init();
    }

    private void init() {
        btnCapture.setVisibility(View.VISIBLE);
        btnPanel.setVisibility(View.INVISIBLE);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int img_w;
        switch (type) {
            case "nicFront":
                img_w = width / 2;
                int img_h = img_w * 8 / 6;
                nicFront.getLayoutParams().width = img_w;
                topic.setText(getString(R.string.front_title));
                nicInfo.setText(getString(R.string.nic_front_info));
                nicText.setText(getString(R.string.nic_front_lbl));
                nicFront.setImageResource(R.drawable.nic_icon);
                break;
            case "nicBack":
                img_w = width - 400;
                nicFront.getLayoutParams().width = img_w;
                topic.setText(getString(R.string.nic_back_title));
                nicInfo.setText(getString(R.string.nic_back_info));
                nicText.setText(getString(R.string.nic_back_lbl));
                nicFront.setImageResource(R.drawable.nic_back_icon);
                break;
        }
        nicFront.requestLayout();
    }

    private void setData() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int img_w;
        if (type.equals("nicBack")) {
            img_w = width / 2;
            int img_h = img_w * 8 / 6;
            nicFront.getLayoutParams().width = img_w;
            topic.setText(getString(R.string.front_title));
            nicInfo.setText(getString(R.string.nic_front_info));
            nicText.setText(getString(R.string.nic_front_lbl));
            btnCapture.setVisibility(View.INVISIBLE);
            btnPanel.setVisibility(View.VISIBLE);
            if (NicScanner.image != null)
                nicFront.setImageBitmap(NicScanner.image);
            else
                nicFront.setImageResource(R.drawable.nic_icon);
            type = "nicFront";
        }
        nicFront.requestLayout();
    }

    @OnClick({R.id.btnCapture, R.id.btnRetry})
    public void takePhoto() {
        btnCapture.setVisibility(View.INVISIBLE);
        Intent intent;
        switch (type) {
            case "nicFront":
                intent = new Intent(this, NicScanner.class);
                startActivityForResult(intent, 1);
                break;
            case "nicBack":
                intent = new Intent(this, NicBackScanner.class);
                startActivityForResult(intent, 2);
                break;
        }
    }

    @OnClick(R.id.btnContinue)
    public void onContinue() {
        if (type.equals("nicFront")) {
            type = "nicBack";
            init();
        } else if (type.equals("nicBack")) {
            Intent intent = new Intent(this, PassportDetails.class);
            intent.putExtra("data", (Serializable) nicData);
            intent.putExtra("type", "nic");
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1) {
            if (intent != null) {
                boolean val = intent.getBooleanExtra("isCompleted", false);
                if (!val) {
                    type = "nicFront";
                    init();
                } else {
                    btnPanel.setVisibility(View.VISIBLE);
                    nicFront.setImageBitmap(NicScanner.image);
                }
            }

        } else if (requestCode == 2) {
            if (intent != null) {
                boolean val = intent.getBooleanExtra("isCompleted", false);
                if (!val) {
                    type = "nicBack";
                    init();
                } else {
                    btnPanel.setVisibility(View.VISIBLE);
                    nicFront.setImageBitmap(NicBackScanner.bitmap);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (type.equals("nicFront")) {
            this.finish();
            super.onBackPressed();
        } else {
            setData();
        }
    }

    @OnClick(R.id.backBtn)
    public void goBack() {
        if (type.equals("nicBack")) {
            Intent intent = new Intent(this, NicScanner.class);
            startActivityForResult(intent, 1);
        } else {
            this.finish();
            super.onBackPressed();
        }
    }
}
