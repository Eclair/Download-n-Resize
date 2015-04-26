package com.eclair.downloadnresize.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by andrewcherkashin on 4/24/15.
 */
public class DRService extends Service {
    private final DRBinder binder = new DRBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void startImageDownload(Uri imageURL) {
        // TODO: Implement image download
    }

    public class DRBinder extends Binder {
        public DRService getService() {
            return DRService.this;
        }
    };
}
