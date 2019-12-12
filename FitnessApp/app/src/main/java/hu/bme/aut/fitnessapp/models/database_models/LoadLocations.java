package hu.bme.aut.fitnessapp.models.database_models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.entities.Location;

public class LoadLocations extends DatabaseConnection{

    private static final String LOCATIONS = "Locations";

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

    private ValueEventListener eventListener;

    public void loadLocations() {
        eventListener = new ValueEventListener() {
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
                logger.error(DB_ERROR, databaseError.toException());
            }

        };
        getDatabaseReference().child(LOCATIONS).child(getUserId()).addValueEventListener(eventListener);

    }

    public void addNewItem(int id, Location newItem) {
        getDatabaseReference().child(LOCATIONS).child(getUserId()).child(Integer.toString(id)).child("Name").setValue(newItem.getName());
        for(int i = 0; i < newItem.getEquipment().size(); i++) {
            getDatabaseReference().child(LOCATIONS).child(getUserId()).child(Integer.toString(id)).child("Equipment").child(Integer.toString(i)).setValue(newItem.getEquipment().get(i));
        }
    }

    public void updateItem(Location newItem) {
        getDatabaseReference().child(LOCATIONS).child(getUserId()).child(Integer.toString(newItem.getId())).child("Name").setValue(newItem.getName());
        getDatabaseReference().child(LOCATIONS).child(getUserId()).child(Integer.toString(newItem.getId())).child("Equipment").removeValue();
        for(int i = 0; i < newItem.getEquipment().size(); i++) {
            getDatabaseReference().child(LOCATIONS).child(getUserId()).child(Integer.toString(newItem.getId())).child("Equipment").child(Integer.toString(i)).setValue(newItem.getEquipment().get(i));
        }
    }

    public void removeItem(Location item) {
        getDatabaseReference().child(LOCATIONS).child(getUserId()).child(Integer.toString(item.getId())).removeValue();

    }

    public void removeListeners() {
        if(eventListener != null)getDatabaseReference().child(LOCATIONS).child(getUserId()).removeEventListener(eventListener);
    }
}
