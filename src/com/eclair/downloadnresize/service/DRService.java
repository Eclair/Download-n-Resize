package com.eclair.downloadnresize.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.eclair.downloadnresize.models.Task;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrewcherkashin on 4/24/15.
 */
public class DRService extends Service {
    private final DRBinder binder = new DRBinder();
    private List<Task> taskList;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public int startImageDownload(URL imageURL) {
        if (taskList == null) {
            taskList = new ArrayList<Task>();
        }

        Task newTask = new Task(taskList.size(), imageURL);
        taskList.add(newTask);

        // TODO: Implement image download

        return newTask.id;
    }

    public class DRBinder extends Binder {
        public DRService getService() {
            return DRService.this;
        }
    };
}
