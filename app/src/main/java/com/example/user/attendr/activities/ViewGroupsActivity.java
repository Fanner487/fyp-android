package com.example.user.attendr.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.user.attendr.R;
import com.example.user.attendr.adapters.EventsViewAdapter;
import com.example.user.attendr.adapters.GroupsViewAdapter;
import com.example.user.attendr.callbacks.RegisterCallback;
import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.models.Event;
import com.example.user.attendr.models.UserGroup;
import com.example.user.attendr.network.NetworkInterface;

import java.util.ArrayList;

public class ViewGroupsActivity extends AppCompatActivity {

    private static final String TAG = ViewGroupsActivity.class.getSimpleName();

    Bundle bundle;
    RecyclerView recyclerView;
    DBManager db;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        NetworkInterface.getInstance(this).testToken(new RegisterCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ViewGroupsActivity.this, "Success token", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String response) {
                Toast.makeText(ViewGroupsActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        });
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
