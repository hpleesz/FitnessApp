package hu.bme.aut.fitnessapp.models.database_models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.entities.Equipment;


public class LoadEquipment extends DatabaseConnection {

    private static final String EQUIPMENT = "Equipment";

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
                logger.error(DB_ERROR, databaseError.toException());
            }

        };
        getDatabaseReference().child(EQUIPMENT).addValueEventListener(eventListener);

    }

    public void removeListeners() {
        if(eventListener != null)getDatabaseReference().child(EQUIPMENT).removeEventListener(eventListener);
    }
}
