package com.example.user.attendr.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.example.user.attendr.R;
import com.example.user.attendr.callbacks.EventCreateUpdateCallback;
import com.example.user.attendr.callbacks.TimeSetCallback;
import com.example.user.attendr.models.Event;
import com.example.user.attendr.network.NetworkInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Eamon on 08/02/2018.
 *
 * Activity for creating and updating Events
 */


public class CreateEventActivity extends AppCompatActivity {

    final String TAG = CreateEventActivity.class.getSimpleName();

    EditText etEventName;
    EditText etLocation;
    EditText etAttendees;
    TextView tvStartTime;
    TextView tvFinishTime;
    TextView tvSignInTime;
    Button btnStartTime;
    Button btnFinishTime;
    Button btnSignInTime;
    Button btnSubmit;
    Switch switchAttendanceRequired;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        etEventName = findViewById(R.id.tvEventName);
        etLocation = findViewById(R.id.etLocation);
        etAttendees = findViewById(R.id.etAttendees);

        tvStartTime = findViewById(R.id.tvStartTime);
        tvFinishTime = findViewById(R.id.tvFinishTime);
        tvSignInTime = findViewById(R.id.tvSignInTime);

        btnStartTime = findViewById(R.id.btnStartTime);
        btnFinishTime = findViewById(R.id.btnFinishTime);
        btnSignInTime = findViewById(R.id.btnSignInTime);
        btnSubmit = findViewById(R.id.btnSubmit);

        switchAttendanceRequired = findViewById(R.id.switchAttendanceRequired);

        btnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextViewTimeFromDateTimeDialog(tvStartTime);
            }
        });

        btnFinishTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextViewTimeFromDateTimeDialog(tvFinishTime);
            }
        });
        btnSignInTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextViewTimeFromDateTimeDialog(tvSignInTime);
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(allFieldsFilled()){
                    String eventName = etEventName.getText().toString().trim();
                    String location = etLocation.getText().toString().trim();
                    ArrayList<String> attendees = toList(etAttendees.getText().toString().trim());
                    String startTime = tvStartTime.getText().toString().trim();
                    String finishTime = tvFinishTime.getText().toString().trim();
                    String signInTime = tvSignInTime.getText().toString().trim();
                    boolean attendanceRequired = switchAttendanceRequired.isChecked();

                    Log.d(TAG, eventName);
                    Log.d(TAG, location);
                    Log.d(TAG, startTime);
                    Log.d(TAG, finishTime);
                    Log.d(TAG, signInTime);
                    Log.d(TAG, Boolean.toString(attendanceRequired));
                    Log.d(TAG, "Attendees");

                    for(String attendee : attendees){
                        Log.d(TAG, attendee);
                    }

                    Event event = new Event(eventName, location, startTime, finishTime, signInTime, attendees, attendanceRequired);

                    NetworkInterface.getInstance(CreateEventActivity.this).createEvent(event, new EventCreateUpdateCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            try {
                                Toast.makeText(CreateEventActivity.this, "Created event: " + response.get("event_name"), Toast.LENGTH_SHORT).show();
                            }
                            catch (JSONException e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(ANError anError) {
                            Toast.makeText(CreateEventActivity.this, anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(CreateEventActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                }



            }
        });
    }

    /*
    * Pops up a DatePicker Dialog box and TimePicker Dialog box and gathers time/date gathered
    * from user input to set the text view time for start/finish/sign in times
    * Callbacks are implemented for asynchronous setting of the time variables from the user input
    * */
    private void setTextViewTimeFromDateTimeDialog(final TextView textView){

        final DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEventActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                final int year = i;
                final int month = i1;
                final int day = i2;

                getTimeFromDialog(new TimeSetCallback() {
                    @Override
                    public void onTimeSet(int hour, int minute) {

                        // Assign new time from user input of datetime
                        Calendar time = new GregorianCalendar();
                        time.set(Calendar.YEAR, year);
                        time.set(Calendar.MONTH, month);
                        time.set(Calendar.DATE, day);
                        time.set(Calendar.HOUR_OF_DAY, hour);
                        time.set(Calendar.MINUTE, minute);
                        time.set(Calendar.SECOND, 0);


                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
                        Date newTime = time.getTime();
                        Log.d(TAG, sdf.format(newTime));
                        textView.setText(sdf.format(newTime));
                    }
                });
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE));

        datePickerDialog.show();

    }

    private void getTimeFromDialog(final TimeSetCallback callback){
        final TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                Log.d(TAG, Integer.toString(i));
                Log.d(TAG, Integer.toString(i1));
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

                callback.onTimeSet(i, i1);
            }
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);

        timePickerDialog.show();
    }

    private ArrayList<String> toList(String value){

        String[] tempList = value.split("\n");

        return new ArrayList<>(Arrays.asList(tempList));
    }

    private boolean allFieldsFilled(){
        if(etEventName.getText().toString().length() > 0 && etLocation.getText().toString().length() > 0
                && etAttendees.getText().toString().length() > 0 && !tvStartTime.getText().equals(getString(R.string.start_time))
                && !tvSignInTime.getText().equals(getString(R.string.sign_in_time))
                && !tvFinishTime.getText().equals(getString(R.string.finish_time))){

            return true;
        }
        else{
            return false;
        }
    }
}
