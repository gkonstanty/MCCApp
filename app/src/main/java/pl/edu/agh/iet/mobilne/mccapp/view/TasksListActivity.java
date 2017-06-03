package pl.edu.agh.iet.mobilne.mccapp.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import pl.edu.agh.iet.mobilne.mccapp.R;
import pl.edu.agh.iet.mobilne.mccapp.domain.Task;

public class TasksListActivity extends AppCompatActivity {

    private final String TAG = "TaskListActivity";

    private ListView taskView;
    private TaskListAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_list);



        //generate list
//        ArrayList<String> list = new ArrayList<String>();
//        list.add("item1");
//        list.add("item2");
        ArrayList<Task> list = new ArrayList<Task>();

        //instantiate custom adapter
        taskAdapter = new TaskListAdapter(list, this);

        //handle listview and assign adapter
        taskView = (ListView)findViewById(R.id.listView);
        taskView.setAdapter(taskAdapter);
    }

}
