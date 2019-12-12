package hu.bme.aut.fitnessapp.models.user_models.workout_models;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public abstract class StretchWarmUpModel extends VideoModel {

    private List<String> items;
    private int idx = 0;

    private Context activity;

    private StretchWarmUpModel.ExerciseListener exerciseListener;
    private StretchWarmUpModel.ExercisesEndListener exercisesEndListener;
    private StretchWarmUpModel.ExerciseListLoadedListener exerciseListLoadedListener;

    public interface ExerciseListener {
        void onExerciseLoaded();
    }

    public interface ExercisesEndListener {
        void onExercisesFinished();
    }

    public interface ExerciseListLoadedListener {
        void onExerciseListLoaded();
    }


    public StretchWarmUpModel(Object object) {
        super(object);
        exerciseListener = (StretchWarmUpModel.ExerciseListener)object;
        exercisesEndListener = (StretchWarmUpModel.ExercisesEndListener)object;
        exerciseListLoadedListener = (StretchWarmUpModel.ExerciseListLoadedListener)object;
        this.activity = (AppCompatActivity)object;
    }

    @Override
    public void navigateRight() {
        if (items.size() > idx + 1) {
            idx = idx + 1;
            exerciseListener.onExerciseLoaded();
        } else {
            exercisesEndListener.onExercisesFinished();
        }
    }

    @Override
    public void navigateLeft() {
        if (idx > 0) {
            idx = idx -1;
            exerciseListener.onExerciseLoaded();
        } else {
            exercisesEndListener.onExercisesFinished();
        }
    }

    public abstract void loadItems();

    public abstract void removeListeners();

    public ExerciseListLoadedListener getExerciseListLoadedListener() {
        return exerciseListLoadedListener;
    }

    public String getExerciseItem() {
        return items.get(idx);
    }

    public List<String> getItems() {
        return items;
    }

    public int getIdx() {
        return idx;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public Context getActivity() {
        return activity;
    }

}
