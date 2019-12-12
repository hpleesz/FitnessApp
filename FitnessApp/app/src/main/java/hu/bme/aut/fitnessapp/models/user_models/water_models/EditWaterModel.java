package hu.bme.aut.fitnessapp.models.user_models.water_models;

import androidx.fragment.app.DialogFragment;

import hu.bme.aut.fitnessapp.models.database_models.LoadWater;

public class EditWaterModel implements LoadWater.WaterLoadedListener{

    private double waterSaved;

    private EditWaterModel.WaterLoadListener waterLoadListener;

    public interface WaterLoadListener {
        void onWaterLoaded(String water);
    }

    public EditWaterModel(DialogFragment activity) {
        waterLoadListener = (EditWaterModel.WaterLoadListener)activity;
    }

    public void loadWaterEntry() {
        LoadWater loadWater = new LoadWater();
        loadWater.setListLoadedListener(this);
        loadWater.loadWaterToday();
    }

    @Override
    public void onWaterLoaded(double water) {
        waterSaved = water;
        waterLoadListener.onWaterLoaded(Double.toString(waterSaved));
    }

    public double getWater(String text) {
        double water = 0;
        try {
            water = Double.parseDouble(text);
        } catch (NumberFormatException f) {
            water = waterSaved;
        }
        return water;
    }

    public void setWaterSaved(double waterSaved) {
        this.waterSaved = waterSaved;
    }

}
