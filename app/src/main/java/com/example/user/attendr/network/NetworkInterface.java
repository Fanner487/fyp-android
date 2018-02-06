package com.example.user.attendr.network;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.example.user.attendr.models.Event;
import com.jacksonandroidnetworking.JacksonParserFactory;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eamon on 06/02/2018.
 */

public class NetworkInterface {

    final String TAG = NetworkInterface.class.getSimpleName();
    private static NetworkInterface instance;
    private static Context context;

    public ArrayList<Event> returnList = new ArrayList<Event>();

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


    public ArrayList<Event> getOrganisedEvents(){
        AndroidNetworking.get("http://46.101.13.145:8000/api/profile/{username}/{type}/{time}/")
                .addPathParameter("username", "eamont22")
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

                        returnList = new ArrayList<Event>(events);

                    }

                    @Override
                    public void onError(ANError anError) {
                        // handle error
                        Log.d(TAG, anError.getErrorDetail());
                        Log.d(TAG, anError.getMessage());
                        Log.d(TAG, Integer.toString(anError.getErrorCode()));
                    }
                });

        return returnList;
    }
}
