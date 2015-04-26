package com.eclair.downloadnresize.models;

import android.graphics.Bitmap;
import android.net.Uri;

import java.net.URL;

/**
 * Created by andrewcherkashin on 4/26/15.
 */
public class Task {
    public int id;
    public TaskState state;
    public URL webURL;
    public Bitmap bitmap;
    public float progress;

    public Task(int id, URL webURL) {
        this.id = id;
        this.webURL = webURL;
        this.state = TaskState.Unknown;
    }

    public String getFilename() {
        String[] components = webURL.getFile().split("\\/");
        return components[components.length - 1];
    }

    public String stateToDisplay() {
        switch (state) {
            case Downloading: return "Downloading...";
            case Resizing: return "Resizing...";
            default: return state.toString();
        }
    }

    public enum TaskState {
        Unknown,
        Downloading,
        Resizing,
        Failed,
        Done
    };
}
