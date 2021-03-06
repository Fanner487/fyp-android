package com.example.user.attendr.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.attendr.adapters.EventsViewAdapter;
import com.example.user.attendr.credentials.CredentialManager;
import com.example.user.attendr.enums.TimeType;
import com.example.user.attendr.interfaces.ListenerInterface;
import com.example.user.attendr.R;
import com.example.user.attendr.callbacks.EventApiCallback;
import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.models.Event;
import com.example.user.attendr.network.NetworkCheck;
import com.example.user.attendr.network.NetworkInterface;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ListenerInterface {

    private final String TAG = MainActivity.class.getSimpleName();

    DBManager db;
    Toolbar toolbar;
    FloatingActionMenu fam;
    DrawerLayout drawer;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    FloatingActionButton fabCreateEvent, fabCreateGroup;
    SwipeRefreshLayout swipeRefreshLayout;

    TextView tvName;
    TextView tvEmail;
    TextView tvUsername;

    RecyclerView rvOngoing;
    RecyclerView rvUpcoming;
    LinearLayoutManager linearLayoutManagerOngoing;
    LinearLayoutManager linearLayoutManagerUpcoming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabCreateEvent = findViewById(R.id.fab_create_event);
        fabCreateGroup = findViewById(R.id.fab_create_group);
        fam = findViewById(R.id.fab_menu);
        swipeRefreshLayout = findViewById(R.id.swipe_container);

        rvOngoing = findViewById(R.id.rvOngoing);
        rvUpcoming = findViewById(R.id.rvUpcoming);

        NetworkInterface.getInstance(this).getEventsForUser( new EventApiCallback() {
            @Override
            public void onSuccess() {
                setAdaptersWithData();
            }

            @Override
            public void onFailure() {

            }
        });


        db = new DBManager(this).open();

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Get navigation header to populate text in there
        View headerView = navigationView.getHeaderView(0);
        tvEmail = headerView.findViewById(R.id.tvEmail);
        tvName = headerView.findViewById(R.id.tvName);
        tvUsername = headerView.findViewById(R.id.tvUsername);

        setNavBarHeadersWithUserDetails();
        setListeners();

        setAdaptersWithData();

        checkCalendarPermissions();
    }

    //Prompts user to grant calendar permissions
    private void checkCalendarPermissions(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALENDAR},
                    999);
        }
    }

    private void setAdaptersWithData(){

        ArrayList<Event> ongoingEvents = db.getEvents(TimeType.ONGOING);
        ArrayList<Event> upcomingEvents = db.getEvents(TimeType.FUTURE);

        linearLayoutManagerOngoing = new LinearLayoutManager(MainActivity.this);
        linearLayoutManagerUpcoming = new LinearLayoutManager(MainActivity.this);

        Bundle ongoingExtras = new Bundle();
        ongoingExtras.putSerializable(BundleAndSharedPreferencesConstants.TIME_TYPE, TimeType.ONGOING);

        Bundle upcomingExtras = new Bundle();
        upcomingExtras.putSerializable(BundleAndSharedPreferencesConstants.TIME_TYPE, TimeType.FUTURE);

        rvOngoing.setLayoutManager(linearLayoutManagerOngoing);
        rvUpcoming.setLayoutManager(linearLayoutManagerUpcoming);

        EventsViewAdapter eventsViewAdapterOngoing = new EventsViewAdapter(MainActivity.this, ongoingEvents, ongoingExtras);
        EventsViewAdapter eventsViewAdapterUpcoming = new EventsViewAdapter(MainActivity.this, upcomingEvents, upcomingExtras);

        rvOngoing.setAdapter(eventsViewAdapterOngoing);
        rvUpcoming.setAdapter(eventsViewAdapterUpcoming);

        eventsViewAdapterOngoing.notifyDataSetChanged();
        eventsViewAdapterUpcoming.notifyDataSetChanged();

    }

    @Override
    public void setListeners() {

        fabCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateUpdateEventActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(BundleAndSharedPreferencesConstants.CREATE_OR_UPDATE, BundleAndSharedPreferencesConstants.CREATE);
                intent.putExtras(bundle);
                startActivity(intent);
                fam.close(true);

            }
        });

        fabCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateUpdateViewUserGroupActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(BundleAndSharedPreferencesConstants.CREATE_OR_UPDATE, BundleAndSharedPreferencesConstants.CREATE);
                intent.putExtras(bundle);
                startActivity(intent);
                fam.close(true);

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(NetworkCheck.alertIfNotConnectedToInternet(MainActivity.this, swipeRefreshLayout)){

                    NetworkInterface.getInstance(MainActivity.this).getEventsForUser(new EventApiCallback() {
                        @Override
                        public void onSuccess() {
                            setAdaptersWithData();
                            Toast.makeText(MainActivity.this, getString(R.string.data_updated), Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onFailure() {
                            setAdaptersWithData();
                            Toast.makeText(MainActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    swipeRefreshLayout.setRefreshing(false);

                }
            }
        });
    }

    private void setNavBarHeadersWithUserDetails(){

        String fullName = CredentialManager.getCredential(getApplicationContext(), BundleAndSharedPreferencesConstants.FIRST_NAME) + " "
                + CredentialManager.getCredential(getApplicationContext(), BundleAndSharedPreferencesConstants.LAST_NAME);
        String greeting = getString(R.string.welcome_user) + " " + CredentialManager.getCredential(getApplicationContext(), BundleAndSharedPreferencesConstants.FIRST_NAME);

        tvName.setText(fullName);
        tvEmail.setText(CredentialManager.getCredential(getApplicationContext(), BundleAndSharedPreferencesConstants.EMAIL));
        tvUsername.setText(CredentialManager.getCredential(getApplicationContext(), BundleAndSharedPreferencesConstants.USERNAME));
        getSupportActionBar().setTitle(greeting);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.attending_events) {

            Intent intent = new Intent(this, AttendingEventsViewerActivity.class);
            startActivity(intent);

        } else if (id == R.id.organised_events) {

            Intent intent = new Intent(this, OrganiseEventsViewerActivity.class);
            startActivity(intent);


        } else if (id == R.id.groups) {

            Intent intent = new Intent(this, ViewGroupsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(BundleAndSharedPreferencesConstants.CREATE_OR_UPDATE, BundleAndSharedPreferencesConstants.CREATE);
            intent.putExtras(bundle);
            startActivity(intent);

        } else if (id == R.id.logout) {

            Intent i = new Intent(getApplicationContext(), LoginActivity.class);

            // Clear all activities and credentials from SharedPreferences and events before going back to login screen
            CredentialManager.clearCredentials(getApplicationContext());
            db.deleteAllEvents();
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
