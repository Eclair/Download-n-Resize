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
import com.eclair.downloadnresize.helpers.DRReporter;
import com.eclair.downloadnresize.service.DRService;

import java.net.URL;
import java.net.MalformedURLException;

public class MainActivity extends Activity {
    private EditText urlField;
    private ImageButton downloadButton;
    private ListView tasksListView;

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
    }

    private void bindWidgets() {
        urlField = (EditText)findViewById(R.id.urlField);
        downloadButton = (ImageButton)findViewById(R.id.downloadButton);
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
            int newTaskId = boundService.startImageDownload(imageURL);
            DRReporter.reportTaskStatus(this, newTaskId, "Created");
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
            DRReporter.reportServiceStatus(this, "Service Started");
        }
    }

    private void stopService() {
        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
            DRReporter.reportServiceStatus(this, "Service Stopped");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService();
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
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
        }
    };
}
