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
import com.example.user.attendr.activities.CreateUpdateViewUserGroupActivity;
import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.constants.DbConstants;
import com.example.user.attendr.models.UserGroup;

import java.util.List;

/**
 * Created by Eamon on 17/02/2018.
 *
 * RecyclerView adapter for viewing user defined groups of members
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
        TextView tvNumberUsers;
        TextView tvDescription;

        //Gets assigned to each card view
        UserGroup currentGroup;

        public GroupsViewHolder(View view) {
            super(view);

            tvGroupName = view.findViewById(R.id.tvGroupName);
            tvNumberUsers = view.findViewById(R.id.tvNumberUsers);
            tvDescription = view.findViewById(R.id.tvDescription);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(view.getContext(), CreateUpdateViewUserGroupActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(BundleAndSharedPreferencesConstants.CREATE_OR_UPDATE, BundleAndSharedPreferencesConstants.UPDATE);
                    bundle.putInt(DbConstants.GROUP_KEY_ROW_ID, currentGroup.getId());
                    intent.putExtras(bundle);
                    view.getContext().startActivity(intent);
                }
            });
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
        holder.tvNumberUsers.setText(Integer.toString(groups.get(position).getUsers().size()));
        holder.tvDescription.setText(groups.get(position).getDescription() );

        //Set the current event object for the card on click listener
        holder.currentGroup = groups.get(position);
    }


    @Override
    public int getItemCount() {
        return this.groups.size();
    }
}
