package hu.bme.aut.fitnessapp;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import hu.bme.aut.fitnessapp.controllers.user.settings.SettingsActivity;
import hu.bme.aut.fitnessapp.models.user_models.settings_models.SettingsModel;


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
        settingsModel.setGainMuscle(true);
        settingsModel.setLoseWeight(true);

        boolean valid = settingsModel.isValid(1,1,1);
        assertTrue(valid);

        settingsModel.setMale(true);
        valid = settingsModel.isValid(1,1,1);
        assertFalse(valid);

        settingsModel.setMale(false);
        settingsModel.setLoseWeight(false);
        settingsModel.setGainMuscle(false);
        valid = settingsModel.isValid(1,1,1);
        assertFalse(valid);

        settingsModel.setGainMuscle(true);
        valid = settingsModel.isValid(1,0,1);
        assertFalse(valid);


    }
}
