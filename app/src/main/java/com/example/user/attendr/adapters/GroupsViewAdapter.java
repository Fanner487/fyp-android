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
import com.example.user.attendr.activities.CreateUserGroupActivity;
import com.example.user.attendr.activities.ViewEventActivity;
import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.constants.DbConstants;
import com.example.user.attendr.models.Event;
import com.example.user.attendr.models.UserGroup;

import java.util.List;

/**
 * Created by Eamon on 17/02/2018.
 */

public class GroupsViewAdapter extends RecyclerView.Adapter<GroupsViewAdapter.GroupsViewHolder>{

    private static final String TAG = GroupsViewAdapter.class.getSimpleName();

    private Context context;

    public GroupsViewAdapter(Context context, List<UserGroup> groups) {
        this.context = context;
        this.groups = groups;
    }

    private List<UserGroup> groups;

    public class GroupsViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupName;

        //Gets assigned to each card view
        UserGroup currentGroup;

        public GroupsViewHolder(View view) {
            super(view);

            tvGroupName = view.findViewById(R.id.tvGroupName);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(view.getContext(), currentGroup.getGroupName(), Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "-----");
                    for(UserGroup group: groups){
                        Log.d(TAG, group.toString());
                    }
                    Log.d(TAG, "-----");

                    Intent intent = new Intent(view.getContext(), CreateUserGroupActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(BundleAndSharedPreferencesConstants.CREATE_OR_UPDATE, BundleAndSharedPreferencesConstants.UPDATE);
                    bundle.putInt(DbConstants.GROUP_KEY_ROW_ID, currentGroup.getId());
                    intent.putExtras(bundle);
                    view.getContext().startActivity(intent);
                }
            });

//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(view.getContext(), currentEvent.getEventName(), Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, currentEvent.toString());
//
//                    Intent intent = new Intent(view.getContext(), ViewEventActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putInt(DbConstants.EVENT_KEY_EVENT_ID, currentEvent.getEventId());
//                    intent.putExtras(bundle);
//                    view.getContext().startActivity(intent);
//                }
//            });


        }
    }

    public GroupsViewAdapter() {
        super();
    }

    @Override
    public GroupsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_item, parent, false);

        return new GroupsViewAdapter.GroupsViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(GroupsViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called");
        holder.tvGroupName.setText(groups.get(position).getGroupName());

        //Set the current event object for the card on click listener
        holder.currentGroup = groups.get(position);
    }


    @Override
    public int getItemCount() {
        return this.groups.size();
    }
}
