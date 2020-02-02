package com.meetsid.userApp.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.meetsid.userApp.Activities.NotificationPage;
import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.ServerUtils.PushNotificationMsg;
import java.util.Map;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class FirebaseMessage extends FirebaseMessagingService {
    String TAG = "Firebase Message";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("TOKEN", s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        PushNotificationMsg notificationMsg = new PushNotificationMsg();
        for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            switch (key) {
                case "ex_type":
                    Common.loginType = "v_login";
                    break;
                case "id":
                    notificationMsg.setId(value);
                    break;
                case "type":
                    notificationMsg.setType(value);
                    break;
                case "body_title":
                    notificationMsg.setTitle(value);
            }
            Log.d(TAG, "key, " + key + " value " + value);
        }
        Intent intent = new Intent(this, NotificationPage.class);
        intent.putExtra("notificationId", 123);
        intent.putExtra("pushID", notificationMsg.getId());
        String msg = notificationMsg.getTitle() + "\n" + remoteMessage.getNotification().getBody();
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =  new NotificationCompat.Builder(this)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setContentText(msg)
                .setOngoing(true);
        notificationmanager.notify(123, mBuilder.build());
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }
}
