package hu.bme.aut.fitnessapp.Models.GymModels;

import android.content.Context;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Controllers.Adapters.EquipmentAdapter;
import hu.bme.aut.fitnessapp.Entities.Equipment;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadEquipment;

public class NewPublicLocationModel implements LoadEquipment.EquipmentLoadedListener{

    public interface ListLoaded {
        void onListLoaded(ArrayList<Equipment> locations);
    }

    private NewPublicLocationModel.ListLoaded listener;

    public NewPublicLocationModel(Context activity) {
        listener = (NewPublicLocationModel.ListLoaded)activity;
    }

    public void loadEquipment() {
        LoadEquipment loadEquipment = new LoadEquipment(this);
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


    public boolean openCloseTimesValid(ArrayList<String[]> open_hours) {
        for(String[] day : open_hours) {
            if((day[0].equals("") && !day[1].equals("")) || (!day[0].equals("") && day[1].equals(""))) {
                return false;
            }
        }
        return true;
    }

    public boolean openCloseTimesDiffValid(ArrayList<String[]> open_hours) {
        for(String[] day : open_hours) {
            if(!day[0].equals("")) {
                String open = day[0].replace(":", "");
                String close = day[1].replace(":", "");
                open = open.replaceAll("^0+", "");
                close = close.replaceAll("^0+", "");

                int open_num = 0;
                int close_num = 0;
                if(!open.equals("")) open_num = Integer.parseInt(open);
                if(!close.equals("")) close_num = Integer.parseInt(close);

                int diff = close_num - open_num;
                if(diff <= 0) return false;
            }
        }
        return true;
    }


}
