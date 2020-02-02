package com.meetsid.userApp.Utils.FaceDetectUtils;

import java.util.ArrayList;
import java.util.List;

public class TurnerReader {
    ArrayList<Float> dataList;
    TurnSide type;
    TurnerCB turnerCB;
    int index = 0;
    int xSpan = 10;

    public TurnerReader(TurnSide type, TurnerCB turnerCB) {
        this.dataList = new ArrayList<>();
        this.type = type;
        this.turnerCB = turnerCB;
    }

    public void reset() {
        dataList.clear();
    }

    float[] findMin(List<Float> arr) {
        float min = 1000;
        int index = 0;
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i) < min) {
                min = arr.get(i);
                index = i;
            }
        }
        float[] minData = {min, index};
        return minData;
    }

    float[] findMax(List<Float> arr) {
        float max = -1;
        int index = 0;
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i) > max) {
                max = arr.get(i);
                index = i;
            }
        }
        float[] maxData = {max, index};
        return maxData;
    }

    public void addValue(float angleY) {

        float angle = angleY * type.value;
        dataList.add(angle);
        if (this.dataList.size() < this.xSpan) {
            return;
        }

        int end = this.dataList.size() - 1;
        int lindex = (end - this.xSpan) < 0 ? 0 : (end - this.xSpan);

        if (end <= lindex) {
            return;
        }
        List<Float> spanData = dataList.subList(lindex, end);
        float[] maxData = findMax(spanData);
        int maxIndex = (int) maxData[1];
        if (maxIndex <= 0 || maxIndex == xSpan) {
            return;
        }
        List<Float> lowerSpanData = spanData.subList(0, maxIndex);
        List<Float> upperSpanData = spanData.subList(maxIndex, xSpan - 1);
        if (lowerSpanData.size() == 0 || upperSpanData.size() == 0)
            return;
        float[] minLower = findMin(lowerSpanData);
        float[] minUpper = findMin(upperSpanData);
        if (minLower[0] < 12 && maxData[0] > 40 && minUpper[0] < 12) {
            System.out.println("minL:" + minLower[0]);
            System.out.println("minU:" + minUpper[0]);
            System.out.println("max:" + maxData[0]);
            System.out.println("maxIndex:" + maxData[1]);
            reset();
            this.turnerCB.onComplete();
        }
    }
}
