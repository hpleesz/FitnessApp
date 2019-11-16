package hu.bme.aut.fitnessapp.Models.Gym;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import hu.bme.aut.fitnessapp.Adapters.EquipmentAdapter;
import hu.bme.aut.fitnessapp.Entities.Equipment;
import hu.bme.aut.fitnessapp.Entities.PublicLocation;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadEquipment;

public class NewPublicLocationModel implements LoadEquipment.EquipmentLoadedListener{

    private DatabaseReference databaseReference;

    private ArrayList<Equipment> equipmentList;

    private EquipmentAdapter adapter;

    private EditText name;
    private EditText description;
    private EditText country;
    private EditText city;
    private EditText zip;
    private EditText address;

    private ArrayList<EditText> openHours;
    private ArrayList<CheckBox> checkBoxes;

    private Context activity;

    public interface ListLoaded {
        void onListLoaded(ArrayList<Equipment> locations);
    }

    private NewPublicLocationModel.ListLoaded listener;

    public NewPublicLocationModel(Context activity) {
        listener = (NewPublicLocationModel.ListLoaded)activity;
        //databaseReference = FirebaseDatabase.getInstance().getReference();
        this.activity = activity;
        //loadEquipment();
    }

    public void loadEquipment() {
        LoadEquipment loadEquipment = new LoadEquipment(this);
        loadEquipment.loadEquipment();
    }

    @Override
    public void onEquipmentLoaded(ArrayList<Equipment> equipment) {
        equipmentList = equipment;
        listener.onListLoaded(equipmentList);
    }
    /*
    private void loadEquipment() {

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                equipmentList = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    int id = Integer.parseInt(dataSnapshot1.getKey());
                    String name = (String) dataSnapshot1.getValue();
                    Equipment equipment = new Equipment(id, name);
                    equipmentList.add(equipment);
                }
                listener.onListLoaded(equipmentList);
                //((NewPublicLocationActivity)activity).initRecyclerView(equipmentList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Equipment").addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener

    }

     */

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















    public ArrayList<Equipment> getEquipmentList() {
        return equipmentList;
    }

    public EquipmentAdapter getAdapter() {
        return adapter;
    }

    public EditText getName() {
        return name;
    }

    public EditText getDescription() {
        return description;
    }

    public EditText getCountry() {
        return country;
    }

    public EditText getCity() {
        return city;
    }

    public EditText getZip() {
        return zip;
    }

    public EditText getAddress() {
        return address;
    }

    public ArrayList<EditText> getOpenHours() {
        return openHours;
    }

    public ArrayList<CheckBox> getCheckBoxes() {
        return checkBoxes;
    }

    public void setAdapter(EquipmentAdapter adapter) {
        this.adapter = adapter;
    }

    public Context getActivity() {
        return activity;
    }

}
