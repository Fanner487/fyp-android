package com.example.user.attendr;

import android.content.ContentValues;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.models.Event;
import com.example.user.attendr.models.UserGroup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Eamon on 10/03/2018.
 */

public class DbManagerUnitTest {
    private static final String TAG = "DbManagerUnitTest";

    private DBManager db;


    @Before
    public void createDb(){

//        RenamingDelegatingContext
        Context context = InstrumentationRegistry.getTargetContext();

        db = new DBManager(context).open();

    }

    @After
    public void finish(){
        db.close();
    }

    @Test
    public void testPreConditions(){
        assertNotNull(db);
    }

    @Test
    public void testInsertGroup(){

        String groupName = "test";
        ArrayList<String> users = new ArrayList<>();
        users.add("a");
        users.add("b");
        String description = "test";

        UserGroup userGroup = new UserGroup(groupName, description, users);

        userGroup.setUsername("test");

        long insert = db.insertUserGroup(userGroup);

        assertTrue(insert > 0);
    }

    @Test
    public void testGetGroupWithId(){

        String groupName = "testWithID";
        ArrayList<String> users = new ArrayList<>();
        users.add("a");
        users.add("b");
        String description = "testWithID";

        UserGroup userGroup = new UserGroup(groupName, description, users);

        userGroup.setUsername("test");

        long insert = db.insertUserGroup(userGroup);
        assertTrue(insert > 0);
        UserGroup fetchedGroup = db.getGroupWithId((int)insert);

        assertEquals(fetchedGroup.getGroupName(), groupName);
        assertEquals(fetchedGroup.getDescription(), description);
        assertEquals(fetchedGroup.getUsers(), users);

    }

    @Test
    public void testDeleteGroup(){

        String groupName = "deletegroup";
        ArrayList<String> users = new ArrayList<>();
        users.add("a");
        users.add("b");
        String description = "delete";

        UserGroup userGroup = new UserGroup(groupName, description, users);

        userGroup.setUsername("test");

        long insert = db.insertUserGroup(userGroup);
        assertTrue(insert > 0);
        UserGroup fetchedGroup = db.getGroupWithId((int)insert);

        assertEquals(fetchedGroup.getGroupName(), groupName);
        assertEquals(fetchedGroup.getDescription(), description);
        assertEquals(fetchedGroup.getUsers(), users);

        long delete = db.deleteGroup(fetchedGroup);

        assertTrue(delete > 0);

    }

    @Test
    public void testUpdateGroup(){

        String groupName = "updategroup";
        ArrayList<String> users = new ArrayList<>();
        users.add("a");
        users.add("b");
        String description = "update";

        UserGroup userGroup = new UserGroup(groupName, description, users);

        userGroup.setUsername("test");

        long insert = db.insertUserGroup(userGroup);
        assertTrue(insert > 0);
        UserGroup fetchedGroup = db.getGroupWithId((int)insert);

        assertEquals(fetchedGroup.getGroupName(), groupName);
        assertEquals(fetchedGroup.getDescription(), description);
        assertEquals(fetchedGroup.getUsers(), users);

        String updateMessageDescription = "New Update";
        fetchedGroup.setDescription(updateMessageDescription);

        long updatedRows = db.updateGroup(fetchedGroup);

        assertEquals(updatedRows, 1);
        assertEquals(db.getGroupWithId((int)insert).getDescription(), updateMessageDescription);
    }

    @Test
    public void testGroupAlreadyExistsWithUser(){

        String groupName = "group duplicate";
        ArrayList<String> users = new ArrayList<>();
        users.add("a");
        users.add("b");
        String description = "dup";

        UserGroup userGroup = new UserGroup(groupName, description, users);

        userGroup.setUsername("testDuplicate");

        UserGroup userGroup2 = new UserGroup(groupName, description, users);

        userGroup2.setUsername("testDuplicate");

        long insert1 = db.insertUserGroup(userGroup);
        long insert2 = db.insertUserGroup(userGroup2);
        assertTrue(insert1 > 0);
        assertTrue(insert2 > 0);

        boolean groupsAlreadyExists = db.groupAlreadyExistsWithUser(userGroup2);

        assertTrue(groupsAlreadyExists);

    }

    @Test
    public void testEventCreate(){
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

        long insert = db.insertEvent(event);

        assertTrue(insert > 0);
    }

    @Test
    public void testGetEventWithId(){

        int eventId = 777;
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

        Event event = new Event(eventId, organiser, eventName, location, startTime, finishTime, signInTime, attendees, attending, attendanceRequired);
        Log.d(TAG, event.toString());
        long insert = db.insertEvent(event);

        assertTrue(insert > 0);
        Event fetchedEvent = db.getEventWithEventId(eventId);

        Log.d(TAG, fetchedEvent.toString());

        assertEquals(fetchedEvent.getEventName(), eventName);
        assertEquals(fetchedEvent.getLocation(), location);
        assertEquals(fetchedEvent.getOrganiser(), organiser);
        assertEquals(fetchedEvent.getStartTime(), startTime);
        assertEquals(fetchedEvent.getFinishTime(), finishTime);
        assertEquals(fetchedEvent.getSignInTime(), signInTime);
        assertEquals(fetchedEvent.getAttendees(), attendees);
        assertEquals(fetchedEvent.getAttending(), attending);
        assertEquals(fetchedEvent.isAttendanceRequired(), attendanceRequired);
    }

    @Test
    public void testDeleteAllEvents(){

        int eventId = 777;
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

        Event event = new Event(eventId, organiser, eventName, location, startTime, finishTime, signInTime, attendees, attending, attendanceRequired);
        Log.d(TAG, event.toString());
        long insert = db.insertEvent(event);

        assertTrue(insert > 0);

        long deletedEvents = db.deleteAllEvents();

        assertTrue(deletedEvents > 0);
    }
}
