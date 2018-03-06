package com.example.user.attendr.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.example.user.attendr.R;
import com.example.user.attendr.activities.UserStatsActivity;
import com.example.user.attendr.callbacks.EventApiCallback;
import com.example.user.attendr.callbacks.EventCreateUpdateCallback;
import com.example.user.attendr.constants.DbConstants;
import com.example.user.attendr.enums.EventType;
import com.example.user.attendr.interfaces.ListenerInterface;
import com.example.user.attendr.models.Event;
import com.example.user.attendr.network.NetworkCheck;
import com.example.user.attendr.network.NetworkInterface;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Eamon on 15/02/2018.
 * <p>
 * Array adapter for viewing attendees in a list of the ViewEventActivity
 */

public class AttendeesViewAdapter extends RecyclerView.Adapter<AttendeesViewAdapter.AttendeesViewHolder>{

    private static final String TAG = AttendeesViewAdapter.class.getSimpleName();

    private Context context;
    private List<String> attendees;
    private List<String> attending;
    private Event currentEvent;
    private AlertDialog choiceDialog;
    private EventType eventType;

    public AttendeesViewAdapter(Context context, Event event, EventType eventType) {

        ArrayList<String> listOfAttending = sortAttendees(event.getAttendees(), event.getAttending());
        this.context = context;
        this.attendees = listOfAttending;
        this.attending = event.getAttending();
        this.currentEvent = event;
        this.eventType = eventType;

    }

    public class AttendeesViewHolder extends RecyclerView.ViewHolder {
        TextView tvAttendee;
        ImageView imageView;

        //Gets assigned to each view
//        Event currentEvent;

        public AttendeesViewHolder(View view) {
            super(view);
            tvAttendee = view.findViewById(R.id.tvAttendee);
            imageView = view.findViewById(R.id.imageView);

            /**
             * Creates alert dialog for organiser to choose between
             */
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {

                    if(eventType == EventType.ORGANISE){
                        organiseEventListener(view);
                    }
                    else if(eventType == EventType.ATTEND){
                        attendingEventListener(view);
                    }

//                    final String[] choices = context.getResources().getStringArray(R.array.user_options);
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setTitle(tvAttendee.getText().toString());
//                    builder.setSingleChoiceItems(choices, -1, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            switch (i) {
//                                case 0:
//                                    viewUserStatsActivity(tvAttendee.getText().toString());
//                                    break;
//
//                                case 1:
//                                    removeUserFromEvent(currentEvent, tvAttendee.getText().toString(), view);
//                                    break;
//
//                                case 2:
//                                    manualSignInUser(currentEvent.getEventId(), tvAttendee.getText().toString(), view);
//                                    break;
//
//                                case 3:
//                                    removeUserFromAttending(currentEvent.getEventId(), tvAttendee.getText().toString(), view);
//                                    break;
//                            }
//
//                            choiceDialog.dismiss();
//                        }
//
//
//                    });
//
//                    choiceDialog = builder.create();
//                    choiceDialog.show();

                    return true;
                }
            });

        }

        boolean attendingEventListener(View view){
            final String[] choices = context.getResources().getStringArray(R.array.user_options_attending);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(tvAttendee.getText().toString());
            builder.setSingleChoiceItems(choices, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case 0:
                            viewUserStatsActivity(tvAttendee.getText().toString());
                            break;
                    }

                    choiceDialog.dismiss();
                }


            });

            choiceDialog = builder.create();
            choiceDialog.show();

            return true;
        }

        boolean organiseEventListener(final View view){
            final String[] choices = context.getResources().getStringArray(R.array.user_options);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(tvAttendee.getText().toString());
            builder.setSingleChoiceItems(choices, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case 0:
                            viewUserStatsActivity(tvAttendee.getText().toString());
                            break;

                        case 1:
                            removeUserFromEvent(currentEvent, tvAttendee.getText().toString(), view);
                            break;

                        case 2:
                            manualSignInUser(currentEvent.getEventId(), tvAttendee.getText().toString(), view);
                            break;

                        case 3:
                            removeUserFromAttending(currentEvent.getEventId(), tvAttendee.getText().toString(), view);
                            break;
                    }

                    choiceDialog.dismiss();
                }


            });

            choiceDialog = builder.create();
            choiceDialog.show();

            return true;
        }

    }


    // Sort attendees by all attending alphabetically, then all other attendees
    // then by all other attendees alphabetically
    private ArrayList<String> sortAttendees(List<String> attendees, List<String> attending) {
        ArrayList<String> result = new ArrayList<>();
        List<String> newAttendees = new ArrayList<>();

        if (attending != null) {
            Collections.sort(attending);

            for (String name : attendees) {
                if (!attending.contains(name)) {
                    newAttendees.add(name);
                }
            }

            Collections.sort(newAttendees);

            result.addAll(attending);
            result.addAll(newAttendees);
        } else {
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

        if (isAttending(attendees.get(position))) {

            Log.d(TAG, "Setting " + attendees.get(position) + " to Attending");
            holder.imageView.setColorFilter(ContextCompat.getColor(context, R.color.attendee_green));
            holder.tvAttendee.setText(this.attendees.get(position));
        } else {
            Log.d(TAG, "Setting " + attendees.get(position) + " to NOT Attending");
            holder.imageView.setColorFilter(ContextCompat.getColor(context, R.color.attendee_red));
            holder.tvAttendee.setText(this.attendees.get(position));

        }
    }

    @Override
    public int getItemCount() {
        return this.attendees.size();
    }

    // Checks to see if attendee is in attendee list
    private boolean isAttending(String attendee) {

        boolean result = false;

        if (attending != null) {
            for (String name : attending) {

                if (name.trim().toLowerCase().equals(attendee.trim().toLowerCase())) {
                    result = true;
                }
            }
        }

        return result;
    }

    private void viewUserStatsActivity(String user) {

        Intent intent = new Intent(context, UserStatsActivity.class);
        Bundle extras = new Bundle();
        extras.putString(DbConstants.GROUP_KEY_ROW_USERNAME, user);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    private void removeUserFromAttending(int eventId, String user, View view) {

        if(NetworkCheck.alertIfNotConnectedToInternet(context, view)){
            NetworkInterface.getInstance(context).removeUserFromAttending(user, eventId, new EventCreateUpdateCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    Toast.makeText(context, context.getString(R.string.removed_user_from_attending), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(ANError anError) {
                    Toast.makeText(context, context.getString(R.string.error_removing_user_from_attending), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void removeUserFromEvent(Event event, String user, View view) {

        if(NetworkCheck.alertIfNotConnectedToInternet(context, view)){
            NetworkInterface.getInstance(context).removeUserFromAttendees(event, user, new EventCreateUpdateCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    Toast.makeText(context, context.getString(R.string.user_removed_from_event), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(ANError anError) {
                    Toast.makeText(context, context.getString(R.string.error_removing_user_from_event), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void manualSignInUser(int eventId, String user, View view) {

        if(NetworkCheck.alertIfNotConnectedToInternet(context, view)){

            NetworkInterface.getInstance(context).manualSignInUser(user, eventId,
                    new EventCreateUpdateCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.d(TAG, response.toString());
                            Toast.makeText(context, context.getString(R.string.data_updated), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(ANError anError) {
                            Log.d(TAG, anError.toString());
                            Toast.makeText(context, context.getString(R.string.error_removing_user_from_event), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }


}
