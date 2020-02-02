package com.meetsid.userApp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.databinding.library.BuildConfig;

import com.meetsid.userApp.Activities.NavActivity;
import com.meetsid.userApp.Activities.NotificationPage;
import com.meetsid.userApp.Activities.StartingActivity;
import com.meetsid.userApp.FaceDetectionUtil.ScannerActivity;
import com.meetsid.userApp.Utils.Common;
import com.meetsid.userApp.Utils.ServerUtils.ErrorObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btnSignup)
    Button btnSignup;
    @BindView(R.id.btnFaceID)
    Button btnFaceID;
    @BindView(R.id.meetsidLogo)
    ImageView meetsidLogo;
    @BindView(R.id.bottomLayer)
    LinearLayout bottomLayer;
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUESTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        try {
            extractErrorObjects();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (BuildConfig.FLAVOR.equalsIgnoreCase("electionApp")) {
            meetsidLogo.setImageResource(R.drawable.emblem);
            ImageView poweredByIcon = new ImageView(this);
            poweredByIcon.setImageResource(R.drawable.whitepowered);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 65);
            params.bottomMargin = 20;
            poweredByIcon.setLayoutParams(params);
            bottomLayer.addView(poweredByIcon);
        } else {
            meetsidLogo.setImageResource(R.drawable.meetsid_logo_vertical_white);
        }
        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 10);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            }
        }
//        if(ActivityCompat.checkSelfPermission(this, Manifest.permission_group.CAMERA) != PackageManager.PERMISSION_GRANTED)
    }

    @OnClick(R.id.btnFaceID)
    public void faceLogin() {
        Common.loginType = "login";
        Intent intent = new Intent(this, ScannerActivity.class);
        intent.putExtra("method", "login");
        startActivity(intent);
    }

    @OnClick(R.id.btnSignup)
    public void gotoSignUp() {
        btnSignup.setEnabled(false);
        Intent intent = new Intent(this, NavActivity.class);
        startActivity(intent);
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "Permission granted!");
        if (allPermissionsGranted()) {
            requestPermission();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

//    @OnClick(R.id.btnEmail)
//    public void gotoLogin() {
//        boolean x = Common.validateNIC("926810731v");
//        System.out.println(x);
//        Intent intent = new Intent(this, EmailVerification.class);
//        startActivity(intent);
//        String mrz = "PBLKAJAYANTHA<<MAPUTUGALA<ARACHCHIGE<<<<<<<<\nN8318579<0LKA9206296F2905235926810731V<<<<08";
//        boolean x = Common.validatePassportNumber(mrz);
//        System.out.println(MrzFormat.get(mrz));
//        MrzRecord record = MrzParser.parse(mrz);
//        System.out.println(record);
//    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnSignup.setEnabled(true);
    }

    private void extractErrorObjects() throws UnsupportedEncodingException {
        InputStream is = getResources().openRawResource(R.raw.errors);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";

        try {
            while ((line = reader.readLine()) != null) {
                // Split the line into different tokens (using the comma as a separator).
                String[] tokens = line.split("\t");
                if (tokens[0].equals("endpoint"))
                    continue;
                if (tokens[1].equals("Client")) {
                    ErrorObject errorObject = new ErrorObject();
                    errorObject.setEndpoint(tokens[0]);
                    errorObject.setSide(tokens[1]);
                    errorObject.setCode(tokens[2]);
                    errorObject.setTitle(tokens[3]);
                    errorObject.setDescription(tokens[4]);
                    ErrorObject.addErrorObject(tokens[2], errorObject);
                } else {
                    continue;
                }
            }
        } catch (IOException e1) {
            Log.e("MainActivity", "Error" + line, e1);
            e1.printStackTrace();
        }
    }
}
