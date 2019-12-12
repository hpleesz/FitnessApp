package hu.bme.aut.fitnessapp.models.user_models.location_models;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.entities.Equipment;
import hu.bme.aut.fitnessapp.entities.PublicLocation;
import hu.bme.aut.fitnessapp.models.database_models.LoadEquipment;
import hu.bme.aut.fitnessapp.models.database_models.LoadPublicLocations;

public class ViewPublicLocationDetailsModel implements LoadEquipment.EquipmentLoadedListener, LoadPublicLocations.PublicLocationsByIDLoadedListener{

    private PublicLocation publicLocation;
    private ArrayList<Equipment> equipmentList;

    private LoadEquipment loadEquipment;
    private LoadPublicLocations loadPublicLocations;

    private ViewPublicLocationDetailsModel.DisplayReadyListener listener;

    public interface DisplayReadyListener {
        void onTitleReady(String title);
        void onDetailsReady(String desc, String address, String equipment, ArrayList<String> hours);
    }

    public ViewPublicLocationDetailsModel(Object object, PublicLocation publicLocation) {
        listener = (ViewPublicLocationDetailsModel.DisplayReadyListener)object;
        this.publicLocation = publicLocation;
    }

    public void loadEquipment() {
        loadEquipment = new LoadEquipment(this);
        loadEquipment.loadEquipment();
    }

    @Override
    public void onEquipmentLoaded(ArrayList<Equipment> equipment) {
       equipmentList = equipment;
       loadPublicLocation();
    }


    private void loadPublicLocation() {
        loadPublicLocations = new LoadPublicLocations();
        loadPublicLocations.setListLoadedByIDListener(this);
        loadPublicLocations.loadPublicLocationByID(Long.toString(publicLocation.getId()));

    }

    @Override
    public void onPublicLocationsByIDLoaded(PublicLocation location) {
        publicLocation = location;
        listener.onTitleReady(publicLocation.getName());
        String fullAddress = publicLocation.getCountry() + "\n" + publicLocation.getZip() + ", " + publicLocation.getCity() + "\n" + publicLocation.getAddress();

        List<Integer> equipment = publicLocation.getEquipment();
        String equipmentText = "";
        if(equipment.contains(4) && equipment.contains(5)) equipment.remove(Integer.valueOf(4));
        if(equipment.contains(6) && equipment.contains(7)) equipment.remove(Integer.valueOf(6));
        if(equipment.size() == 1) {
            equipmentText = equipmentList.get(equipment.get(0)-1).getName();
        }
        else {
            for (int equ : equipment) {
                if(equ != 1) {
                    if (equipmentText.equals(""))
                        equipmentText = equipmentList.get(equ - 1).getName();
                    else
                        equipmentText = equipmentText + "\n" + equipmentList.get(equ - 1).getName();
                }
            }
        }

        ArrayList<String> hourText = new ArrayList<>();
        for(int i = 0; i < publicLocation.getOpenHours().size(); i ++) {
            if(publicLocation.getOpenHours().get(i)[0].equals("")) {
                hourText.add("Closed");
            }
            else {
                String text = publicLocation.getOpenHours().get(i)[0] + " - " + publicLocation.getOpenHours().get(i)[1];
                hourText.add(text);
            }
        }

        listener.onDetailsReady(publicLocation.getDescription(), fullAddress, equipmentText, hourText);
    }


    public PublicLocation getPublicLocation() {
        return publicLocation;
    }

    public void removeListeners() {
        if(loadPublicLocations != null) loadPublicLocations.removeListeners();
        if(loadEquipment != null) loadEquipment.removeListeners();
    }
}
