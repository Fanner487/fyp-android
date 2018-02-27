package com.example.user.attendr.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.user.attendr.R;
import com.example.user.attendr.adapters.EventsViewAdapter;
import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.constants.DbConstants;
import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.enums.AttendanceType;
import com.example.user.attendr.enums.EventType;
import com.example.user.attendr.enums.TimeType;
import com.example.user.attendr.models.Event;

import java.util.ArrayList;

/*
* Created by Eamon on 26/02/2018
*
* Allows organiser of events to view a users attendance to their events such as
* % attended, and lists of which ones they signed in for or not.
* */

public class UserStatsActivity extends AppCompatActivity {

    DBManager db;
    Bundle bundle;
    String username;

    TextView tvUsername;
    TextView tvPercentage;
    TextView tvEventsAttended;
    TextView tvAttended;
    TextView tvNotAttended;
    RecyclerView rvAttended;
    RecyclerView rvNotAttended;

    LinearLayoutManager linearLayoutManagerAttended;
    LinearLayoutManager linearLayoutManagerNotAttended;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stats);

        db = new DBManager(getApplicationContext()).open();
        bundle = getIntent().getExtras();

        username = bundle.getString(DbConstants.GROUP_KEY_ROW_USERNAME);

        tvUsername = findViewById(R.id.tvUsername);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvEventsAttended = findViewById(R.id.tvEventsAttended);
        tvAttended = findViewById(R.id.tvAttended);
        tvNotAttended = findViewById(R.id.tvNotAttended);
        rvAttended = findViewById(R.id.rvAttended);
        rvNotAttended = findViewById(R.id.rvNotAttended);

        setAdaptersWithData();
        setTextViews();

    }

    /**
     * Sets and formats the text views of the activity
     */
    private void setTextViews() {
        tvUsername.setText(username);
        tvPercentage.setText(Integer.toString(getPercentageAttendanceForUser(username)) + "%");

        int totalEventsWithUser = getEventsWithUserInAttendees(db.getEvents(EventType.ORGANISE, TimeType.PAST), username).size();
        int eventsAttended = getEventsOrganisedWithUserInAttendees(username, AttendanceType.ATTENDING).size();

        tvEventsAttended.setText(Integer.toString(eventsAttended) + "/" + Integer.toString(totalEventsWithUser));

    }

    private void setAdaptersWithData() {

        ArrayList<Event> eventsAttended = getEventsOrganisedWithUserInAttendees(username, AttendanceType.ATTENDING);
        ArrayList<Event> eventsNotAttended = getEventsOrganisedWithUserInAttendees(username, AttendanceType.NOT_ATTENDING);

        linearLayoutManagerAttended = new LinearLayoutManager(UserStatsActivity.this);
        linearLayoutManagerNotAttended = new LinearLayoutManager(UserStatsActivity.this);

        rvAttended.setLayoutManager(linearLayoutManagerAttended);
        rvNotAttended.setLayoutManager(linearLayoutManagerNotAttended);

        Bundle extras = new Bundle();
        extras.putSerializable(BundleAndSharedPreferencesConstants.EVENT_TYPE, bundle.getSerializable(BundleAndSharedPreferencesConstants.EVENT_TYPE));
        extras.putSerializable(BundleAndSharedPreferencesConstants.TIME_TYPE, bundle.getSerializable(BundleAndSharedPreferencesConstants.TIME_TYPE));

        EventsViewAdapter eventsViewAdapterAttended = new EventsViewAdapter(UserStatsActivity.this, eventsAttended, extras);
        EventsViewAdapter eventsViewAdapterNotAttended = new EventsViewAdapter(UserStatsActivity.this, eventsNotAttended, extras);

        rvAttended.setAdapter(eventsViewAdapterAttended);
        rvNotAttended.setAdapter(eventsViewAdapterNotAttended);

        eventsViewAdapterAttended.notifyDataSetChanged();
        eventsViewAdapterNotAttended.notifyDataSetChanged();
    }


    public int getPercentageAttendanceForUser(String user) {

        ArrayList<Event> eventsOrganised = getEventsWithUserInAttendees(db.getEvents(EventType.ORGANISE, TimeType.PAST), user);
        ArrayList<Event> eventsWithUserInAttending = getEventsOrganisedWithUserInAttendees(user, AttendanceType.ATTENDING);

        float result;

        if (eventsOrganised.size() < 1) {
            result = 0f;
        } else {
            result = ((float) eventsWithUserInAttending.size() / (float) eventsOrganised.size() * 100.0f);
        }

        return (int) Math.floor(result);

    }

    /**
     * filters events by user being in the attendees
     */
    private ArrayList<Event> getEventsWithUserInAttendees(ArrayList<Event> events, String user) {

        ArrayList<Event> result = new ArrayList<>();

        for (Event event : events) {

            if (event.getAttendees().contains(user)) {
                result.add(event);
            }
        }

        return result;
    }

    /**
     * returns Lists for adapters for events that the user did or did not sign in for
     */
    private ArrayList<Event> getEventsOrganisedWithUserInAttendees(String user, AttendanceType type) {

        ArrayList<Event> events = getEventsWithUserInAttendees(db.getEvents(EventType.ORGANISE, TimeType.PAST), user);
        ArrayList<Event> result = new ArrayList<>();

        for (Event event : events) {

            if (event.getAttending() != null) {

                if (type == AttendanceType.ATTENDING) {

                    if (event.getAttending().contains(user)) {

                        result.add(event);
                    }
                }
                else if (type == AttendanceType.NOT_ATTENDING) {

                    if (!event.getAttending().contains(user)) {

                        result.add(event);
                    }
                }
            }
        }

        return result;
    }
}
