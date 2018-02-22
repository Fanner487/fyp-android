package com.example.user.attendr.activities;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.user.attendr.SignInActivity;
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

public class ViewEventActivity extends AppCompatActivity implements ListenerInterface{

    private static final String TAG = ViewEventActivity.class.getSimpleName();

    TextView tvEventName;
    TextView tvLocation;
    TextView tvOrganiser;
    TextView tvStartTime;
    TextView tvSignInTime;
    TextView tvFinishTime;
    Button btnUpdate;
    Button btnSignIn;
    int eventId;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    DBManager db;
    Bundle bundle;
    Toolbar toolbar;

    SwipeRefreshLayout swipeRefreshLayout;

    TimeType timeType;
    EventType eventType;

    @Override
    protected void onResume() {
        super.onResume();

        if(NetworkCheck.isConnectedToInternet(ViewEventActivity.this)){
            NetworkCheck.redirectToLoginIfTokenExpired(ViewEventActivity.this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        bundle = getIntent().getExtras();
        db = new DBManager(this).open();

        if(NetworkCheck.isConnectedToInternet(ViewEventActivity.this)){
            NetworkCheck.redirectToLoginIfTokenExpired(ViewEventActivity.this);
        }

        tvEventName = findViewById(R.id.tvEventName);
        tvLocation = findViewById(R.id.tvLocation);
        tvOrganiser = findViewById(R.id.tvOrganiser);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvSignInTime = findViewById(R.id.tvSignInTime);
        tvFinishTime = findViewById(R.id.tvFinishTime);
        btnUpdate = findViewById(R.id.btnUpdate);
        swipeRefreshLayout = findViewById(R.id.swipe_container);
        btnSignIn = findViewById(R.id.btnSignIn);

        timeType = (TimeType) bundle.getSerializable(BundleAndSharedPreferencesConstants.TIME_TYPE);
        eventType = (EventType) bundle.getSerializable(BundleAndSharedPreferencesConstants.EVENT_TYPE);

        setButtonVisibilities();

        populateAdapter();

        setListeners();

    }

    // Hides event sign-in button unless user is attendee to event happening right now
    // Hides update button if the event is not an attending one
    private void setButtonVisibilities(){

        if(timeType == TimeType.ONGOING && eventType == EventType.ATTEND){
            btnSignIn.setVisibility(View.VISIBLE);
        }
        else{
            btnSignIn.setVisibility(View.INVISIBLE);
        }

        if(eventType == EventType.ATTEND){
            btnUpdate.setVisibility(View.INVISIBLE);
        }
    }

    public void populateAdapter(){

        eventId = bundle.getInt(DbConstants.EVENT_KEY_EVENT_ID);
        Event event = db.getEventWithEventId(eventId);

        Log.d(TAG, event.toString());

        tvEventName.setText(event.getEventName());
        tvLocation.setText(event.getLocation());
        tvOrganiser.setText(event.getOrganiser());
        tvStartTime.setText(Event.parseDateToDisplayTime(event.getStartTime()));
        tvSignInTime.setText(Event.parseDateToDisplayTime(event.getSignInTime()));
        tvFinishTime.setText(Event.parseDateToDisplayTime(event.getFinishTime()));
        recyclerView = findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        AttendeesViewAdapter attendeesViewAdapter = new AttendeesViewAdapter(getApplicationContext(), event.getAttendees(), event.getAttending());

        recyclerView.setAdapter(attendeesViewAdapter);

        setListeners();
    }

    @Override
    public void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                if(NetworkCheck.alertIfNotConnectedToInternet(getApplicationContext(), swipeRefreshLayout)){

                    NetworkInterface.getInstance(getApplicationContext()).getEventsForUser(new EventApiCallback() {
                        @Override
                        public void onSuccess() {
                            populateAdapter();
                            Toast.makeText(getApplicationContext(), getString(R.string.data_updated), Toast.LENGTH_SHORT).show();
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
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "Event ID: " + Integer.toString(bundle.getInt(DbConstants.EVENT_KEY_EVENT_ID)));

                Bundle bundle = new Bundle();
                bundle.putString(BundleAndSharedPreferencesConstants.CREATE_OR_UPDATE, BundleAndSharedPreferencesConstants.UPDATE);
                bundle.putInt(DbConstants.EVENT_KEY_EVENT_ID, eventId);
                startNewActivity(CreateUpdateEventActivity.class, bundle);
                finish();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Event ID: " + Integer.toString(bundle.getInt(DbConstants.EVENT_KEY_EVENT_ID)));
                Bundle bundle = new Bundle();
                bundle.putInt(DbConstants.EVENT_KEY_EVENT_ID, eventId);

                startNewActivity(SignInActivity.class, bundle);
            }
        });
    }

    public void startNewActivity(Class<?> cls, Bundle bundle){
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
