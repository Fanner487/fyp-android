package com.example.user.attendr.activities;

import android.app.ActionBar;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.user.attendr.ListenerInterface;
import com.example.user.attendr.R;
import com.example.user.attendr.adapters.AttendeesViewAdapter;
import com.example.user.attendr.callbacks.EventApiCallback;
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

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    DBManager db;
    Bundle bundle;
    Toolbar toolbar;

    SwipeRefreshLayout swipeRefreshLayout;

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
        swipeRefreshLayout = findViewById(R.id.swipe_container);

        bundle = getIntent().getExtras();

        db = new DBManager(this).open();

        populateAdapter();

        setListeners();

    }

    public void populateAdapter(){

        int eventId = bundle.getInt(DbConstants.EVENT_KEY_EVENT_ID);
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

                if(NetworkCheck.isConnectedToInternet(getApplicationContext())){
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
                    Snackbar snackbar = Snackbar
                            .make(swipeRefreshLayout, getString(R.string.not_connected_to_internet), Snackbar.LENGTH_LONG);

                    snackbar.show();
                    swipeRefreshLayout.setRefreshing(false);
                }

//                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        finish();
//                    }
//                });
//                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    // Checks to see if user is online to get updates from server
    private boolean isOnline() {
        ConnectivityManager connectivityManager;
        boolean connected = false;
        try {
            connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();

        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }

        return connected;
    }

}
