package com.jeff_fennell.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jeff on 6/9/15.
 */
public class TaskDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Task.db";
    //private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS" + TABLE_NAME;
    private static final String SQL_CREATE_ENTRIES ="";

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
        //db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

//    @Override
//    public void onOpen(){
//
//    }
}
