package hu.bme.aut.fitnessapp.models.user_models.water_models;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;

import hu.bme.aut.fitnessapp.models.database_models.LoadWater;
import hu.bme.aut.fitnessapp.models.database_models.LoadWeight;

public class WaterModel implements LoadWeight.CurrentWeightLoadedListener, LoadWater.WaterLoadedListener {

    private LoadWater loadWater;
    private LoadWeight loadWeight;

    private double display;
    private double water2;
    private Double currentWeight;

    private long today;

    private WaterModel.WaterListener waterListener;

    public interface WaterListener {
        void onConsumedLoaded(double water);
        void onRecommendedLoaded(double water);
    }

    public WaterModel(Object object) {
        waterListener = (WaterModel.WaterListener)object;
    }

    private void calculateToday() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.set(year, month, day, 0,0,0);
        today = calendar.getTimeInMillis() / 1000;
    }

    public void calculateRecommendedText() {
        float recommended = (float) (currentWeight * 0.033 + 1);
        display = Math.round(recommended * 10d) / 10d;
    }


    public int calculatePercent() {
        return (int) ((water2 / display) * 100);
    }


    public void addWater(double newItem) {
        water2 = water2 + newItem;
        loadWater.addNewItem(today, round(water2,2));
        waterListener.onConsumedLoaded(round(water2,2));
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void editWater(double newItem) {
        water2 = newItem;
        loadWater.addNewItem(today, round(water2,2));
        waterListener.onConsumedLoaded(round(water2,2));

    }

    public void loadWeight() {
        calculateToday();
        loadWeight = new LoadWeight();
        loadWeight.setCurrentWeightLoadedListener(this);
        loadWeight.loadCurrentWeight();
    }

    @Override
    public void onCurrentWeightLoaded(double weight) {
        currentWeight = weight;
        calculateRecommendedText();
        waterListener.onRecommendedLoaded(display);
        loadWater();
    }

    private void loadWater() {
        loadWater = new LoadWater();
        loadWater.setListLoadedListener(this);
        loadWater.loadWaterToday();
    }

    @Override
    public void onWaterLoaded(double water) {
        water2 = water;
        waterListener.onConsumedLoaded(water2);

    }

    public double getDisplay() {
        return display;
    }

    public void setDisplay(double display) {
        this.display = display;
    }

    public void setWater2(double water2) {
        this.water2 = water2;
    }

    public void setCurrentWeight(Double currentWeight) {
        this.currentWeight = currentWeight;
    }

    public void removeListeners() {
        if(loadWeight != null) loadWeight.removeListeners();
    }
}