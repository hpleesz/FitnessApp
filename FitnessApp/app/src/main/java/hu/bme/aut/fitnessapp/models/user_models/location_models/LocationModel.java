package hu.bme.aut.fitnessapp.models.user_models.location_models;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.entities.Location;
import hu.bme.aut.fitnessapp.entities.PublicLocation;
import hu.bme.aut.fitnessapp.entities.UserPublicLocation;
import hu.bme.aut.fitnessapp.models.database_models.LoadLocations;
import hu.bme.aut.fitnessapp.models.database_models.LoadPublicLocations;
import hu.bme.aut.fitnessapp.models.database_models.LoadUserPublicLocations;

public class LocationModel implements LoadLocations.LocationsLoadedListener, LoadUserPublicLocations.UserPublicLocationsLoadedListener, LoadPublicLocations.PublicLocationsByIDLoadedListener{

    private ArrayList<Location> itemlist;
    private ArrayList<PublicLocation> publicItemlist;
    private PublicLocation newPublicLocation;
    private ArrayList<UserPublicLocation> publicIDs;

    private LoadLocations loadLocations;
    private LoadUserPublicLocations loadUserPublicLocations;
    private LoadPublicLocations loadPublicLocations;

    private LocationModel.LocationsLoaded listener;
    private LocationModel.PublicLocationsLoaded listener2;

    public interface LocationsLoaded {
        void onLocationsLoaded(ArrayList<Location> locations);
    }

    public interface PublicLocationsLoaded {
        void onPublicLocationsLoaded(ArrayList<PublicLocation> publicLocations);
    }


    public LocationModel(Object object, PublicLocation publicLocation) {
        listener = (LocationModel.LocationsLoaded)object;
        listener2 = (LocationModel.PublicLocationsLoaded)object;

        newPublicLocation = publicLocation;

    }

    public void loadLocations() {
        loadLocations = new LoadLocations();
        loadLocations.setListLoadedListener(this);
        loadLocations.loadLocations();
    }

    @Override
    public void onLocationsLoaded(ArrayList<Location> locations) {
        itemlist = locations;
        listener.onLocationsLoaded(itemlist);
    }

    public void loadListPublic() {
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
        publicItemlist = new ArrayList<>();
        if(publicIDs.isEmpty()) {
            if(newPublicLocation != null) {
                addNewItem();
            }
            listener2.onPublicLocationsLoaded(publicItemlist);
        }
        loadPublicLocations = new LoadPublicLocations();
        loadPublicLocations.setListLoadedByIDListener(this);
        for(UserPublicLocation loc: publicIDs) {
            loadPublicLocations.loadPublicLocationByID(loc.getGymId());
        }

    }

    @Override
    public void onPublicLocationsByIDLoaded(PublicLocation publicLocation) {
        int idx = -1;
        for(int i = 0; i < publicItemlist.size(); i++) {
            if(publicItemlist.get(i).getId() == publicLocation.getId()) {
                idx = i;
                break;
            }
        }
        //update
        if(idx > -1) {
            publicItemlist.set(idx, publicLocation);
        }
        //add
        else {
            publicItemlist.add(publicLocation);
        }

        listener2.onPublicLocationsLoaded(publicItemlist);
        if(newPublicLocation != null) {
            addNewItem();
        }
    }

    public void createLocationItem(final Location newItem) {
        int id = 0;
        if(!itemlist.isEmpty()) id = itemlist.get(itemlist.size()-1).getId() +1;
        loadLocations.addNewItem(id, newItem);

    }

    public void updateLocationItem(final Location newItem) {
        loadLocations.updateItem(newItem);
    }

    public void deleteLocationItem(final Location item) {
        loadLocations.removeItem(item);
    }

    public void deletePublicLocationItem(PublicLocation item) {
        String idx = "";
        for(UserPublicLocation loc : publicIDs) {
            if(Long.parseLong(loc.getGymId()) == item.getId()) {
                idx = Long.toString(loc.getId());
            }
        }
        loadUserPublicLocations.removeItem(idx);
    }

    public void addNewItem() {
        PublicLocation publicLocation = newPublicLocation;
        newPublicLocation = null;

        for(UserPublicLocation loc : publicIDs) {
            if(Long.toString(publicLocation.getId()).equals(loc.getGymId())) return;
        }

        int id = 0;
        if(!publicIDs.isEmpty()) id = publicIDs.get(publicIDs.size()-1).getId() + 1;
        loadUserPublicLocations.addNewItem(id, publicLocation);
    }

    public List<Location> getItemlist() {
        return itemlist;
    }

    public void removeListeners() {
        if(loadLocations != null) loadLocations.removeListeners();
        if(loadUserPublicLocations != null)loadUserPublicLocations.removeListeners();
        if(loadPublicLocations != null) loadPublicLocations.removeListeners();
    }

}
