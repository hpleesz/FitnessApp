package hu.bme.aut.fitnessapp.Models.DatabaseModels;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Exercise;

public class LoadExercises extends DatabaseConnection{

    public interface ExercisesLoadedListener {
        void onExercisesLoaded(ArrayList<Exercise> exercises);
    }

    private LoadExercises.ExercisesLoadedListener listLoadedListener;

    public LoadExercises(Object object) {
        super();
        listLoadedListener = (LoadExercises.ExercisesLoadedListener)object;
    }

    public void loadExercises() {

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Exercise> exerciseList = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String id = dataSnapshot1.getKey();
                    int equipment1 = dataSnapshot1.child("Equipment1").getValue(Integer.class);
                    int equipment2 = dataSnapshot1.child("Equipment2").getValue(Integer.class);
                    String muscles = dataSnapshot1.child("Muscles").getValue(String.class);
                    String name = dataSnapshot1.child("Name").getValue(String.class);
                    int reps_time = dataSnapshot1.child("Rep_Time").getValue(Integer.class);

                    String[] muscleArray = muscles.split(", ");

                    Exercise exercise = new Exercise(Integer.parseInt(id), equipment1,equipment2, muscleArray, name, reps_time);
                    exerciseList.add(exercise);
                }

                listLoadedListener.onExercisesLoaded(exerciseList);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        getDatabaseReference().child("Exercises").addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener

    }

}
