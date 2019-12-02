package hu.bme.aut.fitnessapp.Models.UserModels.WorkoutModels;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Equipment;
import hu.bme.aut.fitnessapp.Entities.Exercise;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadEquipment;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadWorkoutDetails;

public class ExerciseListModel implements LoadEquipment.EquipmentLoadedListener{

    private ArrayList<Exercise> exerciseItems;
    private ArrayList<Equipment> equipmentItems;

    public interface EquipmentLoadedListener {
        void onEquipmentLoaded();
    }

    private LoadEquipment loadEquipment;
    private EquipmentLoadedListener equipmentLoadedListener;

    public ExerciseListModel(Object object, ArrayList<Exercise> exerciseItems) {
        this.exerciseItems = exerciseItems;
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

    public ArrayList<Exercise> getExerciseItems() {
        return exerciseItems;
    }

    public ArrayList<Equipment> getEquipmentItems() {
        return equipmentItems;
    }

    public void removeListeners() {
        if(loadEquipment != null) loadEquipment.removeListeners();
    }

}
