package pl.edu.agh.iet.mobilne.mccapp.view;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pl.edu.agh.iet.mobilne.mccapp.R;
import pl.edu.agh.iet.mobilne.mccapp.domain.PiTask;
import pl.edu.agh.iet.mobilne.mccapp.domain.Task;
import pl.edu.agh.iet.mobilne.mccapp.persistency.MccDBContract;
import pl.edu.agh.iet.mobilne.mccapp.persistency.MccDBOpenHelper;
import pl.edu.agh.iet.mobilne.mccapp.rest_handlers.ResultRequest;

/**
 * Created by gaba on 02.06.17.
 */

public class TaskListAdapter extends BaseAdapter implements ListAdapter {

    private final String TAG = "TaskListAdapter";

    private ArrayList<Task> list = new ArrayList<Task>();
    private Context context;
    private Activity act;


    public TaskListAdapter(ArrayList<Task> list, Context context, Activity act) {
        this.list = list;
        this.context = context;
        this.act = act;

        refreshList();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return list.get(pos).getId();
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.resource_task_list, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position).toString());

        //Handle buttons and add onClickListeners
        Button buttonPredict = (Button) view.findViewById(R.id.button_predict);
        Button buttonDetails = (Button) view.findViewById(R.id.button_details);
        Button buttonResult = (Button) view.findViewById(R.id.button_result);

        buttonPredict.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO: do something
                notifyDataSetChanged();
            }
        });
        buttonDetails.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO: do something
                notifyDataSetChanged();
            }
        });
        buttonResult.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Task task = list.get(position);
                String result = task.getResult();
                if (result.equals("")){
                    new ResultRequest(context, act).execute(task);
                }
                String toastMsg = "Result: " + result;
                Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();

            }
        });

        Task task = list.get(position);
        if (task.isDone()){
            buttonPredict.setEnabled(false);
//            Log.d(TAG, "task: " + task.getId() + "is DONE!!!!!");
//        } else {
//            Log.d(TAG, "task: " + task.getId() + "is not DONE");
        }

        return view;
    }


    public void refreshList(){
        Cursor taskCursor = readFromDB();

        taskCursor.moveToFirst();
        try {
            do {
                Long server_id = Long.parseLong(taskCursor.getString(
                        taskCursor.getColumnIndexOrThrow(MccDBContract.TaskEntry.SERVER_ID)
                ));
                String isDoneStr = taskCursor.getString(
                        taskCursor.getColumnIndexOrThrow(MccDBContract.TaskEntry.DONE)
                );
                boolean isDone = false;
                if (isDoneStr != null && isDoneStr.equals("1")){
                    isDone = true;
                }
                String result = taskCursor.getString(
                        taskCursor.getColumnIndexOrThrow(MccDBContract.TaskEntry.RESULT)
                );
                String taskParams;
                String workerParams;
                String prediction;

                boolean newTask = true;
                for (Task task: list) {
                    if (task.getId() == server_id) {
                        task.setResult(result);
                        task.setDone(isDone);
                        newTask = false;
                    }
                }

                if (newTask) {
                    // TODO: check this when I get some sleep
                    // TODO: add all fields at the beginng of while
                    Task task = new PiTask(null, 0);
                    task.setId(server_id);
                    task.setResult(result);
                    task.setDone(isDone);
                    list.add(task);
                }
            } while ((taskCursor.moveToNext()));
        } finally {
            taskCursor.close();
        }
    }


    private Cursor readFromDB() {

        long date = 0;


        SQLiteDatabase database = new MccDBOpenHelper(context).getReadableDatabase();

        String[] projection = {
                MccDBContract.TaskEntry._ID,
                MccDBContract.TaskEntry.SERVER_ID,
                MccDBContract.TaskEntry.DONE,
                MccDBContract.TaskEntry.RESULT
        };

        Cursor cursor = database.query(
                MccDBContract.TaskEntry.TABLE_NAME,     // The table to query
                projection,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // don't sort
        );

        Log.d(TAG, "The total cursor count is " + cursor.getCount());
        return cursor;
    }

}