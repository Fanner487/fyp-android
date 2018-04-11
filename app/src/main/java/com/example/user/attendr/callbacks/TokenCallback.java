package com.example.user.attendr.callbacks;

import org.json.JSONObject;

/**
 * Created by Eamon on 21/02/2018.
 *
 * Callback for requesting JWT token
 */

public interface TokenCallback {

    void onSuccess(JSONObject response);
    void onFailure();
}
