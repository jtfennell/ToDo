package com.jeff_fennell.todo;

import android.provider.BaseColumns;

/**
 * Created by jeff on 6/9/15.
 */
public final class TaskContract {

    //empty constructor to prevent instantiation
    public TaskContract(){}

    public static abstract class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DETAILS = "details";
        public static final String COLUMN_NAME_COMPLETE = "complete";
        public static final String COLUMN_NAME_DATE_CREATED = "date_created";
    }
}
