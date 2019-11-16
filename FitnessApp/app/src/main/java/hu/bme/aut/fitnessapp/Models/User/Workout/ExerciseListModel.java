package hu.bme.aut.fitnessapp.Models.User.Workout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Equipment;
import hu.bme.aut.fitnessapp.Entities.Exercise;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadEquipment;

public class ExerciseListModel implements LoadEquipment.EquipmentLoadedListener{


    private ArrayList<Exercise> exerciseItems;
    private ArrayList<Equipment> equipmentItems;

    public interface EquipmentLoadedListener {
        void onEquipmentLoaded();
    }

    private EquipmentLoadedListener equipmentLoadedListener;

    public ExerciseListModel(Object object, ArrayList<Exercise> exerciseItems) {
        this.exerciseItems = exerciseItems;
        equipmentLoadedListener = (ExerciseListModel.EquipmentLoadedListener)object;
    }

    public void startWorkout() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Workout_Details").child(userId).child("In_Progress");
        databaseReference.setValue(true);
    }

    public void loadEquipment() {
        LoadEquipment loadEquipment = new LoadEquipment(this);
        loadEquipment.loadEquipment();
    }

    public ArrayList<Exercise> getExerciseItems() {
        return exerciseItems;
    }

    public ArrayList<Equipment> getEquipmentItems() {
        return equipmentItems;
    }

    @Override
    public void onEquipmentLoaded(ArrayList<Equipment> equipment) {
        equipmentItems = equipment;
        equipmentLoadedListener.onEquipmentLoaded();
    }
}
