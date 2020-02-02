package com.meetsid.userApp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.textfield.TextInputLayout;
import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.Common;
import com.meetsid.userApp.Utils.ServerUtils.ConnectServer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DocumentConfirm extends AppCompatActivity {
    @BindView(R.id.infoView)
    LinearLayout infoView;
    @BindView(R.id.imageFront)
    ImageView imageFront;
    @BindView(R.id.imageBack)
    ImageView imageBack;
    HashMap<String, String> license_data;
    HashMap<String, String> dataList = new HashMap<>();
    Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_document_confirm);
        Intent intent = getIntent();
        if (intent.hasExtra("data")) {
            license_data = (HashMap<String, String>) intent.getSerializableExtra("data");
        }
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        font = ResourcesCompat.getFont(this, R.font.metropolis_regular);
        int index = 0;
        for (Map.Entry<String, String> entry : license_data.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String keyVal = getName(entry.getKey());
            dataList.put(keyVal, value);
            addDataField(key, value, false, index);
            index++;
        }
        if (DocumentCapture.docImage != null)
            imageFront.setImageBitmap(DocumentCapture.docImage);
        if (DocumentScanner.image != null) {
            dataList.put("type", "license");
            dataList.put("data", Common.getBase64ImageString(DocumentScanner.image));
            imageBack.setImageBitmap(DocumentScanner.image);
        }
        if (CustomBarcodeScanner.image != null) {
            dataList.put("type", "newNic");
            dataList.put("data", Common.getBase64ImageString(CustomBarcodeScanner.image));
            imageBack.setImageBitmap(CustomBarcodeScanner.image);
        }
        infoView.requestFocus();
//        hideKeyboard(this);
    }

    public String getName(String text) {
        text = text.toUpperCase();
        text = StringUtils.remove(WordUtils.capitalizeFully(text, ' '), " ");
        char c[] = text.toCharArray();
        c[0] += 32;
        text = new String(c);
        return text;
    }

    private void addDataField(String hint, String value, boolean isEnable, int index) {
        EditText editText = new EditText(this);
        editText.setText(value);
        editText.setEnabled(isEnable);
        editText.setTypeface(font);
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editTextParams.leftMargin = 30;
        editTextParams.rightMargin = 30;

        TextInputLayout textInputLayout = new TextInputLayout(this);
        textInputLayout.setTypeface(font);
        LinearLayout.LayoutParams textInputLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        textInputLayoutParams.leftMargin = 30;
        textInputLayoutParams.rightMargin = 30;
        textInputLayoutParams.topMargin = 50;
        textInputLayout.setLayoutParams(textInputLayoutParams);
        textInputLayout.addView(editText, editTextParams);
        textInputLayout.setHint(hint);
        infoView.addView(textInputLayout, index);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @OnClick(R.id.btnRetry)
    public void onRetry() {
        this.onBackPressed();
    }

    @OnClick(R.id.btnContinue)
    public void onContinue() {
        SharedPreferences prf = getSharedPreferences(Common.username, Context.MODE_PRIVATE);
        String username = prf.getString(Common.username, null);
        dataList.put("username", username);
        dataList.put("additionalData", Common.getBase64ImageString(DocumentCapture.docImage));
        ConnectServer.connect().addDocument(dataList, this);
    }

    @OnClick(R.id.backBtn)
    public void goBack() {
        this.onBackPressed();
    }
}
