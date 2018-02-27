package com.example.user.attendr.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.example.user.attendr.R;
import com.example.user.attendr.activities.UserStatsActivity;
import com.example.user.attendr.callbacks.EventCreateUpdateCallback;
import com.example.user.attendr.constants.DbConstants;
import com.example.user.attendr.models.Event;
import com.example.user.attendr.network.NetworkCheck;
import com.example.user.attendr.network.NetworkInterface;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Eamon on 15/02/2018.
 *
 * Array adapter for viewing attendees in a list of the ViewEventActivity
 */

public class AttendeesViewAdapter extends RecyclerView.Adapter<AttendeesViewAdapter.AttendeesViewHolder>{

    private static final String TAG = AttendeesViewAdapter.class.getSimpleName();

    private Context context;
    private List<String> attendees;
    private List<String> attending;
    private Event currentEvent;

    public AttendeesViewAdapter(Context context, Event event) {

        ArrayList<String> listOfAttending = sortAttendees(event.getAttendees(), event.getAttending());
        this.context = context;
        this.attendees = listOfAttending;
        this.attending = event.getAttending();
        this.currentEvent = event;
    }

    public class AttendeesViewHolder extends RecyclerView.ViewHolder {
        TextView tvAttendee;

        //Gets assigned to each view
//        Event currentEvent;

        public AttendeesViewHolder(View view) {
            super(view);
            tvAttendee = view.findViewById(R.id.tvAttendee);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    Toast.makeText(view.getContext(), tvAttendee.getText(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), UserStatsActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString(DbConstants.GROUP_KEY_ROW_USERNAME, tvAttendee.getText().toString());
                    intent.putExtras(extras);
                    view.getContext().startActivity(intent);
                }


            });

            // Delete from list
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {

                    Log.d(TAG, "Current Event");
                    Log.d(TAG, currentEvent.toString());

                    NetworkInterface.getInstance(view.getContext()).removeUserFromAttendees(currentEvent, tvAttendee.getText().toString(),
                            new EventCreateUpdateCallback() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.user_removed_from_event), Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onFailure(ANError anError) {
                                    Toast.makeText(view.getContext(), view.getContext().getString(R.string.error_removing_user_from_event), Toast.LENGTH_SHORT).show();

                                }
                            });
                    return true;
                }
            });

        }
    }



    // Sort attendees by all attending alphabetically, then all other attendees
    // then by all other attendees alphabetically
    private ArrayList<String> sortAttendees(List<String> attendees, List<String> attending){
        ArrayList<String> result = new ArrayList<>();
        List<String> newAttendees = new ArrayList<>();

        if(attending != null){
            Collections.sort(attending);

            for(String name: attendees){
                if(!attending.contains(name)){
                    newAttendees.add(name);
                }
            }

            Collections.sort(newAttendees);

            result.addAll(attending);
            result.addAll(newAttendees);
        }
        else{
            Collections.sort(attendees);
            result.addAll(attendees);
        }

        return result;
    }

    @Override
    public AttendeesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendee_item, parent, false);

        return new AttendeesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AttendeesViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called: " + attendees.get(position));

        if(isAttending(attendees.get(position))){

            Log.d(TAG, "Setting " + attendees.get(position) + " to Attending");
            //TODO: change background colour whenever it comes up
            holder.tvAttendee.setBackgroundColor(Color.RED);
            holder.tvAttendee.setText(this.attendees.get(position));
        }
        else{
            Log.d(TAG, "Setting " + attendees.get(position) + " to NOT Attending");
            holder.tvAttendee.setText(this.attendees.get(position));

        }
    }

    @Override
    public int getItemCount() {
        return this.attendees.size();
    }

    // Checks to see if attendee is in attendee list
    private boolean isAttending(String attendee){

        boolean result = false;

        if(attending != null){
            for(String name : attending){

                if(name.trim().toLowerCase().equals(attendee.trim().toLowerCase())){
                    result = true;
                }
            }
        }

        return result;
    }
}
