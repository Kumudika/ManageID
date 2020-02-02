package com.meetsid.userApp.TextDetectionUtil;

import androidx.annotation.NonNull;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

public interface TextDetectionResultListener {
    void onSuccess(@NonNull FirebaseVisionText texts);

    void onFailure(@NonNull Exception e);
}
