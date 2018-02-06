package com.example.user.attendr.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.attendr.R;
import com.example.user.attendr.callbacks.RegisterCallback;
import com.example.user.attendr.network.NetworkInterface;

public class RegisterActivity extends AppCompatActivity {

    final String TAG = RegisterActivity.class.getSimpleName();

    EditText etUsername;
    EditText etEmail;
    EditText etPassword;
    EditText etPasswordConfirm;
    EditText etFirstName;
    EditText etLastName;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NetworkInterface.getInstance(getApplicationContext())
                        .register(etUsername.getText().toString(), etEmail.getText().toString(), etPassword.getText().toString(),
                                etPasswordConfirm.getText().toString(), etFirstName.getText().toString(), etLastName.getText().toString(),
                                new RegisterCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(RegisterActivity.this, "Account made", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        getApplicationContext().startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(String response) {

                                        Log.d(TAG, response);
                                        Toast.makeText(RegisterActivity.this, "Error making account", Toast.LENGTH_SHORT).show();

                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                                        alertDialogBuilder.setMessage(response.toString());
                                                alertDialogBuilder.setPositiveButton("yes",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface arg0, int arg1) {
                                                                Toast.makeText(RegisterActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                                                            }
                                                        });

                                        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        });

                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                });
            }
        });

    }
}