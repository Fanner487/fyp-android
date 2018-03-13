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

import com.example.user.attendr.R;
import com.example.user.attendr.activities.LoginActivity;
import com.example.user.attendr.callbacks.TokenVerifyCallback;
import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.credentials.CredentialManager;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Eamon on 16/02/2018.
 *
 * Static class that checks to see if phone has access to internet.
 * Shows AlertDialog if user JWT expired and SnackBar informing user not connected to the internet
 */

public class NetworkCheck {

    /*
    * returns boolean from result of checking if phone is connected to the internet
    * */
    public static boolean isConnectedToInternet(Context context){
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

        return connected;
    }

    /*
    * Snackbar appears if user not connected to the internet
    * */
    public static boolean alertIfNotConnectedToInternet(Context context, View view) {

        boolean connected = true;

        if(isConnectedToInternet(context)){
           redirectToLoginIfTokenExpired(context);
        }
        else{
            Snackbar snackbar = Snackbar.make(view, context.getString(R.string.not_connected_to_internet), Snackbar.LENGTH_SHORT);
            snackbar.show();
            connected = false;
        }

        return connected;
    }

    /*
    * AlertDialog prompts user to sign in again if the JWT token expired
    * */
    public static void redirectToLoginIfTokenExpired(final Context context) {

        if(NetworkCheck.isConnectedToInternet(context)){

            NetworkInterface.getInstance(context).verifyToken(new TokenVerifyCallback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure() {
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle(context.getString(R.string.token_expired));
                    alertDialog.setMessage(context.getString(R.string.sign_in_again));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {

                                    CredentialManager.clearCredentials(context);

                                    Intent i = new Intent(context, LoginActivity.class);
                                    // set the new task and clear flags
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(i);

                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.cancel),
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
