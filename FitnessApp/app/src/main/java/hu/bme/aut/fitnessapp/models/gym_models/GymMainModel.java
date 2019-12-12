package hu.bme.aut.fitnessapp.models.gym_models;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.entities.PublicLocation;
import hu.bme.aut.fitnessapp.models.database_models.LoadPublicLocations;
import hu.bme.aut.fitnessapp.models.database_models.LoadUserPublicLocations;

public class GymMainModel implements LoadPublicLocations.PublicLocationsByCreatorLoadedListener{

    private GymMainModel.ListLoaded listener;

    private LoadPublicLocations loadPublicLocations;


    public interface ListLoaded {
        void onListLoaded(ArrayList<PublicLocation> locations);
    }


    public GymMainModel(Object object, PublicLocation publicLocation) {
        listener = (GymMainModel.ListLoaded)object;

        loadPublicLocations = new LoadPublicLocations();
        if(publicLocation != null) {
            loadPublicLocations.addNewItem(publicLocation);
        }

    }

    public void loadList() {
        loadPublicLocations.setListLoadedByCreatorListener(this);
        loadPublicLocations.loadPublicLocationsByCreator();
    }

    @Override
    public void onPublicLocationsByCreatorLoaded(ArrayList<PublicLocation> publicLocations) {
        listener.onListLoaded(publicLocations);
    }


    public void deleteItem(final PublicLocation item) {
        loadPublicLocations.removeItem(item);
        LoadUserPublicLocations loadUserPublicLocations = new LoadUserPublicLocations();
        loadUserPublicLocations.removeUserPublicLocation(Long.toString(item.getId()));
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public void removeListeners() {
        if(loadPublicLocations != null) loadPublicLocations.removeListeners();
    }

}


