package com.example.qrcodereader.util;

import android.graphics.Bitmap;
// Microsoft Copilot 4/8/2024 "Generate java docs for the following class"
/**
 * The ImageUpload interface defines a callback method for notifying when an image is uploaded.
 */
public interface ImageUpload {
    /**
     * Callback method invoked when an image is uploaded successfully.
     *
     * @param ID The ID of the uploaded image.
     */
    void isUploaded(String ID);
}
