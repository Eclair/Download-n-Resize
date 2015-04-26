package com.eclair.downloadnresize.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by andrewcherkashin on 4/26/15.
 */
public class DRReporter {
    final static String TAG_REPORTER = "DRReporter";
    static Toast currentToast;

    public static void reportError(Context context, String message) {
        report(context, "Error:", message);
    }

    public static void reportServiceStatus(Context context, String status) {
        report(context, "Service:", status);
    }

    private static void report(Context context, String type, String message) {
        showToast(Toast.makeText(context, type + " " + message, Toast.LENGTH_SHORT));
        Log.d(TAG_REPORTER, type + " " + message);
    }

    private static void showToast(Toast toast) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = toast;
        currentToast.show();
    }
}
