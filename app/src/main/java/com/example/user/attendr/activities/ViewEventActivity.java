package com.example.user.attendr.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.EventLogTags;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.user.attendr.R;
import com.example.user.attendr.adapters.AttendeesViewAdapter;
import com.example.user.attendr.constants.DbConstants;
import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.models.Event;

public class ViewEventActivity extends AppCompatActivity {

    private static final String TAG = ViewEventActivity.class.getSimpleName();

    TextView tvEventName;
    TextView tvLocation;
    TextView tvOrganiser;
    TextView tvStartTime;
    TextView tvSignInTime;
    TextView tvFinishTime;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    DBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        tvEventName = findViewById(R.id.tvEventName);
        tvLocation = findViewById(R.id.tvLocation);
        tvOrganiser = findViewById(R.id.tvOrganiser);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvSignInTime = findViewById(R.id.tvSignInTime);
        tvFinishTime = findViewById(R.id.tvFinishTime);

        Bundle bundle = getIntent().getExtras();
        int eventId = bundle.getInt(DbConstants.EVENT_KEY_EVENT_ID);

        db = new DBManager(this).open();

        Log.d(TAG, "Event ID: " + Integer.toString(eventId));

        Event event = db.getEventWithEventId(eventId);

        Log.d(TAG, event.toString());


        tvEventName.setText(event.getEventName());
        tvLocation.setText(event.getLocation());
        tvOrganiser.setText(event.getOrganiser());
        tvStartTime.setText(event.getStartTime());
        tvSignInTime.setText(event.getSignInTime());
        tvFinishTime.setText(event.getFinishTime());


        recyclerView = findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        AttendeesViewAdapter attendeesViewAdapter = new AttendeesViewAdapter(getApplicationContext(), event.getAttendees());

        recyclerView.setAdapter(attendeesViewAdapter);


    }
}
