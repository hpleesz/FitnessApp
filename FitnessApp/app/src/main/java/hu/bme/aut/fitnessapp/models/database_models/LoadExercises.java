package hu.bme.aut.fitnessapp.models.database_models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.entities.Exercise;

public class LoadExercises extends DatabaseConnection{

    private static final String EXERCISES = "Exercises";

    public interface ExercisesLoadedListener {
        void onExercisesLoaded(ArrayList<Exercise> exercises);
    }

    private LoadExercises.ExercisesLoadedListener listLoadedListener;

    public LoadExercises(Object object) {
        super();
        listLoadedListener = (LoadExercises.ExercisesLoadedListener)object;
    }

    private ValueEventListener eventListener;

    public void loadExercises() {

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Exercise> exerciseList = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String id = dataSnapshot1.getKey();
                    int equipment1 = dataSnapshot1.child("Equipment1").getValue(Integer.class);
                    int equipment2 = dataSnapshot1.child("Equipment2").getValue(Integer.class);
                    String muscles = dataSnapshot1.child("Muscles").getValue(String.class);
                    String name = dataSnapshot1.child("Name").getValue(String.class);
                    int repsTime = dataSnapshot1.child("Rep_Time").getValue(Integer.class);

                    String[] muscleArray = muscles.split(", ");

                    Exercise exercise = new Exercise(Integer.parseInt(id), equipment1,equipment2, muscleArray, name, repsTime);
                    exerciseList.add(exercise);
                }

                listLoadedListener.onExercisesLoaded(exerciseList);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error(DB_ERROR, databaseError.toException());
            }

        };
        getDatabaseReference().child(EXERCISES).addValueEventListener(eventListener);

    }

    public void removeListeners() {
        if(eventListener != null)getDatabaseReference().child(EXERCISES).removeEventListener(eventListener);
    }

}
