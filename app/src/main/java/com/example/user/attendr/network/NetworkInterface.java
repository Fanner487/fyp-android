package com.example.user.attendr.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.example.user.attendr.callbacks.LoginCallback;
import com.example.user.attendr.callbacks.RegisterCallback;
import com.example.user.attendr.models.Event;
import com.jacksonandroidnetworking.JacksonParserFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

/**
 * Created by Eamon on 06/02/2018.
 */

public class NetworkInterface {

    final String TAG = NetworkInterface.class.getSimpleName();
    private static NetworkInterface instance;
    private static Context context;

    private static List<Event> returnList = new ArrayList<>();

    private NetworkInterface(Context context) {

        NetworkInterface.context = context;

        AndroidNetworking.initialize(context.getApplicationContext());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
    }

    public static synchronized NetworkInterface getInstance(Context context){

        if(instance == null){
            instance = new NetworkInterface(context);
        }

        return instance;
    }

//    RequestQueueInstance.getInstance(this).addToRequestQueue(loginRequest);


    public List<Event> getOrganisedEvents(){

        SharedPreferences userDetails = context.getSharedPreferences("", Context.MODE_PRIVATE);
        String sharedUsername = userDetails.getString("username", "");
        Log.d(TAG, "Shared username: "+ sharedUsername);


        AndroidNetworking.get("http://46.101.13.145:8000/api/profile/{username}/{type}/{time}/")
                .addPathParameter("username", sharedUsername)
                .addPathParameter("type", "organising")
                .addPathParameter("time", "all")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsObjectList(Event.class, new ParsedRequestListener<List<Event>>() {
                    @Override
                    public void onResponse(List<Event> events) {
                        // do anything with response
                        Log.d(TAG, "Events size : " + events.size());

                        for(Event event : events){
                            Log.d(TAG, event.toString());
                            Log.d(TAG, event.getFormattedStartTime().toString());
                            Log.d(TAG, event.getFormattedFinishTime().toString());
                            Log.d(TAG, event.getFormattedSignInTime().toString());

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


    public void login(String username, String password, final LoginCallback callback){
        AndroidNetworking.post("http://46.101.13.145:8000/api/login/")
                .addBodyParameter("username", username)
                .addBodyParameter("password", password)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {

                        if(response.code() == 200){
                            callback.onSuccess();
                        }
                        else{
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
                         String passwordConfirm, String firstName, String lastName, final RegisterCallback callback){

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

                        callback.onSuccess();
                    }

                    @Override
                    public void onError(ANError anError) {
                        callback.onFailure(anError.getErrorBody().toString());
                    }
                });
//                .getAsOkHttpResponse(new OkHttpResponseListener() {
//                    @Override
//                    public void onResponse(Response response) {
//
//                        if(response.code() == 201){
//                            callback.onSuccess();
//                        }
//                        else{
//
//                            Log.d(TAG, response.body().toString());
//                            callback.onFailure(response.body().toString());
//                        }
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        callback.onFailure(anError.getErrorBody().toString());
//                    }
//                });

    }


    private void setEvents(List<Event> events){
        this.returnList = events;
    }

    public List<Event> getEvents(){
        return this.returnList;
    }
}
