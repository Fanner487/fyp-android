package com.example.user.attendr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.user.attendr.constants.DbConstants;
import com.example.user.attendr.models.Event;
import com.example.user.attendr.models.UserGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Eamon on 10/02/2018.
 *
 * DB layer of app and all DB CRUD operations are stored here
 */

public class DBManager {

    private final String TAG = DBManager.class.getSimpleName();

    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

//    public static final String KEY_ROW_ID = "_id";
//    public static final String KEY_EVENT_ID = "event_id";
//    public static final String KEY_ORGANISER = "organiser";
//    public static final String KEY_EVENT_NAME = "event_name";
//    public static final String KEY_LOCATION = "location";
//    public static final String KEY_START_TIME = "start_time";
//    public static final String KEY_FINISH_TIME = "finish_time";
//    public static final String KEY_SIGN_IN_TIME = "sign_in_time";
//    public static final String KEY_ATTENDEES = "attendees";
//    public static final String KEY_ATTENDING = "attending";
//    public static final String KEY_ATTENDANCE_REQUIRED = "attendance_required";
//
//    public static final String DATABASE_NAME = "fypDB";
//    public static final String DATABASE_TABLE = "events";
//    public static final int DATABASE_VERSION = 1;
//
//    public static final String DATABASE_CREATE =
//            "create table " + DATABASE_TABLE +
//                    " ( " +
//                    KEY_ROW_ID + " integer primary key autoincrement, " +
//                    KEY_EVENT_ID + " text not null, " +
//                    KEY_ORGANISER + " text not null, " +
//                    KEY_EVENT_NAME + " text not null, " +
//                    KEY_LOCATION + " text not null, " +
//                    KEY_START_TIME + " text not null, " +
//                    KEY_FINISH_TIME + " text not null, " +
//                    KEY_SIGN_IN_TIME + " text not null, " +
//                    KEY_ATTENDEES + " text not null, " +
//                    KEY_ATTENDING + " text not null, " +
//                    KEY_ATTENDANCE_REQUIRED + " integer not null" +
//                    ");";

    public DBManager(Context context) {

        Log.d(TAG, "In Constructor");
        this.context = context;
        DBHelper = new DatabaseHelper(context);
    }

    public DBManager open() throws SQLException {

        Log.d(TAG, "Opening DB");
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //closes the database
    public void close() {

        Log.d(TAG, "Closing DB");
        DBHelper.close();
    }

    public ArrayList<Event> getEvents(){
        Cursor c = db.query(
                false,
                DbConstants.DATABASE_EVENTS_TABLE,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
                );

        return toEvents(c);
    }

    public ArrayList<UserGroup> getGroups(){
        Cursor c = db.query(
                false,
                DbConstants.DATABASE_GROUPS_TABLE,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        return toUserGroups(c);
    }

    public long deleteAllEvents(){
        return db.delete(DbConstants.DATABASE_EVENTS_TABLE, "1", null);
    }

    public void insertEvents(ArrayList<Event> events){

        for(Event event: events){
            insertEvent(event);
        }
    }

    public void insertGroups(ArrayList<UserGroup> groups){

        for(UserGroup group: groups){
            insertUserGroup(group);
        }
    }

    //insert single item from list in database
    public long insertEvent(Event event) throws SQLException
    {

        ContentValues values = toEventContentValues(event);

        return db.insertOrThrow(DbConstants.DATABASE_EVENTS_TABLE, null, values);
    }

    public long insertUserGroup(UserGroup group) throws SQLException
    {

        ContentValues values = toUserGroupContentValues(group);

        return db.insertOrThrow(DbConstants.DATABASE_GROUPS_TABLE, null, values);
    }

    private ArrayList<Event> toEvents(Cursor c){
        ArrayList<Event> events = new ArrayList<>();

        while(c.moveToNext()) {

            events.add(new Event(
                    c.getInt(c.getColumnIndexOrThrow(DbConstants.EVENT_KEY_ROW_ID)),
                    c.getInt(c.getColumnIndexOrThrow(DbConstants.EVENT_KEY_EVENT_ID)),
                    c.getString(c.getColumnIndexOrThrow(DbConstants.EVENT_KEY_ORGANISER)),
                    c.getString(c.getColumnIndexOrThrow(DbConstants.EVENT_KEY_EVENT_NAME)),
                    c.getString(c.getColumnIndexOrThrow(DbConstants.EVENT_KEY_LOCATION)),
                    c.getString(c.getColumnIndexOrThrow(DbConstants.EVENT_KEY_START_TIME)),
                    c.getString(c.getColumnIndexOrThrow(DbConstants.EVENT_KEY_FINISH_TIME)),
                    c.getString(c.getColumnIndexOrThrow(DbConstants.EVENT_KEY_SIGN_IN_TIME)),
                    stringToList(c.getString(c.getColumnIndexOrThrow(DbConstants.EVENT_KEY_ATTENDEES))),
                    stringToList(c.getString(c.getColumnIndexOrThrow(DbConstants.EVENT_KEY_ATTENDING))),
                    intToBoolean(c.getInt(c.getColumnIndexOrThrow(DbConstants.EVENT_KEY_ATTENDANCE_REQUIRED))
            )));
        }
        return events;
    }

    private ArrayList<UserGroup> toUserGroups(Cursor c){
        ArrayList<UserGroup> events = new ArrayList<>();

        while(c.moveToNext()) {

            events.add(new UserGroup(
                    c.getInt(c.getColumnIndexOrThrow(DbConstants.GROUP_KEY_ROW_ID)),
                    c.getString(c.getColumnIndexOrThrow(DbConstants.GROUP_KEY_ROW_USERNAME)),
                    c.getString(c.getColumnIndexOrThrow(DbConstants.GROUP_KEY_ROW_GROUP_NAME)),
                    stringToList(c.getString(c.getColumnIndexOrThrow(DbConstants.GROUP_KEY_ROW_USERS)))
                    ));
        }
        return events;
    }


    private ContentValues toEventContentValues(Event event){

        ContentValues contentValues = new ContentValues();

        contentValues.put(DbConstants.EVENT_KEY_EVENT_ID, event.getEventId());
        contentValues.put(DbConstants.EVENT_KEY_ORGANISER, event.getOrganiser());
        contentValues.put(DbConstants.EVENT_KEY_EVENT_NAME, event.getEventName());
        contentValues.put(DbConstants.EVENT_KEY_LOCATION, event.getLocation());
        contentValues.put(DbConstants.EVENT_KEY_START_TIME, event.getStartTime());
        contentValues.put(DbConstants.EVENT_KEY_FINISH_TIME, event.getFinishTime());
        contentValues.put(DbConstants.EVENT_KEY_SIGN_IN_TIME, event.getSignInTime());
        contentValues.put(DbConstants.EVENT_KEY_ATTENDEES, listToString(event.getAttendees()));
        contentValues.put(DbConstants.EVENT_KEY_ATTENDING, listToString(event.getAttending()));
        contentValues.put(DbConstants.EVENT_KEY_ATTENDANCE_REQUIRED, booleanToInt(event.isAttendanceRequired()));

        return contentValues;
    }

    private ContentValues toUserGroupContentValues(UserGroup group){

        ContentValues contentValues = new ContentValues();

        contentValues.put(DbConstants.GROUP_KEY_ROW_USERNAME, group.getUsername());
        contentValues.put(DbConstants.GROUP_KEY_ROW_GROUP_NAME, group.getGroupName());
        contentValues.put(DbConstants.GROUP_KEY_ROW_USERS, listToString(group.getUsers()));

        return contentValues;
    }

    /*
    * Conversions for lists to strings and booleans to ints and vice versa
    * */

    private String listToString(ArrayList<String> list) {

        String result;

        if (list == null) {
            result = "";
        } else {
            StringBuilder stringBuilder = new StringBuilder();

            for (String name : list) {
                stringBuilder.append(name);
                stringBuilder.append(",");
            }

            result = stringBuilder.toString().replaceAll(",$", "");
        }
        return result;
    }

    private ArrayList<String> stringToList(String listString) {

        ArrayList<String> result;

        if (listString == null || listString.equals("")) {

            result = null;
        } else {
            List<String> list = Arrays.asList(listString.split("\\s*,\\s*"));
            result = new ArrayList<>(list);
        }
        return result;
    }

    private boolean intToBoolean(int input){

        boolean result = false;

        if(input == 0){
            result = false;
        }
        else if(input == 1){
            result = true;
        }

        return result;

    }

    private int booleanToInt(boolean input){
        return (input) ? 1 : 0;
    }
}
