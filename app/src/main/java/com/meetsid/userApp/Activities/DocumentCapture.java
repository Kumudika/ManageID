package com.meetsid.userApp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.meetsid.userApp.FaceDetectionUtil.common.CameraSource;
import com.meetsid.userApp.FaceDetectionUtil.common.GraphicOverlay;
import com.meetsid.userApp.R;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DocumentCapture extends AppCompatActivity {
    @BindView(R.id.camView)
    CameraView camView;
    @BindView(R.id.overlay)
    GraphicOverlay overlay;
    @BindView(R.id.outerBorder)
    View outerBorder;
    @BindView(R.id.btnCapture)
    ImageButton btnCapture;

    CameraSource cameraSource = null;
    String TAG = "Document Capture";
    public static Bitmap docImage;
    boolean isCompleted = false;
    File outputDir;
    File file = null;
    int width;
    int height;
    float outerBoxMarginSide;
    float outerBoxWidth;
    float outerBoxHeight;
    float outerBoxTopMargin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_capture);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

        outerBoxMarginSide = width * 0.05f;
        outerBoxWidth = width - (outerBoxMarginSide * 2);
        outerBoxHeight = (10.0f / 16.0f) * outerBoxWidth;
        outerBoxTopMargin = (height - outerBoxHeight) / 2.0f;
        outerBorder.setX(outerBoxMarginSide);
        outerBorder.setY(outerBoxTopMargin);
        outerBorder.getLayoutParams().width = (int) outerBoxWidth;
        outerBorder.getLayoutParams().height = (int) outerBoxHeight;
        outputDir = this.getCacheDir();
        if (camView != null)
            createCameraSource();
    }

    private void createCameraSource() {
        camView.setFacing(CameraKit.Constants.FACING_BACK);
        cameraSource = new CameraSource(this, overlay);
        cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
        startCameraSource();
    }

    private void startCameraSource() {
        if (camView != null && cameraSource != null && overlay != null) {
            Log.d(TAG, "startCameraSource: ");
            camView.start();
        } else
            Log.d(TAG, "startCameraSource: not started");
    }

    @OnClick(R.id.btnCapture)
    public void onClickCapture() {
        camView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
            @Override
            public void callback(CameraKitImage cameraKitImage) {
                isCompleted = false;
                docImage = cameraKitImage.getBitmap();
                int imageHeight = docImage.getHeight();
                int imageWidth = docImage.getWidth();
                float heightRatio = imageHeight * 1.0f / height;
                float widthRatio = imageWidth * 1.0f / width;
                int w = (int) (outerBoxWidth * widthRatio);
                int x = (int) (outerBoxMarginSide * widthRatio);
                int h = (int) (outerBoxHeight * heightRatio);
                int y = (int) (outerBoxTopMargin * heightRatio);
                docImage = Bitmap.createBitmap(docImage, x, y, w, h);
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
                    docImage.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
                    outStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int w1 = 200;
                int h1 = (int) ((docImage.getHeight() / (docImage.getWidth() * 1.0f)) * w1);
                docImage = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(file.getPath()), w1, h1, true);
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

    @OnClick(R.id.backBtn)
    public void onBackClick() {
        Intent intent = new Intent();
        intent.putExtra("isCompleted", isCompleted);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isCompleted", isCompleted);
        setResult(RESULT_OK, intent);
        finish();
    }


}
