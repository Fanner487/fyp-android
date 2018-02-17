package com.example.user.attendr.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.example.user.attendr.R;

/**
 * Created by Eamon on 16/02/2018.
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
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }

        if(!connected){
            Snackbar snackbar = Snackbar
                    .make(view, context.getString(R.string.not_connected_to_internet), Snackbar.LENGTH_LONG);

            snackbar.show();

        }

        return connected;
    }
}
