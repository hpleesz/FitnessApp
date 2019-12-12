package hu.bme.aut.fitnessapp.models.user_models.workout_models;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import hu.bme.aut.fitnessapp.entities.Equipment;
import hu.bme.aut.fitnessapp.entities.Exercise;
import hu.bme.aut.fitnessapp.entities.Location;
import hu.bme.aut.fitnessapp.entities.Place;
import hu.bme.aut.fitnessapp.entities.PublicLocation;
import hu.bme.aut.fitnessapp.entities.WorkoutDetails;
import hu.bme.aut.fitnessapp.models.database_models.LoadEquipment;
import hu.bme.aut.fitnessapp.models.database_models.LoadExercises;
import hu.bme.aut.fitnessapp.models.database_models.LoadMuscles;
import hu.bme.aut.fitnessapp.models.database_models.LoadPublicLocations;
import hu.bme.aut.fitnessapp.models.database_models.LoadWorkoutDetails;

public class MainModel implements LoadEquipment.EquipmentLoadedListener, LoadMuscles.MusclesLoadedListener, LoadExercises.ExercisesLoadedListener, LoadWorkoutDetails.WorkoutDetailsLoadedListener, LoadPublicLocations.PublicLocationsByIDLoadedListener{

    private List<Exercise> chosenExercises;
    private List<Exercise> exercisesForLocation;
    private List<Integer> equipmentIds;
    private List<Exercise> exerciseList;

    private WorkoutDetails workoutDetails;

    private ArrayList<String> lowerBodyParts;
    private ArrayList<String> upperBodyParts;

    private boolean chosen;

    private LoadEquipment loadEquipment;
    private LoadWorkoutDetails loadWorkoutDetails;
    private LoadMuscles loadMuscles;
    private LoadExercises loadExercises;
    private LoadPublicLocations loadPublicLocations;

    private Random rand = new Random();

    private MainModel.DataReadyListener dataReadyListener;
    private MainModel.ContinueWorkoutListener continueWorkoutListener;
    private MainModel.LocationChosenListener locationChosenListener;

    public interface DataReadyListener {
        void onButtonsReady();
    }

    public interface LocationChosenListener {
        void onLocationChosen(String name);
        void workoutSelected();
    }
    public interface ContinueWorkoutListener {
        void onContinueWorkout();
    }



    public MainModel(Object object) {
        dataReadyListener = (MainModel.DataReadyListener)object;
        continueWorkoutListener = (MainModel.ContinueWorkoutListener)object;
        locationChosenListener = (MainModel.LocationChosenListener)object;

        chosenExercises = new ArrayList<>();
        chosen = false;
    }

    public void loadEquipment() {
        loadEquipment = new LoadEquipment(this);
        loadEquipment.loadEquipment();
    }

    @Override
    public void onEquipmentLoaded(ArrayList<Equipment> equipment) {
    }

    public void loadLowerBodyParts() {
        loadMuscles = new LoadMuscles(this);
        loadMuscles.loadLowerBodyParts();
    }

    public void loadUpperBodyParts() {
        loadMuscles = new LoadMuscles(this);
        loadMuscles.loadUpperBodyParts();
    }

    @Override
    public void onUpperBodyLoaded(ArrayList<String> muscles) {
        upperBodyParts = muscles;
    }

    @Override
    public void onLowerBodyLoaded(ArrayList<String> muscles) {
        lowerBodyParts = muscles;
    }

    public void loadExercises() {
        Log.d("loadsexercises", "load");
        loadExercises = new LoadExercises(this);
        loadExercises.loadExercises();
    }

    @Override
    public void onExercisesLoaded(ArrayList<Exercise> exercises) {
        exerciseList = exercises;
        loadWorkoutDetails();
    }


    private void loadWorkoutDetails() {
        loadWorkoutDetails = new LoadWorkoutDetails();
        loadWorkoutDetails.setListLoadedListener(this);
        loadWorkoutDetails.loadWorkoutDetails();
    }


    @Override
    public void onWorkoutDetailsLoaded(WorkoutDetails workoutDetails) {
        Log.d("wodetails", "loaded");
        this.workoutDetails = workoutDetails;
        dataReadyListener.onButtonsReady();
        continueWorkout();

    }

    private void continueWorkout() {
        if (workoutDetails.isInProgress()) {

            for(int id : workoutDetails.getExercises()) {
                chosenExercises.add(exerciseList.get(id-1));
            }
            continueWorkoutListener.onContinueWorkout();
        }
    }

    public void locationChosen(Location item) {
        chosen = true;
        locationChosenListener.onLocationChosen(item.getName());

        getAvailableEquipment(item);
        getExercisesForLocation();
        makeWorkoutFromSelectedExercises();
    }

    public void setLocationListener(PublicLocation loc) {
        chosen = true;
        loadPublicLocations = new LoadPublicLocations();
        loadPublicLocations.setListLoadedByIDListener(this);
        loadPublicLocations.loadPublicLocationByID(Long.toString(loc.getId()));
    }

    @Override
    public void onPublicLocationsByIDLoaded(PublicLocation publicLocation) {
        locationChosenListener.onLocationChosen(publicLocation.getName());

        getAvailableEquipment(publicLocation);
        getExercisesForLocation();
        makeWorkoutFromSelectedExercises();
    }


    public void getAvailableEquipment(Place loc) {
        equipmentIds = loc.getEquipment();

        if (equipmentIds.contains(5) && !equipmentIds.contains(4))
            equipmentIds.add(4);
        if (equipmentIds.contains(7) && !equipmentIds.contains(6))
            equipmentIds.add(6);
        if (!equipmentIds.contains(1)) equipmentIds.add(1);

    }



    public void getExercisesForLocation() {
        exercisesForLocation = new ArrayList<>();
        for(Exercise exercise : exerciseList) {
            if(equipmentIds.contains(exercise.getEquipment1()) && equipmentIds.contains(exercise.getEquipment2()))
                exercisesForLocation.add(exercise);
        }
    }

    private void makeWorkoutFromSelectedExercises() {
        chosenExercises = new ArrayList<>();
        ArrayList<String> bodyParts = new ArrayList<>();

        switch (workoutDetails.getType()) {
            case "Upper body":
                while (bodyParts.size() < 10) {
                    bodyParts.add(upperBodyParts.get(getRandomNumber(upperBodyParts.size())));
                }
                selectExercises(bodyParts, 10);
                break;

            case "Lower body":
                while (bodyParts.size() < 10) {
                    bodyParts.add(lowerBodyParts.get(getRandomNumber(lowerBodyParts.size())));
                }
                selectExercises(bodyParts, 10);
                break;

            case "Cardio 1":
            case "Cardio 2":
                while (bodyParts.size() < 4) {
                    bodyParts.add(upperBodyParts.get(getRandomNumber(upperBodyParts.size())));
                }
                while (bodyParts.size() < 8) {
                    bodyParts.add(lowerBodyParts.get(getRandomNumber(lowerBodyParts.size())));
                }
                selectExercises(bodyParts, 8);
                selectCardio();
                break;
        }

        LoadWorkoutDetails loadDetails = new LoadWorkoutDetails();
        loadDetails.removeExercises();
        for (int j = 0; j < chosenExercises.size(); j++) {
            loadDetails.addNewExercise(Integer.toString(j), chosenExercises.get(j).getId());
        }

        locationChosenListener.workoutSelected();

    }

    private int getRandomNumber(int max) {
        return this.rand.nextInt(max);
    }

    private void selectExercises(ArrayList<String> bodyParts, int limit) {
        for (int i = 0; i < limit; i++) {
            ArrayList<Exercise> exerciseItems = new ArrayList<>();
            String bodyPart = bodyParts.get(i);
            for (int j = 0; j < exercisesForLocation.size(); j++) {
                for (int k = 0; k < exercisesForLocation.get(j).getMuscles().length; k++) {
                    if (exercisesForLocation.get(j).getMuscles()[k].contains(bodyPart))
                        exerciseItems.add(exercisesForLocation.get(j));
                }
            }
            if (!exerciseItems.isEmpty()) {
                int random = getRandomNumber(exerciseItems.size());
                Exercise exercise = exerciseItems.get(random);
                if (!chosenExercises.contains(exercise)) {
                    chosenExercises.add(exercise);
                    if (exercise.getName().contains("left")) {
                        Exercise exerciseItem = exerciseItems.get(random + 1);
                        chosenExercises.add(exerciseItem);
                        i++;
                    }
                    if (exercise.getName().contains("right")) {
                        Exercise exerciseItem = exerciseItems.get(random - 1);
                        chosenExercises.add(exerciseItem);
                        i++;
                    }
                } else i--;
            } else {
                if (workoutDetails.getType().equals("Upper body")) {
                    bodyParts.set(i, upperBodyParts.get(getRandomNumber(upperBodyParts.size())));
                    i--;
                } else {
                    bodyParts.set(i, lowerBodyParts.get(getRandomNumber(lowerBodyParts.size())));
                    i--;
                }
            }
        }
    }


    public void selectCardio() {
        ArrayList<Exercise> cardio = new ArrayList<>();
        for (int i = exercisesForLocation.size() - 1; i >= 0; i--) {
            if (exercisesForLocation.get(i).getMuscles()[0].contains("Cardiovascular System"))
                cardio.add(exercisesForLocation.get(i));
        }
        int random = getRandomNumber(cardio.size());
        chosenExercises.add(cardio.get(random));
    }

    public List<Exercise> getChosenExercises() {
        return chosenExercises;
    }

    public boolean isChosen() {
        return chosen;
    }

    public List<Exercise> getExercisesForChosenLocation() {
        return exercisesForLocation;
    }

    public void setEquipmentIds(List<Integer> equipmentIds) {
        this.equipmentIds = equipmentIds;
    }

    public void setExerciseList(List<Exercise> exerciseList) {
        this.exerciseList = exerciseList;
    }
    public void setChosenExercises(List<Exercise> chosenExercises) {
        this.chosenExercises = chosenExercises;
    }

    public void setExercisesForLocation(List<Exercise> exercisesForLocation) {
        this.exercisesForLocation = exercisesForLocation;
    }
    public List<Integer> getEquipmentIds() {
        return equipmentIds;
    }
    public WorkoutDetails getWorkoutDetails() {
        return workoutDetails;
    }

    public void removeListeners() {
        if(loadEquipment != null) loadEquipment.removeListeners();
        if(loadExercises != null) loadExercises.removeListeners();
        if(loadMuscles != null) loadMuscles.removeListeners();
        if(loadWorkoutDetails != null) loadWorkoutDetails.removeListeners();
        if(loadPublicLocations != null) loadPublicLocations.removeListeners();
    }

}
