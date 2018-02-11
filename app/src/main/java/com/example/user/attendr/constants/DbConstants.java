package com.example.user.attendr.constants;

/**
 * Created by Eamon on 11/02/2018.
 */

public class DbConstants {

    public static final String EVENT_KEY_ROW_ID = "_id";
    public static final String EVENT_KEY_EVENT_ID = "event_id";
    public static final String EVENT_KEY_ORGANISER = "organiser";
    public static final String EVENT_KEY_EVENT_NAME = "event_name";
    public static final String EVENT_KEY_LOCATION = "location";
    public static final String EVENT_KEY_START_TIME = "start_time";
    public static final String EVENT_KEY_FINISH_TIME = "finish_time";
    public static final String EVENT_KEY_SIGN_IN_TIME = "sign_in_time";
    public static final String EVENT_KEY_ATTENDEES = "attendees";
    public static final String EVENT_KEY_ATTENDING = "attending";
    public static final String EVENT_KEY_ATTENDANCE_REQUIRED = "attendance_required";

    public static final String GROUP_KEY_ROW_ID = "_id";
    public static final String GROUP_KEY_ROW_USERNAME = "username";
    public static final String GROUP_KEY_ROW_GROUP_NAME = "group_name";
    public static final String GROUP_KEY_ROW_USERS = "users";

    public static final String DATABASE_NAME = "fypDB";
    public static final String DATABASE_EVENTS_TABLE = "events";
    public static final String DATABASE_GROUPS_TABLE = "groups";
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_EVENTS_CREATE =
            "create table " + DATABASE_EVENTS_TABLE +
                    " ( " +
                    EVENT_KEY_ROW_ID + " integer primary key autoincrement, " +
                    EVENT_KEY_EVENT_ID + " text not null, " +
                    EVENT_KEY_ORGANISER + " text not null, " +
                    EVENT_KEY_EVENT_NAME + " text not null, " +
                    EVENT_KEY_LOCATION + " text not null, " +
                    EVENT_KEY_START_TIME + " text not null, " +
                    EVENT_KEY_FINISH_TIME + " text not null, " +
                    EVENT_KEY_SIGN_IN_TIME + " text not null, " +
                    EVENT_KEY_ATTENDEES + " text not null, " +
                    EVENT_KEY_ATTENDING + " text not null, " +
                    EVENT_KEY_ATTENDANCE_REQUIRED + " integer not null" +
                    ");";

    public static final String DATABASE_GROUPS_CREATE =
            "create table " + DATABASE_GROUPS_TABLE +
                    " ( " +
                    GROUP_KEY_ROW_ID + " integer primary key autoincrement, " +
                    GROUP_KEY_ROW_USERNAME + " text not null, " +
                    GROUP_KEY_ROW_GROUP_NAME + " text not null unique, " +
                    GROUP_KEY_ROW_USERS + " text not null unique" +
                    ");";
}
