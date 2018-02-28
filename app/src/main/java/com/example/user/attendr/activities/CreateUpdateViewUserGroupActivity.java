package com.example.user.attendr.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.attendr.callbacks.EventDeleteCallback;
import com.example.user.attendr.enums.AttendanceType;
import com.example.user.attendr.enums.EventType;
import com.example.user.attendr.enums.TimeType;
import com.example.user.attendr.interfaces.ListenerInterface;
import com.example.user.attendr.R;
import com.example.user.attendr.callbacks.UserGroupCreateCallback;
import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.constants.DbConstants;
import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.models.Event;
import com.example.user.attendr.models.UserGroup;
import com.example.user.attendr.network.NetworkCheck;
import com.example.user.attendr.network.NetworkInterface;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateUpdateViewUserGroupActivity extends AppCompatActivity implements ListenerInterface{
    private static final String TAG = CreateUpdateViewUserGroupActivity.class.getSimpleName();

    EditText etGroupName;
    EditText etUsers;
    Button btnSubmit;
    Button btnDelete;
    Bundle bundle;
    DBManager db;

    UserGroup existingGroup;
    String createOrUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_update_view_user_group);

        db = new DBManager(this).open();
        bundle = getIntent().getExtras();

        createOrUpdate = bundle.getString(BundleAndSharedPreferencesConstants.CREATE_OR_UPDATE);
        etGroupName = findViewById(R.id.etGroupName);
        etUsers = findViewById(R.id.etUsers);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnDelete = findViewById(R.id.btnDelete);

        if(createOrUpdate.equals(BundleAndSharedPreferencesConstants.UPDATE)){
            btnSubmit.setText(getString(R.string.update));
            populateWithExistingData();
        }
        else{
            btnSubmit.setText(getString(R.string.create));
            btnDelete.setVisibility(View.INVISIBLE);
        }

        ArrayList<Event> eventsUserSignedIn = getEventsOrganisedWithUserInAttendees("r", AttendanceType.ATTENDING);
        ArrayList<Event> eventsUserNotSignedIn = getEventsOrganisedWithUserInAttendees("r", AttendanceType.NOT_ATTENDING);

        Log.d(TAG, "-------------------");
        Log.d(TAG, "eventsUserSignedIn");
        for(Event event : eventsUserSignedIn){
            Log.d(TAG, event.toString());
        }

        Log.d(TAG, "-------------------");
        Log.d(TAG, "eventsUserNotSignedIn");
        for(Event event : eventsUserNotSignedIn){
            Log.d(TAG, event.toString());
        }

        Log.d(TAG, "-------------------");
        Log.d(TAG, "Percentage");
        Log.d(TAG, Integer.toString(getPercentageAttendanceForUser("r")));

        setListeners();
    }

    public void populateWithExistingData(){
        existingGroup = db.getGroupWithId(bundle.getInt(DbConstants.GROUP_KEY_ROW_ID));

        Log.d(TAG, existingGroup.toString());
        etGroupName.setText(existingGroup.getGroupName());
        etUsers.setText(listToString(existingGroup.getUsers()));
    }

    private ArrayList<String> toList(String value){

        String[] tempList = value.split("\n");

        return new ArrayList<>(Arrays.asList(tempList));
    }

    @Override
    public void setListeners() {

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(NetworkCheck.alertIfNotConnectedToInternet(CreateUpdateViewUserGroupActivity.this, btnSubmit)){

                    final UserGroup group = new UserGroup(
                            etGroupName.getText().toString(),
                            toList(etUsers.getText().toString().toLowerCase().trim())
                    );
                    /*
                    * Perform either create or update on group
                    * */
                    if(createOrUpdate.equals(BundleAndSharedPreferencesConstants.CREATE)){

                        NetworkInterface.getInstance(CreateUpdateViewUserGroupActivity.this).verifyGroup(group, createOrUpdate, new UserGroupCreateCallback() {
                            @Override
                            public void onSuccess() {

                                if(db.insertUserGroup(group) > 0){
                                    Toast.makeText(CreateUpdateViewUserGroupActivity.this, getString(R.string.verified_group), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(String response) {
                                AlertDialog alertDialog = new AlertDialog.Builder(CreateUpdateViewUserGroupActivity.this).create();
                                alertDialog.setTitle("Alert");
                                alertDialog.setMessage(response);
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        });
                    }
                    else if(createOrUpdate.equals(BundleAndSharedPreferencesConstants.UPDATE)){
                        NetworkInterface.getInstance(CreateUpdateViewUserGroupActivity.this).verifyGroup(group, createOrUpdate, new UserGroupCreateCallback() {
                            @Override
                            public void onSuccess() {
                                // Sets ID of group to be group ID passed in
                                group.setId(bundle.getInt(DbConstants.GROUP_KEY_ROW_ID));

                                if(db.updateGroup(group) > 0 ){

                                    Toast.makeText(CreateUpdateViewUserGroupActivity.this, getString(R.string.group_updated), Toast.LENGTH_SHORT).show();

                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(String response) {
                                AlertDialog alertDialog = new AlertDialog.Builder(CreateUpdateViewUserGroupActivity.this).create();
                                alertDialog.setTitle("Alert");
                                alertDialog.setMessage(response);
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        });
                    }
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CreateUpdateViewUserGroupActivity.this);
                builder.setTitle(getString(R.string.are_you_sure));
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if(db.deleteGroup(existingGroup) > 0){
                            Toast.makeText(CreateUpdateViewUserGroupActivity.this, getString(R.string.group_deleted), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Toast.makeText(CreateUpdateViewUserGroupActivity.this, getString(R.string.group_delete_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
    }


    // Converts an Arraylist of strings into a newline deliminated string
    // For adding a group of members into a
    private String listToString(ArrayList<String> list) {

        String result;

        if (list == null) {
            result = "";
        } else {
            StringBuilder stringBuilder = new StringBuilder();

            for (String name : list) {
                stringBuilder.append(name);
                stringBuilder.append("\n");
            }

            result = stringBuilder.toString().replaceAll("\n$", "");
        }
        return result;
    }


    public int getPercentageAttendanceForUser(String user){

        ArrayList<Event> eventsOrganised = getEventsWithUserInAttendees(db.getEvents(EventType.ORGANISE, TimeType.PAST), user);
        ArrayList<Event> eventsWithUserInAttending = getEventsOrganisedWithUserInAttendees(user, AttendanceType.ATTENDING);


        float result;

        if(eventsOrganised.size() < 1){
            result = 0f;
        }
        else{
            result = ((float) eventsWithUserInAttending.size() / (float) eventsOrganised.size() * 100.0f);
        }

        return (int)Math.floor(result);

    }

    private ArrayList<Event> getEventsWithUserInAttendees(ArrayList<Event> events, String user){

        ArrayList<Event> result = new ArrayList<>();

        for(Event event : events){

            if(event.getAttendees().contains(user)) {
                result.add(event);
            }
        }

        return result;
    }



    private ArrayList<Event> getEventsOrganisedWithUserInAttendees(String user, AttendanceType type){
        ArrayList<Event> events = getEventsWithUserInAttendees(db.getEvents(EventType.ORGANISE, TimeType.PAST), user);
        ArrayList<Event> result = new ArrayList<>();


        for(Event event : events){

            if (event.getAttending() != null) {

                if(type == AttendanceType.ATTENDING){

                    if (event.getAttending().contains(user)) {

                        result.add(event);
                    }
                }
                else if(type == AttendanceType.NOT_ATTENDING){

                    if (! event.getAttending().contains(user)) {

                        result.add(event);
                    }
                }
            }
        }

        return result;
    }

    private String getLoggedInUser(){
        SharedPreferences userDetails = getSharedPreferences("", MODE_PRIVATE);
        return userDetails.getString(BundleAndSharedPreferencesConstants.USERNAME, "");
    }
}
