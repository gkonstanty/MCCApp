package pl.edu.agh.iet.mobilne.mccapp.persistency;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by gaba on 02.06.17.
 */

public class MccDBOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "MccApp.db";


    public MccDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: " + MccDBContract.TaskEntry.TABLE_CREATE);
        db.execSQL(MccDBContract.TaskEntry.TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // who cares
        db.execSQL(MccDBContract.TaskEntry.DELETE_ENTRIES);
        onCreate(db);
    }


}