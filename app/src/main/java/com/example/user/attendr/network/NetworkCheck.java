package com.example.user.attendr.network;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.example.user.attendr.R;
import com.example.user.attendr.activities.LoginActivity;
import com.example.user.attendr.callbacks.TokenVerifyCallback;
import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Eamon on 16/02/2018.
 *
 * Checks to see if phone has access to internet. If not, pop up a snackbar to give warning message
 */

public class NetworkCheck {

    // Checks to see if user is online to get updates from server
    public static boolean isConnectedToInternet(Context context, View view) {

        ConnectivityManager connectivityManager;

        boolean connected = false;
        try {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!connected){
            Snackbar snackbar = Snackbar.make(view, context.getString(R.string.not_connected_to_internet), Snackbar.LENGTH_LONG);
            snackbar.show();

        }

        return connected;
    }

    public static boolean tokenExpired(final Context context) {


//        SharedPreferences userDetails = getApplicationContext().getSharedPreferences("", MODE_PRIVATE);
//        SharedPreferences.Editor edit = userDetails.edit();
//        edit.putString(BundleAndSharedPreferencesConstants.USERNAME, "");
//        edit.putString(BundleAndSharedPreferencesConstants.TOKEN, "");
//        edit.putBoolean(BundleAndSharedPreferencesConstants.LOGGED_IN, false);
//        edit.apply();
//
//        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
//        // set the new task and clear flags
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(i);

        boolean expired = true;

        NetworkInterface.getInstance(context).verifyToken(new TokenVerifyCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure() {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Token Expired");
                alertDialog.setMessage("Please sign-in again");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                        SharedPreferences userDetails = context.getSharedPreferences("", MODE_PRIVATE);
                                        SharedPreferences.Editor edit = userDetails.edit();
                                        edit.putString(BundleAndSharedPreferencesConstants.USERNAME, "");
                                        edit.putString(BundleAndSharedPreferencesConstants.TOKEN, "");
                                        edit.putBoolean(BundleAndSharedPreferencesConstants.LOGGED_IN, false);
                                        edit.apply();

                                        Intent i = new Intent(context, LoginActivity.class);
                                        // set the new task and clear flags
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        context.startActivity(i);

                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        return expired;
    }
}
