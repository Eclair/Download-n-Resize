package com.eclair.downloadnresize;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import com.eclair.downloadnresize.adapters.TaskListAdapter;
import com.eclair.downloadnresize.helpers.DRReporter;
import com.eclair.downloadnresize.models.Task;
import com.eclair.downloadnresize.service.DRService;

import java.net.URL;
import java.net.MalformedURLException;

public class MainActivity extends Activity {
    private EditText urlField;
    private ImageButton downloadButton;
    private ImageButton clearButton;
    private ListView tasksListView;

    private TaskListAdapter taskListAdapter;

    private boolean isServiceBound;
    private DRService boundService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        bindWidgets();

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processUserInput(urlField.getText().toString().trim());
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urlField.setText("");
            }
        });

        taskListAdapter = new TaskListAdapter(this);
        tasksListView.setAdapter(taskListAdapter);
    }

    private void bindWidgets() {
        urlField = (EditText)findViewById(R.id.urlField);
        downloadButton = (ImageButton)findViewById(R.id.downloadButton);
        clearButton = (ImageButton)findViewById(R.id.clearButton);
        tasksListView = (ListView)findViewById(R.id.tasksListView);
    }

    private void processUserInput(String urlString) {
        if (isValidURL(urlString)) {
            startDownloadTask(urlFromString(urlString));
        } else {
            DRReporter.reportError(MainActivity.this, "Invalid URL");
        }
    }

    private void startDownloadTask(URL imageURL) {
        if (imageURL != null) {
            if (isServiceBound) {
                Task task = boundService.startImageDownload(imageURL);
                taskListAdapter.addTask(task);
                DRReporter.reportTaskStatus(this, task.id, "Created");
            } else {
                DRReporter.reportError(this, "Service unbound");
            }
        } else {
            DRReporter.reportError(this, "Can't cast string to URL");
        }
    }

    private boolean isValidURL(String url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    private URL urlFromString(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private void startService() {
        if (!isServiceBound) {
            Intent intent = new Intent(this, DRService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            taskListAdapter.clearTasks();
            DRReporter.reportServiceStatus(this, "Service Started");
        }
    }

    private void stopService() {
        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
            boundService = null;
            DRReporter.reportServiceStatus(this, "Service Stopped");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.service_control_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.startServiceAction:
                startService();
                return true;
            case R.id.stopServiceAction:
                stopService();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DRService.DRBinder binder = (DRService.DRBinder)service;
            boundService = binder.getService();
            boundService.setServiceListener(serviceListener);
            taskListAdapter.clearTasks();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            boundService = null;
            isServiceBound = false;
        }
    };

    private DRService.ServiceListener serviceListener = new DRService.ServiceListener() {
        @Override
        public void onTaskUpdate(Task task) {
            taskListAdapter.updateTask(task);
        }
    };
}
