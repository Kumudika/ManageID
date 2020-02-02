// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.meetsid.userApp.TextDetectionUtil.TextDetection;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.meetsid.userApp.Activities.NicScanner;
import com.meetsid.userApp.Activities.PassportScanner;
import com.meetsid.userApp.TextDetectionUtil.MRZCallback;
import com.meetsid.userApp.TextDetectionUtil.Other.TextFrameMetadata;
import com.meetsid.userApp.TextDetectionUtil.Other.TextGraphicOverlay;
import com.meetsid.userApp.TextDetectionUtil.TextDetectionResultListener;
import com.meetsid.userApp.Utils.MRZ.MrzParser;
import com.meetsid.userApp.Utils.MRZ.MrzRecord;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Processor for the text recognition demo.
 */
public class TextRecognitionProcessor {

    private static final String TAG = "TextRecProc";

    private final FirebaseVisionTextRecognizer detector;
    TextDetectionResultListener textDetectionResultListener;
    MRZCallback mrzCallback;

    // Whether we should ignore process(). This is usually caused by feeding input data faster than
    // the model can handle.
    private final AtomicBoolean shouldThrottle = new AtomicBoolean(false);
    String type;
    int deviceHeight;
    int deviceWidth;

    public TextRecognitionProcessor(String type, int height, int width) {
        this.type = type;
        deviceHeight = height;
        deviceWidth = width;
        detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
    }

    public void setTextDetectionResultListener(TextDetectionResultListener textDetectionResultListener) {
        this.textDetectionResultListener = textDetectionResultListener;
    }

    public void setMRZCallback(MRZCallback mrzCallback) {
        this.mrzCallback = mrzCallback;
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

    protected Task<FirebaseVisionText> detectInImage(FirebaseVisionImage image) {
        Bitmap img = image.getBitmap();
        int width = img.getWidth();
        int height = img.getHeight();
        float widthRatio = width * 1.0f / deviceWidth;
        float heightRatio = height * 1.0f / deviceHeight;
        Bitmap croppedImg = null;
        if (type.equalsIgnoreCase("nic")) {
            int w = (int) (NicScanner.innerBoxWidth * widthRatio);
            int x = (width - w) / 2;
            int h = (int) (NicScanner.innerBoxHeight * heightRatio);
            int y = (int) (NicScanner.innerTopMargin * heightRatio);
            croppedImg = Bitmap.createBitmap(img, x, y, w, h);
            NicScanner.image = img;
        } else {
            int w = (int) (PassportScanner.outerBoxWidth * widthRatio);
            int x = (width - w) / 2;
            int h = (int) (PassportScanner.innerBoxHeight * heightRatio);
            int y = (int) (PassportScanner.innerTopMargin * heightRatio);
            croppedImg = Bitmap.createBitmap(img, x, y, w, h);
            PassportScanner.image = img;
        }
        return detector.processImage(FirebaseVisionImage.fromBitmap(croppedImg));
    }

    private void detectInVisionImage(FirebaseVisionImage image, final TextFrameMetadata metadata, final TextGraphicOverlay graphicOverlay) {
        detectInImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText results) {
                                shouldThrottle.set(false);
                                if (type.equalsIgnoreCase("nic"))
                                    textDetectionResultListener.onSuccess(results);
                                else {
                                    StringBuilder fullRead = new StringBuilder();
                                    List<FirebaseVisionText.TextBlock> blocks = results.getTextBlocks();
                                    Iterator<FirebaseVisionText.TextBlock> blockIterator = blocks.iterator();
                                    while (blockIterator.hasNext()) {
                                        StringBuilder temp = new StringBuilder();
                                        List<FirebaseVisionText.Line> lines = blockIterator.next().getLines();
                                        for (FirebaseVisionText.Line line : lines) {
                                            temp.append(line.getText()).append("\n");
                                        }
                                        fullRead.append(temp);
                                    }
                                    String text = fullRead.toString().replace("«", "<").replace("\t", "").replace(" ", "");
                                    Log.i(TAG, fullRead.toString());
                                    try {
                                        MrzRecord record = MrzParser.parse(text);
                                        System.out.println(record);
                                        mrzCallback.onSuccess(record);
                                    } catch (Exception ex) {
                                        mrzCallback.onMRZReadFailure();
                                    }
                                }
                                graphicOverlay.clear();
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                shouldThrottle.set(false);
                                textDetectionResultListener.onFailure(e);
                            }
                        });
        shouldThrottle.set(true);
    }
}
