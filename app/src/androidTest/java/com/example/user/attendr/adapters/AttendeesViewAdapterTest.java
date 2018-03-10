package com.example.user.attendr.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.v4.content.ContextCompat;

import com.example.user.attendr.enums.EventType;
import com.example.user.attendr.models.Event;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Eamon on 10/03/2018.
 */

public class AttendeesViewAdapterTest {

    private AttendeesViewAdapter attendeesViewAdapter;
    private Context targetContext;


    @Before
    public void setUp() throws Exception{

        targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();

    }

    @Test
    public void testGetItemCountOrganise(){

        int id = 1;
        int eventId = 1;
        String organiser = "test";
        String eventName = "test";
        String location = "test";
        String startTime = "test";
        String finishTime = "test";
        String signInTime = "test";
        ArrayList<String> attendees =  new ArrayList<>();
        attendees.add("a");
        attendees.add("b");
        ArrayList<String> attending = new ArrayList<>();
        attending.add("a");
        attending.add("b");
        boolean attendanceRequired = true;

        Event event = new Event(id, eventId, organiser, eventName, location, startTime, finishTime, signInTime, attendees, attending, attendanceRequired);

        attendeesViewAdapter = new AttendeesViewAdapter(targetContext, event, EventType.ORGANISE);
        assertEquals(attendeesViewAdapter.getItemCount(), 2);
    }

    @Test
    public void testGetItemCountAttending(){

        int id = 1;
        int eventId = 1;
        String organiser = "test";
        String eventName = "test";
        String location = "test";
        String startTime = "test";
        String finishTime = "test";
        String signInTime = "test";
        ArrayList<String> attendees =  new ArrayList<>();
        attendees.add("a");
        attendees.add("b");
        ArrayList<String> attending = new ArrayList<>();
        boolean attendanceRequired = true;

        Event event = new Event(id, eventId, organiser, eventName, location, startTime, finishTime, signInTime, attendees, attending, attendanceRequired);

        attendeesViewAdapter = new AttendeesViewAdapter(targetContext, event, EventType.ATTEND);
        assertEquals(attendeesViewAdapter.getItemCount(), 2);
    }
}
