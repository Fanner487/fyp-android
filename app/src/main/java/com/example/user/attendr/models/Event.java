package com.example.user.attendr.models;

import android.content.Context;
import android.util.Log;

import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.constants.TimeFormats;
import com.example.user.attendr.credentials.CredentialManager;
import com.example.user.attendr.enums.EventType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Eamon on 05/02/2018.
 *
 * Event model class for Jackson (JSON parsing) and ORMLite (DB ORM)
 */

public class Event {

    private final String TAG = Event.class.getSimpleName();

    private int id;

    @JsonProperty("event_id")
    private int eventId;

    @JsonProperty("organiser")
    private String organiser;

    @JsonProperty("event_name")
    private String eventName;

    @JsonProperty("location")
    private String location;

    @JsonProperty("start_time")
    private String startTime;

    @JsonProperty("finish_time")
    private String finishTime;

    @JsonProperty("sign_in_time")
    private String signInTime;

    @JsonProperty("attendees")
    private ArrayList<String> attendees;

    @JsonProperty("attending")
    private ArrayList<String> attending;

    @JsonProperty("attendance_required")
    private boolean attendanceRequired;


    public Event() {
    }

    public Event(int id, int eventId, String organiser, String eventName, String location, String startTime, String finishTime, String signInTime, ArrayList<String> attendees, ArrayList<String> attending, boolean attendanceRequired) {
        this.id = id;
        this.eventId = eventId;
        this.organiser = organiser;
        this.eventName = eventName;
        this.location = location;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.signInTime = signInTime;
        this.attendees = attendees;
        this.attending = attending;
        this.attendanceRequired = attendanceRequired;
    }

    public Event(int eventId, String organiser, String eventName, String location, String startTime, String finishTime, String signInTime, ArrayList<String> attendees, ArrayList<String> attending, boolean attendanceRequired) {
        this.eventId = eventId;
        this.organiser = organiser;
        this.eventName = eventName;
        this.location = location;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.signInTime = signInTime;
        this.attendees = attendees;
        this.attending = attending;
        this.attendanceRequired = attendanceRequired;
    }

    public Event(int eventId, String organiser, String eventName, String location, Date startTime, Date finishTime, Date signInTime, ArrayList<String> attendees, ArrayList<String> attending, boolean attendanceRequired) {
        this.eventId = eventId;
        this.organiser = organiser;
        this.eventName = eventName;
        this.location = location;
        this.startTime = parseDateToString(startTime);
        this.finishTime = parseDateToString(finishTime);
        this.signInTime = parseDateToString(signInTime);
        this.attendees = attendees;
        this.attending = attending;
        this.attendanceRequired = attendanceRequired;
    }
    public Event(int id, int eventId, String organiser, String eventName, String location, Date startTime, Date finishTime, Date signInTime, ArrayList<String> attendees, ArrayList<String> attending, boolean attendanceRequired) {
        this.id = id;
        this.eventId = eventId;
        this.organiser = organiser;
        this.eventName = eventName;
        this.location = location;
        this.startTime = parseDateToString(startTime);
        this.finishTime = parseDateToString(finishTime);
        this.signInTime = parseDateToString(signInTime);
        this.attendees = attendees;
        this.attending = attending;
        this.attendanceRequired = attendanceRequired;
    }

    public Event(String eventName, String location, String startTime, String finishTime, String signInTime, ArrayList<String> attendees, boolean attendanceRequired) {

        this.eventName = eventName;
        this.location = location;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.signInTime = signInTime;
        this.attendees = attendees;
        this.attendanceRequired = attendanceRequired;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("id")
    public int getEventId() {
        return eventId;
    }

    @JsonProperty("id")
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    @JsonProperty("organiser")
    public String getOrganiser() {
        return organiser;
    }

    @JsonProperty("organiser")
    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }

    @JsonProperty("event_name")
    public String getEventName() {
        return eventName;
    }

    @JsonProperty("event_name")
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(String location) {
        this.location = location;
    }

    @JsonProperty("start_time")
    public String getStartTime() {
        return startTime;
    }

    @JsonProperty("start_time")
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @JsonProperty("finish_time")
    public String getFinishTime() {
        return finishTime;
    }

    @JsonProperty("finish_time")
    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    @JsonProperty("sign_in_time")
    public String getSignInTime() {
        return signInTime;
    }

    @JsonProperty("sign_in_time")
    public void setSignInTime(String signInTime) {
        this.signInTime = signInTime;
    }

    @JsonProperty("attendees")
    public ArrayList<String> getAttendees() {
        return attendees;
    }

    @JsonProperty("attendees")
    public void setAttendees(ArrayList<String> attendees) {
        this.attendees = attendees;
    }

    @JsonProperty("attending")
    public ArrayList<String> getAttending() {
        if(attending == null){
            return new ArrayList<String>();
        }
        return attending;
    }

    @JsonProperty("attending")
    public void setAttending(ArrayList<String> attending) {
        this.attending = attending;
    }

    @JsonProperty("attendance_required")
    public boolean isAttendanceRequired() {
        return attendanceRequired;
    }

    @JsonProperty("attendance_required")
    public void setAttendanceRequired(boolean attendanceRequired) {
        this.attendanceRequired = attendanceRequired;
    }

    public EventType getEventType(Context context){

        EventType eventType;
        String loggedInUser = CredentialManager.getCredential(context, BundleAndSharedPreferencesConstants.USERNAME);
        if(getOrganiser().equals(loggedInUser)){
            eventType = EventType.ORGANISE;
        }
        else{
            eventType = EventType.ATTEND;
        }

        return eventType;
    }

    public Date getFormattedStartTime() {
        return parseDateTimeField(this.getStartTime());
    }

    public Date getFormattedFinishTime() {
        return parseDateTimeField(this.getFinishTime());
    }

    public Date getFormattedSignInTime() {
        return parseDateTimeField(this.getSignInTime());
    }

    public static String parseDateToString(Date date) {

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat(TimeFormats.ISO_FORMAT, Locale.ENGLISH);
        df.setTimeZone(tz);

        return df.format(date);
    }

    public static String parseDateToDisplayTime(String date) {

        Date newDate = parseDateTimeField(date);

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat(TimeFormats.DISPLAY_FORMAT, Locale.ENGLISH);
        df.setTimeZone(tz);

        return df.format(newDate);
    }

    public static long toMilliseconds(String date){
        Date dateField = parseDateTimeField(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateField);
        return calendar.getTimeInMillis();
    }

    public static Date parseDateTimeField(String date) {

        DateFormat df = new SimpleDateFormat(TimeFormats.ISO_FORMAT, Locale.ENGLISH);

        Date newDate = new Date();

        try {

            newDate = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String parseToIsoTime(String time) {

        SimpleDateFormat sdf = new SimpleDateFormat(TimeFormats.DISPLAY_FORMAT, Locale.ENGLISH);

        Date newTime = null;

        try {
            newTime = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat isoFormat = new SimpleDateFormat(TimeFormats.ISO_FORMAT, Locale.ENGLISH);

        return isoFormat.format(newTime);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", organiser='" + organiser + '\'' +
                ", eventName='" + eventName + '\'' +
                ", location='" + location + '\'' +
                ", startTime='" + startTime + '\'' +
                ", finishTime='" + finishTime + '\'' +
                ", signInTime='" + signInTime + '\'' +
                ", attendees=" + attendees +
                ", attending=" + attending +
                ", attendanceRequired=" + attendanceRequired +
                '}';
    }
}
