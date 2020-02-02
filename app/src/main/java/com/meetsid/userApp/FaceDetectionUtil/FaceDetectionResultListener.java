package com.meetsid.userApp.FaceDetectionUtil;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.meetsid.userApp.FaceDetectionUtil.common.FrameMetadata;
import com.meetsid.userApp.FaceDetectionUtil.common.GraphicOverlay;

import java.util.List;

/**
 * Created by Jaison.
 */
public interface FaceDetectionResultListener {
    void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<FirebaseVisionFace> faces,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay);

    void onFailure(@NonNull Exception e);
}
