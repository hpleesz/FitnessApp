package hu.bme.aut.fitnessapp;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Controllers.Gym.NewPublicLocationActivity;
import hu.bme.aut.fitnessapp.Models.Gym.NewPublicLocationModel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GymUnitTest {

    private NewPublicLocationActivity newPublicLocationActivity;
    private NewPublicLocationModel newPublicLocationModel;

    @Before
    public void initGym() {
        newPublicLocationActivity = new NewPublicLocationActivity();
        newPublicLocationModel = new NewPublicLocationModel(newPublicLocationActivity);
    }

    @Test
    public void openCloseTimesDiffValidTest() {
        ArrayList<String[]> open_hours = new ArrayList<>();
        open_hours.add(new String[]{"08:00", "10:00"});
        open_hours.add(new String[]{"00:00", "00:01"});

        boolean val = newPublicLocationModel.openCloseTimesDiffValid(open_hours);
        assertTrue(val);

        open_hours = new ArrayList<>();
        open_hours.add(new String[]{"11:00", "10:00"});
        open_hours.add(new String[]{"00:00", "00:01"});

        val = newPublicLocationModel.openCloseTimesDiffValid(open_hours);
        assertFalse(val);

        open_hours = new ArrayList<>();
        open_hours.add(new String[]{"08:00", "10:00"});
        open_hours.add(new String[]{"00:00", "00:00"});

        val = newPublicLocationModel.openCloseTimesDiffValid(open_hours);
        assertFalse(val);

        open_hours = new ArrayList<>();
        open_hours.add(new String[]{"", ""});
        open_hours.add(new String[]{"00:00", "00:01"});

        val = newPublicLocationModel.openCloseTimesDiffValid(open_hours);
        assertTrue(val);
    }

    @Test
    public void openCloseTimesValidTest() {
        ArrayList<String[]>open_hours = new ArrayList<>();
        open_hours.add(new String[]{"08:00", "10:00"});
        open_hours.add(new String[]{"00:00", "00:01"});

        boolean val = newPublicLocationModel.openCloseTimesValid(open_hours);
        assertTrue(val);

        open_hours.add(new String[]{"", "00:00"});

        val = newPublicLocationModel.openCloseTimesValid(open_hours);
        assertFalse(val);
    }
}
