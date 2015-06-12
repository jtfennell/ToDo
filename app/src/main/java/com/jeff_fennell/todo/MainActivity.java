package com.jeff_fennell.todo;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {
    public static TaskDbHelper mainDbHelper;
    public static SQLiteDatabase mainDb;
    public static Boolean dbOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openDatabase();
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

    public void openDatabase() {
        //TODO-open up database in background thread because it is a time-expensive operation
        //subclass AsyncTask-- http://developer.android.com/reference/android/os/AsyncTask.html
        //use this function in CreateTask when inserting values into database
        if(!dbOpen){
            try{
                //Access database. If schema not already set up, it will automatically be created
                mainDbHelper = new TaskDbHelper(getApplicationContext());
                mainDb = mainDbHelper.getWritableDatabase();
                dbOpen = mainDb.isOpen();
            }catch (Exception e) {
                Log.d("exception caught:", "opening database", e);
            }
        }
    }

    public void loadTasks() {

        //close database
        mainDb.close();
        dbOpen = false;
    }


}
