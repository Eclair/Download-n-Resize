package com.eclair.downloadnresize.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.eclair.downloadnresize.R;
import com.eclair.downloadnresize.models.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrewcherkashin on 4/26/15.
 */
public class TaskListAdapter extends BaseAdapter {
    private Context context;
    private List<Task> taskList;

    public TaskListAdapter(Context context) {
        this.context = context;
        this.taskList = new ArrayList<Task>();
    }

    public void addTask(Task task) {
        taskList.add(task);
        notifyDataSetChanged();
    }

    public void updateTask(Task task) {
        taskList.set(task.id, task);
        notifyDataSetChanged();
    }

    public void clearTasks() {
        taskList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.task_item, null);
        }

        Task task = this.taskList.get(position);

        ImageView taskImage = (ImageView)view.findViewById(R.id.taskImage);
        TextView taskImageName = (TextView)view.findViewById(R.id.taskImageName);
        TextView taskState = (TextView)view.findViewById(R.id.taskState);
        ProgressBar taskProgressBar = (ProgressBar)view.findViewById(R.id.taskProgressBar);
        TextView taskProgressText = (TextView)view.findViewById(R.id.taskProgressText);

        taskImage.setImageBitmap(task.bitmap);
        taskImageName.setText(task.getFilename());
        taskState.setText(task.stateToDisplay());

        taskProgressBar.setProgress((int)(task.progress * 100));
        taskProgressText.setText("" + (int)(task.progress * 100) + "%");

        return view;
    }
}
