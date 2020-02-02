package com.meetsid.userApp.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.ServerUtils.MessageType;

public class AppAlertDialog {
    public static void serverAlertDialog(Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Error!");
        alertDialog.setMessage("There is a server error please connect after few minutes");
        alertDialog.setIcon(R.drawable.error_white);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialog.show();
    }

    public static void errorMessageDialog(Context context, String msg, MessageType type) {
        CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder();
        alert.showDialog(context, msg, type);
    }

    public static void openActivityErrorDialog(Context context, String msg, MessageType type, final Class<? extends Activity> ActivityToOpen) {
        CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder();
        alert.showDialogAndOpenActivity(context, msg, type, ActivityToOpen);
    }
}
