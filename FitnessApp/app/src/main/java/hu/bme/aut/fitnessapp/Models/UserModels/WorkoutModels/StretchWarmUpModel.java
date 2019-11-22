package hu.bme.aut.fitnessapp.Models.UserModels.WorkoutModels;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public abstract class StretchWarmUpModel extends VideoModel {

    private ArrayList<String> items;
    private int idx = 0;

    private Context activity;

    public interface ExerciseListener {
        void onExerciseLoaded();
    }

    public interface ExercisesEndListener {
        void onExercisesFinished();
    }

    public interface ExerciseListLoadedListener {
        void onExerciseListLoaded();
    }

    private StretchWarmUpModel.ExerciseListener exerciseListener;
    private StretchWarmUpModel.ExercisesEndListener exercisesEndListener;
    private StretchWarmUpModel.ExerciseListLoadedListener exerciseListLoadedListener;

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

    public ExerciseListLoadedListener getExerciseListLoadedListener() {
        return exerciseListLoadedListener;
    }

    public String getExerciseItem() {
        return items.get(idx);
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public int getIdx() {
        return idx;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public Context getActivity() {
        return activity;
    }

}
