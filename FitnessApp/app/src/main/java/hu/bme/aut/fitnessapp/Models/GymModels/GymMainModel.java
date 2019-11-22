package hu.bme.aut.fitnessapp.Models.GymModels;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.PublicLocation;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadPublicLocations;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadUserPublicLocations;

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
        loadUserPublicLocations.removeUserPublicLocation(Long.toString(item.id));
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

}


