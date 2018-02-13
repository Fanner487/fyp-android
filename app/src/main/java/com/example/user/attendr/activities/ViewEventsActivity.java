package com.example.user.attendr.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.user.attendr.R;
import com.example.user.attendr.adapters.RecyclerViewAdapter;
import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.models.Event;

import java.util.ArrayList;

public class ViewEventsActivity extends AppCompatActivity {

    private final String TAG = ViewEventsActivity.class.getSimpleName();

    RecyclerView recyclerView;
    DBManager db;
    LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        db = new DBManager(this).open();
        ArrayList<Event> events = db.getEvents();

        linearLayoutManager = new LinearLayoutManager(ViewEventsActivity.this);

        // Setting the recycler view
        // TODO: Check if layout manager is needed
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(events, ViewEventsActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);

    }
}
