package com.example.user.attendr.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.user.attendr.activities.MainActivity;
import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.credentials.CredentialManager;
import com.example.user.attendr.enums.EventType;

import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
/**
 * Created by Eamon on 10/03/2018.
 */

public class EventUnitTest {

    @Test
    public void testCreate(){

        int id = 1;
        int eventId = 1;
        String organiser = "test";
        String eventName = "test";
        String location = "test";
        String startTime = "test";
        String finishTime = "test";
        String signInTime = "test";
        ArrayList<String> attendees =  new ArrayList<>();
        ArrayList<String> attending = new ArrayList<>();
        boolean attendanceRequired = true;

        Event event = new Event(id, eventId, organiser, eventName, location, startTime, finishTime, signInTime, attendees, attending, attendanceRequired);

        assertEquals(event.getId() , id);
        assertEquals(event.getEventId(), eventId);
        assertEquals(event.getEventName(), eventName);
        assertEquals(event.getLocation(), location);
        assertEquals(event.getStartTime(), startTime);
        assertEquals(event.getFinishTime(), finishTime);
        assertEquals(event.getSignInTime(), signInTime);
        assertEquals(event.getAttendees(), attendees);
        assertEquals(event.getAttending(), attending);
        assertEquals(event.isAttendanceRequired(), attendanceRequired);
    }

    @Test
    public void testParseDateToString(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, 3);
        calendar.set(Calendar.YEAR, 2018);
        calendar.set(Calendar.DATE, 23);
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 55);
        calendar.set(Calendar.SECOND, 0);

        Date date = calendar.getTime();

        assertEquals(Event.parseDateToString(date), "2018-04-23T13:55:00Z");
    }

    @Test
    public void testParseDateToDisplayTime(){

        String date = "2018-04-23T13:55:00Z";

        assertEquals(Event.parseDateToDisplayTime(date), "13:55 23/04/2018");
    }

    @Test
    public void testParseToIsoTime(){
        String date = "13:55 23/04/2018";

        assertEquals(Event.parseToIsoTime(date), "2018-04-23T13:55:00Z");
    }

//    @Test
//    public void testGetEventType(){
//
//        int id = 1;
//        int eventId = 1;
//        String organiser = "test";
//        String eventName = "test";
//        String location = "test";
//        String startTime = "test";
//        String finishTime = "test";
//        String signInTime = "test";
//        ArrayList<String> attendees =  new ArrayList<>();
//        ArrayList<String> attending = new ArrayList<>();
//        boolean attendanceRequired = true;
//
//        Event eventClass = spy(Event.class);
//
//        Event event = new Event(id, eventId, organiser, eventName, location, startTime, finishTime, signInTime, attendees, attending, attendanceRequired);
//
//        Context context = mock(MainActivity.class);
//        CredentialManager credentialManager = mock(CredentialManager.class);
////        when(event.getEventType(context)).then(EventType.ORGANISE);
//
////        doReturn(EventType.ORGANISE).when(CredentialManager.getCredential(context, BundleAndSharedPreferencesConstants.USERNAME));
//        doReturn(EventType.ORGANISE).when(eventClass).getEventType(context);
////        doReturn(EventType.ORGANISE).when(Event.class).(context);
//
//        assertEquals(event.getEventType(context), EventType.ORGANISE);
//    }
}
