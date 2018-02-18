package com.example.user.attendr.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.user.attendr.R;
import com.example.user.attendr.adapters.EventsViewAdapter;
import com.example.user.attendr.adapters.GroupsViewAdapter;
import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.models.Event;
import com.example.user.attendr.models.UserGroup;

import java.util.ArrayList;

public class ViewGroupsActivity extends AppCompatActivity {

    private static final String TAG = ViewGroupsActivity.class.getSimpleName();

    Bundle bundle;
    RecyclerView recyclerView;
    DBManager db;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_groups);

        db = new DBManager(this).open();
        recyclerView = findViewById(R.id.recyclerView);


        setAdapterWithData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdapterWithData();
    }

    public void setAdapterWithData(){

        ArrayList<UserGroup> groups = db.getGroups();

        Log.d(TAG, "---------");
        for(UserGroup group : groups){
            Log.d(TAG, group.toString());
        }
        Log.d(TAG, "---------");

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        GroupsViewAdapter groupsViewAdapter = new GroupsViewAdapter(getApplicationContext(), groups);

        recyclerView.setAdapter(groupsViewAdapter);
        groupsViewAdapter.notifyDataSetChanged();
    }

}
