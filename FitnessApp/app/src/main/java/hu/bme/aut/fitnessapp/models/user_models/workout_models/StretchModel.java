package hu.bme.aut.fitnessapp.models.user_models.workout_models;

import android.content.Context;
import java.util.ArrayList;

import hu.bme.aut.fitnessapp.models.database_models.LoadStretch;

public class StretchModel extends StretchWarmUpModel implements LoadStretch.StretchLoadedListener {

    public StretchModel(Context activity) {
        super(activity);
    }

    private LoadStretch loadStretch;

    public void loadItems() {
        loadStretch = new LoadStretch(this);
        loadStretch.loadStretch();
    }


    @Override
    public void onStretchLoaded(ArrayList<String> stretchList) {

        setItems(stretchList);
        getExerciseListLoadedListener().onExerciseListLoaded();
    }

    public void removeListeners() {
        if(loadStretch != null) loadStretch.removeListeners();
    }
}
