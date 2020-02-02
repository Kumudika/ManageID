package com.meetsid.userApp.Utils.FaceDetectUtils;

public abstract class IBlinkReader {
    protected final int expectedCount;
    protected final BlinkCB cb;

    IBlinkReader(int expectedCount, BlinkCB cb) {
        this.expectedCount = expectedCount;
        this.cb = cb;
    }

    public abstract void addValue(double left, double right);

    public abstract void resetBlinkReader();
}
