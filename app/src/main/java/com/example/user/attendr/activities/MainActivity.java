package com.example.user.attendr.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.user.attendr.interfaces.ListenerInterface;
import com.example.user.attendr.R;
import com.example.user.attendr.callbacks.EventApiCallback;
import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.models.UserGroup;
import com.example.user.attendr.network.NetworkInterface;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ListenerInterface {

    private final String TAG = MainActivity.class.getSimpleName();

    DBManager db;
//    FloatingActionButton fab;
    Toolbar toolbar;
    FloatingActionMenu fam;
    com.github.clans.fab.FloatingActionButton fabCreateEvent, fabCreateGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabCreateEvent = findViewById(R.id.fab_create_event);
        fabCreateGroup = findViewById(R.id.fab_create_group);
        fam = findViewById(R.id.fab_menu);

        SharedPreferences userDetails = getSharedPreferences("", Context.MODE_PRIVATE);
        String username = userDetails.getString("username", "");

        getSupportActionBar().setTitle(getString(R.string.welcome_user) + " " + username);

        NetworkInterface.getInstance(this).getEventsForUser(new EventApiCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure() {

            }
        });

        db = new DBManager(this).open();

        ArrayList<UserGroup> groups = db.getGroups();
        Log.d(TAG, "Groups");
        for (UserGroup g : groups) {
            Log.d(TAG, g.toString());
        }

//        fab = findViewById(R.id.fab);



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setListeners();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            SharedPreferences userDetails = getApplicationContext().getSharedPreferences("", MODE_PRIVATE);
            SharedPreferences.Editor edit = userDetails.edit();
            edit.putString(BundleAndSharedPreferencesConstants.USERNAME, "");
            edit.putString(BundleAndSharedPreferencesConstants.TOKEN, "");
            edit.putBoolean(BundleAndSharedPreferencesConstants.LOGGED_IN, false);
            edit.apply();

            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            // set the new task and clear flags
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    }
}
