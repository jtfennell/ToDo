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
    public static final int DATABASE_VERSION = 16;
    public static final String DATABASE_NAME = "Task.db";

    //sql formatting
    private static final String TEXT_TYPE = " TEXT";
    private static final String LARGE_SIZE = "(100)";
    private static final String SMALL_SIZE = "(20)";
    private static final String TINY_SIZE = "(1)";
    private static final String INT_TYPE = " INT";
    private static final String CHAR_TYPE = " CHAR";
    private static final String DATE_TYPE = " TIMESTAMP";
    private static final String COMMA_SEP = ", ";

    //create table sql statements
    private static final String CREATE_TABLE_TASKS = "CREATE TABLE IF NOT EXISTS " + TaskContract.TaskEntry.TABLE_NAME +
            " (" + TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY," +
            TaskContract.TaskEntry.COLUMN_NAME_DETAILS + TEXT_TYPE + LARGE_SIZE + COMMA_SEP +
            TaskContract.TaskEntry.COLUMN_NAME_TITLE + TEXT_TYPE + SMALL_SIZE + COMMA_SEP +
            TaskContract.TaskEntry.COLUMN_NAME_DATE_CREATED + DATE_TYPE + COMMA_SEP +
            TaskContract.TaskEntry.COLUMN_NAME_COMPLETE + CHAR_TYPE + TINY_SIZE + ")";

    private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE IF NOT EXISTS " + CategoryContract.categoryEntry.TABLE_NAME +
            " (" + CategoryContract.categoryEntry._ID + " INTEGER PRIMARY KEY, " +
            CategoryContract.categoryEntry.COLUMN_NAME_CATEGORY + TEXT_TYPE + SMALL_SIZE + COMMA_SEP +
            CategoryContract.categoryEntry.COLUMN_NAME_DATE_CREATED + DATE_TYPE + ")";

    //table deletion sql statements
    private static final String DELETE_TABLE_TASKS =
            "DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME;
    private static final String DELETE_TABLE_CATEGORY =
            "DROP TABLE IF EXISTS " + CategoryContract.categoryEntry.TABLE_NAME;

    private static final String ADD_DEFAULT_CATEGORIES = "INSERT INTO " + CategoryContract.categoryEntry.TABLE_NAME
            + "('"  + CategoryContract.categoryEntry.COLUMN_NAME_CATEGORY +  "')"
            + " VALUES(" + "'Select a category')";


    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TASKS);
        db.execSQL(CREATE_TABLE_CATEGORY);
        db.execSQL(ADD_DEFAULT_CATEGORIES);
    }

    //look at options for implementing this method. current implementation copied from
    //http://developer.android.com/training/basics/data-storage/databases.html
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //categories for tasks  added at version 8
        if (oldVersion < 8) {
            db.execSQL(CREATE_TABLE_CATEGORY);
            db.execSQL(ADD_DEFAULT_CATEGORIES);
        }

    }

    @Override
    public void onOpen(SQLiteDatabase db){

    }

    public void showDatabaseCreateString() {
        Log.d("connect String:", CREATE_TABLE_TASKS);
    }

}