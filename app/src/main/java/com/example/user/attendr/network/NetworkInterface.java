package com.example.user.attendr.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndStringRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.example.user.attendr.R;
import com.example.user.attendr.callbacks.EventApiCallback;
import com.example.user.attendr.callbacks.EventCreateUpdateCallback;
import com.example.user.attendr.callbacks.EventDeleteCallback;
import com.example.user.attendr.callbacks.LoginCallback;
import com.example.user.attendr.callbacks.RegisterCallback;
import com.example.user.attendr.callbacks.TokenCallback;
import com.example.user.attendr.callbacks.TokenVerifyCallback;
import com.example.user.attendr.callbacks.UserGroupCreateCallback;
import com.example.user.attendr.constants.ApiUrls;
import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.constants.NetworkConstants;
import com.example.user.attendr.constants.TimeFormats;
import com.example.user.attendr.credentials.CredentialManager;
import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.enums.EventType;
import com.example.user.attendr.models.Event;
import com.example.user.attendr.models.UserGroup;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Eamon on 06/02/2018.
 *
 * Singleton class that is contains all API calls to the server
 */

public class NetworkInterface {

    private final String TAG = NetworkInterface.class.getSimpleName();

    private static NetworkInterface instance;
    private static Context context;
    private static DBManager db;


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

    public void getEventsForUser(final EventApiCallback eventApiCallback){

        AndroidNetworking.get(ApiUrls.EVENTS_FOR_USER)
                .addPathParameter(NetworkConstants.USERNAME, getLoggedInUser())
                .addHeaders(BundleAndSharedPreferencesConstants.AUTHORIZATION_HEADER, getAuthorizationHeaderToken())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsObjectList(Event.class, new ParsedRequestListener<List<Event>>() {
                    @Override
                    public void onResponse(List<Event> events) {
                        // do anything with response
                        Log.d(TAG, "Events size : " + events.size());

                        for (Event event : events) {
                            Log.d(TAG, event.toString());
                        }

                        /*
                        * Adding to DB
                        * */

                        db = new DBManager(context.getApplicationContext()).open();
                        ArrayList<Event> newEvents = new ArrayList<>(events);

                        db.deleteAllEvents();
                        db.insertEvents(newEvents);

                        ArrayList<Event> dbList = db.getAllEvents();

                        for(Event eventDb: dbList){
                            Log.d(TAG, eventDb.toString());
                        }

                        eventApiCallback.onSuccess();

                    }

                    @Override
                    public void onError(ANError anError) {
                        // handle error
                        Log.d(TAG, anError.getErrorDetail());
                        Log.d(TAG, anError.getMessage());
                        Log.d(TAG, Integer.toString(anError.getErrorCode()));

                        eventApiCallback.onFailure();
                    }
                });
    }

    public void getTokenForUser(String username, String password, final TokenCallback tokenCallback){

        AndroidNetworking.post("http://46.101.13.145:8000/api/api-token-auth/")
                .setPriority(Priority.MEDIUM)
                .addBodyParameter(NetworkConstants.USERNAME, username)
                .addBodyParameter(NetworkConstants.PASSWORD, password)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {

                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {

                        if(okHttpResponse.code() == 200) {
                            tokenCallback.onSuccess(response);
                        }
                        else{
                            tokenCallback.onFailure();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        tokenCallback.onFailure();
                    }
                });



    }

    public void getEvents(final EventType type) {

        String typeString = "";

        if(type == EventType.ATTEND){
            typeString = "attending";
        }
        else if(type == EventType.ORGANISE){
            typeString = "organising";
        }

        AndroidNetworking.get(ApiUrls.PROFILE)
                .addPathParameter("username", getLoggedInUser())
                .addPathParameter("type", typeString)
                .addPathParameter("time", "all")
                .addHeaders(BundleAndSharedPreferencesConstants.AUTHORIZATION_HEADER, getAuthorizationHeaderToken())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsObjectList(Event.class, new ParsedRequestListener<List<Event>>() {
                    @Override
                    public void onResponse(List<Event> events) {
                        // do anything with response
                        Log.d(TAG, "Events size : " + events.size());

                        for (Event event : events) {
                            Log.d(TAG, event.toString());
                        }

                        /*
                        * Adding to DB
                        * */

                        db = new DBManager(context.getApplicationContext()).open();
                        ArrayList<Event> newEvents = new ArrayList<>(events);

                        db.deleteAllEvents();
                        db.insertEvents(newEvents);

                        ArrayList<Event> dbList = db.getAllEvents();

                        for(Event eventDb: dbList){
                            Log.d(TAG, eventDb.toString());
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        // handle error
                        Log.d(TAG, anError.getErrorDetail());
                        Log.d(TAG, anError.getMessage());
                        Log.d(TAG, Integer.toString(anError.getErrorCode()));
                    }
                });


    }

    public void login(final String username, final String password, final LoginCallback callback) {

        AndroidNetworking.post(ApiUrls.LOGIN)
                .addBodyParameter(NetworkConstants.USERNAME, username)
                .addBodyParameter(NetworkConstants.PASSWORD, password)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, final JSONObject loginResponse) {
                        Log.d(TAG, "login status: " + okHttpResponse.code());

                        getTokenForUser(username, password, new TokenCallback() {
                            @Override
                            public void onSuccess(JSONObject response) {

                                String token;
                                try {
                                    token = response.getString(BundleAndSharedPreferencesConstants.TOKEN);

                                    Log.d(TAG, "login Token callback");

                                    String firstName = loginResponse.getString(BundleAndSharedPreferencesConstants.FIRST_NAME);
                                    String lastName = loginResponse.getString(BundleAndSharedPreferencesConstants.LAST_NAME);
                                    String email = loginResponse.getString(BundleAndSharedPreferencesConstants.EMAIL);

                                    CredentialManager.setCredentials(context, username, firstName, lastName, email, token);

                                    callback.onSuccess();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure() {
                                Log.d(TAG, "login Token callback failure");
                            }
                        });
                    }
                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "in OnError");
                        callback.onFailure();
                    }
                });

    }

    public void register(String username, String email, String password,
                         String passwordConfirm, String firstName, String lastName, final RegisterCallback callback) {

        AndroidNetworking.post(ApiUrls.REGISTER)
                .addBodyParameter(NetworkConstants.USERNAME, username)
                .addBodyParameter(NetworkConstants.EMAIL, email)
                .addBodyParameter(NetworkConstants.PASSWORD, password)
                .addBodyParameter(NetworkConstants.PASSWORD_CONFIRM, passwordConfirm)
                .addBodyParameter(NetworkConstants.FIRST_NAME, firstName)
                .addBodyParameter(NetworkConstants.LAST_NAME, lastName)
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
                        callback.onFailure(anError.getErrorBody());
                    }
                });
    }

    public void createEvent(Event event, final EventCreateUpdateCallback eventCreateUpdateCallback) {

        // JSON object to append event fields into for the request body
        JSONObject eventAsJsonObject = eventAsJsonObject(event);

        AndroidNetworking.post(ApiUrls.EVENTS)
                .addJSONObjectBody(eventAsJsonObject)
                .setPriority(Priority.MEDIUM)
                .addHeaders(BundleAndSharedPreferencesConstants.AUTHORIZATION_HEADER, getAuthorizationHeaderToken())
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

    public void verifyToken(final TokenVerifyCallback tokenVerifyCallback){

        SharedPreferences userDetails = context.getSharedPreferences("", MODE_PRIVATE);
        String token = userDetails.getString(BundleAndSharedPreferencesConstants.TOKEN, "");
        AndroidNetworking.post("http://46.101.13.145:8000/api/api-token-verify/")
                .addBodyParameter(BundleAndSharedPreferencesConstants.TOKEN, token)
                .build()
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {

                        if(response.code() == 200){
                            tokenVerifyCallback.onSuccess();
                        }
                        else{
                            tokenVerifyCallback.onFailure();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        tokenVerifyCallback.onFailure();
                    }
                });
    }

    public void removeUserFromAttendees(final Event event, String user, final EventCreateUpdateCallback eventCreateUpdateCallback){

        Log.d(TAG, "Old attendees");
        for(String name : event.getAttendees()){
            Log.d(TAG, name);
        }
        ArrayList<String> newAttendees = new ArrayList<>();

        for(String name : event.getAttendees()){
            if(!name.equals(user)){
                newAttendees.add(name);
            }
        }

        Log.d(TAG, "New attendees");
        for(String name : newAttendees){
            Log.d(TAG, name);
        }

        event.setAttendees(newAttendees);

        Log.d(TAG, "event to update with removing member");
        Log.d(TAG, event.toString());

        updateEvent(event, new EventCreateUpdateCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                eventCreateUpdateCallback.onSuccess(response);
            }

            @Override
            public void onFailure(ANError anError) {
                eventCreateUpdateCallback.onFailure(anError);
            }
        });
    }

    public void updateEvent(Event event, final EventCreateUpdateCallback eventCreateUpdateCallback){

        Log.d(TAG, "in UpdateEvent");
        Log.d(TAG, event.toString());

        // JSON object to append event fields into for the request body
        JSONObject eventAsJsonObject = eventAsJsonObject(event);


        JSONArray jsonArrayAttending = new JSONArray();

        Log.d(TAG, "Before get attending");

        try{
            if(event.getAttending() != null){

                Log.d(TAG, "in get attending");
                for (String name : event.getAttending()) {
                    jsonArrayAttending.put(name);
                }

                eventAsJsonObject.put(NetworkConstants.ATTENDING, jsonArrayAttending);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        AndroidNetworking.patch(ApiUrls.EVENTS + Integer.toString(event.getEventId()) + "/")
                .addJSONObjectBody(eventAsJsonObject)
                .setPriority(Priority.MEDIUM)
                .addHeaders(BundleAndSharedPreferencesConstants.AUTHORIZATION_HEADER, getAuthorizationHeaderToken())
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

        AndroidNetworking.delete(ApiUrls.EVENTS + Integer.toString(eventId) + "/")
                .addHeaders(BundleAndSharedPreferencesConstants.AUTHORIZATION_HEADER, getAuthorizationHeaderToken())
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

    public void verifyGroup(final UserGroup group, final String type, final UserGroupCreateCallback callback){

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        db = new DBManager(context).open();

        try {

            for(String username: group.getUsers()){

                jsonArray.put(username);
            }

            jsonObject.put(NetworkConstants.USERNAMES, jsonArray);
        }
        catch(JSONException e){
            e.printStackTrace();
        }


        AndroidNetworking.post(ApiUrls.VERIFY_GROUP)
                .addJSONObjectBody(jsonObject)
                .addHeaders(BundleAndSharedPreferencesConstants.AUTHORIZATION_HEADER, getAuthorizationHeaderToken())
                .build()
                .getAsOkHttpResponseAndString(new OkHttpResponseAndStringRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, String response) {

                        // Sets username of current logged in user to be stored in DB
                        group.setUsername(getLoggedInUser());

                        if(type.equals(BundleAndSharedPreferencesConstants.CREATE)){
                            if(!db.groupAlreadyExistsWithUser(group)){

                                callback.onSuccess();
                            }
                            else{
                                callback.onFailure(context.getString(R.string.group_exists_with_same_name));

                            }
                        }
                        else{
                            callback.onSuccess();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        callback.onFailure(anError.getErrorBody());
                    }
                });
    }

    /*
    * Converts times into a ISO-8601 standard so server can correctly read it
    * */
    private String parseToIsoTime(String time) {

        Log.d(TAG, "in parseToIsoTime");
        Log.d(TAG, time);

        SimpleDateFormat sdf = new SimpleDateFormat(TimeFormats.DISPLAY_FORMAT, Locale.ENGLISH);

        Date newTime = null;

        try {
            newTime = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat isoFormat = new SimpleDateFormat(TimeFormats.ISO_FORMAT, Locale.ENGLISH);

        Log.d(TAG, isoFormat.format(newTime));
        return isoFormat.format(newTime);
    }

    public void manualSignInUser(String user, int eventId, final EventCreateUpdateCallback eventCreateUpdateCallback){

        JSONObject jsonObject = new JSONObject();

        try{

            jsonObject.put(NetworkConstants.USER, user);
            jsonObject.put(NetworkConstants.EVENT_ID, eventId);
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        AndroidNetworking.post(ApiUrls.MANUAL_SIGN_IN)
                .addJSONObjectBody(jsonObject)
                .addHeaders(BundleAndSharedPreferencesConstants.AUTHORIZATION_HEADER, getAuthorizationHeaderToken())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        eventCreateUpdateCallback.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        eventCreateUpdateCallback.onFailure(anError);
                    }
                });

    }

    public void removeUserFromAttending(String user, int eventId, final EventCreateUpdateCallback eventCreateUpdateCallback){

        JSONObject jsonObject = new JSONObject();

        try{

            jsonObject.put(NetworkConstants.USER, user);
            jsonObject.put(NetworkConstants.EVENT_ID, eventId);
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        AndroidNetworking.post(ApiUrls.REMOVE_USER_FROM_ATTENDING)
                .addJSONObjectBody(jsonObject)
                .addHeaders(BundleAndSharedPreferencesConstants.AUTHORIZATION_HEADER, getAuthorizationHeaderToken())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        eventCreateUpdateCallback.onSuccess(response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        eventCreateUpdateCallback.onFailure(anError);
                    }
                });
    }

    // Returns logged in user from shared preferences
    private String getLoggedInUser(){
        SharedPreferences userDetails = context.getSharedPreferences("", MODE_PRIVATE);
        return userDetails.getString(BundleAndSharedPreferencesConstants.USERNAME, "");
    }

    private String getAuthorizationHeaderToken(){
        SharedPreferences userDetails = context.getSharedPreferences("", MODE_PRIVATE);
        return BundleAndSharedPreferencesConstants.JWT + " " + userDetails.getString(BundleAndSharedPreferencesConstants.TOKEN, "");
    }

    private JSONObject eventAsJsonObject(Event event){
        JSONObject create = new JSONObject();

        try{
            create.put(NetworkConstants.ORGANISER, getLoggedInUser());
            create.put(NetworkConstants.EVENT_NAME, event.getEventName());
            create.put(NetworkConstants.LOCATION, event.getLocation());
            create.put(NetworkConstants.START_TIME, event.getStartTime());
            create.put(NetworkConstants.FINISH_TIME, event.getFinishTime());
            create.put(NetworkConstants.SIGN_IN_TIME, event.getSignInTime());
            create.put(NetworkConstants.ATTENDANCE_REQUIRED, Boolean.toString(event.isAttendanceRequired()));

            JSONArray jsonArray = new JSONArray();

            for (String name : event.getAttendees()) {
                jsonArray.put(name);
            }

            create.put(NetworkConstants.ATTENDEES, jsonArray);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return create;
    }

}
