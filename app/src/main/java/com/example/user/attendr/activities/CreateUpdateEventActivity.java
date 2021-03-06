package com.example.user.attendr.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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
import com.example.user.attendr.enums.EventType;
import com.example.user.attendr.interfaces.ListenerInterface;
import com.example.user.attendr.R;
import com.example.user.attendr.callbacks.EventApiCallback;
import com.example.user.attendr.callbacks.EventCreateUpdateCallback;
import com.example.user.attendr.callbacks.EventDeleteCallback;
import com.example.user.attendr.callbacks.TimeSetCallback;
import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.constants.DbConstants;
import com.example.user.attendr.constants.TimeFormats;
import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.models.Event;
import com.example.user.attendr.models.UserGroup;
import com.example.user.attendr.network.NetworkCheck;
import com.example.user.attendr.network.NetworkInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by Eamon on 08/02/2018.
 *
 * Activity for creating and updating Events
 *
 */


public class CreateUpdateEventActivity extends AppCompatActivity implements ListenerInterface{

    final String TAG = CreateUpdateEventActivity.class.getSimpleName();

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
    Button btnDelete;
    Switch switchAttendanceRequired;
    Spinner spinner;

    DBManager db;
    Event existingEvent;

    ArrayList<UserGroup> groups;
    ArrayList<String> groupNames;

    Bundle bundle;
    String createOrUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_update_event);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DBManager(this).open();

        bundle = getIntent().getExtras();

        createOrUpdate = bundle.getString(BundleAndSharedPreferencesConstants.CREATE_OR_UPDATE);

        if(createOrUpdate.equals(BundleAndSharedPreferencesConstants.CREATE)){
            getSupportActionBar().setTitle(R.string.create_event);
        }
        else{
            getSupportActionBar().setTitle(R.string.update_event);
        }

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
        btnDelete = findViewById(R.id.btnDelete);

        switchAttendanceRequired = findViewById(R.id.switchAttendanceRequired);
        spinner = findViewById(R.id.spinner);

        if(createOrUpdate.equals(BundleAndSharedPreferencesConstants.CREATE)){
            btnDelete.setVisibility(View.INVISIBLE);
        }
        else if(createOrUpdate.equals(BundleAndSharedPreferencesConstants.UPDATE)){

            populateWithExistingData();
        }

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

        setListeners();
    }

    /*
    * DatePicker Dialog box and TimePicker Dialog box and gathers time/date gathered
    * from user input to set the text view time for start/finish/sign in times
    * Callbacks are implemented for asynchronous setting of the time variables from the user input
    * */
    private void setTextViewTimeFromDateTimeDialog(final TextView textView) {

        final DatePickerDialog datePickerDialog = new DatePickerDialog(CreateUpdateEventActivity.this, new DatePickerDialog.OnDateSetListener() {
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


                        SimpleDateFormat sdf = new SimpleDateFormat(TimeFormats.DISPLAY_FORMAT, Locale.ENGLISH);
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
        final TimePickerDialog timePickerDialog = new TimePickerDialog(CreateUpdateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
        groupNames.add(getString(R.string.select_from_group_names));

        for (UserGroup group : groups) {
            groupNames.add(group.getGroupName());
        }
    }

    @Override
    public void setListeners() {

        setTimeListeners();
        groupPickListener();
        createUpdateListener();
        deleteListener();
    }

    private void populateWithExistingData(){

        existingEvent = db.getEventWithEventId(bundle.getInt(DbConstants.EVENT_KEY_EVENT_ID));
        etEventName.setText(existingEvent.getEventName());
        etLocation.setText(existingEvent.getLocation());
        tvStartTime.setText(Event.parseDateToDisplayTime(existingEvent.getStartTime()));
        tvFinishTime.setText(Event.parseDateToDisplayTime(existingEvent.getFinishTime()));
        tvSignInTime.setText(Event.parseDateToDisplayTime(existingEvent.getSignInTime()));
        etAttendees.setText(listToString(existingEvent.getAttendees()));
        switchAttendanceRequired.setChecked(existingEvent.isAttendanceRequired());

    }


    private void setTimeListeners(){

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

    private void groupPickListener(){
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
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void createUpdateListener(){

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if(NetworkCheck.alertIfNotConnectedToInternet(CreateUpdateEventActivity.this, view)){
                    // Continues if all fields are filled
                    if (allFieldsFilled()) {

                        String eventName = etEventName.getText().toString().trim();
                        String location = etLocation.getText().toString().trim();
                        ArrayList<String> attendees = toList(etAttendees.getText().toString().trim().toLowerCase());
                        String startTime = Event.parseToIsoTime(tvStartTime.getText().toString().trim());
                        String finishTime = Event.parseToIsoTime(tvFinishTime.getText().toString().trim());
                        String signInTime = Event.parseToIsoTime(tvSignInTime.getText().toString().trim());
                        boolean attendanceRequired = switchAttendanceRequired.isChecked();

                        final Event event = new Event(eventName, location, startTime, finishTime, signInTime, attendees, attendanceRequired);

                        /*
                        * If the activity is of type CREATE or UPDATE will determine CRUD API call to server
                        * whether it is a post or patch request
                        * */
                        if(createOrUpdate.equals(BundleAndSharedPreferencesConstants.CREATE)){

                            createEvent(event);
                        }
                        else if(createOrUpdate.equals(BundleAndSharedPreferencesConstants.UPDATE)){

                            updateEvent(event);
                        }

                    } else {
                        Toast.makeText(CreateUpdateEventActivity.this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void deleteListener(){

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                // Prompt the users with a dialog box
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateUpdateEventActivity.this);
                builder.setTitle(getString(R.string.are_you_sure));
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if(NetworkCheck.alertIfNotConnectedToInternet(getApplicationContext(), view)){

                            NetworkInterface.getInstance(getApplicationContext()).deleteEvent(bundle.getInt(DbConstants.EVENT_KEY_EVENT_ID),
                                    new EventDeleteCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Toast.makeText(CreateUpdateEventActivity.this, getString(R.string.event_deleted), Toast.LENGTH_SHORT).show();

                                            Intent i = new Intent(getApplicationContext(), LoginActivity.class);

                                            // Clears all existing activities
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);

                                            finish();
                                        }

                                        @Override
                                        public void onFailure() {
                                            Toast.makeText(CreateUpdateEventActivity.this, getString(R.string.event_delete_error), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void createEvent(Event event){

        NetworkInterface.getInstance(CreateUpdateEventActivity.this).createEvent(event, new EventCreateUpdateCallback() {
            @Override
            public void onSuccess(final JSONObject response) {
                try {
                    Toast.makeText(CreateUpdateEventActivity.this, getString(R.string.created_event) + " " + response.get("event_name"), Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "New event ID: " + Integer.toString(response.getInt("id")));

                    // Sync DB with server do get new event passed off into the new viewing activity
                    NetworkInterface.getInstance(CreateUpdateEventActivity.this).getEventsForUser(new EventApiCallback() {
                        @Override
                        public void onSuccess() {

                            try{
                                Intent intent = new Intent(CreateUpdateEventActivity.this, ViewEventActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt(DbConstants.EVENT_KEY_EVENT_ID, (response.getInt("id")));
                                bundle.putSerializable(BundleAndSharedPreferencesConstants.EVENT_TYPE, EventType.ORGANISE);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            }
                            catch (JSONException e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure() {}
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ANError anError) {

                String errorMessage = parseErrors(anError.getErrorBody());

                AlertDialog alertDialog = new AlertDialog.Builder(CreateUpdateEventActivity.this).create();
                alertDialog.setTitle(getString(R.string.alert));
                alertDialog.setMessage(errorMessage);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    private void updateEvent(final Event event){

        event.setEventId(bundle.getInt(DbConstants.EVENT_KEY_EVENT_ID));
        event.setAttending(existingEvent.getAttending());

        NetworkInterface.getInstance(CreateUpdateEventActivity.this).updateEvent(event, new EventCreateUpdateCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Toast.makeText(CreateUpdateEventActivity.this, getString(R.string.data_updated), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(CreateUpdateEventActivity.this, ViewEventActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(DbConstants.EVENT_KEY_EVENT_ID, event.getEventId());
                bundle.putSerializable(BundleAndSharedPreferencesConstants.EVENT_TYPE, EventType.ORGANISE);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(ANError anError) {
                Toast.makeText(CreateUpdateEventActivity.this, anError.getErrorBody(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Parses server errors from JSON to a human-readable string
    private String parseErrors(String response){
        JSONObject jsonObject;

        StringBuilder builder = new StringBuilder();
        try{
            jsonObject = new JSONObject(response);
            Iterator<String> keys = jsonObject.keys();

            while(keys.hasNext()){
                String key = keys.next();
                String val = jsonObject.getString(key).replace("[", "").replace("\"", "").replace("]", "");;

                // Capitalises first letter of key
                String capitalKey = key.substring(0, 1).toUpperCase() + key.substring(1);

                builder.append(capitalKey);
                builder.append(": ");
                builder.append(val);
                builder.append("\n");
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return builder.toString();
    }

}
