package com.example.user.attendr.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Eamon on 10/02/2018.
 */

public class DBManager {

    final String TAG = DBManager.class.getSimpleName();

    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public static final String KEY_ROW_ID = "_id";
    public static final String KEY_EVENT_ID = "event_id";
    public static final String KEY_EVENT_NAME = "event_name";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_START_TIME = "start_time";
    public static final String KEY_FINISH_TIME = "finish_time";
    public static final String KEY_SIGN_IN_TIME = "sign_in_time";
    public static final String KEY_ATTENDEES = "attendees";
    public static final String KEY_ATTENDING = "attending";
    public static final String KEY_ATTENDANCE_REQUIRED = "attendance_required";

    public static final String DATABASE_NAME = "fypDB";
    public static final String DATABASE_TABLE = "events";
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE +
                    " ( " +
                    KEY_ROW_ID + " integer primary key autoincrement, " +
                    KEY_EVENT_ID + " text not null, " +
                    KEY_EVENT_NAME + " text not null, " +
                    KEY_LOCATION + " text not null, " +
                    KEY_START_TIME+ " text not null, " +
                    KEY_FINISH_TIME + " text not null, " +
                    KEY_SIGN_IN_TIME + " text not null, " +
                    KEY_ATTENDEES + " text not null, " +
                    KEY_ATTENDING + " text not null, " +
                    KEY_ATTENDANCE_REQUIRED + " integer not null" +
                    ");";

    public DBManager(Context context)
    {
        this.context = context;
        DBHelper = new DatabaseHelper(context);
    }

    public DBManager open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //closes the database
    public void close()
    {
        DBHelper.close();
    }



    private static String listToString(ArrayList<String> list){

        String result;

        if(list == null){
            result = "";
        }
        else{
            StringBuilder stringBuilder = new StringBuilder();

            for(String name: list){
                stringBuilder.append(name);
                stringBuilder.append(",");
            }

            result = stringBuilder.toString().replaceAll(",$", "");
        }
        return result;
    }

    private static ArrayList<String> stringToList(String listString){

        ArrayList<String> result;

        if(listString == null || listString.equals("")){

            result = null;
        }
        else{
            List<String> list = Arrays.asList(listString.split("\\s*,\\s*"));
            result = new ArrayList<>(list);
        }
        return result;
    }
}
