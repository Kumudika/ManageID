package com.meetsid.userApp.Utils.FaceDetectUtils;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public interface BlinkCB {
    void onBlinkComplete();

    void onBlink(int count);

    void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face);
}
