package com.example.user.attendr.adapters;

import android.content.Context;
import android.content.Intent;
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

import com.example.user.attendr.R;
import com.example.user.attendr.activities.ViewEventActivity;
import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.constants.DbConstants;
import com.example.user.attendr.credentials.CredentialManager;
import com.example.user.attendr.enums.EventType;
import com.example.user.attendr.models.Event;

import java.util.List;

/**
 * Created by Eamon on 12/02/2018.
 */

public class EventsViewAdapter extends RecyclerView.Adapter<EventsViewAdapter.EventsViewHolder> {

    private final String TAG = EventsViewAdapter.class.getSimpleName();

    private List<Event> eventList;
    private Context context;
    private Bundle bundle;


    public class EventsViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName;
        TextView tvLocation;
        TextView tvStartTime;
        TextView tvFinishTime;
        TextView tvSignInTime;
        TextView tvNumberAttending;
        TextView tvNumberNotAttending;
        TextView tvTotalAttendees;
        ImageView ivAttending;
        ImageView ivNotAttending;
        ImageView ivAttendanceRequired;

        //Gets assigned to each card view
        Event currentEvent;

        public EventsViewHolder(View view) {
            super(view);
            tvEventName = view.findViewById(R.id.tvEventName);
            tvLocation = view.findViewById(R.id.tvLocation);
            tvStartTime = view.findViewById(R.id.tvStartTime);
            tvFinishTime = view.findViewById(R.id.tvFinishTime);
            tvSignInTime = view.findViewById(R.id.tvSignInTime);
            tvNumberAttending = view.findViewById(R.id.tvNumberAttending);
            tvNumberNotAttending = view.findViewById(R.id.tvNumberNotAttending);
            tvTotalAttendees = view.findViewById(R.id.tvTotalAttendees);
            ivAttending = view.findViewById(R.id.ivAttending);
            ivNotAttending = view.findViewById(R.id.ivNotAttending);
            ivAttendanceRequired = view.findViewById(R.id.ivAttendanceRequired);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(view.getContext(), ViewEventActivity.class);
                    Bundle extras = new Bundle();
                    extras.putInt(DbConstants.EVENT_KEY_EVENT_ID, currentEvent.getEventId());
                    extras.putSerializable(BundleAndSharedPreferencesConstants.EVENT_TYPE, currentEvent.getEventType(context));
                    extras.putSerializable(BundleAndSharedPreferencesConstants.TIME_TYPE, bundle.getSerializable(BundleAndSharedPreferencesConstants.TIME_TYPE));
                    intent.putExtras(extras);

                    view.getContext().startActivity(intent);

                }
            });
        }

    }

    public EventsViewAdapter(Context context, List<Event> eventList, Bundle bundle) {
        this.eventList = eventList;
        this.context = context;
        this.bundle = bundle;
    }

    @Override
    public EventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_card, parent, false);

        return new EventsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EventsViewHolder holder, int position) {

        // Populate the text view fields
        Log.d(TAG, "onBindViewHolder called");
        holder.tvEventName.setText(eventList.get(position).getEventName());
        holder.tvLocation.setText(eventList.get(position).getLocation());
        holder.tvStartTime.setText(Event.parseDateToDisplayTime(eventList.get(position).getStartTime()));
        holder.tvFinishTime.setText(Event.parseDateToDisplayTime(eventList.get(position).getFinishTime()));
        holder.tvSignInTime.setText(Event.parseDateToDisplayTime(eventList.get(position).getSignInTime()));

        holder.tvTotalAttendees.setText(String.valueOf(eventList.get(position).getAttendees().size()));
        holder.tvNumberAttending.setText(String.valueOf(eventList.get(position).getAttending().size()));
        holder.tvNumberNotAttending.setText(String.valueOf(eventList.get(position).getAttendees().size() - eventList.get(position).getAttending().size()));

        holder.ivAttending.setColorFilter(ContextCompat.getColor(context, R.color.attendee_green));
        holder.ivNotAttending.setColorFilter(ContextCompat.getColor(context, R.color.attendee_red));

        // set attendance required image to check or cross with colour
        if(eventList.get(position).isAttendanceRequired()){
            holder.ivAttendanceRequired.setImageResource(R.drawable.icon_check);
            holder.ivAttendanceRequired.setColorFilter(ContextCompat.getColor(context, R.color.attendee_green));
        }
        else{
            holder.ivAttendanceRequired.setImageResource(R.drawable.icon_close);
            holder.ivAttendanceRequired.setColorFilter(ContextCompat.getColor(context, R.color.attendee_red));

        }

        //Set the current event object for the card on click listener
        holder.currentEvent = eventList.get(position);

    }

    @Override
    public int getItemCount() {
        return this.eventList.size();
    }


}
