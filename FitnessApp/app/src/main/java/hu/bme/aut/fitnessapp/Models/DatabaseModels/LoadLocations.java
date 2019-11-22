package hu.bme.aut.fitnessapp.Models.DatabaseModels;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Location;

public class LoadLocations extends DatabaseConnection{

    public interface LocationsLoadedListener {
        void onLocationsLoaded(ArrayList<Location> locations);
    }

    private LoadLocations.LocationsLoadedListener listLoadedListener;

    public LoadLocations() {
        super();
    }

    public void setListLoadedListener(Object object) {
        listLoadedListener = (LoadLocations.LocationsLoadedListener)object;

    }

    public void loadLocations() {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Location> itemlist = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    int id = Integer.parseInt(dataSnapshot1.getKey());
                    String name = dataSnapshot1.child("Name").getValue(String.class);
                    ArrayList<Integer> equipment = new ArrayList<>();

                    for(DataSnapshot dataSnapshot2: dataSnapshot1.child("Equipment").getChildren()) {
                        int idx = dataSnapshot2.getValue(Integer.class);
                        equipment.add(idx);
                    }

                    Location location = new Location(id, name, equipment);

                    itemlist.add(location);


                }
                listLoadedListener.onLocationsLoaded(itemlist);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        getDatabaseReference().child("Locations").child(getUserId()).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;
    }

    public void addNewItem(int id, Location newItem) {
        getDatabaseReference().child("Locations").child(getUserId()).child(Integer.toString(id)).child("Name").setValue(newItem.name);
        for(int i = 0; i < newItem.equipment.size(); i++) {
            getDatabaseReference().child("Locations").child(getUserId()).child(Integer.toString(id)).child("Equipment").child(Integer.toString(i)).setValue(newItem.equipment.get(i));
        }
    }

    public void updateItem(Location newItem) {
        getDatabaseReference().child("Locations").child(getUserId()).child(Integer.toString(newItem.id)).child("Name").setValue(newItem.name);
        getDatabaseReference().child("Locations").child(getUserId()).child(Integer.toString(newItem.id)).child("Equipment").removeValue();
        for(int i = 0; i < newItem.equipment.size(); i++) {
            getDatabaseReference().child("Locations").child(getUserId()).child(Integer.toString(newItem.id)).child("Equipment").child(Integer.toString(i)).setValue(newItem.equipment.get(i));
        }
    }

    public void removeItem(Location item) {
        getDatabaseReference().child("Locations").child(getUserId()).child(Integer.toString(item.id)).removeValue();

    }

}
