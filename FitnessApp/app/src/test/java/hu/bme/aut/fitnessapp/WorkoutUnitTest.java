package hu.bme.aut.fitnessapp;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Equipment;
import hu.bme.aut.fitnessapp.Entities.Exercise;
import hu.bme.aut.fitnessapp.Entities.Location;
import hu.bme.aut.fitnessapp.Entities.User;
import hu.bme.aut.fitnessapp.Entities.WorkoutDetails;
import hu.bme.aut.fitnessapp.Controllers.User.Workout.ExerciseInfoActivity;
import hu.bme.aut.fitnessapp.Controllers.User.Workout.MainActivity;
import hu.bme.aut.fitnessapp.Controllers.User.Workout.StretchActivity;
import hu.bme.aut.fitnessapp.Controllers.User.Workout.WarmUpActivity;
import hu.bme.aut.fitnessapp.Models.User.Water.WaterModel;
import hu.bme.aut.fitnessapp.Models.User.Workout.ExerciseInfoModel;
import hu.bme.aut.fitnessapp.Models.User.Workout.MainModel;
import hu.bme.aut.fitnessapp.Models.User.Workout.StretchModel;
import hu.bme.aut.fitnessapp.Models.User.Workout.WarmUpModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class WorkoutUnitTest {

    private WarmUpActivity warmUpActivity;
    private WarmUpModel warmUpModel;

    private StretchActivity stretchActivity;
    private StretchModel stretchModel;

    private ExerciseInfoActivity exerciseInfoActivity;
    private ExerciseInfoModel exerciseInfoModel;

    private MainActivity mainActivity;
    private MainModel mainModel;

    @Before
    public void initWorkout() {
        warmUpActivity = new WarmUpActivity();
        warmUpModel = new WarmUpModel(warmUpActivity, "");

        stretchActivity = new StretchActivity();
        stretchModel = new StretchModel(stretchActivity);

        exerciseInfoActivity = new ExerciseInfoActivity();
        exerciseInfoModel = new ExerciseInfoModel(exerciseInfoActivity, new ArrayList<Exercise>());

        mainActivity = new MainActivity();
        mainModel = new MainModel(mainActivity);
    }


    @Test
    public void getTypeWarmUpTest() {
        warmUpModel.setType("Cardio1");
        warmUpModel.getType();
        assertTrue(warmUpModel.isLower());

        warmUpModel.setType("Lower body");
        warmUpModel.getType();
        assertTrue(warmUpModel.isLower());

        warmUpModel.setType("Upper body");
        warmUpModel.getType();
        assertFalse(warmUpModel.isLower());
    }

    @Test
    public void transformNameTest() {
        String name = stretchModel.transformName("Video name, Left / Right");
        assertEquals("video_name_left_right", name);
    }

    @Test
    public void setEquipmentsTest() {
        ArrayList<Equipment> equipment = new ArrayList<>();
        equipment.add(new Equipment(1, "Equ1"));
        equipment.add(new Equipment(2, "Equ2"));
        equipment.add(new Equipment(3, "Equ3"));
        equipment.add(new Equipment(4, "Equ4"));

        String[] muscles = {"m1", "m2"};
        Exercise exercise = new Exercise(1, 1, 1, muscles, "Exercise", 1);

        exerciseInfoModel.setEquipmentItems(equipment);
        String text = exerciseInfoModel.setEquipments(exercise);

        assertEquals("Equ1", text);

        exercise = new Exercise(1, 3, 1, muscles, "Exercise", 1);
        text = exerciseInfoModel.setEquipments(exercise);

        assertEquals("Equ3", text);

        exercise = new Exercise(1, 3, 4, muscles, "Exercise", 1);
        text = exerciseInfoModel.setEquipments(exercise);

        assertEquals("Equ3, Equ4", text);

    }

    @Test
    public void itemUsesWeightTest() {
        Exercise exercise = new Exercise(1, 1, 1, new String[]{}, "Exercise", 1);

        boolean val = exerciseInfoModel.itemUsesWeight(exercise);
        assertFalse(val);

        exercise = new Exercise(1, 5, 1, new String[]{}, "Exercise", 1);

        val = exerciseInfoModel.itemUsesWeight(exercise);
        assertTrue(val);

        exercise = new Exercise(1, 8, 8, new String[]{}, "Exercise", 1);

        val = exerciseInfoModel.itemUsesWeight(exercise);
        assertFalse(val);
    }

    @Test
    public void setWorkoutTypeTest() {
        exerciseInfoModel.setUser(new User("Name", 1998, 10, 12, true, false, 1, 1.0, 1.0));
        exerciseInfoModel.setWorkoutDetails(new WorkoutDetails("Lower body", new ArrayList<Integer>(), true));
        String type = exerciseInfoModel.setWorkoutType();

        assertEquals("Upper body", type);

        exerciseInfoModel.setUser(new User("Name", 1998, 10, 12, true, true, 1, 1.0, 1.0));
        exerciseInfoModel.setWorkoutDetails(new WorkoutDetails("Lower body", new ArrayList<Integer>(), true));
        type = exerciseInfoModel.setWorkoutType();

        assertEquals("Cardio 1", type);

        exerciseInfoModel.setUser(new User("Name", 1998, 10, 12, false, true, 1, 1.0, 1.0));
        exerciseInfoModel.setWorkoutDetails(new WorkoutDetails("Cardio 2", new ArrayList<Integer>(), true));
        type = exerciseInfoModel.setWorkoutType();

        assertEquals("Cardio 1", type);
    }

    @Test
    public void getAvailableEquipmentTest() {
        ArrayList<Integer> equipment = new ArrayList<>();
        equipment.add(5);
        equipment.add(6);
        equipment.add(7);
        equipment.add(20);

        Location location = new Location(1, "Name", equipment);

        mainModel.getAvailableEquipment(location);
        assertTrue(mainModel.getEquipment_ids().contains(5));
        assertTrue(mainModel.getEquipment_ids().contains(6));
        assertTrue(mainModel.getEquipment_ids().contains(7));
        assertTrue(mainModel.getEquipment_ids().contains(20));
        assertTrue(mainModel.getEquipment_ids().contains(4));
        assertTrue(mainModel.getEquipment_ids().contains(1));

    }

    @Test
    public void getExercisesForLocationTest() {

        ArrayList<Integer> equipment = new ArrayList<>();
        equipment.add(4);
        equipment.add(6);
        equipment.add(1);
        equipment.add(20);


        ArrayList<Exercise> exercises = new ArrayList<>();
        exercises.add(new Exercise(1, 3, 1, new String[]{}, "Name1", 1));
        exercises.add(new Exercise(2, 4, 1, new String[]{}, "Name2", 1));
        exercises.add(new Exercise(3, 1, 1, new String[]{}, "Name3", 1));

        mainModel.setEquipment_ids(equipment);
        mainModel.setExerciseList(exercises);
        mainModel.getExercisesForLocation();

        assertEquals(2, mainModel.getExercisesForChosenLocation().size());
        assertEquals("Name2", mainModel.getExercisesForChosenLocation().get(0).name);
        assertEquals("Name3", mainModel.getExercisesForChosenLocation().get(1).name);


    }

    /*
    @Test
    public void makeWorkoutFromSelectedExercisesTest() {

    }

    @Test
    public void selectExercisesTest() {

    }

     */


    @Test
    public void selectCardioTest() {
        ArrayList<Exercise> exercises = new ArrayList<>();
        exercises.add(new Exercise(1, 3, 1, new String[]{"Shoulders"}, "Name1", 1));
        exercises.add(new Exercise(2, 4, 1, new String[]{"Cardiovascular System"}, "Name2", 1));
        exercises.add(new Exercise(3, 1, 1, new String[]{"Quads", "Cardiovascular System"}, "Name3", 1));

        mainModel.setExercisesForLocation(exercises);
        mainModel.setChosenExercises(new ArrayList<Exercise>());

        mainModel.selectCardio();

        assertEquals(1, mainModel.getChosenExercises().size());
        assertTrue(mainModel.getChosenExercises().get(0).name.equals("Name2") || mainModel.getChosenExercises().get(0).name.equals("Name3"));
    }

}

