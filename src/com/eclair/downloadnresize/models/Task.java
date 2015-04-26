package com.eclair.downloadnresize.models;

import android.net.Uri;

import java.net.URL;

/**
 * Created by andrewcherkashin on 4/26/15.
 */
public class Task {
    public int id;
    public TaskState state;
    public URL webUri;
    public Uri imageUri;

    public Task(int id, URL webUri) {
        this.id = id;
        this.webUri = webUri;
    }

    public enum TaskState {
        Unknown,
        Downloading,
        Resizing,
        Done
    };
}
