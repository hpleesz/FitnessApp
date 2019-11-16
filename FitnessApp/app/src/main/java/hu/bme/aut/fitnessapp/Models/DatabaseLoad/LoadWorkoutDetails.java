package hu.bme.aut.fitnessapp.Models.DatabaseLoad;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Exercise;
import hu.bme.aut.fitnessapp.Entities.WorkoutDetails;

public class LoadWorkoutDetails extends DatabaseConnection {

    public interface WorkoutDetailsLoadedListener {
        void onWorkoutDetailsLoaded(WorkoutDetails workoutDetails);
    }

    private LoadWorkoutDetails.WorkoutDetailsLoadedListener listLoadedListener;

    public LoadWorkoutDetails(Object object) {
        super();
        listLoadedListener = (LoadWorkoutDetails.WorkoutDetailsLoadedListener)object;
    }

    public void loadWorkoutDetails() {

        ValueEventListener eventListener = new ValueEventListener() {
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
                // Handle possible errors.
            }

        };
        getDatabaseReference().child("Workout_Details").child(getUserId()).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener

    }

}
