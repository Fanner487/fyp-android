package com.example.user.attendr.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.user.attendr.R;
import com.example.user.attendr.adapters.EventsViewAdapter;
import com.example.user.attendr.callbacks.EventApiCallback;
import com.example.user.attendr.constants.BundleAndSharedPreferencesConstants;
import com.example.user.attendr.database.DBManager;
import com.example.user.attendr.enums.EventType;
import com.example.user.attendr.enums.TimeType;
import com.example.user.attendr.interfaces.ListenerInterface;
import com.example.user.attendr.models.Event;
import com.example.user.attendr.network.NetworkCheck;
import com.example.user.attendr.network.NetworkInterface;

import java.util.ArrayList;

/**
 * Created by Eamon on 13/02/2018.
 *
 * Fragment for viewing events.
 * Bundle specifies if the events are organised/attending events
 * and whether they're in the past/present/future
 */

public class ViewEventsFragment extends Fragment implements ListenerInterface{
    private static final String TAG = ViewEventsFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Bundle bundle;
    RecyclerView recyclerView;
    DBManager db;
    LinearLayoutManager linearLayoutManager;

    SwipeRefreshLayout swipeRefreshLayout;

    private View view;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ViewEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_view_events, container, false);

        db = new DBManager(getContext()).open();
        bundle = getArguments();

        swipeRefreshLayout = view.findViewById(R.id.swipe_container);

        setListeners();

        recyclerView = view.findViewById(R.id.recyclerView);

        setAdapterWithData();

        // Inflate the layout for this fragment
        return view;
    }

    public void setAdapterWithData(){

        ArrayList<Event> events = getEventsWithParameters(bundle);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        EventsViewAdapter eventsViewAdapter = new EventsViewAdapter(getContext(), events, bundle);

        recyclerView.setAdapter(eventsViewAdapter);
        eventsViewAdapter.notifyDataSetChanged();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(NetworkCheck.alertIfNotConnectedToInternet(getContext(), swipeRefreshLayout)){

                    NetworkInterface.getInstance(getContext()).getEventsForUser(new EventApiCallback() {

                        @Override
                        public void onSuccess() {
                            setAdapterWithData();
                            Toast.makeText(view.getContext(), getString(R.string.data_updated), Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onFailure() {
                            setAdapterWithData();
                            Toast.makeText(view.getContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    swipeRefreshLayout.setRefreshing(false);

                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    // Fetch events from DB according bundle parameters passed into fragment
    // Organised/atteding events and in the past/present/future are returned
    private ArrayList<Event> getEventsWithParameters(Bundle bundle){

        return db.getEvents((EventType) bundle.getSerializable(BundleAndSharedPreferencesConstants.EVENT_TYPE),
                (TimeType)bundle.getSerializable(BundleAndSharedPreferencesConstants.TIME_TYPE));
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }



}
