//package com.example.user.attendr.activities;
//
//import android.content.SharedPreferences;
//import android.support.test.rule.ActivityTestRule;
//import android.support.v4.widget.SwipeRefreshLayout;
//
//import com.example.user.attendr.R;
//import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
//import com.example.user.attendr.database.DBManager;
//import com.example.user.attendr.models.Event;
//import com.example.user.attendr.models.UserGroup;
//import com.github.clans.fab.FloatingActionButton;
//import com.github.clans.fab.FloatingActionMenu;
//
//import org.junit.Rule;
//import org.junit.Test;
//
//import static junit.framework.Assert.assertNotNull;
//
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.test.InstrumentationRegistry;
//import android.support.test.filters.SmallTest;
//import android.support.test.rule.ActivityTestRule;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Spinner;
//import android.widget.Switch;
//import android.widget.TextClock;
//import android.widget.TextView;
//
//import com.example.user.attendr.R;
//import com.example.user.attendr.constants.DbConstants;
//import com.github.clans.fab.FloatingActionButton;
//import com.github.clans.fab.FloatingActionMenu;
//
//import org.junit.Rule;
//import org.junit.Test;
//
//import java.util.ArrayList;
//
//import static junit.framework.Assert.assertEquals;
//import static junit.framework.Assert.assertNotNull;
//import static junit.framework.Assert.assertNull;
//import static junit.framework.Assert.assertTrue;
//
///**
// * Created by Eamon on 10/03/2018.
// */
//
//public class CreateUpdateEventActivityTest {
//    private static final String TAG = "CreateUpdateEventActivity";
//
//    @Rule
//    public ActivityTestRule<CreateUpdateEventActivity> ruleCreate  = new ActivityTestRule<CreateUpdateEventActivity>(CreateUpdateEventActivity.class){
//        @Override
//        protected Intent getActivityIntent() {
//            Context targetContext = InstrumentationRegistry.getInstrumentation()
//                    .getTargetContext();
//            Intent result = new Intent(targetContext, CreateUpdateEventActivity.class);
//
//            Bundle bundle = new Bundle();
//            bundle.putString(BundleAndSharedPreferencesConstants.CREATE_OR_UPDATE, BundleAndSharedPreferencesConstants.CREATE);
//            result.putExtras(bundle);
//            return result;
//        }
//    };
//
//    @Rule
//    public ActivityTestRule<CreateUpdateEventActivity> ruleUpdate  = new ActivityTestRule<CreateUpdateEventActivity>(CreateUpdateEventActivity.class){
//        @Override
//        protected Intent getActivityIntent() {
//            Context targetContext = InstrumentationRegistry.getInstrumentation()
//                    .getTargetContext();
//            Intent result = new Intent(targetContext, CreateUpdateEventActivity.class);
//
//            Bundle bundle = new Bundle();
//            bundle.putString(BundleAndSharedPreferencesConstants.CREATE_OR_UPDATE, BundleAndSharedPreferencesConstants.UPDATE);
//            bundle.putInt(DbConstants.EVENT_KEY_EVENT_ID, 567);
//            result.putExtras(bundle);
//            return result;
//        }
//    };
//
//
//
//
//    @Test
//    public void testTextViewExistsCreate(){
//
////        Context context = InstrumentationRegistry.getTargetContext();
//
////        SharedPreferences sharedPreferences = context.getSharedPreferences("", Context.MODE_PRIVATE);
////        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        CreateUpdateEventActivity activity = ruleCreate.getActivity();
//
//        EditText etEventName = activity.findViewById(R.id.tvEventName);
//        EditText etLocation = activity.findViewById(R.id.etLocation);
//        EditText etAttendees = activity.findViewById(R.id.etAttendees);
//
//        TextView tvStartTime = activity.findViewById(R.id.tvStartTime);
//        TextView tvFinishTime = activity.findViewById(R.id.tvFinishTime);
//        TextView tvSignInTime = activity.findViewById(R.id.tvSignInTime);
//
//        Button btnStartTime = activity.findViewById(R.id.btnStartTime);
//        Button btnFinishTime = activity.findViewById(R.id.btnFinishTime);
//        Button btnSignInTime = activity.findViewById(R.id.btnSignInTime);
//        Button btnSubmit = activity.findViewById(R.id.btnSubmit);
//        Button btnDelete = activity.findViewById(R.id.btnDelete);
//
//        Switch switchAttendanceRequired = activity.findViewById(R.id.switchAttendanceRequired);
//        Spinner spinner = activity.findViewById(R.id.spinner);
//
//        assertNotNull(etEventName);
//        assertNotNull(etAttendees);
//        assertNotNull(etLocation);
//        assertNotNull(tvFinishTime);
//        assertNotNull(tvSignInTime);
//        assertNotNull(tvSignInTime);
//        assertNotNull(tvStartTime);
//        assertNotNull(btnSubmit);
//        assertNotNull(btnDelete);
//        assertNotNull(btnStartTime);
//        assertNotNull(btnFinishTime);
//        assertNotNull(btnSignInTime);
//        assertNotNull(switchAttendanceRequired);
//        assertNotNull(spinner);
//    }
//
//    @Test
//    public void testTextViewExistsUpdate(){
//
////        Context context = InstrumentationRegistry.getTargetContext();
//
////        SharedPreferences sharedPreferences = context.getSharedPreferences("", Context.MODE_PRIVATE);
////        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        Context context = InstrumentationRegistry.getTargetContext();
//
//        DBManager db = new DBManager(context).open();
//
//        int eventId = 567;
//        String organiser = "test";
//        String eventName = "test";
//        String location = "test";
//        String startTime = "test";
//        String finishTime = "test";
//        String signInTime = "test";
//        ArrayList<String> attendees =  new ArrayList<>();
//        ArrayList<String> attending = new ArrayList<>();
//        boolean attendanceRequired = true;
//
//        Event event = new Event(eventId, organiser, eventName, location, startTime, finishTime, signInTime, attendees, attending, attendanceRequired);
//
//        final long insert = db.insertEvent(event);
//
//        Log.d(TAG, "Update Event ID: " + Long.toString(insert));
//
//        CreateUpdateEventActivity activity = ruleUpdate.getActivity();
//
//        assertNotNull(activity);
//
//        EditText etEventName = activity.findViewById(R.id.tvEventName);
//        EditText etLocation = activity.findViewById(R.id.etLocation);
//        EditText etAttendees = activity.findViewById(R.id.etAttendees);
//
//        TextView tvStartTime = activity.findViewById(R.id.tvStartTime);
//        TextView tvFinishTime = activity.findViewById(R.id.tvFinishTime);
//        TextView tvSignInTime = activity.findViewById(R.id.tvSignInTime);
//
//        Button btnStartTime = activity.findViewById(R.id.btnStartTime);
//        Button btnFinishTime = activity.findViewById(R.id.btnFinishTime);
//        Button btnSignInTime = activity.findViewById(R.id.btnSignInTime);
//        Button btnSubmit = activity.findViewById(R.id.btnSubmit);
//        Button btnDelete = activity.findViewById(R.id.btnDelete);
//
//        Switch switchAttendanceRequired = activity.findViewById(R.id.switchAttendanceRequired);
//        Spinner spinner = activity.findViewById(R.id.spinner);
//
//        assertNotNull(etEventName);
//        assertNotNull(etAttendees);
//        assertNotNull(etLocation);
//        assertNotNull(tvFinishTime);
//        assertNotNull(tvSignInTime);
//        assertNotNull(tvSignInTime);
//        assertNotNull(tvStartTime);
//        assertNotNull(btnSubmit);
//        assertNotNull(btnDelete);
//        assertNotNull(btnStartTime);
//        assertNotNull(btnFinishTime);
//        assertNotNull(btnSignInTime);
//        assertNotNull(switchAttendanceRequired);
//        assertNotNull(spinner);
//    }
//}
