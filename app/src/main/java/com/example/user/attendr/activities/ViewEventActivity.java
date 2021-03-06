package com.example.user.attendr.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.attendr.callbacks.DataChangedCallback;
import com.example.user.attendr.constants.ApiUrls;
import com.example.user.attendr.credentials.CredentialManager;
import com.example.user.attendr.enums.EventType;
import com.example.user.attendr.enums.TimeType;
import com.example.user.attendr.interfaces.ListenerInterface;
import com.example.user.attendr.R;
import com.example.user.attendr.adapters.AttendeesViewAdapter;
import com.example.user.attendr.callbacks.EventApiCallback;
import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.constants.DbConstants;
import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.models.Event;
import com.example.user.attendr.network.NetworkCheck;
import com.example.user.attendr.network.NetworkInterface;

/*
 * Created by Eamon on 26/02/2018
 *
 * Views an event with attendees. Users can view the event on Google Maps.
 * */


public class ViewEventActivity extends AppCompatActivity implements ListenerInterface{

    private static final String TAG = ViewEventActivity.class.getSimpleName();

    TextView tvEventName;
    TextView tvLocation;
    TextView tvOrganiser;
    TextView tvStartTime;
    TextView tvSignInTime;
    TextView tvFinishTime;
    TextView tvPercentage;
    TextView tvAttended;
    Button btnUpdateOrSignIn;
    Button btnViewOnMap;

    int eventId;
    Event event;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    DBManager db;
    Bundle bundle;

    SwipeRefreshLayout swipeRefreshLayout;

    TimeType timeType;
    EventType eventType;

    @Override
    protected void onResume() {
        super.onResume();

        updateDataFromNetwork();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        bundle = getIntent().getExtras();
        db = new DBManager(this).open();


        eventId = bundle.getInt(DbConstants.EVENT_KEY_EVENT_ID);

        tvEventName = findViewById(R.id.tvEventName);
        tvLocation = findViewById(R.id.tvLocation);
        tvOrganiser = findViewById(R.id.tvOrganiser);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvSignInTime = findViewById(R.id.tvSignInTime);
        tvFinishTime = findViewById(R.id.tvFinishTime);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvAttended = findViewById(R.id.tvAttended);
        btnUpdateOrSignIn = findViewById(R.id.btnUpdateOrSignIn);
        btnViewOnMap = findViewById(R.id.btnViewOnMap);
        swipeRefreshLayout = findViewById(R.id.swipe_container);

        timeType = (TimeType) bundle.getSerializable(BundleAndSharedPreferencesConstants.TIME_TYPE);
        eventType = (EventType) bundle.getSerializable(BundleAndSharedPreferencesConstants.EVENT_TYPE);

        assignEvent();
        eventType = getEventType(event);

        setButtonVisibilities();

        updateData();

        getSupportActionBar().setTitle(event.getEventName());

    }

    private EventType getEventType(Event event){

        String username = CredentialManager.getCredential(getApplicationContext(), BundleAndSharedPreferencesConstants.USERNAME);
        EventType eventType;

        if(username.equals(event.getOrganiser())){
            eventType = EventType.ORGANISE;
        }
        else{
            eventType = EventType.ATTEND;
        }

        return eventType;
    }

    private void updateData(){

        assignEvent();
        populateAdapter();
        setListeners();
        populateTextViews();
    }

    private void assignEvent(){
        event = db.getEventWithEventId(eventId);
    }

    private String getPercentageAttended(){

        int result;

        if(event.getAttending() == null || event.getAttending().size() == 0){
            result = 0;
        }
        else{
            float percentage = ((float) event.getAttending().size() / (float) event.getAttendees().size() * 100.0f);
            result = (int) Math.floor(percentage);
        }

        return Integer.toString(result) + "%";
    }

    private String getUsersAttended(){

        int result;

        if(event.getAttending() == null || event.getAttending().size() == 0){
            result = 0;
        }
        else{
            result = event.getAttending().size();
        }

        return Integer.toString(result) + "/" + Integer.toString(event.getAttendees().size());
    }

    private void populateTextViews(){
        tvEventName.setText(event.getEventName());
        tvLocation.setText(event.getLocation());
        tvOrganiser.setText(event.getOrganiser());
        tvStartTime.setText(Event.parseDateToDisplayTime(event.getStartTime()));
        tvSignInTime.setText(Event.parseDateToDisplayTime(event.getSignInTime()));
        tvFinishTime.setText(Event.parseDateToDisplayTime(event.getFinishTime()));
        tvPercentage.setText(getPercentageAttended());
        tvAttended.setText(getUsersAttended());

    }

    // Hides event sign-in button unless user is attendee to event happening right now
    // Hides update button if the event is not an attending one
    private void setButtonVisibilities(){

        if(eventType.equals(EventType.ORGANISE)){
            btnUpdateOrSignIn.setVisibility(View.VISIBLE);
            btnUpdateOrSignIn.setText(R.string.update);
        }
        else if(timeType.equals(TimeType.ONGOING) && eventType.equals(EventType.ATTEND)){
            btnUpdateOrSignIn.setVisibility(View.VISIBLE);
            btnUpdateOrSignIn.setText(R.string.sign_in);
        }
        else{
            btnUpdateOrSignIn.setVisibility(View.GONE);
        }
    }

    public void populateAdapter(){

        Log.d(TAG, event.toString());


        recyclerView = findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        AttendeesViewAdapter attendeesViewAdapter = new AttendeesViewAdapter(ViewEventActivity.this, event, eventType, new DataChangedCallback() {
            @Override
            public void onDataChanged() {
                updateDataFromNetwork();
            }

        });

        recyclerView.setAdapter(attendeesViewAdapter);

        setListeners();
    }

    @Override
    public void setListeners() {

        // Redirects to Google Maps with location in address bar
        btnViewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri map = Uri.parse(ApiUrls.MAPS_ADDRESS_URL + tvLocation.getText().toString());
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(map);
                i.setPackage(ApiUrls.MAPS_APP_URL);
                startActivity(i);

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                if(NetworkCheck.alertIfNotConnectedToInternet(ViewEventActivity.this, swipeRefreshLayout)){

                    NetworkInterface.getInstance(getApplicationContext()).getEventsForUser(new EventApiCallback() {
                        @Override
                        public void onSuccess() {
                            updateData();
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onFailure() {
                            populateAdapter();
                            Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
                else{
                    swipeRefreshLayout.setRefreshing(false);

                }
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        btnUpdateOrSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Change function of button between update and sign into event
                if(eventType.equals(EventType.ORGANISE)){
                    Log.d(TAG, "In update event");

                    Bundle bundle = new Bundle();
                    bundle.putString(BundleAndSharedPreferencesConstants.CREATE_OR_UPDATE, BundleAndSharedPreferencesConstants.UPDATE);
                    bundle.putInt(DbConstants.EVENT_KEY_EVENT_ID, eventId);
                    startNewActivity(CreateUpdateEventActivity.class, bundle);
                    finish();
                }
                else if(eventType.equals(EventType.ATTEND)){
                    Bundle bundle = new Bundle();
                    bundle.putInt(DbConstants.EVENT_KEY_EVENT_ID, eventId);

                    startNewActivity(SignInActivity.class, bundle);
                }
            }
        });
    }

    public void startNewActivity(Class<?> cls, Bundle bundle){

        Intent intent = new Intent(getApplicationContext(), cls);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void updateDataFromNetwork(){
        if(NetworkCheck.alertIfNotConnectedToInternet(ViewEventActivity.this, swipeRefreshLayout)){

            NetworkInterface.getInstance(getApplicationContext()).getEventsForUser(new EventApiCallback() {
                @Override
                public void onSuccess() {
                    updateData();
                }

                @Override
                public void onFailure() {
                    populateAdapter();
                    Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
