package com.meetsid.userApp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.meetsid.userApp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomBarcodeScanner extends AppCompatActivity implements CustomBarcode.ResultHandler {
    @BindView(R.id.view)
    CustomBarcode view;
    boolean isCompleted = false;
    public static Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_barcode_scanner);
        ButterKnife.bind(this);
        List<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.PDF_417);
        view.setFormats(formats);
        view.setResultHandler(this);
    }

    @Override
    public void handleResult(Result rawResult) {
        if (CustomBarcode.croppedImage != null) {
            image = CustomBarcode.croppedImage;
            Log.i("test", rawResult.getText());
            String[] info = rawResult.getText().split("\n");
            HashMap<String, String> data = new HashMap<>();
            data.put("Personal No", info[1]);
            String[] names = toFirstCharUpperAll(info[6]).split(" ");
            String lName = names[names.length - 1];
            data.put("Last Name", lName);
            String otherNames = "";
            for (int i = 0; i < names.length - 1; i++)
                otherNames = otherNames + " " + names[i];
            data.put("Other Name", otherNames);
            data.put("Full Name", toFirstCharUpperAll(info[6]));
            data.put("Date of Birth", info[2]);
            data.put("Sex", info[3]);
            data.put("Address", toFirstCharUpperAll(info[7]));
            data.put("Register Date", info[4]);
            data.put("City", toFirstCharUpperAll(info[8]));
            isCompleted = true;
            Intent intent = new Intent();
            intent.putExtra("isCompleted", isCompleted);
            intent.putExtra("data", data);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            view.startCamera();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        view.setResultHandler(this);
        view.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        view.stopCamera();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isCompleted", isCompleted);
        setResult(RESULT_OK, intent);
        finish();
    }

    public String toFirstCharUpperAll(String string) {
        string = string.toLowerCase();
        StringBuffer sb = new StringBuffer(string);
        for (int i = 0; i < sb.length(); i++)
            if (i == 0 || sb.charAt(i - 1) == ' ')//first letter to uppercase by default
                sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
        return sb.toString();
    }
}
