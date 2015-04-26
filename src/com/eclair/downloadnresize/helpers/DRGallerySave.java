package com.eclair.downloadnresize.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by andrewcherkashin on 4/26/15.
 */
public class DRGallerySave {
    private static final String IMAGES_NAME_PREFIX = "resized-image-";

    public static boolean saveToGallery(Context context, Bitmap bitmap) {
        File outputFile = writeImageToDisk(bitmap);
        if (outputFile == null) {
            return false;
        }

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);;
        Uri contentUri = Uri.fromFile(outputFile);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);

        return true;
    }

    private static String generateFilename() {
        return IMAGES_NAME_PREFIX + System.currentTimeMillis() + ".png";
    }

    private static File writeImageToDisk(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        File file = new File(Environment.getExternalStorageDirectory(), generateFilename());
        try {
            if (!file.createNewFile()) {
                return null;
            }
            FileOutputStream output = new FileOutputStream(file);
            output.write(bytes.toByteArray());
            output.close();
        } catch (IOException e) {
            return null;
        }
        return file;
    }
}
