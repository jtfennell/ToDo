package com.jeff_fennell.todo;

import android.provider.BaseColumns;

/**
 * Created by jeff on 6/30/15.
 */
public class CategoryContract {

    //empty constructor to prevent instantiation
    public CategoryContract(){}

    public static abstract class categoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "CATEGORY";
        public static final String COLUMN_NAME_CATEGORY = "CATEGORY";
        public static final String COLUMN_NAME_DATE_CREATED = "date_created";
    }

}
