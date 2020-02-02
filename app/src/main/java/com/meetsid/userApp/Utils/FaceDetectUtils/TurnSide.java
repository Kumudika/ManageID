package com.meetsid.userApp.Utils.FaceDetectUtils;

public enum TurnSide {
    LEFT(1),
    RIGHT(-1);

    public final int value;

    TurnSide(int val) {
        this.value = val;
    }
}
