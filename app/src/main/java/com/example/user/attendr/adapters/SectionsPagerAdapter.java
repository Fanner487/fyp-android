package com.example.user.attendr.adapters;

/**
 * Created by Eamon on 13/02/2018.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.user.attendr.fragments.ViewEventsFragment;


public class SectionsPagerAdapter extends FragmentPagerAdapter {

//    private final String TAG = SectionsPagerAdapter.class.getSimpleName();

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        Fragment fragment = null;

        switch (position){
            case 0:
                fragment = new ViewEventsFragment();
                break;
            case 1:
                fragment = new ViewEventsFragment();
                break;
            case 2:
                fragment = new ViewEventsFragment();
                break;
        }

//        if(position == 0){
//            fragment = new PickerFragment();
//        }
//        else if(position == 1){
//            fragment = new DrawerFragment();
//        }

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