package com.example.user.attendr.activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.attendr.R;
import com.example.user.attendr.callbacks.UserGroupCreateCallback;
import com.example.user.attendr.models.UserGroup;
import com.example.user.attendr.network.NetworkInterface;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateUserGroupActivity extends AppCompatActivity {

    EditText etGroupName;
    EditText etUsers;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_group);

        etGroupName = findViewById(R.id.etGroupName);
        etUsers = findViewById(R.id.etUsers);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserGroup group = new UserGroup(
                        etGroupName.getText().toString(),
                        toList(etUsers.getText().toString().trim())
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

    private ArrayList<String> toList(String value){

        String[] tempList = value.split("\n");

        return new ArrayList<>(Arrays.asList(tempList));
    }
}
