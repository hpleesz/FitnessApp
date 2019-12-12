package hu.bme.aut.fitnessapp.models.user_models.workout_models;

import android.content.Context;
import java.util.ArrayList;

import hu.bme.aut.fitnessapp.controllers.user.workout.WarmUpActivity;
import hu.bme.aut.fitnessapp.models.database_models.LoadWarmUp;

public class WarmUpModel extends StretchWarmUpModel implements LoadWarmUp.WarmUpLoadedListener{

    private String type;
    private boolean lower = true;

    private LoadWarmUp loadWarmUp;

    public WarmUpModel(Context activity, String type) {
        super(activity);
        this.type = type;
    }


    public void loadItems() {
        setType(((WarmUpActivity)getActivity()).getIntentType());
        getType();
        loadWarmUp = new LoadWarmUp(this);
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

    public void removeListeners() {
        if(loadWarmUp != null) loadWarmUp.removeListeners();
    }
}
