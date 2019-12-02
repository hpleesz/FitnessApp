package hu.bme.aut.fitnessapp.Models.DatabaseModels;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Equipment;


public class LoadEquipment extends DatabaseConnection {

    public interface EquipmentLoadedListener {
        void onEquipmentLoaded(ArrayList<Equipment> equipment);
    }

    private LoadEquipment.EquipmentLoadedListener listLoadedListener;

    public LoadEquipment(Object object) {
        super();
        listLoadedListener = (LoadEquipment.EquipmentLoadedListener)object;
    }

    private ValueEventListener eventListener;

    public void loadEquipment() {

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Equipment> equipmentList = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    int id = Integer.parseInt(dataSnapshot1.getKey());
                    String name = (String) dataSnapshot1.getValue();
                    Equipment equipment = new Equipment(id, name);
                    equipmentList.add(equipment);
                }
                listLoadedListener.onEquipmentLoaded(equipmentList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }

        };
        getDatabaseReference().child("Equipment").addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener

    }

    public void removeListeners() {
        if(eventListener != null)getDatabaseReference().child("Equipment").removeEventListener(eventListener);
    }
}
