package com.meetsid.userApp.Activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.meetsid.userApp.R;

public class ElectionMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_election_message);
        ImageView poweredByIcon = findViewById(R.id.poweredBy);
        poweredByIcon.setImageResource(R.drawable.colorpowered);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, 65);
        params.bottomMargin = 20;
        params.bottomToBottom = R.id.bottomLayer;
        params.leftToLeft = R.id.bottomLayer;
        params.rightToRight = R.id.bottomLayer;
        poweredByIcon.setLayoutParams(params);
    }
}
