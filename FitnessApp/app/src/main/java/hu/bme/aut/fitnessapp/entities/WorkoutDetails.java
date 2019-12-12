package hu.bme.aut.fitnessapp.entities;

import java.util.List;

public class WorkoutDetails {

    private String type;
    private List<Integer> exercises;
    private boolean inProgress;

    public WorkoutDetails() {}

    public WorkoutDetails(String type, List<Integer> exercises, boolean inProgress) {
        this.type = type;
        this.exercises = exercises;
        this.inProgress = inProgress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Integer> getExercises() {
        return exercises;
    }

    public void setExercises(List<Integer> exercises) {
        this.exercises = exercises;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

}
