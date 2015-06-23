package com.jeff_fennell.todo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.jeff_fennell.dataEntities.Task;


public class MainActivity extends ActionBarActivity {
    public static TaskDbHelper mainDbHelper;
    public static SQLiteDatabase mainDb;
    public static final int staticRequestCode = 7;
    public static final String bulletPoint = "\u2022 ";
    public static final String databaseEmptyMessage = "There are no tasks to display";
    public static final int sizeOfTaskTitle = 20;

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
        if (id == R.id.new_task){
            createNewTask();
        }

        return super.onOptionsItemSelected(item);
    }

    public void createNewTask() {
        Intent createNewTask = new Intent(this,CreateTask.class);
        startActivityForResult(createNewTask, staticRequestCode);
    }

    public static void openDatabase(String readOrWrite) {
        //TODO-open up database in background thread because it is a time-expensive operation (will slow down UI thread)
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

    /**
     * Retrieves the table of tasks from the database and renders them
     */
    public void loadTasks() {
        //Specify which columns from the database will actually be used
        String[] projection = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_NAME_TITLE,
                TaskContract.TaskEntry.COLUMN_NAME_COMPLETE,
                TaskContract.TaskEntry.COLUMN_NAME_DETAILS
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

        boolean allRowsLoaded = false;
        //move cursor to first row in the table

        Task task = new Task();

        //only try to read the database values if there is something in the table
        if (c.getCount() > 0) {
            c.moveToFirst();
            while (!allRowsLoaded) {
                task.setTitle( c.getString(
                        c.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_TITLE)
                ));

                task.setId(c.getLong(
                        c.getColumnIndex(TaskContract.TaskEntry._ID)
                ));

                task.setComplete(c.getString(
                        c.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_COMPLETE)
                ));

                task.setDetails(c.getString(
                        c.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_DETAILS)
                ));

                //render the task
                createTaskView(task);

                //moveToNext returns true if there is a row in the next position
                allRowsLoaded = !c.moveToNext();
            }
        }
        else {
            renderEmptyDatabaseMessage();
        }
    }

    /**
     * Renders an individual task
     *
     * @param task - contains details about task to be created
     */
    public void createTaskView(Task task) {
        final String title = task.getTitle();
        //get the viewgroup from the pre-defined layout
        LinearLayout taskList = (LinearLayout) findViewById(R.id.task_list);
        
        //create a container
        LinearLayout TaskContainer = new LinearLayout(this);

        //create a text view to hold the task title
        TextView titleHolder = new TextView(this);
        titleHolder.setText(bulletPoint + task.getTitle());
        titleHolder.setTextSize(sizeOfTaskTitle);

        //store task id for updating database later
        titleHolder.setTag(task.getId());

        //set dimensions of textView
        titleHolder.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        //create a checkbox for the task
        CheckBox taskComplete = new CheckBox(this);

        //create onClick handler
        taskComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View checkbox) {
                LinearLayout taskContainer = (LinearLayout) checkbox.getParent();
                TextView taskTitle = null;
                Boolean foundTitle = false;
                int layoutChildren = taskContainer.getChildCount();

                for (int i = 0; i < layoutChildren; i++) {
                    if (taskContainer.getChildAt(i) instanceof TextView && !foundTitle) {
                        taskTitle = (TextView) taskContainer.getChildAt(i);
                        //makes sure the Button does not override the title b/c it is a subclass of TextView
                        foundTitle = true;
                    }
                }
                updateTaskStatus(taskTitle, ((CheckBox) checkbox).isChecked());
                toggleCrossOut(taskTitle, ((CheckBox) checkbox).isChecked());

            }
        });

        if (task.getComplete().equals(Task.TASK_COMPLETE)){
          taskComplete.setChecked(true);
            toggleCrossOut(titleHolder, ((CheckBox)taskComplete).isChecked());
        }

        final Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Confirm deletion");
                alertDialog.setMessage("Are you sure you want to delete task: \"" + title + "\"?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                LinearLayout taskContainer = (LinearLayout) deleteButton.getParent();
                            }
                        });
                alertDialog.show();
            }
        });

        TaskContainer.addView(titleHolder);
        TaskContainer.addView(taskComplete);
        TaskContainer.addView(deleteButton);

        taskList.addView(TaskContainer);
    }

    /**
     * receives the newly created task from the create task activity for rendering
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        LinearLayout taskList = (LinearLayout) findViewById(R.id.task_list);
        taskList.removeAllViews();
        loadTasks();
        /**
        if (requestCode == staticRequestCode) {
            if (resultCode == Activity.RESULT_OK){
                Task newTask = data.getParcelableExtra(CreateTask.taskIdentifier);

                //render the new task to the task list
                createTaskView(newTask.getTitle(), newTask.getComplete());
            }
        }
         */
    }

    public void renderEmptyDatabaseMessage() {
        LinearLayout taskList = (LinearLayout) findViewById(R.id.task_list);
        TextView emptyMessageHolder = new TextView(this);

        emptyMessageHolder.setText(databaseEmptyMessage);

        taskList.addView(emptyMessageHolder);
    }

    public void updateTaskStatus(TextView taskTitle, boolean checked) {
        long taskId = (long) taskTitle.getTag();
        Log.d("Id: ", Long.toString(taskId));
        String taskStatus;

        if (checked){
            taskStatus = Task.TASK_COMPLETE;
        } else {
            taskStatus = Task.TASK_NOT_COMPLETE;
        }

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_NAME_COMPLETE, taskStatus);
        String selection = TaskContract.TaskEntry._ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(taskId)};

        int count = MainActivity.mainDb.update(
                TaskContract.TaskEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }

    public void toggleCrossOut(TextView taskTitle, boolean completed) {
        if (completed) {
            taskTitle.setPaintFlags(taskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            taskTitle.setPaintFlags(taskTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

}
