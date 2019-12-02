package hu.bme.aut.fitnessapp.Models.UserModels.MeasurementModels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hu.bme.aut.fitnessapp.Entities.User;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadBodyParts;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadMeasurements;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadUser;

public class MeasurementsModel implements LoadUser.UserLoadedListener, LoadBodyParts.BodyPartsLoadedListener, LoadMeasurements.LastMeasurementsLoadedListener {

    private ArrayList<String> body_parts;

    public interface CurrentMeasurementsListener {
        void onMeasurementsLoaded(ArrayList<String> measurements);
    }

    public interface GenderListener {
        void onGenderLoaded(int gender);
    }

    private MeasurementsModel.CurrentMeasurementsListener measurementsListener;
    private MeasurementsModel.GenderListener genderListener;

    private LoadMeasurements loadMeasurements;
    private LoadBodyParts loadBodyParts;
    private LoadUser loadUser;

    public MeasurementsModel(Object object) {
        measurementsListener = (MeasurementsModel.CurrentMeasurementsListener)object;
        genderListener = (MeasurementsModel.GenderListener)object;
    }

    public void loadBodyPartsDatabase() {
        loadBodyParts = new LoadBodyParts(this);
        loadBodyParts.loadBodyParts();
    }


    @Override
    public void onBodyPartsLoaded(ArrayList<String> bodyParts) {
        this.body_parts = bodyParts;
        loadMeasurements();
    }

    public void loadMeasurements() {
        loadMeasurements = new LoadMeasurements();
        loadMeasurements.setLastMeasurementsLoadedListener(this);
        loadMeasurements.loadLastMeasurements(this.body_parts);
    }

    @Override
    public void onLastMeasurementsLoaded(HashMap<String, Double> measurements) {
        ArrayList<String> texts = new ArrayList<>();
        for(int i = 0; i < body_parts.size(); i++) {
            Double text = ((Map<String, Double>) measurements).get(body_parts.get(i));
            if(text != null) {
                texts.add(text + " " + "cm");
            }
            else {
                texts.add("-- cm");
            }
        }

        measurementsListener.onMeasurementsLoaded(texts);
    }

    public void loadUser() {
        loadUser = new LoadUser();
        loadUser.setListLoadedListener(this);
        loadUser.loadUser();
    }


    @Override
    public void onUserLoaded(User user) {
        genderListener.onGenderLoaded(user.gender);

        loadBodyPartsDatabase();
    }

    public void removeListeners() {
        if(loadMeasurements != null) loadMeasurements.removeListeners();
        if(loadBodyParts != null) loadBodyParts.removeListeners();
        if(loadUser != null) loadUser.removeListeners();
    }


}
