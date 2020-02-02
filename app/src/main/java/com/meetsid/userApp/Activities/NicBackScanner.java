package com.meetsid.userApp.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.meetsid.userApp.FaceDetectionUtil.common.CameraSource;
import com.meetsid.userApp.FaceDetectionUtil.common.GraphicOverlay;
import com.meetsid.userApp.R;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NicBackScanner extends AppCompatActivity {
    @BindView(R.id.camView)
    CameraView camView;
    @BindView(R.id.graphic_overlay)
    GraphicOverlay graphicOverlay;
    @BindView(R.id.btnCapture)
    Button btnCapture;
    CameraSource cameraSource = null;
    public static Bitmap bitmap = null;
    String TAG = "take_picture";
    File outputDir;
    File file = null;
    boolean isCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nic_back_scanner);
        ButterKnife.bind(this);
        outputDir = this.getCacheDir();
        if (camView != null)
            createCameraSource();
    }

    private void createCameraSource() {
        camView.setFacing(CameraKit.Constants.FACING_FRONT);
        cameraSource = new CameraSource(this, graphicOverlay);
        cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
        startCameraSource();
    }

    private void startCameraSource() {
        if (camView != null && cameraSource != null && graphicOverlay != null) {
            Log.d(TAG, "startCameraSource: ");
            camView.start();
        } else
            Log.d(TAG, "startCameraSource: not started");
    }

    @OnClick(R.id.btnCapture)
    public void takeSelfie() {
        camView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
            @Override
            public void callback(CameraKitImage cameraKitImage) {
                isCompleted = false;
                bitmap = cameraKitImage.getBitmap();
                int h = 0;
                int w = 0;
                if (bitmap.getWidth() > bitmap.getHeight()) {
                    w = bitmap.getHeight() / 2 * 3;
                    h = bitmap.getHeight();
                } else {
                    h = bitmap.getWidth() / 2 * 3;
                    w = bitmap.getWidth();
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h);
                file = null;
                try {
                    file = File.createTempFile("nicBack", ".jpg", outputDir);
                    if (!file.exists())
                        file.mkdirs();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                OutputStream outStream = null;
                try {
                    outStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
                    outStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int w1 = 200;
                int h1 = (int) ((bitmap.getHeight() / (bitmap.getWidth() * 1.0f)) * w1);
                bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(file.getPath()), w1, h1, true);
                if (file.length() / 1000 > 300) {

                }
                isCompleted = true;
                Intent intent = new Intent();
                intent.putExtra("isCompleted", isCompleted);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (camView != null)
            camView.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isCompleted", isCompleted);
        setResult(RESULT_OK, intent);
        finish();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}
