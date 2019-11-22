package hu.bme.aut.fitnessapp.Models.UserModels.WorkoutModels;

import android.content.Context;
import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Controllers.User.Workout.WarmUpActivity;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadWarmUp;

public class WarmUpModel extends StretchWarmUpModel implements LoadWarmUp.WarmUpLoadedListener{

    private String type;
    private boolean lower = true;

    public WarmUpModel(Context activity, String type) {
        super(activity);
        this.type = type;
    }


    public void loadItems() {
        setType(((WarmUpActivity)getActivity()).getIntentType());
        getType();
        LoadWarmUp loadWarmUp = new LoadWarmUp(this);
        loadWarmUp.loadWarmUp(lower);
    }

    public void getType() {
        switch (type) {
            case "Cardio 1":
            case "Cardio 2":
            case "Lower body":
                lower = true;
                break;
            case "Upper body":
                lower = false;
                break;
        }
    }

    @Override
    public void onWarmUpLoaded(ArrayList<String> warmUpList) {
        setItems(warmUpList);
        getExerciseListLoadedListener().onExerciseListLoaded();

    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isLower() {
        return lower;
    }

}
