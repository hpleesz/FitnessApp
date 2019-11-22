package hu.bme.aut.fitnessapp.Models.UserModels.WaterModels;

public class NewWaterModel {

    public double getWater(String text) {
        double water = 0.0;
        try {
            water = Double.parseDouble(text);
        } catch (NumberFormatException f) {
            water = 0.0;
        }
        return water;
    }
}
