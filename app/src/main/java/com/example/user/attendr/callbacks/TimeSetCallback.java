package com.example.user.attendr.callbacks;

/**
 * Created by Eamon on 08/02/2018.
 *
 * Callback for onDateSet() when creating event (CreateEventActivity).
 * Sends result of user input of TimePickerDialog back to DatePicker to
 * combine date and time and instantiating the time
 */

public interface TimeSetCallback {

    void onTimeSet(int hour, int minute);
}
