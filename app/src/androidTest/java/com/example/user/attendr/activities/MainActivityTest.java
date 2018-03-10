package com.example.user.attendr.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.user.attendr.R;
import com.example.user.attendr.constants.DbConstants;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.junit.Rule;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
/**
 * Created by Eamon on 10/03/2018.
 */

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> rule  = new  ActivityTestRule<>(MainActivity.class);

    @Test
    public void testTextViewExists(){
        MainActivity activity = rule.getActivity();

        FloatingActionButton fabCreateEvent = activity.findViewById(R.id.fab_create_event);
        FloatingActionButton fabCreateGroup = activity.findViewById(R.id.fab_create_group);
        FloatingActionMenu fam = activity.findViewById(R.id.fab_menu);
        SwipeRefreshLayout swipeRefreshLayout = activity.findViewById(R.id.swipe_container);

        assertNotNull(fabCreateEvent);
        assertNotNull(fabCreateGroup);
        assertNotNull(fam);
        assertNotNull(swipeRefreshLayout);
    }
}
