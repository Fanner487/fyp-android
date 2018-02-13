package com.example.user.attendr.adapters;

/**
 * Created by Eamon on 13/02/2018.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.user.attendr.enums.EventType;
import com.example.user.attendr.enums.TimeType;
import com.example.user.attendr.fragments.ViewEventsFragment;


public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final String TAG = SectionsPagerAdapter.class.getSimpleName();
    EventType eventType;

    public SectionsPagerAdapter(FragmentManager fm, EventType eventType) {
        super(fm);

        this.eventType = eventType;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        Fragment fragment = null;
        Bundle bundle = new Bundle();

        switch (position){
            case 0:
                fragment = new ViewEventsFragment();

                bundle.putSerializable("event_type", this.eventType);
                bundle.putSerializable("time_type", TimeType.PAST);
                fragment.setArguments(bundle);
                break;
            case 1:
                fragment = new ViewEventsFragment();

                bundle.putSerializable("event_type", this.eventType);
                bundle.putSerializable("time_type", TimeType.ONGOING);
                fragment.setArguments(bundle);
                break;
            case 2:
                fragment = new ViewEventsFragment();
                bundle.putSerializable("event_type", this.eventType);
                bundle.putSerializable("time_type", TimeType.FUTURE);
                fragment.setArguments(bundle);
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    //displays title on tabs
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {

            case 0:
                return "PAST";
            case 1:
                return "ONGOING";
            case 2:
                return "FUTURE";
        }
        return null;
    }
}