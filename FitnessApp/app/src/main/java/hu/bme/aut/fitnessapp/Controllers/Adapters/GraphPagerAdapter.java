package hu.bme.aut.fitnessapp.Controllers.Adapters;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import hu.bme.aut.fitnessapp.Controllers.User.Measurements.MeasurementsGraphFragment;

public class GraphPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_PAGES = 12;

    public GraphPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

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
                bundle.putString("body part", "Right_Upper_Arm");
                fragment.setArguments(bundle);
                return fragment;
            case 5:
                bundle.putString("body part", "Left_Upper_Arm");
                fragment.setArguments(bundle);
                return fragment;
            case 6:
                bundle.putString("body part", "Right_Forearm");
                fragment.setArguments(bundle);
                return fragment;
            case 7:
                bundle.putString("body part", "Left_Forearm");
                fragment.setArguments(bundle);
                return fragment;
            case 8:
                bundle.putString("body part", "Right_Thigh");
                fragment.setArguments(bundle);
                return fragment;
            case 9:
                bundle.putString("body part", "Left_Thigh");
                fragment.setArguments(bundle);
                return fragment;
            case 10:
                bundle.putString("body part", "Right_Calf");
                fragment.setArguments(bundle);
                return fragment;
            case 11:
                bundle.putString("body part", "Left_Calf");
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