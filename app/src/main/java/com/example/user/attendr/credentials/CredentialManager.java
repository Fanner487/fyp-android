package com.example.user.attendr.credentials;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Eamon on 06/03/2018.
 */

public class CredentialManager {

    public static void setCredentials(Context context, String username, String firstName, String lastName, String email, String token){

        SharedPreferences userDetails = context.getSharedPreferences("", MODE_PRIVATE);
        SharedPreferences.Editor edit = userDetails.edit();
        edit.putString(BundleAndSharedPreferencesConstants.USERNAME, username);
        edit.putString(BundleAndSharedPreferencesConstants.FIRST_NAME, firstName);
        edit.putString(BundleAndSharedPreferencesConstants.LAST_NAME, lastName);
        edit.putString(BundleAndSharedPreferencesConstants.EMAIL, email);
        edit.putString(BundleAndSharedPreferencesConstants.TOKEN, token);
        edit.putBoolean(BundleAndSharedPreferencesConstants.LOGGED_IN, true);
        edit.apply();

    }

    public static void clearCredentials(Context context){

        SharedPreferences userDetails = context.getSharedPreferences("", MODE_PRIVATE);
        SharedPreferences.Editor edit = userDetails.edit();
        edit.putString(BundleAndSharedPreferencesConstants.USERNAME, "");
        edit.putString(BundleAndSharedPreferencesConstants.FIRST_NAME, "");
        edit.putString(BundleAndSharedPreferencesConstants.LAST_NAME, "");
        edit.putString(BundleAndSharedPreferencesConstants.EMAIL, "");
        edit.putString(BundleAndSharedPreferencesConstants.TOKEN, "");
        edit.putBoolean(BundleAndSharedPreferencesConstants.LOGGED_IN, false);
        edit.apply();

    }

    public static String getCredential(Context context, String credential){

        SharedPreferences userDetails = context.getSharedPreferences("", Context.MODE_PRIVATE);
        return userDetails.getString(credential, "");
    }

    public static boolean getLoggedInCredential(Context context){

        SharedPreferences userDetails = context.getSharedPreferences("", Context.MODE_PRIVATE);
        return userDetails.getBoolean(BundleAndSharedPreferencesConstants.LOGGED_IN, false);
    }
}
