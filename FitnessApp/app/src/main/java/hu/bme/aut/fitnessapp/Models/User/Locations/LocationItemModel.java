package hu.bme.aut.fitnessapp.Models.User.Locations;

//import android.support.v4.app.DialogFragment;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Equipment;
import hu.bme.aut.fitnessapp.Entities.Location;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadEquipment;

public class LocationItemModel implements LoadEquipment.EquipmentLoadedListener{

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private ArrayList<Equipment> equipmentList;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    private Location location;

    public interface ListLoadedListener {
        void onListLoaded(ArrayList<Equipment> equipmentList);
    }

    private LocationItemModel.ListLoadedListener listLoadedListener;

    public LocationItemModel(DialogFragment fragment) {
        listLoadedListener = (LocationItemModel.ListLoadedListener)fragment;

        //loadEquipment();
    }

    public void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void loadEquipment() {
        LoadEquipment loadEquipment = new LoadEquipment(this);
        loadEquipment.loadEquipment();
    }

    @Override
    public void onEquipmentLoaded(ArrayList<Equipment> equipment) {
        equipmentList = equipment;
        listLoadedListener.onListLoaded(equipmentList);
    }

    /* public void loadEquipment() {

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
                listLoadedListener.onListLoaded(equipmentList);
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

}
