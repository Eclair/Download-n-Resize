package com.eclair.downloadnresize.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import com.eclair.downloadnresize.helpers.DRBitmapResizer;
import com.eclair.downloadnresize.helpers.DRGallerySave;
import com.eclair.downloadnresize.helpers.DRReporter;
import com.eclair.downloadnresize.models.Task;
import com.eclair.downloadnresize.network.ImageDownloadAsyncTask;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrewcherkashin on 4/24/15.
 */
public class DRService extends Service {
    public class DRBinder extends Binder {
        public DRService getService() {
            return DRService.this;
        }
    };

    public interface ServiceListener {
        void onTaskUpdate(Task task);
    };

    private final DRBinder binder = new DRBinder();
    private List<Task> taskList;
    private WeakReference<ServiceListener> listener;
    private final int ImageWidth = 320;
    private final int ImageHeight = 480;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setServiceListener(ServiceListener listener) {
        this.listener = new WeakReference<ServiceListener>(listener);
    }

    public Task startImageDownload(URL imageURL) {
        if (taskList == null) {
            taskList = new ArrayList<Task>();
        }

        Task newTask = new Task(taskList.size(), imageURL);
        newTask.state = Task.TaskState.Downloading;
        taskList.add(newTask);

        (new ImageDownloadAsyncTask(getApplicationContext(), progressUpdate)).execute(newTask);

        return newTask;
    }

    private void resizeBitmapImageForTask(int taskId) {
        taskList.get(taskId).bitmap = DRBitmapResizer.resizeBitmapToSize(taskList.get(taskId).bitmap, ImageWidth, ImageHeight);

        if (listener.get() != null) {
            listener.get().onTaskUpdate(taskList.get(taskId));
        }
    }

    private ImageDownloadAsyncTask.ImageDownloadProgressUpdate progressUpdate = new ImageDownloadAsyncTask.ImageDownloadProgressUpdate() {
        @Override
        public void onProgressUpdate(int taskId, float progress) {
            taskList.get(taskId).progress = progress;
            if (listener.get() != null) {
                listener.get().onTaskUpdate(taskList.get(taskId));
            }
        }

        @Override
        public void onSuccess(int taskId, Bitmap bitmap) {
            taskList.get(taskId).bitmap = bitmap;
            if (listener.get() != null) {
                listener.get().onTaskUpdate(taskList.get(taskId));
            }
            resizeBitmapImageForTask(taskId);
        }

        @Override
        public void onError(int taskId, String error) {
            taskList.get(taskId).state = Task.TaskState.Failed;
            if (listener.get() != null) {
                listener.get().onTaskUpdate(taskList.get(taskId));
            }
        }
    };
}
