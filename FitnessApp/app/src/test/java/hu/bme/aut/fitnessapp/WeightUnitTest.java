package hu.bme.aut.fitnessapp;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.entities.Measurement;
import hu.bme.aut.fitnessapp.controllers.user.weight.WeightActivity;
import hu.bme.aut.fitnessapp.models.user_models.weight_models.WeightModel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WeightUnitTest {

    private WeightActivity weightActivity;
    private WeightModel weightModel;

    @Before
    public void initWeight() {
        weightActivity = new WeightActivity();
        weightModel = new WeightModel(weightActivity);
    }

    @Test
    public void isGoalReachedTest() {
        ArrayList<Measurement> measurements = new ArrayList<>();
        measurements.add(new Measurement("123", 20));
        measurements.add(new Measurement("124", 30));
        measurements.add(new Measurement("125", 40));

        weightModel.setItemlist(measurements);
        weightModel.setGoalWeight(50);
        weightModel.setStartingWeight(20);

        assertFalse(weightModel.isGoalReached());

        measurements.add(new Measurement("126", 60));
        weightModel.setItemlist(measurements);

        assertTrue(weightModel.isGoalReached());

        measurements.set(0, new Measurement("123", 70));
        weightModel.setItemlist(measurements);

        assertTrue(weightModel.isGoalReached());



    }
}

