package hu.bme.aut.fitnessapp;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import hu.bme.aut.fitnessapp.entities.Measurement;
import hu.bme.aut.fitnessapp.controllers.user.measurements.NewMeasurementItemDialogFragment;
import hu.bme.aut.fitnessapp.models.user_models.measurement_models.NewMeasurementItemModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MeasurementsUnitTest {

    private NewMeasurementItemDialogFragment newMeasurementItemDialogFragment;
    private NewMeasurementItemModel newMeasurementItemModel;

    @Before
    public void initMeasurements() {
        newMeasurementItemDialogFragment = new NewMeasurementItemDialogFragment();
        newMeasurementItemModel = new NewMeasurementItemModel();
    }

    @Test
    public void alreadyExistsTest() {
        HashMap<String, Double> items = new HashMap<>();
        items.put("Shoulders", 23.3);
        items.put("Waist", 10.1);
        items.put("Hips", 8.0);

        ArrayList<String> body_parts = new ArrayList<>();
        body_parts.add("Left_Calf");
        body_parts.add("Right_Calf");
        body_parts.add("Waist");
        body_parts.add("Right_Thigh");
        body_parts.add("Shoulders");
        body_parts.add("Hips");

        newMeasurementItemModel.setBodyParts(body_parts);

        ArrayList<ArrayList<Measurement>> measurements = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            measurements.add(new ArrayList<Measurement>());
        }
        measurements.get(1).add(new Measurement("1", 10.0));
        measurements.get(2).add(new Measurement("1", 10.0));
        measurements.get(4).add(new Measurement("1", 10.0));

        newMeasurementItemModel.setEntries(measurements);
        newMeasurementItemModel.setAlreadyExists("");
        newMeasurementItemModel.setDate("1");

        boolean val = newMeasurementItemModel.alreadyExists(items);
        assertTrue(val);
        assertEquals("Shoulders, Waist", newMeasurementItemModel.getAlreadyExists());

    }

}

