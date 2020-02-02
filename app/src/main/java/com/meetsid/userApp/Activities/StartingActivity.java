package com.meetsid.userApp.Activities;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.meetsid.userApp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartingActivity extends AppCompatActivity {
    @BindView(R.id.shield)
    ImageView shield;
    @BindView(R.id.basicIntro)
    TextView basicIntro;
    @BindView(R.id.leanmoreText)
    TextView leanmoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_starting);
        ButterKnife.bind(this);
        init();
    }

    void init() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        android.view.ViewGroup.LayoutParams layoutParams = shield.getLayoutParams();
        layoutParams.width = width / 3;
        shield.setLayoutParams(layoutParams);
    }

    @OnClick(R.id.backBtn)
    public void onBackClick() {
        this.finish();
        super.onBackPressed();
    }

    @OnClick(R.id.startButton)
    public void start() {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    @OnClick(R.id.tourButton)
    public void tour() {
        Intent intent = new Intent(this, MeetsidGuide.class);
        startActivity(intent);
    }

    @OnClick(R.id.leanmoreText)
    public void leanMore() {
        String url = "https://www.meetsid.com/coming-soon/";

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
