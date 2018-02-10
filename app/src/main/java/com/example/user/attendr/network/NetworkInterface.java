package com.example.user.attendr.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONArrayRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndStringRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.example.user.attendr.callbacks.EventCreateUpdateCallback;
import com.example.user.attendr.callbacks.EventDeleteCallback;
import com.example.user.attendr.callbacks.LoginCallback;
import com.example.user.attendr.callbacks.RegisterCallback;
import com.example.user.attendr.enums.EventType;
import com.example.user.attendr.models.Event;
import com.jacksonandroidnetworking.JacksonParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Response;

/**
 * Created by Eamon on 06/02/2018.
 *
 * Singleton class that is contains all API calls to the server
 */

public class NetworkInterface {

    private final String TAG = NetworkInterface.class.getSimpleName();

    private static NetworkInterface instance;
    private static Context context;

    private static List<Event> returnList = new ArrayList<>();

    private NetworkInterface(Context context) {

        NetworkInterface.context = context;

        AndroidNetworking.initialize(context.getApplicationContext());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
    }

    public static synchronized NetworkInterface getInstance(Context context) {

        if (instance == null) {
            instance = new NetworkInterface(context);
        }

        return instance;
    }

    public List<Event> getEvents(EventType type) {

        String typeString = "";

        if(type == EventType.ATTENDING){
            typeString = "attending";
        }
        else if(type == EventType.ORGANISE){
            typeString = "organising";
        }

        AndroidNetworking.get("http://46.101.13.145:8000/api/profile/{username}/{type}/{time}/")
                .addPathParameter("username", getLoggedInUser())
                .addPathParameter("type", typeString)
                .addPathParameter("time", "all")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsObjectList(Event.class, new ParsedRequestListener<List<Event>>() {
                    @Override
                    public void onResponse(List<Event> events) {
                        // do anything with response
                        Log.d(TAG, "Events size : " + events.size());

                        for (Event event : events) {
                            Log.d(TAG, event.toString());
                            Log.d(TAG, event.getFormattedStartTime().toString());
                            Log.d(TAG, event.getFormattedFinishTime().toString());
                            Log.d(TAG, event.getFormattedSignInTime().toString());
                            Log.d(TAG, "Attendees");

                            for(String attendee :event.getAttendees()){
                                Log.d(TAG, attendee);
                            }


                        }

                        returnList = events;
//                        setEvents(events);


                    }

                    @Override
                    public void onError(ANError anError) {
                        // handle error
                        Log.d(TAG, anError.getErrorDetail());
                        Log.d(TAG, anError.getMessage());
                        Log.d(TAG, Integer.toString(anError.getErrorCode()));
                    }
                });

        Log.d(TAG, "returning");
        Log.d(TAG, Integer.toString(returnList.size()));
        return returnList;
    }


    public void login(String username, String password, final LoginCallback callback) {

        AndroidNetworking.post("http://46.101.13.145:8000/api/login/")
                .addBodyParameter("username", username)
                .addBodyParameter("password", password)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {

                        if (response.code() == 200) {
                            callback.onSuccess();
                        } else {
                            callback.onFailure();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        callback.onFailure();
                    }
                });
    }

    public void register(String username, String email, String password,
                         String passwordConfirm, String firstName, String lastName, final RegisterCallback callback) {

        AndroidNetworking.post("http://46.101.13.145:8000/api/register/")
                .addBodyParameter("username", username)
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .addBodyParameter("password_confirm", passwordConfirm)
                .addBodyParameter("first_name", firstName)
                .addBodyParameter("last_name", lastName)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        callback.onSuccess();
                    }

                    @Override
                    public void onError(ANError anError) {
                        callback.onFailure(anError.getErrorBody().toString());
                    }
                });
    }

    public void createEvent(Event event, final EventCreateUpdateCallback eventCreateUpdateCallback) {

        // JSON object to append event fields into for the request body
        JSONObject create = new JSONObject();

        try {
            create.put("organiser", getLoggedInUser());
            create.put("event_name", event.getEventName());
            create.put("location", event.getLocation());
            create.put("start_time", parseToIsoTime(event.getStartTime()));
            create.put("finish_time", parseToIsoTime(event.getFinishTime()));
            create.put("sign_in_time", parseToIsoTime(event.getSignInTime()));
            create.put("attendance_required", Boolean.toString(true));
            JSONArray jsonArray = new JSONArray();

            for (String name : event.getAttendees()) {
                jsonArray.put(name);
            }

            create.put("attendees", jsonArray);
            Log.d(TAG, create.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post("http://46.101.13.145:8000/api/events/")
                .addJSONObjectBody(create)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "in onResponse");
                        Log.d(TAG, response.toString());
                        eventCreateUpdateCallback.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "in onError");
                        Log.d(TAG, Integer.toString(anError.getErrorCode()));
                        Log.d(TAG, anError.getErrorBody());
                        Log.d(TAG, anError.getErrorDetail());
                        eventCreateUpdateCallback.onFailure(anError);
                    }
                });
    }

    public void updateEvent(Event event, final EventCreateUpdateCallback eventCreateUpdateCallback) {

        // JSON object to append event fields into for the request body
        JSONObject create = new JSONObject();

        try {
            create.put("organiser", getLoggedInUser());
            create.put("event_name", event.getEventName());
            create.put("location", event.getLocation());
            create.put("start_time", parseToIsoTime(event.getStartTime()));
            create.put("finish_time", parseToIsoTime(event.getFinishTime()));
            create.put("sign_in_time", parseToIsoTime(event.getSignInTime()));
            create.put("attendance_required", Boolean.toString(true));
            JSONArray jsonArray = new JSONArray();

            for (String name : event.getAttendees()) {
                jsonArray.put(name);
            }

            create.put("attendees", jsonArray);
            Log.d(TAG, create.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.patch("http://46.101.13.145:8000/api/events/" + Integer.toString(event.getId()) + "/")
                .addJSONObjectBody(create)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "in onResponse");
                        Log.d(TAG, response.toString());
                        eventCreateUpdateCallback.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "in onError");
                        Log.d(TAG, Integer.toString(anError.getErrorCode()));
                        Log.d(TAG, anError.getErrorBody());
                        Log.d(TAG, anError.getErrorDetail());
                        eventCreateUpdateCallback.onFailure(anError);
                    }
                });

    }

    public void deleteEvent(int eventId, final EventDeleteCallback eventDeleteCallback) {

        AndroidNetworking.delete("http://46.101.13.145:8000/api/events/" + Integer.toString(eventId) + "/")
                .build()
                .getAsOkHttpResponseAndString(new OkHttpResponseAndStringRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, String response) {

                        eventDeleteCallback.onSuccess();
                    }

                    @Override
                    public void onError(ANError anError) {
                        eventDeleteCallback.onFailure();
                    }
                });

    }

    /*
    * Converts times into a ISO-8601 standard so server can correctly read it
    * */
    private String parseToIsoTime(String time) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);

        Date newTime = null;

        try {
            newTime = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);

        Log.d(TAG, isoFormat.format(newTime));
        return isoFormat.format(newTime);
    }

    // Returns logged in user from shared preferences
    private String getLoggedInUser(){
        SharedPreferences userDetails = context.getSharedPreferences("", Context.MODE_PRIVATE);
        return userDetails.getString("username", "");
    }
}
