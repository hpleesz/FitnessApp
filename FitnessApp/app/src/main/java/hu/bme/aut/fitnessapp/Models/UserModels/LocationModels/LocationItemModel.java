package hu.bme.aut.fitnessapp.Models.UserModels.LocationModels;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Equipment;
import hu.bme.aut.fitnessapp.Entities.Location;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadEquipment;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadProfile;

public class LocationItemModel implements LoadEquipment.EquipmentLoadedListener{

    private Location location;

    public interface ListLoadedListener {
        void onListLoaded(ArrayList<Equipment> equipmentList);
    }

    private LocationItemModel.ListLoadedListener listLoadedListener;

    private LoadEquipment loadEquipment;

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
