package com.example.user.attendr.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.attendr.interfaces.ListenerInterface;
import com.example.user.attendr.R;
import com.example.user.attendr.callbacks.RegisterCallback;
import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.network.NetworkCheck;
import com.example.user.attendr.network.NetworkInterface;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by Eamon on 06/02/2018.
 *
 * Activity for Registering
 */

public class RegisterActivity extends AppCompatActivity implements ListenerInterface{

    final String TAG = RegisterActivity.class.getSimpleName();

    EditText etUsername;
    EditText etEmail;
    EditText etPassword;
    EditText etPasswordConfirm;
    EditText etFirstName;
    EditText etLastName;
    Button btnSubmit;

    DBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle(getString(R.string.register));

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        btnSubmit = findViewById(R.id.btnSubmit);

        db = new DBManager(this).open();

        setListeners();
    }

    @Override
    public void setListeners() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerUser();
            }
        });
    }

    private void registerUser(){
        if (NetworkCheck.isConnectedToInternet(RegisterActivity.this)) {
            NetworkInterface.getInstance(getApplicationContext())
                    .register(
                            etUsername.getText().toString().toLowerCase().trim(),
                            etEmail.getText().toString().trim(),
                            etPassword.getText().toString().trim(),
                            etPasswordConfirm.getText().toString().trim(),
                            etFirstName.getText().toString().trim(),
                            etLastName.getText().toString().trim(),
                            new RegisterCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(RegisterActivity.this, R.string.account_made, Toast.LENGTH_SHORT).show();

                                    // Redirect to login screen
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    getApplicationContext().startActivity(intent);
                                }

                                @Override
                                public void onFailure(String response) {

                                    Log.d(TAG, response);
//                                    Toast.makeText(RegisterActivity.this, R.string.error_making_account, Toast.LENGTH_SHORT).show();

                                    String error = parseErrors(response);
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                                    alertDialogBuilder.setTitle(getString(R.string.error));
                                    alertDialogBuilder.setMessage(error);

                                    alertDialogBuilder.setNeutralButton(R.string.ok,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {
//                                                    Toast.makeText(RegisterActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                                                }
                                            });

//                                    alertDialogBuilder.setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            finish();
//                                        }
//                                    });

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                            });
        }
        else{
//            Toast.makeText(RegisterActivity.this, getString(R.string.not_connected_to_internet), Toast.LENGTH_SHORT).show();
            Snackbar snackbar = Snackbar.make(btnSubmit, getString(R.string.not_connected_to_internet), Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
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
