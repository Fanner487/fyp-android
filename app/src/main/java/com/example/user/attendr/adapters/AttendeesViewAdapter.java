package com.example.user.attendr.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.attendr.R;
import com.example.user.attendr.models.Event;

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

    public class AttendeesViewHolder extends RecyclerView.ViewHolder {
        TextView tvAttendee;

        //Gets assigned to each card view
        Event currentEvent;

        public AttendeesViewHolder(View view) {
            super(view);
            tvAttendee = view.findViewById(R.id.tvAttendee);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), tvAttendee.getText(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public AttendeesViewAdapter(Context context, List<String> attendees, List<String> attending) {

        ArrayList<String> listOfAttending = sortAttendees(attendees, attending);
        this.context = context;
        this.attendees = listOfAttending;
        this.attending = attending;
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
        Log.d(TAG, "onBindViewHolder called");

        if(isAttending(attendees.get(position))){

            //TODO: change background colour whenever it comes up
            holder.tvAttendee.setBackgroundColor(Color.RED);
            holder.tvAttendee.setText(attendees.get(position));
        }
        else{
            holder.tvAttendee.setText(attendees.get(position));

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
