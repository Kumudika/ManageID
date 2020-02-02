package com.meetsid.userApp.Utils.FileUtils;

public interface FileUploadCallback {
    void onComplete(String filepath, String response);

    void onError(String filepath, String error);

    void onProgress(String filepath, float process);
}
