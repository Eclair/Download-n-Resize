package com.eclair.downloadnresize.models;

import android.net.Uri;

import java.net.URL;

/**
 * Created by andrewcherkashin on 4/26/15.
 */
public class Task {
    public int id;
    public TaskState state;
    public URL webURL;
    public Uri imageUri;

    public Task(int id, URL webURL) {
        this.id = id;
        this.webURL = webURL;
    }

    public enum TaskState {
        Unknown,
        Downloading,
        Resizing,
        Failed,
        Done
    };
}
