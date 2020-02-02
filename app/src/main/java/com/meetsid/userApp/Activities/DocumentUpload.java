package com.meetsid.userApp.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aigestudio.wheelpicker.WheelPicker;
import com.meetsid.userApp.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DocumentUpload extends AppCompatActivity implements WheelPicker.OnItemSelectedListener {
    @BindView(R.id.docPicker)
    WheelPicker docPicker;
    @BindView(R.id.okBtn)
    Button ok;
    @BindView(R.id.slctdDoc)
    TextView slctdDoc;
    @BindView(R.id.btnNext)
    TextView nxtBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);
        ArrayList<String> docList = (ArrayList<String>) getIntent().getSerializableExtra("docList");
        ButterKnife.bind(this);
        docPicker.setCyclic(false);
        docPicker.setCurved(true);
        docPicker.setSelectedItemTextColor(Color.BLACK);
        docPicker.setVisibleItemCount(2);
        docPicker.setSelectedItemPosition(5);
        docPicker.setFocusable(true);
        docPicker.setSoundEffectsEnabled(true);
        docPicker.setData(docList);
        docPicker.setVisibility(View.INVISIBLE);
        ok.setVisibility(View.INVISIBLE);
        docPicker.setOnItemSelectedListener(this);
        nxtBtn.setEnabled(false);
    }

    @OnClick(R.id.slctdDoc)
    public void onDocSelection() {
        docPicker.setVisibility(View.VISIBLE);
        ok.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.okBtn)
    public void onOkClick() {
        docPicker.setVisibility(View.INVISIBLE);
        ok.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.btnNext)
    public void onNextClick() {
        Intent intent = null;
        String document = slctdDoc.getText().toString();
        switch (document) {
            case "Passport":
//                intent = new Intent(this, AddPassport.class);
                break;
            case "Personal Identification Document":
                intent = new Intent(this, DocCapturePage.class);
                intent.putExtra("type", "nicFront");
                break;
            case "Birth Certificate":
                break;
            case "Driving Licence":
                break;
        }
        if (intent != null)
            startActivity(intent);
    }

    @Override
    public void onItemSelected(WheelPicker picker, Object data, int position) {
        if (picker.getId() == R.id.docPicker) {
            slctdDoc.setText(data.toString());
            nxtBtn.setEnabled(true);
        }
    }
}
