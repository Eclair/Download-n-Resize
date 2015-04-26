package com.eclair.downloadnresize;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class MainActivity extends Activity {
    private EditText urlField;
    private ImageButton downloadButton;
    private ListView tasksListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        bindWidgets();
    }

    private void bindWidgets() {
        urlField = (EditText)findViewById(R.id.urlField);
        downloadButton = (ImageButton)findViewById(R.id.downloadButton);
        tasksListView = (ListView)findViewById(R.id.tasksListView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.service_control_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
