package com.meetsid.userApp.ImageDetectionUtils;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.meetsid.userApp.Activities.DocumentScanner;
import com.meetsid.userApp.TextDetectionUtil.Other.TextFrameMetadata;
import com.meetsid.userApp.TextDetectionUtil.Other.TextGraphicOverlay;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ImageRecognitionProcessor {
    private static final String TAG = "TextRecProc";

    private final FirebaseVisionBarcodeDetector detector;
    BarcodeDetectionResultListner barcodeDetectionResultListner;

    // Whether we should ignore process(). This is usually caused by feeding input data faster than
    // the model can handle.
    private final AtomicBoolean shouldThrottle = new AtomicBoolean(false);
    String type;
    int deviceHeight;
    int deviceWidth;

    public ImageRecognitionProcessor(String type, int height, int width) {
        this.type = type;
        deviceHeight = height;
        deviceWidth = width;
        FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(
                        FirebaseVisionBarcode.FORMAT_PDF417
                )
                .build();
        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);
    }

    public void setTextDetectionResultListener(BarcodeDetectionResultListner barcodeDetectionResultListner) {
        this.barcodeDetectionResultListner = barcodeDetectionResultListner;
    }

    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close Text Detector: " + e);
        }
    }


    public void process(ByteBuffer data, TextFrameMetadata frameMetadata, TextGraphicOverlay graphicOverlay) throws FirebaseMLException {
        if (shouldThrottle.get()) {
            return;
        }
        FirebaseVisionImageMetadata metadata =
                new FirebaseVisionImageMetadata.Builder()
                        .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                        .setWidth(frameMetadata.getWidth())
                        .setHeight(frameMetadata.getHeight())
                        .setRotation(frameMetadata.getRotation())
                        .build();

        detectInVisionImage(FirebaseVisionImage.fromByteBuffer(data, metadata), frameMetadata, graphicOverlay);
    }

    protected Task<List<FirebaseVisionBarcode>> detectInImage(FirebaseVisionImage image) {
        Bitmap img = image.getBitmap();
        int width = img.getWidth();
        int height = img.getHeight();
        float widthRatio = width * 1.0f / deviceWidth;
        float heightRatio = height * 1.0f / deviceHeight;
        Bitmap croppedImg = null;
        if (type.equalsIgnoreCase("license")) {
            int w = (int) (DocumentScanner.boaderBoxWidth * widthRatio);
            int x = (width - w) / 2;
            int h = (int) (DocumentScanner.boaderBoxHeight * heightRatio);
            int y = (int) (DocumentScanner.boaderTopMargin * heightRatio);
            croppedImg = Bitmap.createBitmap(img, x, y, w, h);
            DocumentScanner.image = img;
        }
        return detector.detectInImage(FirebaseVisionImage.fromBitmap(croppedImg));
    }

    private void detectInVisionImage(FirebaseVisionImage image, final TextFrameMetadata metadata, final TextGraphicOverlay graphicOverlay) {
        detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                        shouldThrottle.set(false);
                        if (type.equalsIgnoreCase("license"))
                            barcodeDetectionResultListner.onSuccess(firebaseVisionBarcodes);
                        graphicOverlay.clear();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                shouldThrottle.set(false);
                barcodeDetectionResultListner.onFailure(e);
            }
        }).addOnCompleteListener(new OnCompleteListener<List<FirebaseVisionBarcode>>() {
            @Override
            public void onComplete(@NonNull Task<List<FirebaseVisionBarcode>> task) {
                shouldThrottle.set(false);
            }
        });
        shouldThrottle.set(true);
    }
}
