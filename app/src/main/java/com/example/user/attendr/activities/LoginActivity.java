package com.example.user.attendr.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.attendr.R;
import com.example.user.attendr.callbacks.LoginCallback;
import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.enums.EventType;
import com.example.user.attendr.network.NetworkInterface;

/**
 * Created by Eamon on 06/02/2018.
 *
 * Activity for Login
 */

public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.class.getSimpleName();

    EditText etUsername;
    EditText etPassword;
    Button btnSubmit;


    DBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, etUsername.getText().toString());
                Log.d(TAG, etPassword.getText().toString());

                NetworkInterface.getInstance(getApplicationContext()).login(etUsername.getText().toString().trim().toLowerCase(),
                        etPassword.getText().toString(),
                        new LoginCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(LoginActivity.this, "Log in successful", Toast.LENGTH_SHORT).show();

                                // Assign SharedPreferences username to the login
                                SharedPreferences userDetails = getApplicationContext().getSharedPreferences("", MODE_PRIVATE);
                                SharedPreferences.Editor edit = userDetails.edit();
                                edit.putString("username", etUsername.getText().toString().trim().toLowerCase());
                                edit.apply();

                                // Redirect to MainActivity screen
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                getApplicationContext().startActivity(intent);

                                // Removes activity from the stack
                                finish();
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(LoginActivity.this, "Error sir", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


    }
}
