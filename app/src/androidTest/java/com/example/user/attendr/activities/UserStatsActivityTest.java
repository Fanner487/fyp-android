package com.example.user.attendr.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.user.attendr.R;
import com.example.user.attendr.constants.DbConstants;
import com.github.lzyzsd.circleprogress.DonutProgress;

import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by Eamon on 10/03/2018.
 */

public class UserStatsActivityTest {

    @Rule
    public ActivityTestRule<UserStatsActivity> rule  = new ActivityTestRule<UserStatsActivity>(UserStatsActivity.class){
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent result = new Intent(targetContext, UserStatsActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString(DbConstants.GROUP_KEY_ROW_USERNAME, "test");
//            result.putExtra(DbConstants.EVENT_KEY_EVENT_ID, 1);
            result.putExtras(bundle);
            return result;
        }
    };

    @Test
    public void testTextViewExists(){
        UserStatsActivity activity = rule.getActivity();

        TextView tvUsername = activity.findViewById(R.id.tvUsername);
        TextView tvEventsAttended = activity.findViewById(R.id.tvEventsAttended);
        TextView tvAttended = activity.findViewById(R.id.tvAttended);
        TextView tvNotAttended = activity.findViewById(R.id.tvNotAttended);
        RecyclerView rvAttended = activity.findViewById(R.id.rvAttended);
        RecyclerView rvNotAttended = activity.findViewById(R.id.rvNotAttended);
        DonutProgress donutProgress = activity.findViewById(R.id.donut_progress);
        ScrollView scrollView = activity.findViewById(R.id.scrollView);

        assertNotNull(tvUsername);
        assertNotNull(tvEventsAttended);
        assertNotNull(tvAttended);
        assertNotNull(tvNotAttended);
        assertNotNull(rvAttended);
        assertNotNull(rvNotAttended);
        assertNotNull(donutProgress);
        assertNotNull(scrollView);

    }

}
