package hu.bme.aut.fitnessapp.Controllers.User.Workout;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Models.User.Workout.StretchModel;

public class StretchActivity extends StretchWarmUpActivity {

    private ArrayList<String> items;
    private VideoView videoView;
    private TextView titleTextView;
    private int idx = 0;

    private StretchModel stretchModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setLayoutElements();
        //setFloatingActionButtons();

        //loadItems();

        setModel(new StretchModel(this));

    }

    public StretchModel getStretchModel() {
        return stretchModel;
    }

    public void setStretchModel(StretchModel stretchModel) {
        this.stretchModel = stretchModel;
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
