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

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MultiDocumentUpload extends AppCompatActivity {
    @BindView(R.id.docInfo)
    TextView docInfo;
    @BindView(R.id.docView)
    ImageView docView;
    @BindView(R.id.btnCapture)
    Button btnCapture;
    @BindView(R.id.btnPanel)
    LinearLayout btnPanel;
    @BindView(R.id.topic)
    TextView topic;
    @BindView(R.id.docText)
    TextView docText;
    String type;
    HashMap<String, String> license_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_multi_document_upload);
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
        int img_w = width - 400;
        switch (type) {
            case "l_front":
                docView.getLayoutParams().width = img_w;
                topic.setText(getString(R.string.license_front_title));
                docInfo.setText(getString(R.string.license_front_info));
                docText.setText(getString(R.string.license_front_lbl));
                docView.setImageResource(R.drawable.license_front);
                break;
            case "l_back":
                docView.getLayoutParams().width = img_w;
                topic.setText(getString(R.string.license_back_title));
                docInfo.setText(getString(R.string.license_back_info));
                docText.setText(getString(R.string.license_back_lbl));
                docView.setImageResource(R.drawable.lisence_back);
                break;
            case "n_nic_front":
                docView.getLayoutParams().width = img_w;
                topic.setText(getString(R.string.new_nic_front_title));
                docInfo.setText(getString(R.string.n_nic_front_info));
                docText.setText(getString(R.string.n_nic_front_lbl));
                docView.setImageResource(R.drawable.license_front);
                break;
            case "n_nic_back":
                docView.getLayoutParams().width = img_w;
                topic.setText(getString(R.string.new_nic_back_title));
                docInfo.setText(getString(R.string.n_nic_back_info));
                docText.setText(getString(R.string.n_nic_back_lbl));
                docView.setImageResource(R.drawable.lisence_back);
                break;
        }
        docView.requestLayout();
    }

    private void setData() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int img_w = width - 400;
        if (type.equals("l_back")) {
            docView.getLayoutParams().width = img_w;
            topic.setText(getString(R.string.license_front_title));
            docInfo.setText(getString(R.string.license_front_info));
            docText.setText(getString(R.string.license_front_lbl));
            btnCapture.setVisibility(View.INVISIBLE);
            btnPanel.setVisibility(View.VISIBLE);
            if (DocumentCapture.docImage != null)
                docView.setImageBitmap(DocumentCapture.docImage);
            else
                docView.setImageResource(R.drawable.license_front);
            type = "l_front";
        } else if (type.equals("n_nic_back")) {
            docView.getLayoutParams().width = img_w;
            topic.setText(getString(R.string.new_nic_front_title));
            docInfo.setText(getString(R.string.n_nic_front_info));
            docText.setText(getString(R.string.n_nic_front_lbl));
            btnCapture.setVisibility(View.INVISIBLE);
            btnPanel.setVisibility(View.VISIBLE);
            if (CustomBarcodeScanner.image != null)
                docView.setImageBitmap(CustomBarcodeScanner.image);
            else
                docView.setImageResource(R.drawable.license_front);
            type = "n_nic_front";
        }
        docView.requestLayout();
    }

    @OnClick({R.id.btnCapture, R.id.btnRetry})
    public void takePhoto() {
        btnCapture.setVisibility(View.INVISIBLE);
        Intent intent;
        switch (type) {
            case "l_front":
                intent = new Intent(this, DocumentCapture.class);
                startActivityForResult(intent, 1);
                break;
            case "l_back":
                intent = new Intent(this, DocumentScanner.class);
                startActivityForResult(intent, 2);
                break;
            case "n_nic_front":
                intent = new Intent(this, DocumentCapture.class);
                startActivityForResult(intent, 3);
                break;
            case "n_nic_back":
                intent = new Intent(this, CustomBarcodeScanner.class);
                startActivityForResult(intent, 4);
                break;
        }
    }

    @OnClick(R.id.btnContinue)
    public void onContinue() {
        if (type.equals("l_front")) {
            type = "l_back";
            init();
        } else if (type.equals("l_back")) {
            Intent intent = new Intent(this, DocumentConfirm.class);
            intent.putExtra("data", license_data);
            startActivity(intent);
        } else if (type.equals("n_nic_front")) {
            type = "n_nic_back";
            init();
        } else if (type.equals("n_nic_back")) {
            Intent intent = new Intent(this, DocumentConfirm.class);
            intent.putExtra("data", license_data);
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
                    type = "l_front";
                    init();
                } else {
                    btnPanel.setVisibility(View.VISIBLE);
                    docView.setImageBitmap(DocumentCapture.docImage);
                }
            }

        } else if (requestCode == 2) {
            if (intent != null) {
                boolean val = intent.getBooleanExtra("isCompleted", false);
                if (intent.hasExtra("data")) {
                    license_data = (HashMap<String, String>) intent.getSerializableExtra("data");
                }
                if (!val) {
                    type = "l_back";
                    init();
                } else {
                    btnPanel.setVisibility(View.VISIBLE);
                    docView.setImageBitmap(DocumentScanner.image);
                }
            }
        } else if (requestCode == 3) {
            if (intent != null) {
                boolean val = intent.getBooleanExtra("isCompleted", false);
                if (!val) {
                    type = "n_nic_front";
                    init();
                } else {
                    btnPanel.setVisibility(View.VISIBLE);
                    docView.setImageBitmap(DocumentCapture.docImage);
                }
            }
        } else if (requestCode == 4) {
            if (intent != null) {
                boolean val = intent.getBooleanExtra("isCompleted", false);
                if (intent.hasExtra("data")) {
                    license_data = (HashMap<String, String>) intent.getSerializableExtra("data");
                }
                if (!val) {
                    type = "n_nic_back";
                    init();
                } else {
                    btnPanel.setVisibility(View.VISIBLE);
                    docView.setImageBitmap(CustomBarcodeScanner.image);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (type.equals("l_front")) {
            this.finish();
            super.onBackPressed();
        } else if (type.equals("n_nic_front")) {
            this.finish();
            super.onBackPressed();
        } else {
            setData();
        }
    }

    @OnClick(R.id.backBtn)
    public void goBack() {
        this.onBackPressed();
    }
}
