package com.example.code.forge.utils;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.code.forge.config.TaskConfig;

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

/**
 * Created by User on 01/10.
 */

public final class SuperTask extends AsyncTask<Void, Void, String> {

    private final Context context;

    private SuperTask(Context context) {
        this.context = context;
        Log.d("Stop: ", "1");
    }

    public static void execute(Context context) {
        new SuperTask(context).execute();
        Log.d("Stop: ", "2");
    }

    public interface TaskListener {
        void onTaskRespond(String json);
        ContentValues setRequestValues(ContentValues contentValues);
    }


    public static String createPostString(Set<Map.Entry<String, Object>> set) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        Log.d("Stop: ", "3");
        boolean flag = true;
        Log.d("Stop: ", "4");

        for (Map.Entry<String, Object> value : set) {
            Log.d("Stop: ", "5");
            stringBuilder.append( flag ? "" : "&" );
            Log.d("Stop: ", "6");
            flag = false;
            Log.d("Stop: ", "7");
            stringBuilder.append(URLEncoder.encode(value.getKey(), "UTF-8"));
            Log.d("Stop: ", "8");
            stringBuilder.append("=");
            Log.d("Stop: ", "9");
            stringBuilder.append(URLEncoder.encode(value.getValue().toString(), "UTF-8"));
            Log.d("Stop: ", "10");
        }
        Log.d("Stop: ", "11");
        return stringBuilder.toString();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Log.d("Stop: ", "12");
            URL url = new URL(TaskConfig.LOGIN_URL);
            Log.d("Stop: ", "13");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            Log.d("Stop: ", "14");
            httpURLConnection.setRequestMethod("POST");
            Log.d("Stop: ", "15");
            httpURLConnection.setDoInput(true);
            Log.d("Stop: ", "16");
            httpURLConnection.setDoOutput(true);
            Log.d("Stop: ", "17");

            OutputStream outputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
            Log.d("Stop: ", "18");
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            Log.d("Stop: ", "19");
            String postString = createPostString(((TaskListener)this.context).setRequestValues(new ContentValues()).valueSet());
            Log.d("Stop: ", "20");
            bufferedWriter.write(postString);
            Log.d("Stop: ", "21");

            // clear
            bufferedWriter.flush();
            Log.d("Stop: ", "22");
            bufferedWriter.close();
            Log.d("Stop: ", "23");
            outputStream.close();
            Log.d("Stop: ", "24");

            InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            Log.d("Stop: ", "25");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            Log.d("Stop: ", "26");

            StringBuilder stringBuilder = new StringBuilder();
            Log.d("Stop: ", "27");
            String line = "";
            Log.d("Stop: ", "28");

            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }

            // clear
            bufferedReader.close();
            inputStream.close();

            httpURLConnection.disconnect();

            Log.d("This is the return: ", stringBuilder.toString());
            return stringBuilder.toString();
        } catch (Exception ignored) {
            Log.e("Error here: ", "The error is: ", ignored);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String json) {
        Log.d("Stop: ", "31");
        super.onPostExecute(json);
        Log.d("Stop: ", "32");
        ((TaskListener)this.context).onTaskRespond(json);
        Log.d("Stop: ", "33");
    }
}
