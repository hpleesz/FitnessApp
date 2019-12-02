package hu.bme.aut.fitnessapp.Models.UserModels.WorkoutModels;

import java.util.ArrayList;
import java.util.Random;

import hu.bme.aut.fitnessapp.Entities.Equipment;
import hu.bme.aut.fitnessapp.Entities.Exercise;
import hu.bme.aut.fitnessapp.Entities.Location;
import hu.bme.aut.fitnessapp.Entities.Place;
import hu.bme.aut.fitnessapp.Entities.PublicLocation;
import hu.bme.aut.fitnessapp.Entities.WorkoutDetails;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadEquipment;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadExercises;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadMuscles;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadPublicLocations;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadWorkoutDetails;

public class MainModel implements LoadEquipment.EquipmentLoadedListener, LoadMuscles.MusclesLoadedListener, LoadExercises.ExercisesLoadedListener, LoadWorkoutDetails.WorkoutDetailsLoadedListener, LoadPublicLocations.PublicLocationsByIDLoadedListener{

    private ArrayList<Exercise> chosenExercises;
    private ArrayList<Exercise> exercisesForLocation;
    private ArrayList<Integer> equipment_ids;
    private ArrayList<Exercise> exerciseList;

    private WorkoutDetails workoutDetails;

    private ArrayList<String> lower_body_parts;
    private ArrayList<String> upper_body_parts;

    private boolean chosen;

    private LoadEquipment loadEquipment;
    private LoadWorkoutDetails loadWorkoutDetails;
    private LoadMuscles loadMuscles;
    private LoadExercises loadExercises;
    private LoadPublicLocations loadPublicLocations;

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

    private MainModel.DataReadyListener dataReadyListener;
    private MainModel.ContinueWorkoutListener continueWorkoutListener;
    private MainModel.LocationChosenListener locationChosenListener;

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
        upper_body_parts = muscles;
    }

    @Override
    public void onLowerBodyLoaded(ArrayList<String> muscles) {
        lower_body_parts = muscles;
    }

    public void loadExercises() {
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
        this.workoutDetails = workoutDetails;
        dataReadyListener.onButtonsReady();
        continueWorkout();

    }

    private void continueWorkout() {
        if (workoutDetails.in_progress) {

            for(int id : workoutDetails.exercises) {
                chosenExercises.add(exerciseList.get(id-1));
            }
            continueWorkoutListener.onContinueWorkout();
        }
    }

    public void locationChosen(Location item) {
        chosen = true;
        locationChosenListener.onLocationChosen(item.name);

        getAvailableEquipment(item);
        getExercisesForLocation();
        makeWorkoutFromSelectedExercises();
    }

    public void setLocationListener(PublicLocation loc) {
        chosen = true;
        loadPublicLocations = new LoadPublicLocations();
        loadPublicLocations.setListLoadedByIDListener(this);
        loadPublicLocations.loadPublicLocationByID(Long.toString(loc.id));
    }

    @Override
    public void onPublicLocationsByIDLoaded(PublicLocation publicLocation) {
        locationChosenListener.onLocationChosen(publicLocation.name);

        getAvailableEquipment(publicLocation);
        getExercisesForLocation();
        makeWorkoutFromSelectedExercises();
    }


    public void getAvailableEquipment(Place loc) {
        equipment_ids = loc.equipment;

        if (equipment_ids.contains(5) && !equipment_ids.contains(4))
            equipment_ids.add(4);
        if (equipment_ids.contains(7) && !equipment_ids.contains(6))
            equipment_ids.add(6);
        if (!equipment_ids.contains(1)) equipment_ids.add(1);

    }



    public void getExercisesForLocation() {
        exercisesForLocation = new ArrayList<>();
        for(Exercise exercise : exerciseList) {
            if(equipment_ids.contains(exercise.equipment1) && equipment_ids.contains(exercise.equipment2))
                exercisesForLocation.add(exercise);
        }
    }

    private void makeWorkoutFromSelectedExercises() {
        chosenExercises = new ArrayList<>();
        ArrayList<String> body_parts = new ArrayList<>();

        switch (workoutDetails.type) {
            case "Upper body":
                while (body_parts.size() < 10) {
                    body_parts.add(upper_body_parts.get(getRandomNumber(upper_body_parts.size())));
                }
                selectExercises(body_parts, 10);
                break;

            case "Lower body":
                while (body_parts.size() < 10) {
                    body_parts.add(lower_body_parts.get(getRandomNumber(lower_body_parts.size())));
                }
                selectExercises(body_parts, 10);
                break;

            case "Cardio 1":
            case "Cardio 2":
                while (body_parts.size() < 4) {
                    body_parts.add(upper_body_parts.get(getRandomNumber(upper_body_parts.size())));
                }
                while (body_parts.size() < 8) {
                    body_parts.add(lower_body_parts.get(getRandomNumber(lower_body_parts.size())));
                }
                selectExercises(body_parts, 8);
                selectCardio();
                break;
        }

        LoadWorkoutDetails loadWorkoutDetails = new LoadWorkoutDetails();
        loadWorkoutDetails.removeExercises();
        for (int j = 0; j < chosenExercises.size(); j++) {
            loadWorkoutDetails.addNewExercise(Integer.toString(j), chosenExercises.get(j).id);
        }

        locationChosenListener.workoutSelected();

    }

    private int getRandomNumber(int max) {
        Random r = new Random();
        return r.nextInt(max);
    }

    private void selectExercises(ArrayList<String> body_parts, int limit) {
        for (int i = 0; i < limit; i++) {
            ArrayList<Exercise> exerciseItems = new ArrayList<>();
            String body_part = body_parts.get(i);
            for (int j = 0; j < exercisesForLocation.size(); j++) {
                for (int k = 0; k < exercisesForLocation.get(j).muscles.length; k++) {
                    String s = exercisesForLocation.get(j).muscles[k];
                    if (exercisesForLocation.get(j).muscles[k].contains(body_part))
                        exerciseItems.add(exercisesForLocation.get(j));
                }
            }
            if (exerciseItems.size() > 0) {
                int random = getRandomNumber(exerciseItems.size());
                Exercise exercise = exerciseItems.get(random);
                if (!chosenExercises.contains(exercise)) {
                    chosenExercises.add(exercise);
                    if (exercise.name.contains("left")) {
                        Exercise exerciseItem = exerciseItems.get(random + 1);
                        chosenExercises.add(exerciseItem);
                        i++;
                    }
                    if (exercise.name.contains("right")) {
                        Exercise exerciseItem = exerciseItems.get(random - 1);
                        chosenExercises.add(exerciseItem);
                        i++;
                    }
                } else i--;
            } else {
                if (workoutDetails.type.equals("Upper body")) {
                    body_parts.set(i, upper_body_parts.get(getRandomNumber(upper_body_parts.size())));
                    i--;
                } else {
                    body_parts.set(i, lower_body_parts.get(getRandomNumber(lower_body_parts.size())));
                    i--;
                }
            }
        }
    }


    public void selectCardio() {
        ArrayList<Exercise> cardio = new ArrayList<>();
        for (int i = exercisesForLocation.size() - 1; i >= 0; i--) {
            if (exercisesForLocation.get(i).muscles[0].contains("Cardiovascular System"))
                cardio.add(exercisesForLocation.get(i));
        }
        int random = getRandomNumber(cardio.size());
        chosenExercises.add(cardio.get(random));
    }

    public ArrayList<Exercise> getChosenExercises() {
        return chosenExercises;
    }

    public boolean isChosen() {
        return chosen;
    }

    public ArrayList<Exercise> getExercisesForChosenLocation() {
        return exercisesForLocation;
    }

    public void setEquipment_ids(ArrayList<Integer> equipment_ids) {
        this.equipment_ids = equipment_ids;
    }

    public void setExerciseList(ArrayList<Exercise> exerciseList) {
        this.exerciseList = exerciseList;
    }
    public void setChosenExercises(ArrayList<Exercise> chosenExercises) {
        this.chosenExercises = chosenExercises;
    }

    public void setExercisesForLocation(ArrayList<Exercise> exercisesForLocation) {
        this.exercisesForLocation = exercisesForLocation;
    }
    public ArrayList<Integer> getEquipment_ids() {
        return equipment_ids;
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
