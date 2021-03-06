package com.example.user.attendr.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.attendr.credentials.CredentialManager;
import com.example.user.attendr.interfaces.ListenerInterface;
import com.example.user.attendr.R;
import com.example.user.attendr.callbacks.LoginCallback;
import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.network.NetworkCheck;
import com.example.user.attendr.network.NetworkInterface;

/**
 * Created by Eamon on 06/02/2018.
 * Activity for Login
 */

public class LoginActivity extends AppCompatActivity implements ListenerInterface {

    private final String TAG = LoginActivity.class.getSimpleName();

    EditText etUsername;
    EditText etPassword;
    Button btnSubmit;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(getString(R.string.login));
        redirectUserIfLoggedIn();

        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnRegister = findViewById(R.id.btnRegister);

        setListeners();
    }

    @Override
    public void setListeners() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (NetworkCheck.isConnectedToInternet(LoginActivity.this)) {
                    NetworkInterface.getInstance(getApplicationContext()).login(etUsername.getText().toString().trim().toLowerCase(),
                            etPassword.getText().toString(),
                            new LoginCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(LoginActivity.this, getString(R.string.successful_login), Toast.LENGTH_SHORT).show();

                                    // Redirect to MainActivity screen
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    getApplicationContext().startActivity(intent);

                                    // Removes activity from the stack
                                    finish();
                                }

                                @Override
                                public void onFailure() {
                                    Toast.makeText(LoginActivity.this, getString(R.string.failure_login), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else {

                    Snackbar snackbar = Snackbar.make(btnSubmit, getString(R.string.not_connected_to_internet), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

            }
        });

        // Logout listener
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                getApplicationContext().startActivity(intent);


            }
        });
    }

    private void redirectUserIfLoggedIn() {

        boolean loggedIn = CredentialManager.getLoggedInCredential(getApplicationContext());

        if (loggedIn) {
            // Redirect to MainActivity screen
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            getApplicationContext().startActivity(intent);

            // Removes activity from the stack
            finish();
        }
    }
}
