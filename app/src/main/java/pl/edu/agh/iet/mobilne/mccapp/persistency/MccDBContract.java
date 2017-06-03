package pl.edu.agh.iet.mobilne.mccapp.persistency;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by gaba on 02.06.17.
 */

public class MccDBContract {


    private MccDBContract() {}



    /* Inner class that defines the table contents */
    public static class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "mcc_tasks";
        public static final String SERVER_ID = "server_id";
        public static final String DONE = "done";
        public static final String RESULT = "result";
        public static final String WORKER_PARAMS = "worker_params";
        public static final String TASK_PARAMS = "task_params";
        public static final String PREDICTION = "prediction";

        public static final String TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                SERVER_ID + " INTEGER," +
                DONE + " INTEGER," +
                RESULT + " TEXT," +
                WORKER_PARAMS + " TEXT," +
                TASK_PARAMS + " TEXT," +
                PREDICTION + " TEXT" +
                ")";

        public static final String DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
