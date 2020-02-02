package com.meetsid.userApp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.meetsid.userApp.FaceDetectionUtil.ScannerActivity;
import com.meetsid.userApp.R;

public class NotificationPage extends AppCompatActivity implements OnMapReadyCallback {
    String pushID = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_page);
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = getIntent();
        int id = intent.getIntExtra("notificationId", 123);
        pushID = intent.getStringExtra("pushID");
        notificationmanager.cancel(id);
        showAlert("Title", "Here is the body text");
    }

    private void showAlert(String titleTxt, String msg) {
        Context context = this;
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.push_notification);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView body = (TextView) dialog.findViewById(R.id.body);
        Button acceptBtn = (Button) dialog.findViewById(R.id.btn_accept);
        Button declineBtn = (Button) dialog.findViewById(R.id.btn_decline);
        title.setText(titleTxt);
        body.setText(msg);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ScannerActivity.class);
                intent.putExtra("method", "login");
                intent.putExtra("pushID", pushID);
                intent.putExtra("isDeclined", false);
                context.startActivity(intent);
            }
        });
        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ScannerActivity.class);
                intent.putExtra("method", "login");
                intent.putExtra("pushID", pushID);
                intent.putExtra("isDeclined", true);
                context.startActivity(intent);
            }
        });
        dialog.show();
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
    }
}
