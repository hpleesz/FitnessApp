package hu.bme.aut.fitnessapp.models.user_models.measurement_models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hu.bme.aut.fitnessapp.entities.User;
import hu.bme.aut.fitnessapp.models.database_models.LoadBodyParts;
import hu.bme.aut.fitnessapp.models.database_models.LoadMeasurements;
import hu.bme.aut.fitnessapp.models.database_models.LoadUser;

public class MeasurementsModel implements LoadUser.UserLoadedListener, LoadBodyParts.BodyPartsLoadedListener, LoadMeasurements.LastMeasurementsLoadedListener {

    private ArrayList<String> bodyParts;
    private LoadMeasurements loadMeasurements;
    private LoadBodyParts loadBodyParts;
    private LoadUser loadUser;

    private MeasurementsModel.CurrentMeasurementsListener measurementsListener;
    private MeasurementsModel.GenderListener genderListener;

    public interface CurrentMeasurementsListener {
        void onMeasurementsLoaded(ArrayList<String> measurements);
    }

    public interface GenderListener {
        void onGenderLoaded(int gender);
    }


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
        this.bodyParts = bodyParts;
        loadMeasurements();
    }

    public void loadMeasurements() {
        loadMeasurements = new LoadMeasurements();
        loadMeasurements.setLastMeasurementsLoadedListener(this);
        loadMeasurements.loadLastMeasurements(this.bodyParts);
    }

    @Override
    public void onLastMeasurementsLoaded(HashMap<String, Double> measurements) {
        ArrayList<String> texts = new ArrayList<>();
        for(int i = 0; i < bodyParts.size(); i++) {
            Double text = measurements.get(bodyParts.get(i));
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
        genderListener.onGenderLoaded(user.getGender());

        loadBodyPartsDatabase();
    }

    public void removeListeners() {
        if(loadMeasurements != null) loadMeasurements.removeListeners();
        if(loadBodyParts != null) loadBodyParts.removeListeners();
        if(loadUser != null) loadUser.removeListeners();
    }


}
