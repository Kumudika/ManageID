package com.meetsid.userApp.TextDetectionUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.meetsid.userApp.Utils.MRZ.MrzRecord;

public interface MRZCallback {
    void onSuccess(@Nullable MrzRecord mrz);

    void onFailure(@NonNull Exception e);

    void onMRZReadFailure();
}
