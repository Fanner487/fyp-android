package com.example.user.attendr.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.example.user.attendr.R;
import com.example.user.attendr.callbacks.EventCreateUpdateCallback;
import com.example.user.attendr.callbacks.TimeSetCallback;
import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.models.Event;
import com.example.user.attendr.models.UserGroup;
import com.example.user.attendr.network.NetworkInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Eamon on 08/02/2018.
 * <p>
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
    Spinner spinner;

    DBManager db;

    ArrayList<UserGroup> groups;
    ArrayList<String> groupNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        db = new DBManager(this).open();

        prepareGroups();


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
        spinner = findViewById(R.id.spinner);


        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, groupNames) {

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // First item is disable and it is used for hint
                if (i > 0) {

                    String attendeeString = listToString(groups.get(i - 1).getUsers());
                    etAttendees.setText(attendeeString);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Continues if all fields are filled
                if (allFieldsFilled()) {

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

                    for (String attendee : attendees) {
                        Log.d(TAG, attendee);
                    }

                    Event event = new Event(eventName, location, startTime, finishTime, signInTime, attendees, attendanceRequired);

                    NetworkInterface.getInstance(CreateEventActivity.this).createEvent(event, new EventCreateUpdateCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            try {
                                Toast.makeText(CreateEventActivity.this, "Created event: " + response.get("event_name"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(ANError anError) {
//                            Toast.makeText(CreateEventActivity.this, anError.getErrorBody(), Toast.LENGTH_SHORT).show();

                            try{
//                                JSONObject error = new JSONObject(anError.getErrorBody());
//                                Log.d(TAG, error.getString("non_field_errors"));
//
//                                while(error.keys().hasNext()){
//
//                                }

                                String jsonString = anError.getErrorBody();
                                JSONObject resobj = new JSONObject(jsonString);
                                Iterator<?> keys = resobj.keys();
                                while(keys.hasNext() ) {
                                    String key = (String)keys.next();
                                    if ( resobj.get(key) instanceof JSONObject ) {
                                        JSONObject value = new JSONObject(resobj.get(key).toString());
                                        Log.d(TAG, value.getString("something"));
                                        Log.d(TAG, value.getString("something2"));
                                    }
                                }

                            }
                            catch(JSONException e){
                                e.printStackTrace();
                            }

                            AlertDialog alertDialog = new AlertDialog.Builder(CreateEventActivity.this).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage(anError.getErrorBody());
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    });
                } else {
                    Toast.makeText(CreateEventActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Opens date and time alert dialogs for the user to pick the times
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
    }

    /*
    * Pops up a DatePicker Dialog box and TimePicker Dialog box and gathers time/date gathered
    * from user input to set the text view time for start/finish/sign in times
    * Callbacks are implemented for asynchronous setting of the time variables from the user input
    * */
    private void setTextViewTimeFromDateTimeDialog(final TextView textView) {

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


                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.ENGLISH);
                        Date newTime = time.getTime();
                        Log.d(TAG, sdf.format(newTime));
                        textView.setText(sdf.format(newTime));
                    }
                });
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE));

        datePickerDialog.show();

    }

    private void getTimeFromDialog(final TimeSetCallback callback) {
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

    private ArrayList<String> toList(String value) {

        String[] tempList = value.split("\n");

        return new ArrayList<>(Arrays.asList(tempList));
    }

    // Converts an Arraylist of strings into a newline deliminated string
    // For adding a group of members into a
    private String listToString(ArrayList<String> list) {

        String result;

        if (list == null) {
            result = "";
        } else {
            StringBuilder stringBuilder = new StringBuilder();

            for (String name : list) {
                stringBuilder.append(name);
                stringBuilder.append("\n");
            }

            result = stringBuilder.toString().replaceAll("\n$", "");
        }
        return result;
    }

    private boolean allFieldsFilled() {
        if (etEventName.getText().toString().length() > 0 && etLocation.getText().toString().length() > 0
                && etAttendees.getText().toString().length() > 0 && !tvStartTime.getText().equals(getString(R.string.start_time))
                && !tvSignInTime.getText().equals(getString(R.string.sign_in_time))
                && !tvFinishTime.getText().equals(getString(R.string.finish_time))) {

            return true;
        } else {
            return false;
        }
    }

    private void prepareGroups(){
        // Separate lists for
        groups = db.getGroups();
        groupNames = new ArrayList<>();

        // First value in spinner is greyed out and is un-selectable
        groupNames.add("Select from group names");

        for (UserGroup group : groups) {
            groupNames.add(group.getGroupName());
        }
    }
}
