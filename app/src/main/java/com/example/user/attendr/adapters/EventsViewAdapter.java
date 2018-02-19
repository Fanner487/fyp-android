package com.example.user.attendr.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.attendr.R;
import com.example.user.attendr.activities.ViewEventActivity;
import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.constants.DbConstants;
import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.enums.EventType;
import com.example.user.attendr.enums.TimeType;
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

        //Gets assigned to each card view
        Event currentEvent;

        public EventsViewHolder(View view) {
            super(view);
            tvEventName = view.findViewById(R.id.tvEventName);
            tvLocation = view.findViewById(R.id.tvLocation);
            tvStartTime = view.findViewById(R.id.tvStartTime);
            tvFinishTime = view.findViewById(R.id.tvFinishTime);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), currentEvent.getEventName(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, currentEvent.toString());

                    Intent intent = new Intent(view.getContext(), ViewEventActivity.class);

                    Bundle extras = new Bundle();
                    extras.putInt(DbConstants.EVENT_KEY_EVENT_ID, currentEvent.getEventId());
                    extras.putSerializable(BundleAndSharedPreferencesConstants.EVENT_TYPE, bundle.getSerializable(BundleAndSharedPreferencesConstants.EVENT_TYPE));
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

        Log.d(TAG, "onBindViewHolder called");
        holder.tvEventName.setText(eventList.get(position).getEventName());
        holder.tvLocation.setText(eventList.get(position).getLocation());
        holder.tvStartTime.setText(Event.parseDateToDisplayTime(eventList.get(position).getStartTime()));
        holder.tvFinishTime.setText(Event.parseDateToDisplayTime(eventList.get(position).getFinishTime()));

        //Set the current event object for the card on click listener
        holder.currentEvent = eventList.get(position);

    }

    @Override
    public int getItemCount() {
        return this.eventList.size();
    }
}
