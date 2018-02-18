package com.example.user.attendr.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.user.attendr.interfaces.ListenerInterface;
import com.example.user.attendr.R;
import com.example.user.attendr.adapters.EventsViewAdapter;
import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.enums.EventType;
import com.example.user.attendr.enums.TimeType;
import com.example.user.attendr.models.Event;

import java.util.ArrayList;

public class ViewOrganiseEventsActivity extends AppCompatActivity implements ListenerInterface{

    //todo: don't need this anymore
    private final String TAG = ViewOrganiseEventsActivity.class.getSimpleName();

    RecyclerView recyclerView;
    DBManager db;
    LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        db = new DBManager(this).open();
        ArrayList<Event> events = db.getAllEvents();

        Log.d(TAG, "\n\nGetting organised events\n\n");

        Log.d(TAG, "\nPast");
        displayEvents(db.getEvents(EventType.ORGANISE, TimeType.PAST));
        Log.d(TAG, "\nOngoing");
        displayEvents(db.getEvents(EventType.ORGANISE, TimeType.ONGOING));
        Log.d(TAG, "\nFuture");
        displayEvents(db.getEvents(EventType.ORGANISE, TimeType.FUTURE));


        Log.d(TAG, "\n\nGetting Attending events\n\n");
        Log.d(TAG, "\nPast");
        displayEvents(db.getEvents(EventType.ATTEND, TimeType.PAST));
        Log.d(TAG, "\nOngoing");
        displayEvents(db.getEvents(EventType.ATTEND, TimeType.ONGOING));
        Log.d(TAG, "\nFuture");
        displayEvents(db.getEvents(EventType.ATTEND, TimeType.FUTURE));

        linearLayoutManager = new LinearLayoutManager(ViewOrganiseEventsActivity.this);

        // Setting the recycler view
        // TODO: Check if layout manager is needed
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        EventsViewAdapter eventsViewAdapter = new EventsViewAdapter(events, ViewOrganiseEventsActivity.this);
        recyclerView.setAdapter(eventsViewAdapter);

    }

    private void displayEvents(ArrayList<Event> events){
        Log.d(TAG, "-------------");
        for(Event event: events){
            Log.d(TAG, event.toString());
        }

        Log.d(TAG, "-------------");
    }

    @Override
    public void setListeners() {

    }
}
