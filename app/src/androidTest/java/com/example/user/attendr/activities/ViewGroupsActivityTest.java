package com.example.user.attendr.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.RecyclerView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.user.attendr.R;
import com.example.user.attendr.constants.DbConstants;
import com.github.lzyzsd.circleprogress.DonutProgress;

import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Eamon on 10/03/2018.
 */

public class ViewGroupsActivityTest {

    @Rule
    public ActivityTestRule<ViewGroupsActivity> rule  = new ActivityTestRule<ViewGroupsActivity>(ViewGroupsActivity.class){
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent result = new Intent(targetContext, ViewGroupsActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString(DbConstants.GROUP_KEY_ROW_USERNAME, "test");
            result.putExtras(bundle);
            return result;
        }
    };


    @Test
    public void testTextViewExists(){
        ViewGroupsActivity activity = rule.getActivity();

        RecyclerView recyclerView = activity.findViewById(R.id.recyclerView);

        assertNotNull(recyclerView);

    }

    @Test
    public void testTitleBar(){
        ViewGroupsActivity activity = rule.getActivity();

        String title = activity.getSupportActionBar().getTitle().toString();

        assertEquals(title, "Groups");

    }

}
