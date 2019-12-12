package hu.bme.aut.fitnessapp.models.gym_models;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.entities.Equipment;
import hu.bme.aut.fitnessapp.models.database_models.LoadEquipment;

public class NewPublicLocationModel implements LoadEquipment.EquipmentLoadedListener{

    public interface ListLoaded {
        void onListLoaded(ArrayList<Equipment> locations);
    }

    private NewPublicLocationModel.ListLoaded listener;

    private LoadEquipment loadEquipment;

    public NewPublicLocationModel(Context activity) {
        listener = (NewPublicLocationModel.ListLoaded)activity;
    }

    public void loadEquipment() {
        loadEquipment = new LoadEquipment(this);
        loadEquipment.loadEquipment();
    }

    @Override
    public void onEquipmentLoaded(ArrayList<Equipment> equipment) {
        listener.onListLoaded(equipment);
    }

    public String setTime(int hourOfDay, int minutes) {
        String hour = Integer.toString(hourOfDay);
        String min = Integer.toString(minutes);

        if(hourOfDay < 10) hour = "0" + hour;
        if(minutes < 10) min = "0" + min;

        return hour + ":" + min;
    }


    public boolean openCloseTimesValid(List<String[]> openHours) {
        for(String[] day : openHours) {
            if((day[0].equals("") && !day[1].equals("")) || (!day[0].equals("") && day[1].equals(""))) {
                return false;
            }
        }
        return true;
    }

    public boolean openCloseTimesDiffValid(List<String[]> openHours) {
        for(String[] day : openHours) {
            if(!day[0].equals("")) {
                String open = day[0].replace(":", "");
                String close = day[1].replace(":", "");
                open = open.replaceAll("^0+", "");
                close = close.replaceAll("^0+", "");

                int openNum = 0;
                int closeNum = 0;
                if(!open.equals("")) openNum = Integer.parseInt(open);
                if(!close.equals("")) closeNum = Integer.parseInt(close);

                int diff = closeNum - openNum;
                if(diff <= 0) return false;
            }
        }
        return true;
    }

    public void removeListeners() {
        if(loadEquipment != null) loadEquipment.removeListeners();
    }


}
