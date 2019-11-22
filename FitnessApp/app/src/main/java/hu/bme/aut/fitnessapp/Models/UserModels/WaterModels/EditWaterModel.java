package hu.bme.aut.fitnessapp.Models.UserModels.WaterModels;

import androidx.fragment.app.DialogFragment;

import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadWater;

public class EditWaterModel implements LoadWater.WaterLoadedListener{

    private double water_saved;

    public interface WaterLoadListener {
        void onWaterLoaded(String water);
    }

    private EditWaterModel.WaterLoadListener waterLoadListener;

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
        water_saved = water;
        waterLoadListener.onWaterLoaded(Double.toString(water_saved));
    }

    public double getWater(String text) {
        double water = 0;
        try {
            water = Double.parseDouble(text);
        } catch (NumberFormatException f) {
            water = water_saved;
        }
        return water;
    }

    public void setWater_saved(double water_saved) {
        this.water_saved = water_saved;
    }

}
