package com.jeff_fennell.todo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jeff_fennell.dataEntities.Task;

import java.util.Date;
import java.sql.SQLException;
import java.sql.Timestamp;


public class CreateTask extends ActionBarActivity {
    public static String taskIdentifier = "new task";
    public static String TOAST_CREATE_MESSAGE = "New task added: ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        setListenerOnSpinner();

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
            Intent launchSettings = new Intent(this,Settings.class);
            startActivity(launchSettings);
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveTask(View view) {
        EditText taskTitleInput = (EditText)findViewById(R.id.task_title);
        EditText taskDetailInput = (EditText)findViewById((R.id.task_details));
        String taskTitle = taskTitleInput.getText().toString();
        String taskDetails = taskDetailInput.getText().toString();

        String[] splitTitle = taskTitle.split("");
        String[] splitDetails = taskDetails.split("");
        boolean noTitleInput = splitTitle.length == 1;
        boolean noDetailsInput = splitDetails.length == 1;
        Log.d("title length", Integer.toString(splitTitle.length));
        //input validation
        if (noTitleInput) {
            clearErrors();
            addInputValidationError(getString(R.string.task_title));
        }else {
            //Create map of values, where column names are the keys
            ContentValues values = new ContentValues();

            //determine date & time of post
            Long currentTime = new Date().getTime();

            values.put(TaskContract.TaskEntry.COLUMN_NAME_TITLE, taskTitle);
            values.put(TaskContract.TaskEntry.COLUMN_NAME_DETAILS, taskDetails);
            values.put(TaskContract.TaskEntry.COLUMN_NAME_COMPLETE, Task.TASK_NOT_COMPLETE);
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
                Log.d("Error", "inserting data into table failed", e);
            }

            Context context = getApplicationContext();
            CharSequence text = TOAST_CREATE_MESSAGE + "\"" + taskTitle + "\"";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            finish();
        }

    }

    public void addInputValidationError(String viewWithError) {
        LinearLayout createTaskContainer = (LinearLayout)findViewById(R.id.create_task_container);
        LinearLayout errorContainer = new LinearLayout(this);
        errorContainer.setId(R.id.errorContainerId);

        TextView errorMessage = new TextView(this);
        errorMessage.setTextColor(0xFFFF0000);

        errorMessage.setText(getString(R.string.error_title));

        errorContainer.addView(errorMessage);
        createTaskContainer.addView(errorContainer);
    }

    public void clearErrors() {
        LinearLayout errorContainer = (LinearLayout) findViewById(R.id.errorContainerId);
        LinearLayout createTaskContainer = (LinearLayout)findViewById(R.id.create_task_container);

        createTaskContainer.removeView(errorContainer);
    }

    public void setListenerOnSpinner(){
        final Spinner categorySelection = (Spinner)findViewById(R.id.category_selection);


        categorySelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                Context context = getApplicationContext();
                CharSequence text = parent.getItemAtPosition(position).toString();
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            @Override
        public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
