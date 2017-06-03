package pl.edu.agh.iet.mobilne.mccapp.rest_handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import pl.edu.agh.iet.mobilne.mccapp.domain.Task;
import pl.edu.agh.iet.mobilne.mccapp.persistency.MccDBContract;
import pl.edu.agh.iet.mobilne.mccapp.persistency.MccDBOpenHelper;

/**
 * Created by gaba on 02.06.17.
 */

public class ResultRequest extends AsyncTask<Task, Void, String>{


    private final String TAG = "ResultRequest";
    private Context context;

    public ResultRequest(Context context){
        this.context = context;
    }


    protected String doInBackground(Task... tasks) {

        String tasksAnswer = "";
        for (Task task: tasks) {

            HttpURLConnection client = null;
            BufferedReader reader = null;
            String taskAnswer = "";
            try {
                URL reqURL = new URL(RequestHelper.SERVER_IP + "/" + RequestHelper.SERVER_PING_TASK + "/" + task.getId());
                client = (HttpURLConnection) (reqURL.openConnection());
                client.setRequestMethod("GET");
                client.connect();


                Log.d(TAG, "get response code: " + client.getResponseCode() + " ");


                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                String response = sb.toString();
                Log.d("RESPONSE POST", response);



                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response);
                    taskAnswer = jsonResponse.getString("answer");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // update object (for view)
                task.setResult(taskAnswer);
                task.setDone(true);


                // update db
                ContentValues values = new ContentValues();
                values.put(MccDBContract.TaskEntry.RESULT, taskAnswer);

                SQLiteDatabase database = new MccDBOpenHelper(context).getWritableDatabase();

                String[] args = new String[]{String.valueOf(task.getId())};
                database.update(
                        MccDBContract.TaskEntry.TABLE_NAME,
                        values,
                        MccDBContract.TaskEntry.SERVER_ID +"=?", args);


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
