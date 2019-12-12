package hu.bme.aut.fitnessapp.controllers.adapters;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import hu.bme.aut.fitnessapp.controllers.user.measurements.MeasurementsGraphFragment;

public class GraphPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_PAGES = 12;
    private static final String BODY_PART = "body_part";

    public GraphPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        MeasurementsGraphFragment fragment = new MeasurementsGraphFragment();
        switch (position) {
            case 0:
                bundle.putString(BODY_PART, "Shoulders");
                fragment.setArguments(bundle);
                return fragment;
            case 1:
                bundle.putString(BODY_PART, "Chest");
                fragment.setArguments(bundle);
                return fragment;
            case 2:
                bundle.putString(BODY_PART, "Waist");
                fragment.setArguments(bundle);
                return fragment;
            case 3:
                bundle.putString(BODY_PART, "Hips");
                fragment.setArguments(bundle);
                return fragment;
            case 4:
                bundle.putString(BODY_PART, "Right_Upper_Arm");
                fragment.setArguments(bundle);
                return fragment;
            case 5:
                bundle.putString(BODY_PART, "Left_Upper_Arm");
                fragment.setArguments(bundle);
                return fragment;
            case 6:
                bundle.putString(BODY_PART, "Right_Forearm");
                fragment.setArguments(bundle);
                return fragment;
            case 7:
                bundle.putString(BODY_PART, "Left_Forearm");
                fragment.setArguments(bundle);
                return fragment;
            case 8:
                bundle.putString(BODY_PART, "Right_Thigh");
                fragment.setArguments(bundle);
                return fragment;
            case 9:
                bundle.putString(BODY_PART, "Left_Thigh");
                fragment.setArguments(bundle);
                return fragment;
            case 10:
                bundle.putString(BODY_PART, "Right_Calf");
                fragment.setArguments(bundle);
                return fragment;
            case 11:
                bundle.putString(BODY_PART, "Left_Calf");
                fragment.setArguments(bundle);
                return fragment;
            default:
                bundle.putString(BODY_PART, "Shoulders");
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