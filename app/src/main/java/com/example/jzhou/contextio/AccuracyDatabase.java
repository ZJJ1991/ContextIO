package com.example.jzhou.contextio;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by jzhou on 28.11.2016.
 */


class AccuracyDbHelper extends SQLiteOpenHelper {

        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + AccuracyENTRIES.TABLE_NAME + " (" +
                        AccuracyENTRIES._ID + " INTEGER PRIMARY KEY," +
                        AccuracyENTRIES.column_name1 + TEXT_TYPE  + " )";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + AccuracyENTRIES.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Location.db";

        public AccuracyDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
            onCreate(sqLiteDatabase);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            super.onDowngrade(db, oldVersion, newVersion);
        }




        public  static  class AccuracyENTRIES implements BaseColumns{
            public static String TABLE_NAME = "accuracy";
            public static String column_name1 = "location";
        }
    }




