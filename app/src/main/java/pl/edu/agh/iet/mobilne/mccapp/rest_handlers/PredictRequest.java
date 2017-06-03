package pl.edu.agh.iet.mobilne.mccapp.rest_handlers;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import pl.edu.agh.iet.mobilne.mccapp.domain.Task;

/**
 * Created by gaba on 02.06.17.
 */

public class PredictRequest extends AsyncTask<Task, Void, String>{


    private final String TAG = "PredictRequest";
//    private Task task;

//    public ResultRequest(Task task){
//        this.task = task;
//    }


    protected String doInBackground(Task... tasks) {

        String tasksAnswer = "";
        for (Task task: tasks) {

            HttpURLConnection client = null;
            BufferedReader reader = null;
            String taskAnswer = "";
            try {
                URL reqURL = new URL(RequestHelper.WATCHER_IP + "/" + RequestHelper.WATCHER_PREDICT);
                client = (HttpURLConnection) (reqURL.openConnection());

                client.setRequestMethod("POST");
                client.setRequestProperty("Content-Type", "application/json");
                client.setDoOutput(true);

//                OutputStreamWriter wr = new OutputStreamWriter(client.getOutputStream());
//                wr.write("{\"taskType\": 1, \"taskParameters\": \"hm\", \"workerParameters\": \"hm\" }");
//                wr.flush();

//                Log.d(TAG, "post response code: " + client.getResponseCode() + " ");
//
//
//                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
//                StringBuilder sb = new StringBuilder();
//                String line = null;
//
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line + "\n");
//                }
//
//                String response = sb.toString();
//                Log.d("RESPONSE POST", response);


                // TODO: do something with prediction...


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                try {
                    reader.close();
                    if (client != null) // Make sure the connection is not null.
                    {
                        client.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
        return tasksAnswer;
    }

    protected void onPostExecute(String taskAnswer) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }

}
