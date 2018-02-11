package com.example.user.attendr.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.user.attendr.constants.DbConstants;

/**
 * Created by Eamon on 10/02/2018.
 *
 * Database Helper class for creating and updating SQLite DB
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private final String TAG = DatabaseHelper.class.getSimpleName();

    DatabaseHelper(Context context){

        super(context, DbConstants.DATABASE_NAME, null, DbConstants.DATABASE_VERSION);
        Log.d(TAG, "In Database Helper constructor");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "In onCreate");
        sqLiteDatabase.execSQL(DbConstants.DATABASE_EVENTS_CREATE);
        sqLiteDatabase.execSQL(DbConstants.DATABASE_GROUPS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(TAG, "In onUpgrade");
        sqLiteDatabase.execSQL(DbConstants.DATABASE_EVENTS_CREATE);
        sqLiteDatabase.execSQL(DbConstants.DATABASE_GROUPS_CREATE);
    }
}
