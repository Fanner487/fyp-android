package com.example.user.attendr.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.constants.DbConstants;
import com.example.user.attendr.credentials.CredentialManager;
import com.example.user.attendr.enums.EventType;
import com.example.user.attendr.enums.TimeType;
import com.example.user.attendr.models.Event;
import com.example.user.attendr.models.UserGroup;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

    public Event getEventWithEventId(int eventId){

        Cursor c = db.query(
                false,
                DbConstants.DATABASE_EVENTS_TABLE,
                null,
                DbConstants.EVENT_KEY_EVENT_ID + "=?",
                new String[]{Integer.toString(eventId)},
                null,
                null,
                null,
                null,
                null
        );

        c.moveToFirst();

        return toEvent(c);
    }

    public ArrayList<Event> getEvents(EventType eventType, TimeType timeType) {

        ArrayList<Event> eventsOfType = getEventsOfType(getAllEvents(), eventType);

        ArrayList<Event> eventsOfTime = getEventsOfTime(eventsOfType, timeType);

        for (Event event : eventsOfTime) {
            Log.d(TAG, event.toString());
        }

        return eventsOfTime;
    }

    private ArrayList<Event> getEventsOfTime(ArrayList<Event> events, TimeType timeType) {

        ArrayList<Event> filteredEvents = new ArrayList<>();

        Date now = new Date();

        for (Event event : events) {
            if (timeType == TimeType.PAST) {
                Log.d(TAG, "Adding past event");
                Date eventFinishTime = Event.parseDateTimeField(event.getFinishTime());

                if (now.after(eventFinishTime)) {
                    filteredEvents.add(event);
                }
            } else if (timeType == TimeType.ONGOING) {
                Date eventSignInTime = Event.parseDateTimeField(event.getSignInTime());
                Date eventFinishTime = Event.parseDateTimeField(event.getFinishTime());

                if (now.after(eventSignInTime) && now.before(eventFinishTime)) {
                    Log.d(TAG, "Adding ongoing event");
                    filteredEvents.add(event);
                }
            } else {
                Date eventSignInTime = Event.parseDateTimeField(event.getSignInTime());

                if (now.before(eventSignInTime)) {
                    Log.d(TAG, "Adding future event");
                    filteredEvents.add(event);
                }
            }
        }
        return filteredEvents;
    }

    private ArrayList<Event> getEventsOfType(ArrayList<Event> events, EventType eventType) {

        ArrayList<Event> filteredEvents = new ArrayList<>();

        for (Event event : events) {

            if (eventType == EventType.ORGANISE) {

                if (event.getOrganiser().equals(CredentialManager.getCredential(context, BundleAndSharedPreferencesConstants.USERNAME))) {
                    Log.d(TAG, "Adding organise event");
                    filteredEvents.add(event);
                }
            } else {

                for (String name : event.getAttendees()) {

                    if (name.equals(CredentialManager.getCredential(context, BundleAndSharedPreferencesConstants.USERNAME))) {
                        Log.d(TAG, "Adding attend event");
                        filteredEvents.add(event);
                    }
                }
            }
        }

        return filteredEvents;
    }

    public ArrayList<Event> getAllEvents() {
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

    public long deleteAllEvents() {
        return db.delete(DbConstants.DATABASE_EVENTS_TABLE, "1", null);
    }

    public void insertEvents(ArrayList<Event> events) {

        for (Event event : events) {
            insertEvent(event);
        }
    }

    public void insertGroups(ArrayList<UserGroup> groups) {

        for (UserGroup group : groups) {
            insertUserGroup(group);
        }
    }

    //insert single item from list in database
    public long insertEvent(Event event) throws SQLException {

        ContentValues values = toEventContentValues(event);

        return db.insertOrThrow(DbConstants.DATABASE_EVENTS_TABLE, null, values);
    }

    public long insertUserGroup(UserGroup group) throws SQLException {

        ContentValues values = toUserGroupContentValues(group);

        return db.insertOrThrow(DbConstants.DATABASE_GROUPS_TABLE, null, values);
    }

    // Converts cursors to ArrayList of Events
    private ArrayList<Event> toEvents(Cursor c) {
        ArrayList<Event> events = new ArrayList<>();

        while (c.moveToNext()) {

            events.add(toEvent(c));
        }
        return events;
    }

    private Event toEvent(Cursor c) {

        Event event = new Event(
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
                ));

        return event;
    }

    /*
    * UserGroup DB operations
    * */

    public ArrayList<UserGroup> getGroups() {
        Cursor c = db.query(
                false,
                DbConstants.DATABASE_GROUPS_TABLE,
                null,
                DbConstants.GROUP_KEY_ROW_USERNAME + "=?",
                new String[]{CredentialManager.getCredential(context, BundleAndSharedPreferencesConstants.USERNAME)},
                null,
                null,
                DbConstants.GROUP_KEY_ROW_GROUP_NAME + " ASC",
                null,
                null
        );

        return toUserGroups(c);
    }

    public UserGroup getGroupWithId(int id) {
        Cursor c = db.query(
                false,
                DbConstants.DATABASE_GROUPS_TABLE,
                null,
                DbConstants.GROUP_KEY_ROW_ID + "=?",
                new String[]{Integer.toString(id)},
                null,
                null,
                null,
                null,
                null
        );

        c.moveToFirst();

        return toUserGroup(c);
    }

    private ArrayList<UserGroup> toUserGroups(Cursor c) {
        ArrayList<UserGroup> groups = new ArrayList<>();

        while (c.moveToNext()) {

            groups.add(toUserGroup(c));
        }
        return groups;
    }

    private UserGroup toUserGroup(Cursor c) {

        UserGroup group = new UserGroup(
                c.getInt(c.getColumnIndexOrThrow(DbConstants.GROUP_KEY_ROW_ID)),
                c.getString(c.getColumnIndexOrThrow(DbConstants.GROUP_KEY_ROW_USERNAME)),
                c.getString(c.getColumnIndexOrThrow(DbConstants.GROUP_KEY_ROW_GROUP_NAME)),
                stringToList(c.getString(c.getColumnIndexOrThrow(DbConstants.GROUP_KEY_ROW_USERS))),
                c.getString(c.getColumnIndexOrThrow(DbConstants.GROUP_KEY_ROW_DESCRIPTION))
        );

        Log.d(TAG, "Group");
        Log.d(TAG, group.toString());

        return group;
    }

    public long updateGroup(UserGroup group){

        ContentValues contentValues = toUserGroupContentValues(group);

         return db.update(DbConstants.DATABASE_GROUPS_TABLE, contentValues, DbConstants.GROUP_KEY_ROW_ID + "=?", new String[]{Integer.toString(group.getId())});

    }

    public long deleteGroup(UserGroup group){

        ContentValues contentValues = toUserGroupContentValues(group);

        return db.delete(DbConstants.DATABASE_GROUPS_TABLE, DbConstants.GROUP_KEY_ROW_ID + "=?", new String[]{Integer.toString(group.getId())});

    }

    private ContentValues toEventContentValues(Event event) {

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

    private ContentValues toUserGroupContentValues(UserGroup group) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(DbConstants.GROUP_KEY_ROW_USERNAME, group.getUsername());
        contentValues.put(DbConstants.GROUP_KEY_ROW_GROUP_NAME, group.getGroupName());
        contentValues.put(DbConstants.GROUP_KEY_ROW_USERS, listToString(group.getUsers()));
        contentValues.put(DbConstants.GROUP_KEY_ROW_DESCRIPTION, group.getDescription());

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

    private boolean intToBoolean(int input) {

        boolean result = false;

        if (input == 0) {
            result = false;
        } else if (input == 1) {
            result = true;
        }

        return result;

    }

    private int booleanToInt(boolean input) {
        return (input) ? 1 : 0;
    }

    /*
    * Checks to see if the group name exists for the user
    * Different users logged into the phone can create the same group name
    * as long as they're not duplicate names under the same user
    * */
    public boolean groupAlreadyExistsWithUser(UserGroup group) {
        Cursor c = db.query(
                false,
                DbConstants.DATABASE_GROUPS_TABLE,
                null,
                DbConstants.GROUP_KEY_ROW_GROUP_NAME + "=? and " + DbConstants.GROUP_KEY_ROW_USERNAME + "=?",
                new String[]{group.getGroupName(), group.getUsername()},
                null,
                null,
                null,
                null,
                null
        );

        if (c.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }
    
}
