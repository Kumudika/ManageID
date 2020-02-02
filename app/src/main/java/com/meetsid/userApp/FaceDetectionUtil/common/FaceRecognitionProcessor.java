package com.meetsid.userApp.FaceDetectionUtil.common;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.meetsid.userApp.FaceDetectionUtil.FaceDetectionResultListener;
import com.meetsid.userApp.TextDetectionUtil.Other.TextFrameMetadata;
import com.meetsid.userApp.TextDetectionUtil.Other.TextGraphicOverlay;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FaceRecognitionProcessor {
    private static final String TAG = "TextRecProc";

    private final FirebaseVisionFaceDetector detector;
    FaceDetectionResultListener faceDetectionResultListener;

    // Whether we should ignore process(). This is usually caused by feeding input data faster than
    // the model can handle.
    private final AtomicBoolean shouldThrottle = new AtomicBoolean(false);

    public FaceRecognitionProcessor() {
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.NO_LANDMARKS)
                        .setContourMode(FirebaseVisionFaceDetectorOptions.NO_CONTOURS)
                        .build();
//                        .setPerformanceMode(FaceDetector.ACCURATE_MODE)
//                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
//                        .enableTracking()
//                        .build();

        detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
    }

    public void setFaceDetectionResultListener(FaceDetectionResultListener faceDetectionResultListener) {
        this.faceDetectionResultListener = faceDetectionResultListener;
    }

    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close Text Detector: " + e);
        }
    }


    public void process(ByteBuffer data, TextFrameMetadata frameMetadata, TextGraphicOverlay graphicOverlay) throws FirebaseMLException {

    }

    private Task<List<FirebaseVisionFace>> detectInImage(FirebaseVisionImage image) {
        return detector.detectInImage(image);
    }

    private void detectInVisionImage(FirebaseVisionImage image, final FrameMetadata metadata, final GraphicOverlay graphicOverlay) {
        detectInImage(image)
                .addOnSuccessListener(
                        results -> {
                            FaceRecognitionProcessor.this.onSuccess(image, results,
                                    metadata,
                                    graphicOverlay);
                            shouldThrottle.set(false);
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                FaceRecognitionProcessor.this.onFailure(e);
                                shouldThrottle.set(false);
                            }
                        });
        shouldThrottle.set(true);
    }

    private void onSuccess(FirebaseVisionImage image, List<FirebaseVisionFace> results, FrameMetadata metadata, GraphicOverlay graphicOverlay) {
        if (faceDetectionResultListener != null) {
            faceDetectionResultListener.onSuccess(image.getBitmap(), results, metadata, graphicOverlay);
        }
    }

    private void onFailure(Exception e) {
        faceDetectionResultListener.onFailure(e);
    }

    public void process(ByteBuffer data, FrameMetadata build, GraphicOverlay graphicOverlay) {
        if (shouldThrottle.get()) {
            return;
        }
        FirebaseVisionImageMetadata metadata =
                new FirebaseVisionImageMetadata.Builder()
                        .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                        .setWidth(build.getWidth())
                        .setHeight(build.getHeight())
                        .setRotation(build.getRotation())
                        .build();

        detectInVisionImage(FirebaseVisionImage.fromByteBuffer(data, metadata), build, graphicOverlay);
    }
}
