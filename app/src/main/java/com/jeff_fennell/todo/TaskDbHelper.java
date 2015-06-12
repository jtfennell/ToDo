package com.jeff_fennell.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jeff on 6/9/15.
 */
public class TaskDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Task.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String LARGE_SIZE = "(100)";
    private static final String SMALL_SIZE = "(20)";
    private static final String TINY_SIZE = "(1)";
    private static final String INT_TYPE = " INT";
    private static final String CHAR_TYPE = " CHAR";
    private static final String COMMA_SEP = ", ";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS" + TaskContract.TaskEntry.TABLE_NAME;
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " + TaskContract.TaskEntry.TABLE_NAME +
            " (" + TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY," +
            TaskContract.TaskEntry.COLUMN_NAME_TASK_ID + INT_TYPE + SMALL_SIZE + COMMA_SEP +
            TaskContract.TaskEntry.COLUMN_NAME_DETAILS + TEXT_TYPE + LARGE_SIZE + COMMA_SEP +
            TaskContract.TaskEntry.COLUMN_NAME_COMPLETE + CHAR_TYPE + TINY_SIZE + ")"
            ;

    public TaskDbHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    //look at options for implementing this method. current implementation copied from
    //http://developer.android.com/training/basics/data-storage/databases.html
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db){

    }

    public void showDatabaseCreateString() {
        Log.d("connect String:", SQL_CREATE_ENTRIES);
    }

}
