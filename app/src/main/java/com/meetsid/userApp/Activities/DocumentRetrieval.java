package com.meetsid.userApp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.Common;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DocumentRetrieval extends AppCompatActivity {
    @BindView(R.id.imageLayout)
    LinearLayout imageLayout;
    @BindView(R.id.infoView)
    LinearLayout infoView;
    @BindView(R.id.btnEdit)
    Button btnEdit;

    String type;
    Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_document_retrieval);
        Intent intent = getIntent();
        if (intent.hasExtra("type"))
            type = intent.getStringExtra("type");
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        font = ResourcesCompat.getFont(this, R.font.metropolis_regular);
        Common.isRegCompleted = true;
        JSONObject jsonObject = null;
        LinkedHashMap<String, String> dataMap = null;
        try {
            jsonObject = new JSONObject(Common.jsonObject.get("data").toString());
            dataMap = new Gson().fromJson(jsonObject.toString(), LinkedHashMap.class);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        if (dataMap != null) {
            int index = 0;
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                if (entry.getKey().equalsIgnoreCase("data") ||
                        entry.getKey().equalsIgnoreCase("additionalData") ||
                        entry.getKey().equalsIgnoreCase("type"))
                    continue;
                String key = StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(entry.getKey()), StringUtils.SPACE));
                String value = entry.getValue();
                addDataField(key, value, false, index);
                index++;
            }

            if (type.equals("passport")) {
                ImageView frontView = new ImageView(this);
                frontView.setAdjustViewBounds(true);
                frontView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Bitmap front = null;
                try {
                    if (dataMap.containsKey("data"))
                        front = Common.convert(dataMap.get("data"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (front == null)
                    frontView.setImageResource(R.drawable.nic_icon);
                else
                    frontView.setImageBitmap(front);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        frontView.setForeground(getDrawable(R.drawable.image_boarder));
                    }
                }
                LinearLayout.LayoutParams frontPara = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        300);
                frontView.setLayoutParams(frontPara);
                imageLayout.addView(frontView);
            } else if (type.equals("nic") || type.equalsIgnoreCase("license")
                    || type.equalsIgnoreCase("newNic")) {
                ImageView frontView = new ImageView(this);
                frontView.setAdjustViewBounds(true);
                frontView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Bitmap front = null;
                Bitmap back = null;
                try {
                    if (dataMap.containsKey("data"))
                        front = Common.convert(dataMap.get("data"));
                    if (dataMap.containsKey("additionalData"))
                        back = Common.convert(dataMap.get("additionalData"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (front == null)
                    frontView.setImageResource(R.drawable.nic_icon);
                else
                    frontView.setImageBitmap(front);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        frontView.setForeground(getDrawable(R.drawable.image_boarder));
                    }
                }
                LinearLayout.LayoutParams frontPara = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        300);
                frontPara.rightMargin = 40;
                frontView.setLayoutParams(frontPara);

                ImageView backView = new ImageView(this);
                backView.setAdjustViewBounds(true);
                backView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                if (back == null)
                    backView.setImageResource(R.drawable.nic_back_icon);
                else
                    backView.setImageBitmap(back);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        backView.setForeground(getDrawable(R.drawable.image_boarder));
                    }
                }
                LinearLayout.LayoutParams backPara = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        300);
                backView.setLayoutParams(backPara);
                imageLayout.addView(frontView, 0);
                imageLayout.addView(backView, 1);
            }
        }
        infoView.requestFocus();
    }

    private void addDataField(String hint, String value, boolean isEnable, int index) {
        EditText editText = new EditText(this);
        editText.setText(value);
        editText.setEnabled(isEnable);
        editText.setTypeface(font);
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        TextInputLayout textInputLayout = new TextInputLayout(this);
        textInputLayout.setTypeface(font);
        LinearLayout.LayoutParams textInputLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        textInputLayoutParams.topMargin = 50;
        textInputLayout.setLayoutParams(textInputLayoutParams);
        textInputLayout.addView(editText, editTextParams);
        textInputLayout.setHint(hint);
        infoView.addView(textInputLayout, index);
    }

    @OnClick(R.id.btnEdit)
    public void onEditClick() {
        Intent intent = null;
        if (type.equalsIgnoreCase("nic")) {
            intent = new Intent(this, DocCapturePage.class);
            intent.putExtra("type", "nicFront");
        } else if (type.equalsIgnoreCase("passport")) {
            intent = new Intent(this, PassportScanner.class);
        } else if (type.equalsIgnoreCase("license")) {
            intent = new Intent(this, MultiDocumentUpload.class);
            intent.putExtra("type", "l_front");
        } else if (type.equalsIgnoreCase("newNic")) {
            intent = new Intent(this, MultiDocumentUpload.class);
            intent.putExtra("type", "n_nic_front");
        }
        if (intent != null)
            startActivity(intent);
    }
}
