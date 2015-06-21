package com.jeff_fennell.todo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.jeff_fennell.dataEntities.Task;


public class MainActivity extends ActionBarActivity {
    public static TaskDbHelper mainDbHelper;
    public static SQLiteDatabase mainDb;
    public static final int staticRequestCode = 7;

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

        //only try to read the database values if there is something in the table
        if (c.getCount() > 0) {
            c.moveToFirst();
            while (!allRowsLoaded) {
                String taskTitle = c.getString(
                        c.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_TITLE)
                );

                long taskID = c.getLong(
                        c.getColumnIndex(TaskContract.TaskEntry._ID)
                );

                String taskComplete = c.getString(
                        c.getColumnIndex(TaskContract.TaskEntry.COLUMN_NAME_COMPLETE)
                );

                //render the task
                createTaskView(taskTitle, taskComplete);

                //moveToNext returns true if there is a row in the next position
                allRowsLoaded = !c.moveToNext();
            }
        }
        else {
            //render a message saying that there are no tasks
        }
    }

    /**
     * Renders an individual task
     *
     * @param title String fetched from database indicating the task's title
     * @param taskStatus String fetched from database indicating whether or not the task is finished
     */
    public void createTaskView(String title, String taskStatus) {
        //get the viewgroup from the pre-defined layout
        LinearLayout taskList = (LinearLayout) findViewById(R.id.task_list);

          //Debugging
//        Log.d("title",title);
//        Log.d("task complete",complete);

        //create a text view to hold the task title
        TextView titleHolder = new TextView(this);
        titleHolder.setText(title);
        titleHolder.setTextSize(25);
        //set dimensions of textView
        titleHolder.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //titleHolder.height

        //create a checkbox for the task
        //TODO- checkbox width spans entire parent width right now. see if you can make the width of the textbox "wrap_content"
        CheckBox taskComplete = new CheckBox(this);

        if (taskStatus.equals(Task.TASK_COMPLETE)){
          taskComplete.setChecked(true);
        }
        taskList.addView(titleHolder);
        taskList.addView(taskComplete);
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

        if (requestCode == staticRequestCode) {
            if (resultCode == Activity.RESULT_OK){
                Task newTask = data.getParcelableExtra(CreateTask.taskIdentifier);

                //render the new task to the task list
                createTaskView(newTask.getTitle(), newTask.getComplete());
            }
        }
    }

}
