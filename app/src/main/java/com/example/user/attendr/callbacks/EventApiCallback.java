package com.example.user.attendr.callbacks;

import com.androidnetworking.error.ANError;

import org.json.JSONArray;

import okhttp3.Response;

/**
 * Created by Eamon on 08/02/2018.
 *
 * Callback interface for API CRUD operations on Events
 */

public interface EventApiCallback {

    void onSuccess();
    void onFailure();
}
