package com.example.user.attendr.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Eamon on 05/02/2018.
 *
 * Event model class for Jackson (JSON parsing) and ORMLite (DB ORM)
 */

public class Event {

    private final String TAG = Event.class.getSimpleName();

    private int id;

    @JsonProperty("eventId")
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

    @JsonProperty("eventId")
    public int getEventId() {
        return eventId;
    }

    @JsonProperty("eventId")
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

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
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

    public Date getFormattedStartTime() {
        return parseDateTimeField(this.getStartTime());
    }

    public Date getFormattedFinishTime() {
        return parseDateTimeField(this.getFinishTime());
    }

    public Date getFormattedSignInTime() {
        return parseDateTimeField(this.getSignInTime());
    }

    private String parseDateToString(Date date) {

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'H:mm:ss'Z'");
        df.setTimeZone(tz);

        return df.format(date);
    }

    private Date parseDateTimeField(String date) {

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'H:mm:ss'Z'");
        df.setTimeZone(tz);
        Date newDate = new Date();

        try {

            newDate = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }
}
