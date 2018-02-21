package com.example.user.attendr.callbacks;

import com.androidnetworking.error.ANError;

import org.json.JSONObject;

/**
 * Created by Eamon on 21/02/2018.
 */

public interface TokenCallback {

    void onSuccess(JSONObject response);
    void onFailure();
}
