package com.meetsid.userApp.Utils.FaceDetectUtils;

public interface FaceEventCB {
    void onBlinkDetected();

    void onBlinkCount(int count);

    void onSmileDetected(float probability);

    void onLeftTurnerDetected(float angle);

    void onRightTurnerDetected(float angle);
}
