package com.meetsid.userApp.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

public class PermissionUtil {
    private String[] galleryPermissions = {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
    };

    private String[] cameraPermissions = {
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
    };

    public String[] getGalleryPermissions() {
        return galleryPermissions;
    }

    public String[] getCameraPermissions() {
        return cameraPermissions;
    }

    public boolean verifyPermissions(Context context, String[] grantResults) {
        for (String result : grantResults) {
            if (ActivityCompat.checkSelfPermission(context, result) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public boolean checkMarshMellowPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public static void showPermissionDialog(Context mContext, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, 0);
        builder.setTitle("Need Permission");
        builder.setMessage(msg);
        builder.show();
    }
}