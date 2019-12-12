package hu.bme.aut.fitnessapp.models.user_models.workout_models;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.entities.Location;
import hu.bme.aut.fitnessapp.entities.PublicLocation;
import hu.bme.aut.fitnessapp.entities.UserPublicLocation;
import hu.bme.aut.fitnessapp.models.database_models.LoadLocations;
import hu.bme.aut.fitnessapp.models.database_models.LoadPublicLocations;
import hu.bme.aut.fitnessapp.models.database_models.LoadUserPublicLocations;

public class ChooseLocationItemModel implements LoadLocations.LocationsLoadedListener, LoadUserPublicLocations.UserPublicLocationsLoadedListener, LoadPublicLocations.PublicLocationsByIDLoadedListener{

    private ArrayList<PublicLocation> itemList;
    private ArrayList<Location> locations;
    private ArrayList<UserPublicLocation> publicIDs;

    private LoadLocations loadLocations;
    private LoadUserPublicLocations loadUserPublicLocations;
    private LoadPublicLocations loadPublicLocations;

    public interface LocationsLoaded {
        void onLocationsLoaded();
    }

    public interface PublicLocationsLoaded {
        void onPublicLocationsLoaded();
    }

    private ChooseLocationItemModel.LocationsLoaded listener;
    private ChooseLocationItemModel.PublicLocationsLoaded listener2;


    public ChooseLocationItemModel(Object object) {
        listener = (ChooseLocationItemModel.LocationsLoaded)object;
        listener2 = (ChooseLocationItemModel.PublicLocationsLoaded)object;
    }


    public void loadLocations() {
        loadLocations = new LoadLocations();
        loadLocations.setListLoadedListener(this);
        loadLocations.loadLocations();
    }

    @Override
    public void onLocationsLoaded(ArrayList<Location> locations) {
        this.locations = locations;
        listener.onLocationsLoaded();
    }

    public void loadPublicLocations() {
        loadUserPublicLocations = new LoadUserPublicLocations();
        loadUserPublicLocations.setListLoadedListener(this);
        loadUserPublicLocations.loadUserPublicLocations();
    }

    @Override
    public void onUserPublicLocationsLoaded(ArrayList<UserPublicLocation> userPublicLocations) {
        publicIDs = userPublicLocations;
        loadGyms();
    }

    private  void loadGyms() {
        itemList = new ArrayList<>();
        if(publicIDs.isEmpty()) {
            listener2.onPublicLocationsLoaded();
        }
        for(UserPublicLocation loc: publicIDs) {
            loadPublicLocations = new LoadPublicLocations();
            loadPublicLocations.setListLoadedByIDListener(this);
            loadPublicLocations.loadPublicLocationByID(loc.getGymId());
        }

    }

    @Override
    public void onPublicLocationsByIDLoaded(PublicLocation publicLocation) {
        int idx = -1;
        for(int i = 0; i < itemList.size(); i++) {
            if(itemList.get(i).getId() == publicLocation.getId()) {
                idx = i;
                break;
            }
        }
        if(idx > -1) {
            itemList.set(idx, publicLocation);
        }
        else {
            itemList.add(publicLocation);
        }

        listener2.onPublicLocationsLoaded();

    }


    public List<PublicLocation> getItemList() {
        return itemList;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void removeListeners() {
        if(loadLocations != null) loadLocations.removeListeners();
        if(loadUserPublicLocations != null) loadUserPublicLocations.removeListeners();
        if(loadPublicLocations != null) loadPublicLocations.removeListeners();
    }

}