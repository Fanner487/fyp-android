package com.example.user.attendr.callbacks;

/**
 * Created by Eamon on 21/02/2018.
 *
 * Callback for server token verification
 */

public interface TokenVerifyCallback {

    void onSuccess();
    void onFailure();
}
