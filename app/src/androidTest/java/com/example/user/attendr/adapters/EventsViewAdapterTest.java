package com.example.user.attendr.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;

import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.enums.TimeType;
import com.example.user.attendr.models.Event;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Eamon on 10/03/2018.
 */

public class EventsViewAdapterTest {


    private EventsViewAdapter eventsViewAdapter;
    private Context targetContext;
    private ArrayList<Event> events;


    @Before
    public void setUp() throws Exception{

        targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();

        ArrayList<String> attendees =  new ArrayList<>();
        attendees.add("a");
        attendees.add("b");
        ArrayList<String> attending = new ArrayList<>();
        attending.add("a");
        attending.add("b");

        events = new ArrayList<>();
        events.add(new Event(
                1, 10, "test", "test", "test", "test", "test", "test", attendees, attending, false
        ));

        events.add(new Event(
                2, 11, "test", "test1", "test", "test", "test", "test", attendees, attending, false
        ));
        events.add(new Event(
                3, 12, "test", "test2", "test", "test", "test", "test", attendees, attending, false
        ));
    }

    @Test
    public void testGetItemCount(){

        Bundle bundle = new Bundle();
        bundle.putSerializable(BundleAndSharedPreferencesConstants.TIME_TYPE, TimeType.PAST);
        eventsViewAdapter = new EventsViewAdapter(targetContext, events, bundle);

        assertEquals(eventsViewAdapter.getItemCount(), 3);
    }
}
