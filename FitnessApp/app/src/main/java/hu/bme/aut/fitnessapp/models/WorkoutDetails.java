package hu.bme.aut.fitnessapp.models;

import java.util.ArrayList;

public class WorkoutDetails {
    public String type;
    public ArrayList<Integer> exercises;
    public boolean in_progress;

    public WorkoutDetails() {}

    public WorkoutDetails(String Type, ArrayList<Integer> Exercises, boolean In_Progress) {
        this.type = Type;
        this.exercises = Exercises;
        this.in_progress = In_Progress;
    }
}
