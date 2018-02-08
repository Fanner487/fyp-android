package com.example.user.attendr.callbacks;

import com.androidnetworking.error.ANError;

import org.json.JSONObject;

import okhttp3.Response;

/**
 * Created by Eamon on 08/02/2018.
 */

public interface EventCreateUpdateCallback {

    void onSuccess(JSONObject response);
    void onFailure(ANError anError);
}
