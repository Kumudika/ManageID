package com.meetsid.userApp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import com.aigestudio.wheelpicker.WheelPicker;
import com.google.android.material.textfield.TextInputLayout;
import com.meetsid.userApp.BuildConfig;
import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DocumentSelection extends AppCompatActivity {
    @BindView(R.id.backBtn)
    ImageButton backBtn;
    @BindView(R.id.bottomLayer)
    RelativeLayout bottomLayer;
    @BindView(R.id.countryText)
    EditText countryText;
    @BindView(R.id.countryInput)
    TextInputLayout countryInput;
    @BindView(R.id.countryLayout)
    LinearLayout countryLayout;
    @BindView(R.id.btnCancel)
    TextView btnCancel;
    @BindView(R.id.btnOk)
    TextView btnOk;
    @BindView(R.id.wheelPicker)
    WheelPicker wheelPicker;
    JSONArray m_jArry;
    Typeface textViewFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_document_selection);
        ButterKnife.bind(this);
        if (BuildConfig.FLAVOR.equalsIgnoreCase("electionApp")) {
            ImageView poweredByIcon = new ImageView(this);
            poweredByIcon.setImageResource(R.drawable.colorpowered);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 65);
            params.bottomMargin = 20;
            poweredByIcon.setLayoutParams(params);
            bottomLayer.addView(poweredByIcon);
        }
        init();
    }

    private void init() {
        textViewFont = ResourcesCompat.getFont(this, R.font.metropolis_extralight);
        JSONObject obj = null;
        ArrayList<String> countries = new ArrayList<>();
        try {
            obj = new JSONObject(Objects.requireNonNull(Common.loadJSONFromAsset(R.raw.country_docs, this)));
            m_jArry = obj.getJSONArray("countries");
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String countryName = jo_inside.getString("name");
                countries.add(countryName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        countryText.setInputType(InputType.TYPE_NULL);
        countryLayout.setVisibility(View.INVISIBLE);
        Collections.sort(countries);
        wheelPicker.setCyclic(false);
        wheelPicker.setCurved(true);
        wheelPicker.setSelectedItemTextColor(Color.BLACK);
        wheelPicker.setVisibleItemCount(6);
        wheelPicker.setFocusable(true);
        wheelPicker.setSoundEffectsEnabled(true);
        wheelPicker.setData(countries);
        wheelPicker.setSelectedItemPosition(countries.indexOf("Sri Lanka"));
        wheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                wheelPicker.setSelectedItemPosition(position);
                if (picker.getId() == R.id.wheelPicker) {
                    countryText.setText(data.toString());
                    Common.country = data.toString();
                    try {
                        String[] docList = null;
                        for (int i = 0; i < m_jArry.length(); i++) {
                            JSONObject jo_inside = m_jArry.getJSONObject(i);
                            String countryName = jo_inside.getString("name");
                            if (countryName == data.toString()) {
                                JSONObject obj = jo_inside.getJSONObject("docs");
                                String docs = obj.getString("name");
                                docList = docs.split(",");
                                break;
                            }
                        }
                        setDocsButtons(docList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        String[] docsList = {"Passport", "National Identity Card", "New National Identity Card"};
        setDocsButtons(docsList);
    }

    private void setDocsButtons(String[] docsList) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.docDetails);
        layout.removeAllViews();
        float dpi = getResources().getDisplayMetrics().density;
        int height = 40;
        if (dpi == 0.75)
            height = 32;
        else if (dpi == 1.0)
            height = 42;
        else if (dpi == 1.5)
            height = 63;
        else if (dpi == 2.0)
            height = 84;
        else if (dpi == 3.0)
            height = 126;
        else if (dpi == 4.0)
            height = 168;
        LinearLayout.LayoutParams lprams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, height);
        lprams.bottomMargin = 40;
        lprams.gravity = Gravity.CENTER_HORIZONTAL;


        if (docsList != null) {
            for (int i = 0; i < docsList.length; i++) {
                Button btn = new Button(this);
                btn.setId(i + 1);
                btn.setText(docsList[i]);
                btn.setTextAppearance(this, R.style.ButtonStyle);
                btn.setTypeface(textViewFont);
                btn.setLayoutParams(lprams);
                final int index = i;
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonOnClick(docsList[index]);
                    }
                });
                layout.addView(btn);
            }
        }
    }

    private void buttonOnClick(String docName) {
        Intent intent = null;
        switch (docName) {
            case "Passport":
                intent = new Intent(this, PassportScanner.class);
                break;
            case "Driver’s License":
                intent = new Intent(this, MultiDocumentUpload.class);
                intent.putExtra("type", "l_front");
                break;
            case "National Identity Card":
                intent = new Intent(this, DocCapturePage.class);
                intent.putExtra("type", "nicFront");
                break;
            case "New National Identity Card":
                intent = new Intent(this, MultiDocumentUpload.class);
                intent.putExtra("type", "n_nic_front");
                break;
        }
        if (intent != null)
            startActivity(intent);
    }

    @OnClick(R.id.backBtn)
    public void goBack() {
        this.finish();
        super.onBackPressed();
    }

    @OnClick(R.id.countryText)
    public void setCountry() {
        Common.hideSoftKeyboard(this);
        if (countryLayout.getVisibility() == View.INVISIBLE) {
            countryLayout.setVisibility(View.VISIBLE);
        }
//        if(wheelPicker.getVisibility() == View.INVISIBLE) {
//            wheelPicker.setVisibility(View.VISIBLE);
//        }
    }

    @OnClick({R.id.btnCancel, R.id.btnOk})
    public void hideWheel() {
        countryLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        countryLayout.setVisibility(View.INVISIBLE);
    }
}
