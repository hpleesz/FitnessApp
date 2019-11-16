package hu.bme.aut.fitnessapp;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import hu.bme.aut.fitnessapp.Controllers.User.Settings.SettingsActivity;
import hu.bme.aut.fitnessapp.Models.User.Settings.SettingsModel;


public class SettingsUnitTest {

    private SettingsActivity settingsActivity;
    private SettingsModel settingsModel;

    @Before
    public void initSettings() {
        settingsActivity = new SettingsActivity();
        settingsModel = new SettingsModel(settingsActivity);
    }

    @Test
    public void isValidTest() {
        settingsModel.setFemale(true);
        settingsModel.setMale(false);
        settingsModel.setGain_muscle(true);
        settingsModel.setLose_weight(true);

        boolean valid = settingsModel.isValid(1,1,1);
        assertTrue(valid);

        settingsModel.setMale(true);
        valid = settingsModel.isValid(1,1,1);
        assertFalse(valid);

        settingsModel.setMale(false);
        settingsModel.setLose_weight(false);
        settingsModel.setGain_muscle(false);
        valid = settingsModel.isValid(1,1,1);
        assertFalse(valid);

        settingsModel.setGain_muscle(true);
        valid = settingsModel.isValid(1,0,1);
        assertFalse(valid);


    }
}

