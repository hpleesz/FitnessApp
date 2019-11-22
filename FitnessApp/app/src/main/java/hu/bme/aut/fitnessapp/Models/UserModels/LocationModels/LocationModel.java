package hu.bme.aut.fitnessapp.Models.UserModels.LocationModels;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Location;
import hu.bme.aut.fitnessapp.Entities.PublicLocation;
import hu.bme.aut.fitnessapp.Entities.UserPublicLocation;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadLocations;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadPublicLocations;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadUserPublicLocations;

public class LocationModel implements LoadLocations.LocationsLoadedListener, LoadUserPublicLocations.UserPublicLocationsLoadedListener, LoadPublicLocations.PublicLocationsByIDLoadedListener{

    private ArrayList<Location> itemlist;
    private ArrayList<PublicLocation> public_itemlist;
    private PublicLocation newPublicLocation;
    private ArrayList<UserPublicLocation> publicIDs;

    private LoadLocations loadLocations;
    private LoadUserPublicLocations loadUserPublicLocations;

    public interface LocationsLoaded {
        void onLocationsLoaded(ArrayList<Location> locations);
    }

    public interface PublicLocationsLoaded {
        void onPublicLocationsLoaded(ArrayList<PublicLocation> publicLocations);
    }

    private LocationModel.LocationsLoaded listener;
    private LocationModel.PublicLocationsLoaded listener2;


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
        public_itemlist = new ArrayList<>();
        if(publicIDs.isEmpty()) {
            if(newPublicLocation != null) {
                addNewItem();
            }
            listener2.onPublicLocationsLoaded(public_itemlist);
        }
        LoadPublicLocations loadPublicLocations = new LoadPublicLocations();
        loadPublicLocations.setListLoadedByIDListener(this);
        for(UserPublicLocation loc: publicIDs) {
            loadPublicLocations.loadPublicLocationByID(loc.gym_id);

        }

    }

    @Override
    public void onPublicLocationsByIDLoaded(PublicLocation publicLocation) {
        int idx = -1;
        for(int i = 0; i < public_itemlist.size(); i++) {
            if(public_itemlist.get(i).id == publicLocation.id) {
                idx = i;
                break;
            }
        }
        //update
        if(idx > -1) {
            public_itemlist.set(idx, publicLocation);
        }
        //add
        else {
            public_itemlist.add(publicLocation);
        }

        listener2.onPublicLocationsLoaded(public_itemlist);
        if(newPublicLocation != null) {
            addNewItem();
        }
    }

    public void createLocationItem(final Location newItem) {
        int id = 0;
        if(!itemlist.isEmpty()) id = itemlist.get(itemlist.size()-1).id +1;
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
            if(Long.parseLong(loc.gym_id) == item.id) {
                idx = Long.toString(loc.id);
            }
        }
        loadUserPublicLocations.removeItem(idx);
    }

    public void addNewItem() {
        PublicLocation publicLocation = newPublicLocation;
        newPublicLocation = null;

        for(UserPublicLocation loc : publicIDs) {
            if(Long.toString(publicLocation.id).equals(loc.gym_id)) return;
        }

        int id = 0;
        if(!publicIDs.isEmpty()) id = publicIDs.get(publicIDs.size()-1).id + 1;
        loadUserPublicLocations.addNewItem(id, publicLocation);
    }

    public ArrayList<Location> getItemlist() {
        return itemlist;
    }

    public ArrayList<PublicLocation> getPublic_itemlist() {
        return public_itemlist;
    }
}
