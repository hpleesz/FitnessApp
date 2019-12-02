package hu.bme.aut.fitnessapp.Models.DatabaseModels;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.WorkoutDetails;

public class LoadWorkoutDetails extends DatabaseConnection {

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
                boolean in_progress = dataSnapshot.child("In_Progress").getValue(Boolean.class);

                ArrayList<Integer> exercises = new ArrayList<>();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.child("Exercises").getChildren()) {
                    int exercise_id = dataSnapshot1.getValue(Integer.class);
                    exercises.add(exercise_id);
                }
                WorkoutDetails workoutDetails = new WorkoutDetails(type, exercises, in_progress);

                listLoadedListener.onWorkoutDetailsLoaded(workoutDetails);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }

        };
        getDatabaseReference().child("Workout_Details").child(getUserId()).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener

    }

    public void setProgress(boolean progress) {
        getDatabaseReference().child("Workout_Details").child(getUserId()).child("In_Progress").setValue(progress);
    }

    public void setType(String type) {
        getDatabaseReference().child("Workout_Details").child(getUserId()).child("Type").setValue(type);
    }

    public void addNewExercise(String id, int exercise) {
        getDatabaseReference().child("Workout_Details").child(getUserId()).child("Exercises").child(id).setValue(exercise);
    }

    public void removeExercises() {
        getDatabaseReference().child("Workout_Details").child(getUserId()).child("Exercises").removeValue();
    }

    public void removeListeners() {
        if(eventListener != null) getDatabaseReference().child("Workout_Details").child(getUserId()).addValueEventListener(eventListener);
    }
}
