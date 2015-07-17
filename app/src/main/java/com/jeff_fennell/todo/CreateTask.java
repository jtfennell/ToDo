package com.jeff_fennell.todo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jeff_fennell.dataEntities.Task;

import org.w3c.dom.Text;

import java.util.Date;
import java.sql.SQLException;


public class CreateTask extends ActionBarActivity {
    public static String taskIdentifier = "new task";
    public static String TOAST_CREATE_MESSAGE = "New task added: ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        populateSpinnerWithCategories();
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

        categorySelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String categorySelected = cursor.getString(position);

                Log.d("selected category", categorySelected);
                if (categorySelected.equals("1")) {
                    toggleCategoryEditText("show");
                } else {
                    toggleCategoryEditText("hide");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            public void toggleCategoryEditText(String hideOrShow) {
                LinearLayout categoryContainer = (LinearLayout) findViewById(R.id.category_container);

                if (hideOrShow.equals("show")){
                    EditText categoryEdit = (EditText)getLayoutInflater().inflate(R.layout.category_input,null);
                    categoryEdit.setHint("Enter a category");
                    categoryContainer.addView(categoryEdit);
                } else {
                    //remove add category edit text
                }


            }
        });
    }

    public void populateSpinnerWithCategories() {
        MainActivity.openDatabase("read");

        String[] projection = {
                CategoryContract.categoryEntry.COLUMN_NAME_CATEGORY,
                CategoryContract.categoryEntry._ID
        };

        //specify how results are ordered
        String sortOrder = CategoryContract.categoryEntry.COLUMN_NAME_CATEGORY + " DESC";

        Cursor c = MainActivity.mainDb.query(
                CategoryContract.categoryEntry.TABLE_NAME,
                projection,
                null, //returns all rows of table
                null,
                null, //don't group the rows
                null, //don't filter by row groups
                sortOrder
        );

        Spinner categorySelection = (Spinner)findViewById(R.id.category_selection);

        CursorAdapter taskCategoryAdapter = new CursorAdapter(this, c, CursorAdapter.NO_SELECTION) {

            @Override
            public void bindView(View categories, Context context, Cursor cursor) {
                TextView categoryName = (TextView) categories.findViewById(R.id.category_content);
                String categoryNameText = cursor.getString(cursor.getColumnIndex(CategoryContract.categoryEntry.COLUMN_NAME_CATEGORY));

                categoryName.setText(categoryNameText);
            }

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
               return LayoutInflater.from(context).inflate(R.layout.category_template, parent, false);
            }
        };
        categorySelection.setAdapter(taskCategoryAdapter);
        MainActivity.mainDb.close();
    }
}
