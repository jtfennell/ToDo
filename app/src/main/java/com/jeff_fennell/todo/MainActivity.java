package com.jeff_fennell.todo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;


public class MainActivity extends ActionBarActivity {
    public static TaskDbHelper mainDbHelper;
    public static SQLiteDatabase mainDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainDbHelper = new TaskDbHelper(getApplicationContext());
        openDatabase("read");
        loadTasks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createNewTask(View view) {
        Intent intent = new Intent(this,CreateTask.class);
        startActivity(intent);
    }

    public static void openDatabase(String readOrWrite) {
        //TODO-open up database in background thread because it is a time-expensive operation
        //subclass AsyncTask-- http://developer.android.com/reference/android/os/AsyncTask.html
        //use this function in CreateTask when inserting values into database

            try{
                //Access database. If schema not already set up, it will automatically be created
                if (readOrWrite.equals("read")){
                    mainDb = mainDbHelper.getReadableDatabase();
                } else if (readOrWrite.equals("write")) {
                    mainDb = mainDbHelper.getWritableDatabase();
                }
            }catch (Exception e) {
                Log.d("exception caught:", "opening database", e);
            }
    }

    public void loadTasks() {
        //Specify which columns from the database will actually be used
        String[] projection = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_NAME_TITLE,
                TaskContract.TaskEntry.COLUMN_NAME_COMPLETE
        };

        //specify how results are ordered
        String sortOrder = TaskContract.TaskEntry._ID + " DESC";

        Cursor c = mainDb.query(
                TaskContract.TaskEntry.TABLE_NAME,
                projection,
                null, //returns all rows of table
                null,
                null, //don't group the rows
                null, //don't filter by row groups
                sortOrder
        );
        String[] columns = c.getColumnNames();
        for (String column : columns) {
            Log.d("column name", column);
        }

        boolean allRowsLoaded = false;
        //move cursor to first row in the table
        c.moveToFirst();
        while(!allRowsLoaded){
            String taskTitle = c.getString(
                    c.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_TITLE)
            );

            long taskID = c.getLong(
                    c.getColumnIndex(TaskContract.TaskEntry._ID)
            );

            String taskComplete = c.getString(
                    c.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_COMPLETE)
            );

            Log.d("title",taskTitle);
            Log.d("task complete", taskComplete);
            //Log.d("Id", Long.toString(taskID));
            //moveToNext returns true if there is a row in the next position
            allRowsLoaded = !c.moveToNext();
        }

        //close database
        mainDb.close();
    }
    /**
    public void createTaskView(String title, String complete, ) {
        LinearLayout task = new LinearLayout(this);
    }
    */
}
