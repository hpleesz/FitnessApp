package hu.bme.aut.fitnessapp.models.user_models.location_models;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.entities.Equipment;
import hu.bme.aut.fitnessapp.entities.Location;
import hu.bme.aut.fitnessapp.models.database_models.LoadEquipment;

public class LocationItemModel implements LoadEquipment.EquipmentLoadedListener{

    private Location location;
    private LoadEquipment loadEquipment;

    private LocationItemModel.ListLoadedListener listLoadedListener;

    public interface ListLoadedListener {
        void onListLoaded(ArrayList<Equipment> equipmentList);
    }

    public LocationItemModel(Object object) {
        listLoadedListener = (LocationItemModel.ListLoadedListener)object;

    }

    public void loadEquipment() {
        loadEquipment = new LoadEquipment(this);
        loadEquipment.loadEquipment();
    }

    @Override
    public void onEquipmentLoaded(ArrayList<Equipment> equipment) {
        listLoadedListener.onListLoaded(equipment);
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void removeListeners() {
        if(loadEquipment != null) loadEquipment.removeListeners();
    }


}
