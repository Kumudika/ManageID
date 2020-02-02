package com.meetsid.userApp.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.meetsid.userApp.FaceDetectionUtil.common.CameraSource;
import com.meetsid.userApp.ImageDetectionUtils.BarcodeDetectionResultListner;
import com.meetsid.userApp.ImageDetectionUtils.ImageRecognitionProcessor;
import com.meetsid.userApp.R;
import com.meetsid.userApp.TextDetectionUtil.Camera.TextCameraSource;
import com.meetsid.userApp.TextDetectionUtil.Camera.TextCameraSourcePreview;
import com.meetsid.userApp.TextDetectionUtil.Other.TextGraphicOverlay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DocumentScanner extends AppCompatActivity {
    @BindView(R.id.barcodeOverlay)
    TextGraphicOverlay barcodeOverlay;
    @BindView(R.id.preview)
    TextCameraSourcePreview preview;
    @BindView(R.id.croppedImage)
    ImageView croppedImage;
    @BindView(R.id.border)
    View border;
    public static Bitmap image;
    int height;
    int width;
    public static float boaderTopMargin;
    public static float boaderSideMargin;
    public static float boaderBoxWidth;
    public static float boaderBoxHeight;

    private TextCameraSource mCameraSource = null;
    BarcodeDetectionResultListner barcodeDetectionResultListner = null;
    String TAG = "BarcodeDetection";
    Context context;
    String cameraFilePath = null;
    private AtomicBoolean initialize = new AtomicBoolean(false);
    File outputDir;
    File file = null;
    boolean isCompleted = false;
    private Calendar cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_scanner);
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

        boaderSideMargin = width * 0.05f;
        boaderBoxWidth = width - (boaderSideMargin * 2);
        boaderBoxHeight = (2.5f / 11.0f) * boaderBoxWidth;
        boaderTopMargin = (height - boaderBoxHeight) / 2.0f;

        border.setX(boaderSideMargin);
        border.setY(boaderTopMargin);
        border.getLayoutParams().width = (int) boaderBoxWidth;
        border.getLayoutParams().height = (int) boaderBoxHeight;
    }

    private void createCameraSource() {
        if (mCameraSource == null) {
            mCameraSource = new TextCameraSource(this, barcodeOverlay);
            mCameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
        }
        ImageRecognitionProcessor imageRecognitionProcessor = new ImageRecognitionProcessor("license", height, width);
        imageRecognitionProcessor.setTextDetectionResultListener(getImageRecognitionListner());
        mCameraSource.setMachineLearningImageProcessor(imageRecognitionProcessor);
    }

    private BarcodeDetectionResultListner getImageRecognitionListner() {
        if (barcodeDetectionResultListner == null)
            barcodeDetectionResultListner = new BarcodeDetectionResultListner() {
                @Override
                public void onSuccess(@NonNull List<FirebaseVisionBarcode> barcodes) {
                    isCompleted = false;
                    for (FirebaseVisionBarcode barcode : barcodes) {
                        if (barcode.getValueType() == 12) {
                            preview.stop();
                            HashMap<String, String> data = new HashMap<>();
                            data.put("First Name", toFirstCharUpperAll(barcode.getDriverLicense().getFirstName()));
                            data.put("Middle Name", toFirstCharUpperAll(barcode.getDriverLicense().getMiddleName()));
                            data.put("Last Name", toFirstCharUpperAll(barcode.getDriverLicense().getLastName()));
                            if (barcode.getDriverLicense().getGender().equals("2"))
                                data.put("Gender", "Female");
                            else if (barcode.getDriverLicense().getGender().equals("1"))
                                data.put("Gender", "Male");
                            else
                                data.put("Gender", "Other");
                            data.put("Birth Date", convertToDate(barcode.getDriverLicense().getBirthDate()));
                            data.put("Address Street", toFirstCharUpperAll(barcode.getDriverLicense().getAddressStreet()));
                            data.put("Address City", toFirstCharUpperAll(barcode.getDriverLicense().getAddressCity()));
                            data.put("Address State", barcode.getDriverLicense().getAddressState());
                            data.put("Address Zip", barcode.getDriverLicense().getAddressZip());
                            data.put("Document Type", barcode.getDriverLicense().getDocumentType());
                            data.put("License Number", barcode.getDriverLicense().getLicenseNumber());
                            data.put("Issuing Country", barcode.getDriverLicense().getIssuingCountry());
                            data.put("Issue Date", convertToDate(barcode.getDriverLicense().getIssueDate()));
                            data.put("Expiry Date", convertToDate(barcode.getDriverLicense().getExpiryDate()));
                            int imageHeight = image.getHeight();
                            int imageWidth = image.getWidth();
                            float heightRatio = imageHeight * 1.0f / height;
                            int delta = (int) ((imageWidth - (heightRatio * width)) / 2.0f);
                            imageWidth = (int) (heightRatio * width);
                            float widthRatio = imageWidth * 1.0f / width;
                            int w = (int) (boaderBoxWidth * widthRatio);
                            int x = (int) (boaderSideMargin * widthRatio) + delta;
                            int h = (int) (boaderBoxHeight * heightRatio);
                            int y = (int) (boaderTopMargin * heightRatio);
                            Bitmap croppedImg = Bitmap.createBitmap(image, x, y, w, h);
                            image = croppedImg;
                            file = null;
                            try {
                                file = File.createTempFile("document", ".jpg", outputDir);
                                if (!file.exists())
                                    file.mkdirs();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            OutputStream outStream = null;
                            try {
                                outStream = new FileOutputStream(file);
                                image.compress(Bitmap.CompressFormat.JPEG, 30, outStream);
                                outStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            int h2 = 150;
                            int w2 = (int) (image.getWidth() / (image.getHeight() * 1.0f) * h2);
                            image = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(file.getPath()), w2, h2, true);
//                        if(file.length()/1000 > 300) {
//
//                        }
                            isCompleted = true;
                            Intent intent = new Intent();
                            intent.putExtra("isCompleted", isCompleted);
                            intent.putExtra("data", data);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Exception e) {

                }
            };

        return barcodeDetectionResultListner;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isCompleted", isCompleted);
        setResult(RESULT_OK, intent);
        finish();
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

    public String toFirstCharUpperAll(String string) {
        string = string.toLowerCase();
        StringBuffer sb = new StringBuffer(string);
        for (int i = 0; i < sb.length(); i++)
            if (i == 0 || sb.charAt(i - 1) == ' ')//first letter to uppercase by default
                sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
        return sb.toString();
    }

    public String convertToDate(String inputString) {
        String date = "";
        SimpleDateFormat inputFormat = new SimpleDateFormat("mmddyyyy");
        Date theDate = null;
        try {
            theDate = inputFormat.parse(inputString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(theDate);
        date = cal.get(Calendar.DAY_OF_MONTH) + " " + getMonth(cal.get(Calendar.MINUTE)) + " " + cal.get(Calendar.YEAR);
        return date;
    }

    private String getMonth(int val) {
        String month = null;
        switch (val) {
            case 1:
                month = "January";
                break;
            case 2:
                month = "February";
                break;
            case 3:
                month = "March";
                break;
            case 4:
                month = "April";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "June";
                break;
            case 7:
                month = "July";
                break;
            case 8:
                month = "August";
                break;
            case 9:
                month = "September";
                break;
            case 10:
                month = "October";
                break;
            case 11:
                month = "November";
                break;
            case 12:
                month = "December";
                break;
        }
        return month;
    }
}

