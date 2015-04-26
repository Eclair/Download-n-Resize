package com.eclair.downloadnresize.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
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

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setServiceListener(ServiceListener listener) {
        this.listener = new WeakReference<ServiceListener>(listener);
    }

    public int startImageDownload(URL imageURL) {
        if (taskList == null) {
            taskList = new ArrayList<Task>();
        }

        Task newTask = new Task(taskList.size(), imageURL);
        taskList.add(newTask);

        (new ImageDownloadAsyncTask(getApplicationContext(), progressUpdate)).execute(newTask);

        return newTask.id;
    }

    private ImageDownloadAsyncTask.ImageDownloadProgressUpdate progressUpdate = new ImageDownloadAsyncTask.ImageDownloadProgressUpdate() {
        @Override
        public void onProgressUpdate(int taskId, float progress) {
            DRReporter.reportTaskStatus(getApplicationContext(), taskId, "" + progress);
        }

        @Override
        public void onSuccess(int taskId, Bitmap bitmap) {
            DRReporter.reportTaskStatus(getApplicationContext(), taskId, "Success");
            // TODO: Implement bitmap resizing
        }

        @Override
        public void onError(int taskId, String error) {
            DRReporter.reportTaskStatus(getApplicationContext(), taskId, "Error: " + error);
        }
    };
}
