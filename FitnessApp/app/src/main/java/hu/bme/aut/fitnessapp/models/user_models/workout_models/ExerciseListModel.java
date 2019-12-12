package hu.bme.aut.fitnessapp.models.user_models.workout_models;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.entities.Equipment;
import hu.bme.aut.fitnessapp.entities.Exercise;
import hu.bme.aut.fitnessapp.models.database_models.LoadEquipment;
import hu.bme.aut.fitnessapp.models.database_models.LoadWorkoutDetails;

public class ExerciseListModel implements LoadEquipment.EquipmentLoadedListener{

    private List<Exercise> exerciseItems;
    private ArrayList<Equipment> equipmentItems;

    private LoadEquipment loadEquipment;
    private EquipmentLoadedListener equipmentLoadedListener;

    public interface EquipmentLoadedListener {
        void onEquipmentLoaded();
    }

    public ExerciseListModel(Object object, List<Exercise> exerciseItems) {
        this.exerciseItems = exerciseItems;
        Log.d("exerciselist", Integer.toString(this.exerciseItems.size()));
        equipmentLoadedListener = (ExerciseListModel.EquipmentLoadedListener)object;
    }

    public void startWorkout() {
        LoadWorkoutDetails loadWorkoutDetails = new LoadWorkoutDetails();
        loadWorkoutDetails.setProgress(true);
    }

    public void loadEquipment() {
        loadEquipment = new LoadEquipment(this);
        loadEquipment.loadEquipment();
    }

    @Override
    public void onEquipmentLoaded(ArrayList<Equipment> equipment) {
        equipmentItems = equipment;
        equipmentLoadedListener.onEquipmentLoaded();
    }

    public List<Exercise> getExerciseItems() {
        return exerciseItems;
    }

    public List<Equipment> getEquipmentItems() {
        return equipmentItems;
    }

    public void removeListeners() {
        if(loadEquipment != null) loadEquipment.removeListeners();
    }

}
