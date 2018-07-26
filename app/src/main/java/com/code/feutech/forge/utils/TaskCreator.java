package com.code.feutech.forge.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

public final class TaskCreator extends AsyncTask<Void, Void, String> {
    private final Context context;
    private final Activity activity;
    private final String url;
    private final String id;

    private TaskCreator(Context context, Activity activity, String id, String url) {
        this.context = context;
        this.activity = activity;
        this.url = url;
        this.id = id;
    }

    public static void execute(Context context, Activity activity, String id, String url) {
        new TaskCreator(context, activity, id, url).execute();
    }

    public interface TaskListener {
        void onTaskRespond(String id, String json) throws Exception;

        void onTaskError(String id, Exception e);

        ContentValues setRequestValues(String id, ContentValues contentValues);
    }

    public static String createPostString(Set<Map.Entry<String, Object>> set) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        boolean flag = true;

        for (Map.Entry<String, Object> value : set) {
            stringBuilder.append(flag ? "" : "&");
            flag = false;
            stringBuilder.append(URLEncoder.encode(value.getKey(), "UTF-8"));
            stringBuilder.append("=");
            stringBuilder.append(URLEncoder.encode(value.getValue().toString(), "UTF-8"));
        }
        return stringBuilder.toString();
    }

    public static boolean isSuccessful(JSONObject response) throws JSONException {
        return response.getBoolean("success");
    }

    private void doCatch(final Exception e) {
        Log.e("tagx", "Error: ", e);
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final TaskCreator task = TaskCreator.this;
                    ((TaskListener) task.context).onTaskError(task.id, e);
                } catch (Exception ex) {
                    Log.e("tagx", "Error: ", ex);
                }
            }
        });
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            URL url = new URL(this.url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            String postString = createPostString(((TaskListener) this.context).setRequestValues(this.id, new ContentValues()).valueSet());
            bufferedWriter.write(postString);

            // clear
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            // clear
            bufferedReader.close();
            inputStream.close();

            httpURLConnection.disconnect();

            return stringBuilder.toString();
        } catch (Exception e) {
            this.doCatch(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String json) {
        super.onPostExecute(json);
        try {
            if (json != null) {
                Log.d("tagx", json);
                ((TaskListener) this.context).onTaskRespond(this.id, json);
            } else {
                throw new Exception("No json");
            }
        } catch (Exception e) {
            this.doCatch(e);
        }
    }
}
