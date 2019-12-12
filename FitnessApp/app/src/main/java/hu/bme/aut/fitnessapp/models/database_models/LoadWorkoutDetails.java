package hu.bme.aut.fitnessapp.models.database_models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.entities.WorkoutDetails;

public class LoadWorkoutDetails extends DatabaseConnection {

    private static final String WORKOUT_DETAILS = "Workout_Details";

    public interface WorkoutDetailsLoadedListener {
        void onWorkoutDetailsLoaded(WorkoutDetails workoutDetails);
    }

    private LoadWorkoutDetails.WorkoutDetailsLoadedListener listLoadedListener;

    public LoadWorkoutDetails() {
        super();
    }

    public void setListLoadedListener(Object object) {
        listLoadedListener = (LoadWorkoutDetails.WorkoutDetailsLoadedListener)object;

    }

    private ValueEventListener eventListener;

    public void loadWorkoutDetails() {

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String type = dataSnapshot.child("Type").getValue(String.class);
                boolean inProgress = dataSnapshot.child("In_Progress").getValue(Boolean.class);

                ArrayList<Integer> exercises = new ArrayList<>();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.child("Exercises").getChildren()) {
                    int exerciseId = dataSnapshot1.getValue(Integer.class);
                    exercises.add(exerciseId);
                }
                WorkoutDetails workoutDetails = new WorkoutDetails(type, exercises, inProgress);

                listLoadedListener.onWorkoutDetailsLoaded(workoutDetails);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error(DB_ERROR, databaseError.toException());
            }

        };
        getDatabaseReference().child(WORKOUT_DETAILS).child(getUserId()).addValueEventListener(eventListener);

    }

    public void setProgress(boolean progress) {
        getDatabaseReference().child(WORKOUT_DETAILS).child(getUserId()).child("In_Progress").setValue(progress);
    }

    public void setType(String type) {
        getDatabaseReference().child(WORKOUT_DETAILS).child(getUserId()).child("Type").setValue(type);
    }

    public void addNewExercise(String id, int exercise) {
        getDatabaseReference().child(WORKOUT_DETAILS).child(getUserId()).child("Exercises").child(id).setValue(exercise);
    }

    public void removeExercises() {
        getDatabaseReference().child(WORKOUT_DETAILS).child(getUserId()).child("Exercises").removeValue();
    }

    public void removeListeners() {
        if(eventListener != null) getDatabaseReference().child(WORKOUT_DETAILS).child(getUserId()).removeEventListener(eventListener);
    }
}
