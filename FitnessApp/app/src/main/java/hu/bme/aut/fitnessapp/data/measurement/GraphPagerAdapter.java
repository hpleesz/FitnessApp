package hu.bme.aut.fitnessapp.data.measurement;

import android.icu.util.Measure;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import hu.bme.aut.fitnessapp.MeasurementsGraphActivity;
import hu.bme.aut.fitnessapp.fragments.MeasurementsGraphFragment;

public class GraphPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_PAGES = 12;

    public GraphPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        MeasurementsGraphFragment fragment = new MeasurementsGraphFragment();
        switch (position) {
            case 0:
                bundle.putString("body part", "Shoulders");
                fragment.setArguments(bundle);
                return fragment;
            case 1:
                bundle.putString("body part", "Chest");
                fragment.setArguments(bundle);
                return fragment;
            case 2:
                bundle.putString("body part", "Waist");
                fragment.setArguments(bundle);
                return fragment;
            case 3:
                bundle.putString("body part", "Hips");
                fragment.setArguments(bundle);
                return fragment;
            case 4:
                bundle.putString("body part", "Right Upper Arm");
                fragment.setArguments(bundle);
                return fragment;
            case 5:
                bundle.putString("body part", "Left Upper Arm");
                fragment.setArguments(bundle);
                return fragment;
            case 6:
                bundle.putString("body part", "Right Forearm");
                fragment.setArguments(bundle);
                return fragment;
            case 7:
                bundle.putString("body part", "Left Forearm");
                fragment.setArguments(bundle);
                return fragment;
            case 8:
                bundle.putString("body part", "Right Thigh");
                fragment.setArguments(bundle);
                return fragment;
            case 9:
                bundle.putString("body part", "Left Thigh");
                fragment.setArguments(bundle);
                return fragment;
            case 10:
                bundle.putString("body part", "Right Calf");
                fragment.setArguments(bundle);
                return fragment;
            case 11:
                bundle.putString("body part", "Left Calf");
                fragment.setArguments(bundle);
                return fragment;
            default:
                bundle.putString("body part", "Shoulders");
                fragment.setArguments(bundle);
                return fragment;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}