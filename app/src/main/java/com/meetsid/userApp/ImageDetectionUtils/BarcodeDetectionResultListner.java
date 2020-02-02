package com.meetsid.userApp.ImageDetectionUtils;

import androidx.annotation.NonNull;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

import java.util.List;

public interface BarcodeDetectionResultListner {
    void onSuccess(@NonNull List<FirebaseVisionBarcode> barcodes);

    void onFailure(@NonNull Exception e);
}
