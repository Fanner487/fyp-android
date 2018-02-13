package com.example.user.attendr.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.attendr.R;
import com.example.user.attendr.models.Event;

import java.util.List;

/**
 * Created by Eamon on 12/02/2018.
 */

public class EventsViewAdapter extends RecyclerView.Adapter<EventsViewAdapter.ViewHolder>{

    private final String TAG = EventsViewAdapter.class.getSimpleName();

    private List<Event> eventList;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName;
        TextView tvLocation;
        TextView tvStartTime;
        TextView tvFinishTime;

        //Gets assigned to each card view
        Event currentEvent;

        public ViewHolder(View view) {
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
                }
            });
        }
    }
    public EventsViewAdapter(List<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_card, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Log.d(TAG, "onBindViewHolder called");
        holder.tvEventName.setText(eventList.get(position).getEventName());
        holder.tvLocation.setText(eventList.get(position).getLocation());
        holder.tvStartTime.setText(eventList.get(position).getStartTime());
        holder.tvFinishTime.setText(eventList.get(position).getFinishTime());

        //Set the current event object for the card on click listener
        holder.currentEvent = eventList.get(position);

    }

    @Override
    public int getItemCount() {
        return this.eventList.size();
    }
}
