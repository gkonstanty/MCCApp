package pl.edu.agh.iet.mobilne.mccapp.view;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.edu.agh.iet.mobilne.mccapp.R;
import pl.edu.agh.iet.mobilne.mccapp.domain.PiTask;
import pl.edu.agh.iet.mobilne.mccapp.domain.Task;
import pl.edu.agh.iet.mobilne.mccapp.persistency.MccDBContract;
import pl.edu.agh.iet.mobilne.mccapp.persistency.MccDBOpenHelper;
import pl.edu.agh.iet.mobilne.mccapp.rest_handlers.PredictRequest;


public class MainActivity extends AppCompatActivity {


    private final String TAG = "MainActivity";

    private final String SERVER_IP = "http://34.253.103.15:8080";
    private final String SERVER_SEND_TASK = "tasks";
    private final String SERVER_PING_TASK = "pingResult";

    private Spinner spinnerTasks;

    private Button buttonPredict;
    private Button buttonSend;
    private Button buttonTasksList;

    private TextView textViewAnswer;

    private LinearLayout taskParamsLayout;
//    PI Task
    private EditText piPointsNo;


    private SeekBar seekBarCPU;
    private EditText editTextRAM;

    String taskTypeString;
    StringBuilder taskParamsStr;


    Handler toastHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            String mString=(String)msg.obj;
            Toast.makeText(MainActivity.this, mString, Toast.LENGTH_SHORT).show();
        }
    };

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        // populate view elements
        spinnerTasks = (Spinner) findViewById(R.id.spinner_tasks);

        buttonPredict = (Button) findViewById(R.id.button_predict);
        buttonSend = (Button) findViewById(R.id.button_send);
        textViewAnswer = (TextView) findViewById(R.id.textView_answer);
        buttonTasksList = (Button) findViewById(R.id.button_tasks);

        taskParamsLayout = (LinearLayout) findViewById(R.id.task_params_layout);

        seekBarCPU = (SeekBar) findViewById(R.id.seekBar);
        editTextRAM = (EditText) findViewById(R.id.editText_ram);

        buttonTasksList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TasksListActivity.class));
            }
        });


        spinnerTasks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //this is selected item
                String taskType = adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, "SPINNER: " + taskType);


                String[] tasks = getResources().getStringArray(R.array.tasktypelist);

                if (tasks.length > 0 && taskType.equals(tasks[0])) {
                    taskParamsLayout.removeAllViews();

                    piPointsNo = new EditText(MainActivity.this);
                    piPointsNo.setInputType(InputType.TYPE_CLASS_NUMBER);
                    piPointsNo.setHint(R.string.taskpi_help_points);

                    taskParamsLayout.addView(piPointsNo);

                }

                else if (tasks.length > 1 && taskType.equals(tasks[1])) {
                    taskParamsLayout.removeAllViews();

                    Message msg=new Message();
                    msg.obj="Wait! This task is a false one! :( ";
                    toastHandler.sendMessage(msg);
                }

                else if (tasks.length > 2 && taskType.equals(tasks[2])) {
                    taskParamsLayout.removeAllViews();

                    Message msg=new Message();
                    msg.obj="Wait! This task is a false one! :( ";
                    toastHandler.sendMessage(msg);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });


        database = new MccDBOpenHelper(this).getWritableDatabase();

    }



    public void sendTaskRequest(View view) {

        Log.d(TAG, "sendTaskRequest: ");


        Map<String, String> workerParams = new HashMap<>();
        workerParams.put("cpus", String.valueOf(seekBarCPU.getProgress() + 1));
        workerParams.put("ram", String.valueOf(editTextRAM.getText()));


        Log.d(TAG, "workerParams: " );
        for (Map.Entry<String, String> workerParam : workerParams.entrySet()) {
            Log.d(TAG, "\t" + workerParam.getKey() + ": " + workerParam.getValue());
        }


        final Task task;

        List<String> taskParams = new ArrayList<>();
        int taskType = (int) spinnerTasks.getSelectedItemId();
        taskTypeString = "";
        switch (taskType){
            case 0:
                taskTypeString = "PI";

                String piPointsNoStr = String.valueOf(piPointsNo.getText());
                taskParams.add(piPointsNoStr);

                Log.d(TAG, "sendTaskRequest: " + piPointsNoStr);
                // TODO: check why Long.getLong(piPointsNoStr) does not work
                task = new PiTask(workerParams, 0);
                break;
            case 1:
            case 2:
            default:
                Message msg=new Message();
                msg.obj="Wrong task type! :( ";
                toastHandler.sendMessage(msg);
                return;
        }

        Log.d(TAG, "taskParams: " );
        for (String taskParam: taskParams) {
            Log.d(TAG, "\t" + taskParam);
        }

        taskParamsStr = new StringBuilder("[");
        for (String param: taskParams){

            taskParamsStr.append(param);
            taskParamsStr.append(",");
        }

        // delete last ","
        if (taskParamsStr.length() > 1) {
            taskParamsStr.setLength(taskParamsStr.length() - 1);
        }

        taskParamsStr.append("]");


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

                    String dataToSend = "{\"taskType\": \"" + taskTypeString  + "\" ,\"taskParams\": "+ taskParamsStr.toString() +" }";
                    Log.d(TAG, "dataToSend: " + dataToSend);
                    OutputStreamWriter wr = new OutputStreamWriter(client.getOutputStream());
                    wr.write(dataToSend);
                    wr.flush();

                    Log.d(TAG, "post response code: " + client.getResponseCode() + " ");


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



                    ContentValues values = new ContentValues();
                    values.put(MccDBContract.TaskEntry.RESULT, "");
                    values.put(MccDBContract.TaskEntry.SERVER_ID, taskId);
                    values.put(MccDBContract.TaskEntry.TASK_PARAMS, task.getTaskParametersString());
                    values.put(MccDBContract.TaskEntry.WORKER_PARAMS, task.getWorkerParametersString());

                    long newTaskId = database.insert(MccDBContract.TaskEntry.TABLE_NAME, null, values);

                    task.setId(newTaskId);

                    Message msg=new Message();
                    msg.obj="Created task with ID: " + taskId;
                    toastHandler.sendMessage(msg);


                } catch (Exception e) {
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


        String dataToSend = "{" +
                "\"task_type\": \"" + taskTypeString  + "\" ,\"" +
                // ugly hard code: every task has one and only one param
                "task_params\": "+ taskParams.get(0) +
                "wRAM\": "+ workerParams.get("ram") +
                "wCPU\": "+ workerParams.get("cpus") +
                // TODO: add to form:
                "wIOmin\": "+ "100" +
                "wIOmax\": "+ "3000" +
            " }";

        new PredictRequest(MainActivity.this, MainActivity.this, database, mainPredictHandler, task).execute(dataToSend);
    }



    Handler mainPredictHandler = new Handler();



}
