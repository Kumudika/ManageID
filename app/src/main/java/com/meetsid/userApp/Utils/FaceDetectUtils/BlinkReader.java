package com.meetsid.userApp.Utils.FaceDetectUtils;

import java.math.BigDecimal;

public class BlinkReader extends IBlinkReader {
    BigDecimal max = BigDecimal.ZERO;
    BigDecimal min = BigDecimal.ONE;
    BigDecimal maxDelta = BigDecimal.ZERO;
    BigDecimal currentDelta = BigDecimal.ZERO;

    int blinkCount = 0;
    int count = 1;

    public BlinkReader(int expectedCount, BlinkCB cb) {
        super(expectedCount, cb);
    }

    @Override
    public void addValue(double l, double r) {
        if (l < 0) {
            return;
        }
        BigDecimal left = new BigDecimal(l);
        BigDecimal right = new BigDecimal(r);

        if (left.compareTo(max) > 0) {
            max = left;
            maxDelta = max.subtract(min);
        }
        if (left.compareTo(min) < 0) {
            min = left;
            maxDelta = max.subtract(min);
        }
        currentDelta = max.subtract(left);

        System.out.println("Max:" + maxDelta + " Current:" + currentDelta);
        if (currentDelta.compareTo(maxDelta) < 0 && currentDelta.compareTo(new BigDecimal("0.01")) < 0 && maxDelta.compareTo(new BigDecimal("0.2")) > 0) {
            blinkCount++;
            System.out.println(count + " BlinkedCount:" + blinkCount);
            max = BigDecimal.ZERO;
            min = BigDecimal.ONE;
            maxDelta = BigDecimal.ZERO;
            currentDelta = BigDecimal.ZERO;
            if (this.blinkCount >= this.expectedCount) {
                this.cb.onBlinkComplete();
                return;
            } else {
                this.cb.onBlink(this.blinkCount);
                System.out.println(this.blinkCount);
            }
        }
        count++;
    }

    @Override
    public void resetBlinkReader() {
        max = BigDecimal.ZERO;
        min = BigDecimal.ONE;
        maxDelta = BigDecimal.ZERO;
        currentDelta = BigDecimal.ZERO;

        blinkCount = 0;
        count = 1;
    }
}
