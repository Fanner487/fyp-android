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

import com.example.user.attendr.ListenerInterface;
import com.example.user.attendr.R;
import com.example.user.attendr.callbacks.UserGroupCreateCallback;
import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.constants.DbConstants;
import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.models.Event;
import com.example.user.attendr.models.UserGroup;
import com.example.user.attendr.network.NetworkInterface;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateUserGroupActivity extends AppCompatActivity implements ListenerInterface{
    private static final String TAG = CreateUserGroupActivity.class.getSimpleName();

    EditText etGroupName;
    EditText etUsers;
    Button btnSubmit;
    Bundle bundle;
    DBManager db;

    String createOrUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_group);

        db = new DBManager(this).open();
        bundle = getIntent().getExtras();

        createOrUpdate = bundle.getString(BundleAndSharedPreferencesConstants.CREATE_OR_UPDATE);
        etGroupName = findViewById(R.id.etGroupName);
        etUsers = findViewById(R.id.etUsers);
        btnSubmit = findViewById(R.id.btnSubmit);

        if(createOrUpdate.equals(BundleAndSharedPreferencesConstants.UPDATE)){

            populateWithExistingData();
        }

        setListeners();
    }

    public void populateWithExistingData(){
        UserGroup group = db.getGroupWithId(bundle.getInt(DbConstants.GROUP_KEY_ROW_ID));

        Log.d(TAG, group.toString());
        etGroupName.setText(group.getGroupName());
        etUsers.setText(listToString(group.getUsers()));
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
                UserGroup group = new UserGroup(
                        etGroupName.getText().toString(),
                        toList(etUsers.getText().toString().toLowerCase().trim())
                );

                NetworkInterface.getInstance(CreateUserGroupActivity.this).verifyGroup(group, new UserGroupCreateCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(CreateUserGroupActivity.this, "Verified group", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String response) {
                        AlertDialog alertDialog = new AlertDialog.Builder(CreateUserGroupActivity.this).create();
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
}
