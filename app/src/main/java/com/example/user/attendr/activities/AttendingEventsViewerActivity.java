package com.example.user.attendr.activities;

import android.content.Intent;
import android.net.Uri;

import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.interfaces.ListenerInterface;
import com.example.user.attendr.R;
import com.example.user.attendr.adapters.SectionsPagerAdapter;
import com.example.user.attendr.enums.EventType;
import com.example.user.attendr.fragments.ViewEventsFragment;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

/**
 * Created by Eamon on 16/02/2018.
 *
 * Tab Layout Activity for showing Attending events
 */

public class AttendingEventsViewerActivity extends AppCompatActivity
        implements ViewEventsFragment.OnFragmentInteractionListener, ListenerInterface{

    private static final String TAG = AttendingEventsViewerActivity.class.getSimpleName();

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    TabLayout tabLayout;
    Toolbar toolbar;
    FloatingActionMenu fam;
    FloatingActionButton fabCreateEvent, fabCreateGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attending_events_viewer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle(R.string.attending);


        fabCreateEvent = findViewById(R.id.fab_create_event);
        fabCreateGroup = findViewById(R.id.fab_create_group);
        fam = findViewById(R.id.fab_menu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), EventType.ATTEND);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        setListeners();

    }

    // Needed for the fragment inits
    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void setListeners() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fabCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AttendingEventsViewerActivity.this, CreateUpdateEventActivity.class);
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
                Intent intent = new Intent(AttendingEventsViewerActivity.this, CreateUpdateViewUserGroupActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(BundleAndSharedPreferencesConstants.CREATE_OR_UPDATE, BundleAndSharedPreferencesConstants.CREATE);
                intent.putExtras(bundle);
                startActivity(intent);
                fam.close(true);

            }
        });

    }
}
