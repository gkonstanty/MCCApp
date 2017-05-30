package pl.edu.agh.iet.mobilne.mccapp;

import android.app.DownloadManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import static android.R.attr.button;
import static android.R.attr.data;
import static java.lang.System.in;

public class MainActivity extends AppCompatActivity {

    private final String SERVER_IP = "http://34.253.103.15:8080";
    private final String SERVER_SEND_TASK = "tasks";
    private final String SERVER_PING_TASK = "pingResult";

    private Button buttonCheck;
    private TextView textViewAnswer;

    private int lastTaskId = 0;
    private String lastTaskAnswer = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCheck = (Button) findViewById(R.id.button_check);
        textViewAnswer = (TextView) findViewById(R.id.textView_answer);

        Log.d("MainActivity", "onCreate");

    }


    public void sendTaskRequest(View view) {

        Log.d("MainActivity", "sendTaskRequest: ");

        new Thread(new Runnable() {
            public void run() {
                HttpURLConnection client = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(SERVER_IP + "/" + SERVER_SEND_TASK);
                    client = (HttpURLConnection) url.openConnection();

                    client.setRequestMethod("POST");
                    client.setRequestProperty("Content-Type", "application/json");
                    client.setDoOutput(true);

                    OutputStreamWriter wr = new OutputStreamWriter(client.getOutputStream());
                    wr.write("{\"taskType\": \"VM1\" }");
                    wr.flush();

                    Log.d("\"MainActivity\"", "post response code: " + client.getResponseCode() + " ");


                    reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    String response = sb.toString();
                    Log.d("RESPONSE POST", response);


                    JSONObject jsonResponse = null;
                    String taskId = "";
                    try {
                        jsonResponse = new JSONObject(response);
                        taskId = jsonResponse.getString("taskID");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    lastTaskId = Integer.parseInt(taskId);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            buttonCheck.setText("Check: " + lastTaskId);

                        }
                    });


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (SocketTimeoutException e) {
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
        }).start();
    }

    public void checkTaskStatus(View view) {


        Log.d("MainActivity", "checkTaskStatus: ");

        if (lastTaskId == 0) return;

        new Thread(new Runnable() {
            public void run() {

                HttpURLConnection client = null;
                BufferedReader reader = null;
                try {
                    URL reqURL = new URL(SERVER_IP + "/" + SERVER_PING_TASK + "/" + lastTaskId);
                    client = (HttpURLConnection) (reqURL.openConnection());
                    client.setRequestMethod("GET");
                    client.connect();


                    Log.d("\"MainActivity\"", "post response code: " + client.getResponseCode() + " ");


                    reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    String response = sb.toString();
                    Log.d("RESPONSE POST", response);



                    JSONObject jsonResponse = null;
                    lastTaskAnswer = "";
                    try {
                        jsonResponse = new JSONObject(response);
                        lastTaskAnswer = jsonResponse.getString("answer");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            textViewAnswer.setText("Answer: " + lastTaskAnswer);

                        }
                    });


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
        }).start();
    }
}
