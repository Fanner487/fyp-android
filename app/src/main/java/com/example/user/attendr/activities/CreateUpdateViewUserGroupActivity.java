package com.example.user.attendr.activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class CreateUpdateViewUserGroupActivity extends AppCompatActivity implements ListenerInterface{
    private static final String TAG = CreateUpdateViewUserGroupActivity.class.getSimpleName();

    EditText etGroupName;
    EditText etUsers;
    EditText etDescription;
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

        // Shows back button on top of activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DBManager(this).open();
        bundle = getIntent().getExtras();

        createOrUpdate = bundle.getString(BundleAndSharedPreferencesConstants.CREATE_OR_UPDATE);

        // Sets title of activity based on if it is to create or update group
        if(createOrUpdate.equals(BundleAndSharedPreferencesConstants.CREATE)){
            getSupportActionBar().setTitle(getString(R.string.create_group));

        }
        else if(createOrUpdate.equals(BundleAndSharedPreferencesConstants.UPDATE)){
            getSupportActionBar().setTitle(getString(R.string.update_group));
        }


        etGroupName = findViewById(R.id.etGroupName);
        etUsers = findViewById(R.id.etUsers);
        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnDelete = findViewById(R.id.btnDelete);

        // populate fields with existing data for update
        // Hide delete button if it is update group
        if(createOrUpdate.equals(BundleAndSharedPreferencesConstants.UPDATE)){
            btnSubmit.setText(getString(R.string.update));
            populateWithExistingData();
        }
        else{
            btnSubmit.setText(getString(R.string.create));
            btnDelete.setVisibility(View.INVISIBLE);
        }

        setListeners();
    }

    public void populateWithExistingData(){

        existingGroup = db.getGroupWithId(bundle.getInt(DbConstants.GROUP_KEY_ROW_ID));

        Log.d(TAG, existingGroup.toString());
        etGroupName.setText(existingGroup.getGroupName());
        etUsers.setText(listToString(existingGroup.getUsers()));
        etDescription.setText(existingGroup.getDescription());
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
                            etGroupName.getText().toString().trim(),
                            etDescription.getText().toString(),
                            toList(etUsers.getText().toString().toLowerCase().trim())
                    );

                    /*
                    * Perform either create or update on group
                    * */
                    if(createOrUpdate.equals(BundleAndSharedPreferencesConstants.CREATE)){

                        createUserGroup(group);
                    }
                    else if(createOrUpdate.equals(BundleAndSharedPreferencesConstants.UPDATE)){

                        updateUserGroup(group);
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

    private void createUserGroup(final UserGroup group){

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

                String errorMessage = parseErrors(response);
//                Toast.makeText(CreateUpdateViewUserGroupActivity.this, response, Toast.LENGTH_SHORT).show();
                AlertDialog alertDialog = new AlertDialog.Builder(CreateUpdateViewUserGroupActivity.this).create();
                alertDialog.setTitle(getString(R.string.alert));
                alertDialog.setMessage(errorMessage);
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

    private void updateUserGroup(final UserGroup group){

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
                Toast.makeText(CreateUpdateViewUserGroupActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Parses server errors from JSON to a human-readable string
    private String parseErrors(String response){
        JSONObject jsonObject;

        StringBuilder builder = new StringBuilder();
        try{
            jsonObject = new JSONObject(response);
            Iterator<String> keys = jsonObject.keys();

            while(keys.hasNext()){
                String key = keys.next();
                String val = jsonObject.getString(key).replace("[", "").replace("\"", "").replace("]", "");;

                // Capitalises first letter of key
                String capitalKey = key.substring(0, 1).toUpperCase() + key.substring(1);

                builder.append(capitalKey);
                builder.append(": ");
                builder.append(val);
                builder.append("\n");
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return builder.toString();
    }

}
