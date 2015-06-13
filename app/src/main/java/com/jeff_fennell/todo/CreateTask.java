package com.jeff_fennell.todo;

import android.content.ContentValues;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.Date;
import java.sql.SQLException;
import java.sql.Timestamp;


public class CreateTask extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        MainActivity.openDatabase("write");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_task, menu);

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

    public void saveTask(View view) {
        EditText taskTitleInput = (EditText)findViewById(R.id.task_title);
        EditText taskDetailInput = (EditText)findViewById((R.id.task_details));
        String taskTitle = taskTitleInput.getText().toString();
        String taskDetails = taskDetailInput.getText().toString();

        //TODO - handle cases if either input is empty
        if (taskTitle.equals("")){

        }

        //Create map of values, where column names are the keys
        ContentValues values = new ContentValues();

        //determine date & time of post
        Long currentTime = new Date().getTime();

        values.put(TaskContract.TaskEntry.COLUMN_NAME_TITLE, taskTitle);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_DETAILS, taskDetails);
        values.put(TaskContract.TaskEntry.COLUMN_NAME_COMPLETE, "F");
        values.put(TaskContract.TaskEntry.COLUMN_NAME_DATE_CREATED, currentTime);

        try {
            //Insert the new row into the table, returning primary key of the row (-1 if error)
            long newRowId;
            newRowId = MainActivity.mainDb.insert(
                    TaskContract.TaskEntry.TABLE_NAME,
                    null,
                    values
            );

            if (newRowId == -1) {
                throw new SQLException("There was an error inserting the data into the table");
            }
        }catch (SQLException e){
            Log.d("Error", "inserting data into table failed");
        }

        //Return to Main Activity
        Log.d("closing Activity", "CreateTask");

        finish();
    }
}
