package hu.bme.aut.fitnessapp.Controllers.User.Workout;

import android.os.Bundle;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Models.UserModels.WorkoutModels.StretchModel;

public class StretchActivity extends StretchWarmUpActivity {

    private ArrayList<String> items;
    private int idx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setModel(new StretchModel(this));

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

}
