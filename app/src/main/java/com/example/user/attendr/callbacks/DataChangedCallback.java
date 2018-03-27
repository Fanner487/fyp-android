package com.example.user.attendr.callbacks;

/**
 * Created by Eamon on 11/03/2018.
 *
 * Callback to refresh data and adapters when user applies manual sign-in/removal from attending
 * or delete from event
 */

public interface DataChangedCallback {
    void onDataChanged();
}
