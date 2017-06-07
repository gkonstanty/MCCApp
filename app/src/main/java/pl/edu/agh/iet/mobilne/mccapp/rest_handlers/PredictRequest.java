package pl.edu.agh.iet.mobilne.mccapp.rest_handlers;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import pl.edu.agh.iet.mobilne.mccapp.domain.Task;
import pl.edu.agh.iet.mobilne.mccapp.persistency.MccDBContract;

/**
 * Created by gaba on 02.06.17.
 */

public class PredictRequest extends AsyncTask<String, Void, String>{


    private final String TAG = "PredictRequest";
    private Handler handler;
    private Task task;
    private Context context;
    private Activity act;
    private SQLiteDatabase database;

    public PredictRequest(Context context, Activity act, SQLiteDatabase database, Handler handler, Task task){
        this.context = context;
        this.act = act;
        this.database = database;
        this.handler = handler;

        this.task = task;
    }


    private Runnable runnableCode = new Runnable() {

        @Override
        public void run() {

            Log.d(TAG, "TIMER!!!!");

            String[] projection = {
                    MccDBContract.TaskEntry.DONE
            };
            String whereClause = MccDBContract.TaskEntry.SERVER_ID + " = ?";
            String[] whereArgs = new String[] {
                    String.valueOf(task.getId())
            };

            Cursor cursor = database.query(
                    MccDBContract.TaskEntry.TABLE_NAME,     // The table to query
                    projection,                               // The columns to return
                    whereClause,                                     // The columns for the WHERE clause
                    whereArgs,                                     // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                      // don't sort
            );

            cursor.moveToFirst();
            if (cursor.isFirst()){

                String isDoneStr = cursor.getString(
                        cursor.getColumnIndexOrThrow(MccDBContract.TaskEntry.DONE)
                );
                boolean isDone = false;
                if (isDoneStr != null && isDoneStr.equals("1")){
                    isDone = true;
                }

                if (!isDone){
                    Log.d(TAG, "TIME TO CHECK!!!");
                    new ResultRequest(context, act).execute(task);

                    // TODO: to change the "10000" value
                    handler.postDelayed(runnableCode, 10000);

                } else {
                    Log.d(TAG, "THE RESULT IS HERE!!!");
                }
            } else {
                handler.postDelayed(runnableCode, 10000);

            }
        }
    };

    protected String doInBackground(String... datas) {

        String tasksAnswer = "";
        for (String dataToSend: datas) {

            HttpURLConnection client = null;
            BufferedReader reader = null;
            try {
                URL reqURL = new URL(RequestHelper.WATCHER_IP + "/" + RequestHelper.WATCHER_PREDICT);
                client = (HttpURLConnection) (reqURL.openConnection());

                client.setRequestMethod("POST");
                client.setRequestProperty("Content-Type", "application/json");
                client.setDoOutput(true);

                Log.d(TAG, "dataToSend: " + dataToSend);
                OutputStreamWriter wr = new OutputStreamWriter(client.getOutputStream());
                wr.write(dataToSend);
                wr.flush();


                Log.d(TAG, "post response code: " + client.getResponseCode() + " ");


                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
//
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                String response = sb.toString();
                Log.d("RESPONSE POST", response);


                long timeTowait = Integer.getInteger(response);


//                long timeTowait = 10000;

                Log.d(TAG, "delaying ping!!!");
                handler.postDelayed(runnableCode, timeTowait);



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
