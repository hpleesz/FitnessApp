package hu.bme.aut.fitnessapp.Models.UserModels.LocationModels;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Equipment;
import hu.bme.aut.fitnessapp.Entities.PublicLocation;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadEquipment;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadPublicLocations;

public class ViewPublicLocationDetailsModel implements LoadEquipment.EquipmentLoadedListener, LoadPublicLocations.PublicLocationsByIDLoadedListener{

    private PublicLocation publicLocation;
    private ArrayList<Equipment> equipmentList;

    public interface DisplayReadyListener {
        void onTitleReady(String title);
        void onDetailsReady(String desc, String address, String equipment, ArrayList<String> hours);
    }

    private ViewPublicLocationDetailsModel.DisplayReadyListener listener;

    public ViewPublicLocationDetailsModel(Object object, PublicLocation publicLocation) {
        listener = (ViewPublicLocationDetailsModel.DisplayReadyListener)object;
        this.publicLocation = publicLocation;
    }

    public void loadEquipment() {
        LoadEquipment loadEquipment = new LoadEquipment(this);
        loadEquipment.loadEquipment();
    }

    @Override
    public void onEquipmentLoaded(ArrayList<Equipment> equipment) {
       equipmentList = equipment;
       loadPublicLocation();
    }


    private void loadPublicLocation() {
        LoadPublicLocations loadPublicLocations = new LoadPublicLocations();
        loadPublicLocations.setListLoadedByIDListener(this);
        loadPublicLocations.loadPublicLocationByID(Long.toString(publicLocation.id));

    }

    @Override
    public void onPublicLocationsByIDLoaded(PublicLocation location) {
        publicLocation = location;
        listener.onTitleReady(publicLocation.name);
        String full_address = publicLocation.country + "\n" + publicLocation.zip + ", " + publicLocation.city + "\n" + publicLocation.address;

        ArrayList<Integer> equipment = publicLocation.equipment;
        String equipment_text = "";
        if(equipment.contains(4) && equipment.contains(5)) equipment.remove(Integer.valueOf(4));
        if(equipment.contains(6) && equipment.contains(7)) equipment.remove(Integer.valueOf(6));
        if(equipment.size() == 1) {
            equipment_text = equipmentList.get(equipment.get(0)-1).name;
        }
        else {
            for (int equ : equipment) {
                if(equ != 1) {
                    if (equipment_text.equals(""))
                        equipment_text = equipmentList.get(equ - 1).name;
                    else
                        equipment_text = equipment_text + "\n" + equipmentList.get(equ - 1).name;
                }
            }
        }

        ArrayList<String> hour_text = new ArrayList<>();
        for(int i = 0; i < publicLocation.open_hours.size(); i ++) {
            if(publicLocation.open_hours.get(i)[0].equals("")) {
                hour_text.add("Closed");
            }
            else {
                String text = publicLocation.open_hours.get(i)[0] + " - " + publicLocation.open_hours.get(i)[1];
                hour_text.add(text);
            }
        }

        listener.onDetailsReady(publicLocation.description, full_address, equipment_text, hour_text);
    }


    public PublicLocation getPublicLocation() {
        return publicLocation;
    }

}
