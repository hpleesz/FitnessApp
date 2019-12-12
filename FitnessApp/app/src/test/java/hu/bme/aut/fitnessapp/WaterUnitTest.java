package hu.bme.aut.fitnessapp;

import org.junit.Before;
import org.junit.Test;

import hu.bme.aut.fitnessapp.controllers.user.water.EditWaterDialogFragment;
import hu.bme.aut.fitnessapp.controllers.user.water.NewWaterDialogFragment;
import hu.bme.aut.fitnessapp.controllers.user.water.WaterActivity;
import hu.bme.aut.fitnessapp.models.user_models.water_models.EditWaterModel;
import hu.bme.aut.fitnessapp.models.user_models.water_models.NewWaterModel;
import hu.bme.aut.fitnessapp.models.user_models.water_models.WaterModel;

import static org.junit.Assert.assertEquals;

public class WaterUnitTest {

    private WaterActivity waterActivity;
    private WaterModel waterModel;
    private NewWaterDialogFragment newWaterDialogFragment;
    private NewWaterModel newWaterModel;
    private EditWaterDialogFragment editWaterDialogFragment;
    private EditWaterModel editWaterModel;

    @Before
    public void initWater() {
        waterActivity = new WaterActivity();
        waterModel = new WaterModel(waterActivity);

        newWaterDialogFragment = new NewWaterDialogFragment();
        newWaterModel = new NewWaterModel();

        editWaterDialogFragment = new EditWaterDialogFragment();
        editWaterModel = new EditWaterModel(editWaterDialogFragment);
    }
    @Test
    public void calculateRecommendedTextTest() {
        double current_weight = 80;
        waterModel.setCurrentWeight(current_weight);
        waterModel.calculateRecommendedText();
        double display = waterModel.getDisplay();
        assertEquals(Double.toString(3.6), Double.toString(display));
    }

    @Test
    public void calculatePercentTest() {
        waterModel.setDisplay(3.6);
        waterModel.setWater2(1);
        int percent = waterModel.calculatePercent();
        assertEquals(27, percent);
    }

    @Test
    public void getWaterNewTest() {
        double water = newWaterModel.getWater("3.5");
        assertEquals(Double.toString(3.5), Double.toString(water));

        water = newWaterModel.getWater("text");
        assertEquals(Double.toString(0.0), Double.toString(water));

        water = newWaterModel.getWater(".4");
        assertEquals(Double.toString(0.4), Double.toString(water));
    }

    @Test
    public void getWaterEditTest() {
        editWaterModel.setWaterSaved(1.8);

        double water = editWaterModel.getWater("3.5");
        assertEquals(Double.toString(3.5), Double.toString(water));

        water = editWaterModel.getWater("text");
        assertEquals(Double.toString(1.8), Double.toString(water));

        water = editWaterModel.getWater(".4");
        assertEquals(Double.toString(0.4), Double.toString(water));
    }
}
