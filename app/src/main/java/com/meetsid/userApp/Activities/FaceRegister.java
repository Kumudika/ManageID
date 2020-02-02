package com.meetsid.userApp.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.meetsid.userApp.FaceDetectionUtil.ScannerActivity;
import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.Common;
import com.meetsid.userApp.Utils.ServerUtils.ConnectServer;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class FaceRegister extends AppCompatActivity {
    @BindView(R.id.image)
    CircleImageView image;
    @BindView(R.id.btnUpload)
    Button btnUpload;
    @BindView(R.id.btnRetry)
    Button btnRetry;
    Bitmap bitmap;
    Bitmap live;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_register);
        ButterKnife.bind(this);
//        Intent intent = getIntent();
//        path = intent.getStringExtra("path");
//        live0 = intent.getStringExtra("live0");
        imageResize();
//        Bundle b = getIntent().getExtras();
//        if (b != null) {
//            String uri_Str = b.getString("uri");
//            uri = Uri.parse(uri_Str);
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                image.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(this, ScannerActivity.class);
//        intent.putExtra("method", "register");
//        startActivity(intent);
//    }

    private void imageResize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int s_width = displayMetrics.widthPixels;
        image.getLayoutParams().width = s_width / 3 * 2;
        image.getLayoutParams().height = s_width / 3 * 2;
        for (Map.Entry<String, Bitmap> entry : ((Map<String, Bitmap>) ScannerActivity.images).entrySet()) {
            if (entry.getKey().equals("final")) {
                bitmap = entry.getValue();
            } else {
                live = entry.getValue();
            }
        }
//        bitmap = Common.getBitmap(path);
        image.setImageBitmap(bitmap);
//        live = Common.getBitmap(live0);
    }

    @OnClick({R.id.backBtn, R.id.btnRetry})
    public void setBtnRetry() {
        super.onBackPressed();
    }

    @OnClick(R.id.btnUpload)
    public void uploadFace() {
        SharedPreferences prf = getSharedPreferences(Common.username, Context.MODE_PRIVATE);
        String username = prf.getString(Common.username, null);
        HashMap<String, String> param = new HashMap<>();
        param.put("type", "face");
        param.put("username", username);
        param.put("data", Common.getBase64ImageString(bitmap));
        param.put("live0", Common.getBase64ImageString(live));
        ConnectServer.connect().addFace(param, this);
    }
}
