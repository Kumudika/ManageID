package com.meetsid.userApp.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.meetsid.userApp.FaceDetectionUtil.common.CameraSource;
import com.meetsid.userApp.R;
import com.meetsid.userApp.TextDetectionUtil.Camera.TextCameraSource;
import com.meetsid.userApp.TextDetectionUtil.Camera.TextCameraSourcePreview;
import com.meetsid.userApp.TextDetectionUtil.Other.TextGraphicOverlay;
import com.meetsid.userApp.TextDetectionUtil.TextDetection.TextRecognitionProcessor;
import com.meetsid.userApp.TextDetectionUtil.TextDetectionResultListener;
import com.meetsid.userApp.Utils.Common;
import com.meetsid.userApp.Utils.DobVerification;
import com.meetsid.userApp.Utils.Verification;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;

;

public class NicScanner extends AppCompatActivity {

    @BindView(R.id.barcodeOverlay)
    TextGraphicOverlay barcodeOverlay;
    @BindView(R.id.preview)
    TextCameraSourcePreview preview;
    @BindView(R.id.croppedImage)
    ImageView croppedImage;
    @BindView(R.id.outerBorder)
    View outerBorder;
    @BindView(R.id.innerBorder)
    View innerBorder;
    public static Bitmap image;
    int height;
    int width;
    public static float innerTopMargin;
    public static float innerSideMargin;
    public static float innerBoxWidth;
    public static float innerBoxHeight;
    float outerBoxMarginSide;
    float outerBoxTopMargin;
    float outerBoxWidth;
    float outerBoxHeight;

    private TextCameraSource mCameraSource = null;
    TextDetectionResultListener textDetectionResultListener = null;
    String TAG = "TextDetection";
    Context context;
    String cameraFilePath = null;
    private AtomicBoolean initialize = new AtomicBoolean(false);
    File outputDir;
    File file = null;
    boolean isCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nic_scanner);
        ButterKnife.bind(this);
        context = this;
        init();
        if (preview != null && barcodeOverlay != null) {
            createCameraSource();
            startCameraSource();
        }
    }

    private void init() {
        outputDir = this.getCacheDir();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        width = metrics.widthPixels;

        outerBoxTopMargin = height * 0.2f;
        outerBoxHeight = height - (outerBoxTopMargin * 2);
        outerBoxWidth = (6 / (9 * 1.0f)) * outerBoxHeight;
        outerBoxMarginSide = (width - outerBoxWidth) / 2.0f;

        innerTopMargin = (2.5f / 9.0f) * outerBoxHeight;
        innerBoxHeight = (0.7f / 9.0f) * outerBoxHeight;
        innerTopMargin = innerTopMargin + outerBoxTopMargin;
        innerSideMargin = (1.5f / 6.0f) * outerBoxWidth;
        innerSideMargin = innerSideMargin + outerBoxMarginSide;
        innerBoxWidth = (3.0f / 6.0f) * outerBoxWidth;
//        outerBoxMarginSide = width * 0.1f;
//        outerBoxTopMargin = 0f;
//        outerBoxWidth = width - 2 * (outerBoxMarginSide);
//        outerBoxHeight = (height < (outerBoxWidth * 3 / 2)) ? 0 : outerBoxWidth * 3 / 2;
//        if (outerBoxHeight == 0) {
//            outerBoxHeight = height - (2 * outerBoxMarginSide);
//            outerBoxWidth = outerBoxHeight * 2 / 3;
//        }
//        outerBoxTopMargin = (height - outerBoxHeight) / 2.0f;
//        innerTopMargin = height * 0.34f;
//        innerSideMargin = width * 0.25f;
//        innerBoxWidth = width - (2 * innerSideMargin);
//        innerBoxHeight = height * 0.077f;
        outerBorder.setX(outerBoxMarginSide);
        outerBorder.setY(outerBoxTopMargin);
        outerBorder.getLayoutParams().width = (int) outerBoxWidth;
        outerBorder.getLayoutParams().height = (int) outerBoxHeight;
        innerBorder.setX(innerSideMargin);
        innerBorder.setY(innerTopMargin);
        innerBorder.getLayoutParams().width = (int) innerBoxWidth;
        innerBorder.getLayoutParams().height = (int) innerBoxHeight;
    }

    private void createCameraSource() {
        if (mCameraSource == null) {
            mCameraSource = new TextCameraSource(this, barcodeOverlay);
            mCameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
        }
        TextRecognitionProcessor textRecognitionProcessor = new TextRecognitionProcessor("nic", height, width);
        textRecognitionProcessor.setTextDetectionResultListener(getTextDetectionListener());
        mCameraSource.setMachineLearningFrameProcessor(textRecognitionProcessor);
    }

    private TextDetectionResultListener getTextDetectionListener() {
        if (textDetectionResultListener == null)
            textDetectionResultListener = new TextDetectionResultListener() {
                @Override
                public void onSuccess(@NonNull FirebaseVisionText text) {
                    isCompleted = false;
                    String textCaptured = text.getText();
                    textCaptured = textCaptured.trim().replace(" ", "");
                    textCaptured = textCaptured.replace("v", "V");
                    textCaptured = textCaptured.replace("I", "1");
                    String msg = Verification.validateNIC(textCaptured);
                    if (msg == null) {
                        Common.nic = textCaptured;
                        preview.stop();
                        int imageHeight = image.getHeight();
                        int imageWidth = image.getWidth();
                        float heightRatio = imageHeight * 1.0f / height;
                        int delta = (int) ((imageWidth - (heightRatio * width)) / 2.0f);
                        imageWidth = (int) (heightRatio * width);
                        float widthRatio = imageWidth * 1.0f / width;
                        int w = (int) (outerBoxWidth * widthRatio);
                        int x = (int) (outerBoxMarginSide * widthRatio) + delta;
                        int h = (int) (outerBoxHeight * heightRatio);
                        int y = (int) (outerBoxTopMargin * heightRatio);
                        Bitmap croppedImg = Bitmap.createBitmap(image, x, y, w, h);
                        image = croppedImg;
                        file = null;
                        try {
                            file = File.createTempFile("nicFront", ".jpg", outputDir);
                            if (!file.exists())
                                file.mkdirs();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        OutputStream outStream = null;
                        try {
                            outStream = new FileOutputStream(file);
                            image.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
                            outStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        int h2 = 300;
                        int w2 = (int) (image.getWidth() / (image.getHeight() * 1.0f) * h2);
                        image = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(file.getPath()), w2, h2, true);
//                        if(file.length()/1000 > 300) {
//
//                        }
                        isCompleted = true;
                        Intent intent = new Intent();
                        intent.putExtra("isCompleted", isCompleted);
                        Common.dataList = null;
                        LinkedHashMap<String, String> nicData = new LinkedHashMap<>();
                        nicData.put("NIC", Common.nic);
                        DobVerification dobVerification = new DobVerification();
                        nicData.put("First Name", "");
                        nicData.put("Last Name", "");
                        nicData.put("Date of Birth", dobVerification.calculateDOB(Common.nic));
                        nicData.put("Nationality", "Sri Lanka");
                        nicData.put("Sex", dobVerification.getGender(Common.nic));
                        Common.dataList = nicData;
                        intent.putExtra("type", "nic");
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(@NonNull Exception e) {

                }
            };

        return textDetectionResultListener;
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

    private void startCameraSource() {
        if (mCameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (barcodeOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(mCameraSource, barcodeOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult: " + requestCode);
//        preview.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        if (preview != null)
            preview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
}
