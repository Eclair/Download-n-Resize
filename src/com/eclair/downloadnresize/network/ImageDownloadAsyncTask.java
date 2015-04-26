package com.eclair.downloadnresize.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import com.eclair.downloadnresize.models.Task;

import java.io.*;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by andrewcherkashin on 4/26/15.
 */
public class ImageDownloadAsyncTask extends AsyncTask<Task, Float, Bitmap> {
    public interface ImageDownloadProgressUpdate {
        void onProgressUpdate(int taskId, float progress);
        void onSuccess(int taskId, Bitmap bitmap);
        void onError(int taskId, String error);
    };

    private Context context;
    private WeakReference<ImageDownloadProgressUpdate> progressUpdate;
    private Task currentTask;

    public ImageDownloadAsyncTask(Context context, ImageDownloadProgressUpdate progressUpdate) {
        this.context = context;
        this.progressUpdate = new WeakReference<ImageDownloadProgressUpdate>(progressUpdate);
    }

    private String extensionFromFileName(URL url) {
        String[] components = url.getFile().split("\\.");
        if (components.length > 1) {
            return "." + components[components.length - 1];
        }
        return "";
    }

    private boolean isImage(URLConnection connection) {
        return connection.getHeaderField("Content-Type").startsWith("image/");
    }

    private void onProgressUpdate(final float progress) {
        if (progressUpdate.get() == null) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                progressUpdate.get().onProgressUpdate(currentTask.id, progress);
            }
        });
    }

    private void onSuccess(final Bitmap bitmap) {
        if (progressUpdate.get() == null) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                progressUpdate.get().onSuccess(currentTask.id, bitmap);
            }
        });
    }

    private void onError(final String error) {
        if (progressUpdate.get() == null) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                progressUpdate.get().onError(currentTask.id, error);
            }
        });
    }

    @Override
    protected Bitmap doInBackground(Task... params) {
        this.currentTask = params[0];

        File temporaryFile;

        try {
            URLConnection connection = currentTask.webURL.openConnection();
            connection.connect();

            if (!isImage(connection)) {
                onError("URL is not an image");
                return null;
            }

            int length = connection.getContentLength();

            temporaryFile = File.createTempFile("temporary-image-", extensionFromFileName(currentTask.webURL), context.getCacheDir());

            InputStream input = new BufferedInputStream(connection.getInputStream());
            OutputStream output = new FileOutputStream(temporaryFile);

            byte data[] = new byte[1024];
            int total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress(((float) total / length));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            onError(e.getLocalizedMessage());
            return null;
        }

        return BitmapFactory.decodeFile(temporaryFile.getAbsolutePath());
    }

    @Override
    protected void onProgressUpdate(Float... values) {
        super.onProgressUpdate(values);

        onProgressUpdate(values[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if (bitmap != null) {
            onSuccess(bitmap);
        }
    }
}
