package com.eclair.downloadnresize.helpers;

import android.graphics.Bitmap;

/**
 * Created by andrewcherkashin on 4/26/15.
 */
public class DRBitmapResizer {
    public static Bitmap resizeBitmapToSize(Bitmap bitmap, int width, int height) {
        Bitmap scaledImage = aspectScaledBitmap(bitmap, width, height);
        return centerCroppedBitmap(scaledImage, width, height);
    }

    private static Bitmap aspectScaledBitmap(Bitmap bitmap, int width, int height) {
        float scaledImageWidth, scaledImageHeight;
        float aspectWidth = (float)width / bitmap.getWidth();
        float aspectHeight = (float)height / bitmap.getHeight();
        if (aspectWidth > aspectHeight) {
            scaledImageWidth = width;
            scaledImageHeight = (bitmap.getHeight() * aspectWidth);
        } else {
            scaledImageHeight = height;
            scaledImageWidth = (bitmap.getWidth() * aspectHeight);
        }
        return Bitmap.createScaledBitmap(bitmap, (int)scaledImageWidth, (int)scaledImageHeight, false);
    }

    private static Bitmap centerCroppedBitmap(Bitmap bitmap, int width, int height) {
        int dx = (bitmap.getWidth() - width) / 2;
        int dy = (bitmap.getHeight() - height) / 2;
        return Bitmap.createBitmap(bitmap, dx, dy, width, height);
    }
}
