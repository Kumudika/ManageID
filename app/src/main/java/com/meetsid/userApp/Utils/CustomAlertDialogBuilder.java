package com.meetsid.userApp.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.meetsid.userApp.Activities.PaymentGateway;
import com.meetsid.userApp.Activities.SuccessActivity;
import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.ServerUtils.MessageType;

public class CustomAlertDialogBuilder {
    public void showDialog(Context activity, String msg, MessageType type) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_dialog_message);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        ImageView icon = (ImageView) dialog.findViewById(R.id.icon);
        GradientDrawable gd = new GradientDrawable();
        if (type == MessageType.ERROR) {
            gd.setStroke(2, activity.getResources().getColor(R.color.meetsid_red1));
            icon.setImageResource(R.drawable.error_white);
            icon.setBackgroundColor(activity.getResources().getColor(R.color.meetsid_red1));
            dialogButton.setTextColor(activity.getResources().getColor(R.color.meetsid_red1));
        } else if (type == MessageType.WARNING) {
            gd.setStroke(2, activity.getResources().getColor(R.color.meetsid_yellow));
            icon.setImageResource(R.drawable.warning_icon);
            icon.setBackgroundColor(activity.getResources().getColor(R.color.meetsid_yellow));
            dialogButton.setTextColor(activity.getResources().getColor(R.color.meetsid_yellow));
        } else if (type == MessageType.SUCCESS) {
            gd.setStroke(2, activity.getResources().getColor(R.color.meetsid_teal));
            icon.setImageResource(R.drawable.tick);
            icon.setBackgroundColor(activity.getResources().getColor(R.color.meetsid_teal));
            dialogButton.setTextColor(activity.getResources().getColor(R.color.meetsid_teal));
        } else if (type == MessageType.INFO) {
            gd.setStroke(2, activity.getResources().getColor(R.color.meetsid_blue1));
            icon.setImageResource(R.drawable.information);
            icon.setBackgroundColor(activity.getResources().getColor(R.color.meetsid_blue1));
            dialogButton.setTextColor(activity.getResources().getColor(R.color.meetsid_blue1));
        }
        gd.setCornerRadius(50);
        dialogButton.setBackgroundDrawable(gd);
        text.setText(msg);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void showDialogAndOpenActivity(Context activity, String msg, MessageType type, final Class<? extends Activity> ActivityToOpen) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_dialog_message);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        ImageView icon = (ImageView) dialog.findViewById(R.id.icon);
        GradientDrawable gd = new GradientDrawable();
        if (type == MessageType.ERROR) {
            gd.setStroke(2, activity.getResources().getColor(R.color.meetsid_red1));
            icon.setImageResource(R.drawable.error_white);
            icon.setBackgroundColor(activity.getResources().getColor(R.color.meetsid_red1));
            dialogButton.setTextColor(activity.getResources().getColor(R.color.meetsid_red1));
        } else if (type == MessageType.WARNING) {
            gd.setStroke(2, activity.getResources().getColor(R.color.meetsid_yellow));
            icon.setImageResource(R.drawable.warning_icon);
            icon.setBackgroundColor(activity.getResources().getColor(R.color.meetsid_yellow));
            dialogButton.setTextColor(activity.getResources().getColor(R.color.meetsid_yellow));
        } else if (type == MessageType.SUCCESS) {
            gd.setStroke(2, activity.getResources().getColor(R.color.meetsid_teal));
            icon.setImageResource(R.drawable.tick);
            icon.setBackgroundColor(activity.getResources().getColor(R.color.meetsid_teal));
            dialogButton.setTextColor(activity.getResources().getColor(R.color.meetsid_teal));
        } else if (type == MessageType.INFO) {
            gd.setStroke(2, activity.getResources().getColor(R.color.meetsid_blue1));
            icon.setImageResource(R.drawable.information);
            icon.setBackgroundColor(activity.getResources().getColor(R.color.meetsid_blue1));
            dialogButton.setTextColor(activity.getResources().getColor(R.color.meetsid_blue1));
        }
        gd.setCornerRadius(50);
        dialogButton.setBackgroundDrawable(gd);
        text.setText(msg);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityToOpen.equals(PaymentGateway.class)) {
                    Intent intent = null;
                    if (Common.amount > 0.0) {
                        intent = new Intent(activity, ActivityToOpen);
                    } else {
                        intent = new Intent(activity, SuccessActivity.class);
                        intent.putExtra("msg", "Success!");
                        intent.putExtra("activity", "PaymentGateway");
                    }
                    dialog.cancel();
                    activity.startActivity(intent);
                } else {
                    dialog.cancel();
                    activity.startActivity(new Intent(activity, ActivityToOpen));
                }
            }
        });

        dialog.show();

    }
}
