package com.meetsid.userApp.Activities;

import android.app.Dialog;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.meetsid.userApp.FaceDetectionUtil.common.CameraSource;
import com.meetsid.userApp.R;
import com.meetsid.userApp.TextDetectionUtil.Camera.TextCameraSource;
import com.meetsid.userApp.TextDetectionUtil.Camera.TextCameraSourcePreview;
import com.meetsid.userApp.TextDetectionUtil.MRZCallback;
import com.meetsid.userApp.TextDetectionUtil.Other.TextGraphicOverlay;
import com.meetsid.userApp.TextDetectionUtil.TextDetection.TextRecognitionProcessor;
import com.meetsid.userApp.Utils.Common;
import com.meetsid.userApp.Utils.MRZ.MrzRecord;
import com.meetsid.userApp.Utils.MRZ.Records.MRP;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.meetsid.userApp.Utils.FaceDetectionScanner.Constants.PERMISSION_REQUEST_CAMERA;

public class PassportScanner extends AppCompatActivity {
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
    public static float outerBoxMarginSide;
    public static float outerBoxTopMargin;
    public static float outerBoxWidth;
    public static float outerBoxHeight;
    public static float innerTopMargin;
    public static float innerBoxHeight;

    private TextCameraSource mCameraSource = null;
    TextRecognitionProcessor textRecognitionProcessor;
    MRZCallback mrzCallback = null;
    String TAG = "TextDetection";
    Context context;
    String cameraFilePath = null;
    public static DisplayMetrics metrics;
    File file = null;
    File outputDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport_scanner);
        ButterKnife.bind(this);
        metrics = getResources().getDisplayMetrics();
        context = this;
        init();
        if (preview != null && barcodeOverlay != null) {
            createCameraSource();
            startCameraSource();
        }
    }

    private void init() {
        outputDir = this.getCacheDir();

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        width = metrics.widthPixels;
        barcodeOverlay.getLayoutParams().width = width;
        preview.getLayoutParams().width = width;

        outerBoxMarginSide = width * 0.05f;
        outerBoxWidth = width - (outerBoxMarginSide * 2);
        outerBoxHeight = (7.5f / 12.0f) * outerBoxWidth;
        outerBoxTopMargin = (height - outerBoxHeight) / 2.0f;
//        outerBoxTopMargin = 0.0f;
//        outerBoxWidth = width - 2 * (outerBoxMarginSide);
//        outerBoxHeight = (height < (outerBoxWidth * 8 / 12.5)) ? 0 : outerBoxWidth * 8 / 12.5f;
//        if (outerBoxHeight == 0) {
//
//        }
//        outerBoxTopMargin = (height - outerBoxHeight) / 2.0f;
//        innerTopMargin = outerBoxTopMargin + outerBoxHeight * 0.75f;
//        float innerSideMargin = outerBoxMarginSide;
//        float innerBoxWidth = outerBoxWidth;
//        innerBoxHeight = outerBoxHeight * 0.25f;
        innerBoxHeight = (1.6f / 7.5f) * outerBoxHeight;
        innerTopMargin = (5.8f / 7.5f) * outerBoxHeight;
        innerTopMargin = innerTopMargin + outerBoxTopMargin;
        float innerSideMargin = outerBoxMarginSide;
        float innerBoxWidth = outerBoxWidth;
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
        TextRecognitionProcessor textRecognitionProcessor = new TextRecognitionProcessor("passport", height, width);
        textRecognitionProcessor.setMRZCallback(getMRZCallback());
        mCameraSource.setMachineLearningFrameProcessor(textRecognitionProcessor);
    }

    private MRZCallback getMRZCallback() {
        Context context = this;
        if (mrzCallback == null) {
            mrzCallback = new MRZCallback() {
                @Override
                public void onSuccess(@NonNull MrzRecord mrz) {
                    preview.stop();
                    int imageWidth = image.getWidth();
                    int imageHeight = image.getHeight();
                    float widthRatio = (imageWidth * 1.0f / width);
                    float heightRatio = (imageHeight * 1.0f / height);
                    int x = (widthRatio != 0) ? ((int) (outerBoxMarginSide * widthRatio)) : (imageWidth * 5 / 100);
                    int w = (widthRatio != 0) ? ((int) (outerBoxWidth * widthRatio)) : (imageWidth - (2 * x));
                    int h = (heightRatio != 0) ? (int) (outerBoxHeight * heightRatio) : (int) (w * 8 / 12.5);
                    int y = (heightRatio != 0) ? (int) (outerBoxTopMargin * heightRatio) : ((imageHeight - h) / 2);
                    Bitmap croppedImg = Bitmap.createBitmap(image, x, y, w, h);
                    image = croppedImg;
                    file = null;
                    try {
                        file = File.createTempFile("passport", ".jpg", outputDir);
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
                    int w2 = 200;
                    int h2 = (int) ((image.getHeight() / (image.getWidth() * 1.0f)) * w2);
                    image = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(file.getPath()), w2, h2, true);
                    Intent intent = new Intent(context, PassportDetails.class);
                    LinkedHashMap<String, String> passportData = new LinkedHashMap<>();
                    if (mrz != null) {
                        Common common = new Common();
                        String country = common.getCountry(mrz.issuingCountry, context);
                        passportData.put("Passport No", mrz.documentNumber);
                        passportData.put("Last Name", capitalize(mrz.surname));
                        passportData.put("First Name", capitalize(mrz.givenNames));
                        passportData.put("Issue Country", country);
                        passportData.put("Nationality", country);
                        passportData.put("Passport Type", String.valueOf(mrz.code2));
                        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        DateFormat df1 = new SimpleDateFormat("dd/MM/yy");
                        try {
                            String d1 = df.format(df1.parse(mrz.dateOfBirth.toString().replace("{", "")));
                            String d2 = df.format(df1.parse(mrz.expirationDate.toString().replace("{", "")));
                            passportData.put("Date of Birth", d1);
                            passportData.put("Expire Date", d2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        passportData.put("NIC", ((MRP) mrz).personalNumber);
                        passportData.put("Sex", mrz.sex.name());

                    }
                    Common.dataList = null;
                    Common.dataList = passportData;
                    intent.putExtra("type", "passport");
                    context.startActivity(intent);
                }

                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Please clean some data to free storage", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onMRZReadFailure() {

                }
            };
        }

        return mrzCallback;
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

    private String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());

        Log.d(TAG, "startCameraSource: " + code);

        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, PERMISSION_REQUEST_CAMERA);
            dlg.show();
        }

        if (mCameraSource != null && preview != null && barcodeOverlay != null) {
            try {
                Log.d(TAG, "startCameraSource: ");
                preview.start(mCameraSource, barcodeOverlay);
            } catch (IOException e) {
                Log.d(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        } else
            Log.d(TAG, "startCameraSource: not started");

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
